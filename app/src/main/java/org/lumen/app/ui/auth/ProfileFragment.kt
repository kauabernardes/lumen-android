package org.lumen.app.ui.auth

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.lumen.app.R
import org.lumen.app.adapter.CommunityVerticalAdapter
import org.lumen.app.data.local.TokenManager
import org.lumen.app.data.model.Community
import org.lumen.app.data.model.User
import org.lumen.app.data.remote.Constants.BASE_URL
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentProfileBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.showBottomSheet
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private var currentPage = 1
    private var isLoading = false
    private var hasMorePages = true

    private val args: ProfileFragmentArgs by navArgs()

    private val comunidadesSalvas = mutableListOf<Community>()

    private var selectedImageUri: Uri? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.userIcon.setImageURI(uri)
            updateProfile(username = null, email = null, imageUri = uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenManager = TokenManager(requireContext())
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.userId != null) {
            initUser(args.userId.toString())
        } else {
            initSelf()
        }

        initListener()
    }

    private fun initSelf() {
        binding.name.text = tokenManager.getUsername()
        binding.username.text = "@${tokenManager.getUsername()}"
        Glide.with(this)
            .load(tokenManager.getProfileImage())
            .placeholder(R.drawable.ic_user_circle)
            .into(binding.userIcon)

        binding.btnEdit.isVisible = true

        if (comunidadesSalvas.isNotEmpty()) {
            configurarRecyclerViewInicial(comunidadesSalvas)
            setupScrollListener()
        } else {
            carregarComunidades(tokenManager.getSub())
        }
    }

    private fun initUser(userId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.userApi.user(tokenManager.getBearer(), userId)

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!



                    binding.name.text = user.username
                    binding.username.text = "@${user.username}"
                    binding.btnEdit.isVisible = false

                    Glide.with(this@ProfileFragment)
                        .load("${BASE_URL}${user.imgProfile}")
                        .placeholder(R.drawable.ic_user_circle)
                        .into(binding.userIcon)

                    if (comunidadesSalvas.isNotEmpty()) {
                        configurarRecyclerViewInicial(comunidadesSalvas)
                        setupScrollListener()
                    } else {
                        carregarComunidades(userId)
                    }
                } else {
                    showBottomSheet(message = response.errorMessage())
                }

            } catch (e: Exception) {
                showBottomSheet(message = e.message ?: "Erro ao buscar usuário")
            }
        }
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnEdit.setOnClickListener {
            pickMedia.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun updateProfile(username: String?, email: String?, imageUri: Uri?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val usernamePart = username?.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email?.toRequestBody("text/plain".toMediaTypeOrNull())

                var filePart: MultipartBody.Part? = null

                if (imageUri != null) {
                    val file = getFileFromUri(imageUri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    }
                }

                val response = RetrofitClient.userApi.editProfile(
                    token = tokenManager.getBearer(),
                    username = usernamePart,
                    email = emailPart,
                    file = filePart
                )

                if (response.isSuccessful) {
                    showBottomSheet(message = "Perfil atualizado com sucesso!")
                } else {
                    showBottomSheet(message = response.errorMessage())
                }

            } catch (e: Exception) {
                showBottomSheet(message = "Erro de conexão ao atualizar perfil.")
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val contentResolver = requireContext().contentResolver
        val tempFile = File(requireContext().cacheDir, "temp_profile_upload.jpg")
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e("Profile", "Erro ao processar imagem", e)
            null
        }
    }

    private fun carregarComunidades(userId: String) {
        if (isLoading || !hasMorePages) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.communityApi.userIn(tokenManager.getBearer(), userId, page = currentPage)

                if (response.isSuccessful && response.body() != null) {
                    val communitiesList = response.body()!!.data
                    Log.d("testeK1", communitiesList.toString())
                    hasMorePages = communitiesList.isNotEmpty()

                    if (currentPage == 1) {
                        comunidadesSalvas.clear()
                        comunidadesSalvas.addAll(communitiesList)
                        configurarRecyclerViewInicial(comunidadesSalvas)
                        setupScrollListener()
                    } else {
                        comunidadesSalvas.addAll(communitiesList)
                        (binding.recyclerCommunities.adapter as? CommunityVerticalAdapter)?.addCommunities(communitiesList)
                    }

                    if (hasMorePages) currentPage++
                } else {
                    showBottomSheet(message = response.errorMessage())
                }
            } catch (e: Exception) {
                Log.e("ERRO", "Falha", e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun configurarRecyclerViewInicial(lista: List<Community>) {
        val mutableCommunities = lista.toMutableList()

        val communityAdapter = CommunityVerticalAdapter(mutableCommunities) { communityClicked ->
            val action = ProfileFragmentDirections.actionProfileFragmentToFeedComunidadeFragment(communityClicked.id)
            findNavController().navigate(action)
        }

        binding.recyclerCommunities.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerCommunities.adapter = communityAdapter
    }

    private fun setupScrollListener() {
        binding.recyclerCommunities.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dx > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && hasMorePages) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                            val currentUserId = args.userId ?: tokenManager.getSub()
                            carregarComunidades(currentUserId)
                        }
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}