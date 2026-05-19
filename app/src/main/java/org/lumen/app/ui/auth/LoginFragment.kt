package org.lumen.app.ui.auth

import TokenManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.LoginRequest
import org.lumen.app.databinding.FragmentLoginBinding
import org.lumen.app.util.showBottomSheet

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        tokenManager = TokenManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener(){
        binding.btnEntrar.setOnClickListener {
            val identificador = binding.editIdentificador.text.toString().trim()
            val senha = binding.editSenha.text.toString().trim()

            if (validarDados(identificador, senha)) {
                processarLogin(identificador, senha)
            }
        }
    }

    private fun validarDados(identificador: String, senha: String): Boolean {
        if (identificador.isEmpty()) {
            Toast.makeText(requireContext(), "Insira seu email ou nome de usuário", Toast.LENGTH_SHORT).show()
            return false
        }

        if (senha.isEmpty()) {
            Toast.makeText(requireContext(), "Insira a sua senha", Toast.LENGTH_SHORT).show()
            return false
        }

        if (senha.length < 8 ) {
            Toast.makeText(requireContext(), "Senha muito curta. Minímo 8 caracteres.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun processarLogin(identificador:String, senha: String) {
        binding.btnEntrar.isEnabled = false
        binding.progressBar.isVisible = true
        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val request = LoginRequest(identificador,senha)
                val response = RetrofitClient.authApi.login(request)

                binding.btnEntrar.isEnabled = true
                binding.progressBar.isGone = true

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.access_token
                    tokenManager.saveToken(token)
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    showBottomSheet(message = "Acesso não autorizado.")
                }
            } catch ( e : Exception) {
                Log.i("LOGIN", e.message.toString())
                binding.btnEntrar.isEnabled = true
                binding.progressBar.isGone = true
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}