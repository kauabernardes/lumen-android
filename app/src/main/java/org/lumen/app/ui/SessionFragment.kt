package org.lumen.app.ui

import TokenManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.lumen.app.R
import org.lumen.app.data.remote.Constants
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.PomodoroPhase
import org.lumen.app.data.remote.model.PomodoroState
import org.lumen.app.data.remote.model.PomodoroStatus
import org.lumen.app.databinding.FragmentSessionBinding
import org.lumen.app.util.showBottomSheet
import kotlin.math.min


class SessionFragment : Fragment() {

    private var _binding : FragmentSessionBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private lateinit var token: String
    private lateinit var bearer: String

    private lateinit var status: PomodoroStatus
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
        bearer = tokenManager.getBearer() ?: ""

        conectarSocket()

        initListener()

    }

    private fun initListener(){
        binding.btnToggle.setOnClickListener {
            alternarCronometro()
        }
    }

    private fun alternarCronometro() {
        val sessionId = currentSessionId

        if (sessionId == null) {
            showBottomSheet(message = "Você ainda não entrou em uma sessão.")
            return
        }

        Log.i("SESSION_REST", "Tentando enviar comando toggle ${sessionId}")

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val response = RetrofitClient.sessionApi.toggleTimer(sessionId, bearer)

                if (response.isSuccessful) {

                    Log.i("SESSION_REST", "Comando toggle enviado com sucesso!")
                } else {

                    showBottomSheet(message = "Erro: Apenas o anfitrião pode pausar/iniciar.")
                }
            } catch (e: Exception) {
                showBottomSheet(message = "Erro de conexão ao tentar pausar/iniciar.")
                Log.e("SESSION_REST", "Erro no toggle", e)
            }
        }
    }

    private fun conectarSocket() {
        try{
            val options = IO.Options()
            mSocket = IO.socket(Constants.BASE_URL + "session", options)

            initSocketListener()

            mSocket?.connect()

        } catch(e: Exception) {
            Log.e("SESSION_SOCKET", "Erro na URL do Socket", e)
        }
    }

    private fun entrarNaSessao() {
        if (token == "") return

        val payload = JSONObject().apply { put("token", token) }

        mSocket?.emit("join_session", payload, Ack { args ->
            val response = args[0] as JSONObject

            Log.i("SESSION_DEBUG", "Resposta do Join: $response")

            activity?.runOnUiThread {
                if (response.has("success") && response.getBoolean("success")) {
                    binding.btnToggle.isEnabled = true

                    currentSessionId = response.getString("sessionId")
                    binding.sessionId.text = currentSessionId

                    if (response.has("pomodoro")) {
                        val pomodoroJson = response.getJSONObject("pomodoro").toString()

                        Log.i("SESSION_DEBUG", "JSON Pomodoro: $pomodoroJson")

                        try {
                            val pomodoro = Gson().fromJson(pomodoroJson, PomodoroState::class.java)
                            Log.i("SESSION_DEBUG", "TimeLeft convertido: ${pomodoro.timeLeft}")

                            timerState(pomodoro.timeLeft, pomodoro.phase)
                        } catch (e: Exception) {
                            Log.e("SESSION_DEBUG", "Erro ao converter o Gson", e)
                        }
                    }
                }
            }
        })
    }

    private fun initSocketListener() {

        mSocket?.on(Socket.EVENT_CONNECT) {
            Log.i("SESSION_SOCKET", "Conectado ao servidor Socket!")
            entrarNaSessao()
        }

        mSocket?.on("timer_state") { args ->
            if (args.isNotEmpty()) {
               val jsonString = args[0].toString()
                val pomodoro = Gson().fromJson(jsonString, PomodoroState::class.java)

                activity?.runOnUiThread {
                    timerState(pomodoro.timeLeft, pomodoro.phase, pomodoro.status)
                }
            }
        }
    }

    private fun timerState(
        timeLeft: Int,
        phase: PomodoroPhase = PomodoroPhase.BREAK,
        status: PomodoroStatus = PomodoroStatus.PAUSED
    ) {
      val minutes = timeLeft / 60
        val seconds = timeLeft % 60

        Log.i("SESSION_REST", minutes.toString())

        val formatedMinutes = String.format("%02d", minutes)
        val formatedSeconds = String.format("%02d", seconds)

        binding.minutes.text = formatedMinutes
        binding.seconds.text = formatedSeconds

        if (phase == PomodoroPhase.STUDY) {
            binding.btnStudy.isEnabled = false
            binding.btnShortBreak.isEnabled = true
            binding.btnLongBreak.isEnabled = true
        } else {
            binding.btnStudy.isEnabled = true
            binding.btnShortBreak.isEnabled = false
            binding.btnLongBreak.isEnabled = false
        }


        if (status == PomodoroStatus.RUNNING) {
            binding.btnToggle.text = getString(R.string.btn_session_toggle_pause)
            binding.btnToggle.icon = getDrawable(requireContext(),R.drawable.ic_pause)
        } else {
            binding.btnToggle.text = getString(R.string.btn_session_toggle_resume)
            binding.btnToggle.icon = getDrawable(requireContext(),R.drawable.ic_play)
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