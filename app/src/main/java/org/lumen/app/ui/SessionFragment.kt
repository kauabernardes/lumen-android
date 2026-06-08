package org.lumen.app.ui

import org.lumen.app.data.local.TokenManager
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.lumen.app.R
import org.lumen.app.adapter.UserAdapter
import org.lumen.app.data.model.User
import org.lumen.app.data.remote.Constants
import org.lumen.app.data.remote.RetrofitClient
import org.lumen.app.data.remote.model.ForceBreakRequest
import org.lumen.app.data.remote.model.PomodoroBreak
import org.lumen.app.data.remote.model.PomodoroPhase
import org.lumen.app.data.remote.model.PomodoroState
import org.lumen.app.data.remote.model.PomodoroStatus
import org.lumen.app.databinding.FragmentSessionBinding
import org.lumen.app.util.showBottomSheet


class SessionFragment : Fragment() {

    private var _binding : FragmentSessionBinding? = null
    private val binding get() = _binding!!

    private lateinit var tokenManager: TokenManager

    private lateinit var token: String
    private lateinit var bearer: String

    private lateinit var userAdapter: UserAdapter
    private var mSocket: Socket? = null
    private var currentSessionId: String? = null

    private lateinit var clipboardManager : ClipboardManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionBinding.inflate(inflater, container, false)
        tokenManager = TokenManager(requireContext())
        clipboardManager = getSystemService(requireContext(), ClipboardManager::class.java)!!
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = tokenManager.getToken() ?: ""
        bearer = tokenManager.getBearer() ?: ""


        initListener()

        initBtns()

    }

    private fun initListener(){
        binding.btnToggle.setOnClickListener {
            alternarCronometro()
        }
        binding.btnLongBreak.setOnClickListener {
            forceBreak(PomodoroBreak.LONG)
        }
        binding.btnShortBreak.setOnClickListener {
            forceBreak(PomodoroBreak.SHORT)
        }
        binding.btnStudy.setOnClickListener {
            forceStudy()
        }


    }

    private fun initBtns() {
        val btnJoin = binding.btnJoin
        val btnCreate = binding.btnCreateSession
        val input = binding.inputSessionId
        val btnCopyId = binding.btnCopyId

        input.addTextChangedListener { text ->
            btnJoin.isEnabled = text.toString().isNotEmpty()
        }

        btnJoin.setOnClickListener {
            val sessionId = input.text.toString()

            conectarSocket(sessionId)
        }

        btnCreate.setOnClickListener {

            conectarSocket(null)
        }

        btnCopyId.setOnClickListener {
            val sessionId = currentSessionId

            if (sessionId != null) {
                val clipData = ClipData.newPlainText("text", sessionId)
                clipboardManager.setPrimaryClip(clipData)
                showBottomSheet(message = "ID da sessão copiado!")
            }

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

    private fun forceBreak(type: PomodoroBreak) {

        val sessionId = currentSessionId

        if (sessionId == null) {
            showBottomSheet(message = "Você ainda não entrou em uma sessão.")
            return
        }

        val payload = ForceBreakRequest(type)

        viewLifecycleOwner.lifecycleScope.launch {

            try {
                val response = RetrofitClient.sessionApi.forceBreak(sessionId, payload, bearer)

                if (response.isSuccessful) {
                    showBottomSheet(message = "Tempo de descanso!")
                } else {
                    showBottomSheet(message = "Erro: Apenas o anfitrião pode forçar uma pausa.")
                }
            } catch (e: Exception) {
                showBottomSheet(message = "Erro de conexão ao tentar forçar uma pausa.")
                Log.e("SESSION_REST", "Erro na força de descanso", e)
            }
        }
    }

    private fun forceStudy(){
        val sessionId = currentSessionId

        if (sessionId == null) {
            showBottomSheet(message = "Você ainda não entrou em uma sessão.")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.sessionApi.forceStudy(sessionId, bearer)

                if (response.isSuccessful) {
                    showBottomSheet(message = "Temdo de estudo!")
                } else {
                    showBottomSheet(message = "Erro: Apenas o anfitrião pode determinar tempo de estudo.")
                }
            } catch (e: Exception) {
                showBottomSheet(message = "Erro de conexão ao tentar determinar tempo de estudo.")
                Log.e("SESSION_REST", "Erro na força de estudo", e)
            }
        }
    }


    private fun conectarSocket(sessionId: String? = null) {
        try{
            val options = IO.Options()
            mSocket = IO.socket(Constants.BASE_URL + "session", options)

            initSocketListener(sessionId)

            mSocket?.connect()

        } catch(e: Exception) {
            Log.e("SESSION_SOCKET", "Erro na URL do Socket", e)
        }
    }

    private fun entrarNaSessao(sessionId: String? = null) {
        if (token == "") return

        val payload = JSONObject().apply { put("token", token) }

        if (sessionId != null) {
            payload.put("sessionId", sessionId)
        }

        mSocket?.emit("join_session", payload, Ack { args ->
            val response = args[0] as JSONObject

            Log.i("SESSION_DEBUG", "Resposta do Join: $response")

            activity?.runOnUiThread {
                if (response.has("success") && response.getBoolean("success")) {
                    binding.cardTimer.isVisible = true
                    binding.cardInit.isGone = true

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


                    currentSessionId?.let { id ->
                        initialUserList(id)
                    }
                } else {
                    binding.cardTimer.isVisible = true
                    binding.cardInit.isGone = true
                    showBottomSheet(message = "Erro ao entrar na sessão.")
                }
            }


        })
    }

    private fun initSocketListener(sessionId: String? = null) {

        mSocket?.off(Socket.EVENT_CONNECT)
        mSocket?.off("timer_state")
        mSocket?.off("participants_updated")


        mSocket?.on(Socket.EVENT_CONNECT) {
            Log.i("SESSION_SOCKET", "Conectado ao servidor Socket!")
            entrarNaSessao(sessionId)
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

        mSocket?.on("participants_updated") { args ->
            if (args.isNotEmpty()) {
                val jsonString = args[0].toString()

                Log.i("SESSION_DEBUG", "JSON: $jsonString")

                val participants = Gson().fromJson(jsonString, Array<User>::class.java)

                activity?.runOnUiThread {
                    setUserList(participants.toList())
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
            binding.btnShortBreak.isEnabled = true
            binding.btnLongBreak.isEnabled = true
        }


        if (status == PomodoroStatus.RUNNING) {
            binding.btnToggle.text = getString(R.string.btn_session_toggle_pause)
            binding.btnToggle.icon = getDrawable(requireContext(),R.drawable.ic_pause)
        } else {
            binding.btnToggle.text = getString(R.string.btn_session_toggle_resume)
            binding.btnToggle.icon = getDrawable(requireContext(),R.drawable.ic_play)
            }
        }

    private fun initialUserList(sessionId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.sessionApi.getParticipants(sessionId, bearer)
                if (response.isSuccessful && response.body() != null) {
                    val participants = response.body()!!
                    setUserList(participants)
                } else {
                    Log.e("SESSION_REST", "Erro ao buscar participantes iniciais")
                }
            } catch (e: Exception) {
                Log.e("SESSION_REST", "Falha na conexão ao buscar participantes", e)
            }
        }
    }


    private fun setUserList(userList: List<User>) {
        userAdapter = UserAdapter(userList)
        binding.userList.layoutManager = LinearLayoutManager(requireContext())
        binding.userList.setHasFixedSize(false)
        binding.userList.adapter = userAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()

        mSocket?.disconnect()
        mSocket?.off("timer_state")
        mSocket?.off(Socket.EVENT_CONNECT)

        _binding = null
    }
}