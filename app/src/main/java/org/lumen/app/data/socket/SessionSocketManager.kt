package org.lumen.app.data.socket

import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import org.lumen.app.data.model.JoinSessionRequest
import org.lumen.app.data.model.JoinSessionResponse
import org.lumen.app.data.model.PomodoroState
import org.lumen.app.data.model.UserEvent

class SessionSocketManager (private val socket: Socket) {

    private val gson = Gson()

    // emissão de entrar em sessão

    fun joinSession(token: String, sessionId: String?, onResult: (JoinSessionResponse) -> Unit) {
        val request = JoinSessionRequest(token, sessionId)
        val jsonPayload = JSONObject(gson.toJson(request))

        socket.emit("join_session", jsonPayload, Ack { args ->
            val responseJson = args[0] as JSONObject

            val response = gson.fromJson(responseJson.toString(), JoinSessionResponse::class.java)
            onResult(response)
        })
    }

    // escutadores

    fun observeTimerState(): Flow<PomodoroState> = callbackFlow {
        val listener = Emitter.Listener { args ->
            val dataJson = args[0] as JSONObject
            val state = gson.fromJson(dataJson.toString(), PomodoroState::class.java)
            trySend(state)
        }

        socket.on("timer_state", listener)
        awaitClose { socket.off("timer_state", listener) }
    }

    fun observeUserJoined(): Flow<UserEvent> = callbackFlow {  }



}