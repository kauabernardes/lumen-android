package org.lumen.app.ui

import TokenManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import org.lumen.app.data.remote.Constants
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.databinding.FragmentSessionBinding
import org.lumen.app.util.showBottomSheet


class SessionFragment : Fragment() {

    private var _binding : FragmentSessionBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private lateinit var token: String
    private var mSocket: Socket? = null
    private var currentSessionId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = tokenManager.getToken() ?: ""

        conectarSocket()

        initListener()
        initSocketListener()
    }

    private fun initListener(){

    }

    private fun conectarSocket() {
        try{
            val options = IO.Options()
            mSocket = IO.socket(Constants.BASE_URL + "session", options)
            mSocket?.connect()
            entrarNaSessao()
        } catch(e: Exception) {
            Log.e("SESSION_SOCKET", "Erro na URL do Socket", e)
        }
    }

    private fun entrarNaSessao() {

        if (token == ""){
            showBottomSheet(message= "Você precisa estar logado para acessar essa funcionalidade")
        }

        val payload = JSONObject().apply {
            put("token", token)
        }


        mSocket?.emit("join_session", payload, Ack { args ->
            val response = args[0] as JSONObject

            if (response.has("success") && response.getBoolean("success")) {


                currentSessionId = response.getString("sessionId")
                binding.sessionId.text = currentSessionId

            } else {
                showBottomSheet(message = "Erro ao entrar: ${response.optString("error")}")
            }
        })
    }

    private fun initSocketListener() {
        mSocket?.on("timer_state") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val timeLeft = data.getInt("timeLeft")
                val phase = data.getString("phase") // "study" ou "break"

                // IMPORTANTE: Socket roda em background. Para alterar a tela, precisa voltar pra Thread Principal
                activity?.runOnUiThread {
                    atualizarCronometroNaTela(timeLeft, phase)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mSocket?.disconnect()
        mSocket?.off("timer_state")
        mSocket?.off(Socket.EVENT_CONNECT)

        _binding = null
    }
}