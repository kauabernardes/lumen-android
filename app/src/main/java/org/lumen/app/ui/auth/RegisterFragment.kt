package org.lumen.app.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.lumen.app.R
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.RegisterRequest

import org.lumen.app.databinding.FragmentRegisterBinding
import org.lumen.app.util.errorMessage
import org.lumen.app.util.showBottomSheet


class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        binding.btnCadastrar.setOnClickListener {
            register()
        }

        binding.btnVoltarLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun register() {
        val username = binding.editNome.text.toString()
        val email = binding.editEmailCadastro.text.toString()
        val senha = binding.editSenhaCadastro.text.toString()
        val confirmarSenha = binding.editConfirmarSenha.text.toString()

        if (!validar(username, email, senha, confirmarSenha)) return

        binding.btnCadastrar.isEnabled = false
        binding.progressBar.isVisible = true
        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val request = RegisterRequest(email,username, senha)
                val response = RetrofitClient.authApi.register(request)

                binding.btnCadastrar.isEnabled = true
                binding.progressBar.isGone = true

                if (response.isSuccessful && response.body() != null) {
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    showBottomSheet(message= response.body()!!.message)
                } else {
                    binding.btnCadastrar.isEnabled = true
                    binding.progressBar.isGone = true

                    val eMsg = response.errorMessage()

                    Log.i("CADASTRO", eMsg)
                    showBottomSheet(message = eMsg)
                }
            } catch ( e : Exception) {
                showBottomSheet(message = getString(R.string.error_default))
                binding.btnCadastrar.isEnabled = true
                binding.progressBar.isGone = true
            }

        }

    }

    private fun validar(username : String, email : String, senha : String, confirmarSenha : String) : Boolean {
        if (username.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            showBottomSheet(message = getString(R.string.alert_preencher_todos_campos))
            return false
        }
        if (senha != confirmarSenha) {
           showBottomSheet(message= getString(R.string.alert_senhas_diferente))
            return false
        }
        return true

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}