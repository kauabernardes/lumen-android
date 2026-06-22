package org.lumen.app.util

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.lumen.app.MainActivity
import org.lumen.app.R
import org.lumen.app.data.model.post.Post
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.BottomSheetBinding
import org.lumen.app.databinding.ChatBottomSheetBinding
import retrofit2.Response


fun Fragment.setupPostLikeInteraction(
    post: Post,
    position: Int,
    adapter: RecyclerView.Adapter<*>,
    bearer: String
) {


    post.isLiked = post.isLiked != true

    if (post.isLiked == true) {
        post.likesCount = post.likesCount?.plus(1)
    }else {
        post.likesCount = post.likesCount?.minus(1)
    }
    adapter.notifyItemChanged(position)

    viewLifecycleOwner.lifecycleScope.launch {
        try {
            val response = RetrofitClient.postApi.like(bearer, post.id)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                post.isLiked = body.liked
                post.likesCount = body.totalLikes
                adapter.notifyItemChanged(position)
            } else {

                reverterEstadoDeLike(post, position, adapter)
                showBottomSheet(message = response.errorMessage())
            }
        } catch (e: Exception) {

            reverterEstadoDeLike(post, position, adapter)
            showBottomSheet(message = "Sem conexão com o servidor.")
        }
    }
}

private fun reverterEstadoDeLike(post: Post, position: Int, adapter: RecyclerView.Adapter<*>) {
    post.isLiked = post.isLiked != true
    if (post.isLiked == true) {
        post.likesCount = post.likesCount?.plus(1)
    }else {
        post.likesCount = post.likesCount?.minus(1)
    }
    adapter.notifyItemChanged(position)
}


fun Fragment.showBottomSheet(
    titleDialog: Int? = null,
    titleButton: Int? = null,
    message: String,
    onClick: () -> Unit = {}
){
    val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    val binding = BottomSheetBinding.inflate(layoutInflater, null, false)

    binding.textViewTitle.text = getText(titleDialog ?: R.string.text_title_warning)
    binding.textViewMessage.text = message
    binding.button.text=getText(titleButton ?: R.string.text_button_warning)
    binding.button.setOnClickListener {
        onClick()
        bottomSheetDialog.dismiss()
    }

    bottomSheetDialog.setContentView(binding.root)
    bottomSheetDialog.show()
}
fun Fragment.showChatBottomSheet(
    setupAdapter: (RecyclerView) -> Unit
) {
    val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    val binding = ChatBottomSheetBinding.inflate(layoutInflater, null, false)

    binding.icClose.setOnClickListener {
        bottomSheetDialog.dismiss()
    }
    setupAdapter(binding.recyclerViewBottomSheet)

    bottomSheetDialog.setContentView(binding.root)

    val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

    bottomSheet?.let {
        // 1. Força a altura do container interno para match_parent
        val layoutParams = it.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        it.layoutParams = layoutParams

        // 2. Configura o comportamento para abrir totalmente expandido
        val behavior = BottomSheetBehavior.from(it)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // (Opcional) Impede que ele pare no meio do caminho ao fechar arrastando
        behavior.skipCollapsed = true
    }
    // ---------------------------------------------------

    bottomSheetDialog.show()
}

fun Response<*>.errorMessage(): String {
    return try {

        val errorString = this.errorBody()?.string()

        if (!errorString.isNullOrEmpty()) {
            val jsonObject = JSONObject(errorString)
            jsonObject.optString("message", "Erro inesperado do servidor.")
        } else {
            "Erro desconhecido."
        }
    } catch (e: Exception) {
        "Falha ao processar a resposta do servidor."
    }
}