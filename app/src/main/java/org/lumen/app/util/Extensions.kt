package org.lumen.app.util

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.lumen.app.MainActivity
import org.lumen.app.R
import org.lumen.app.databinding.BottomSheetBinding




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