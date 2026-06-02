import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit
import com.auth0.jwt.JWT

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit { putString("JWT_TOKEN", token) }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("JWT_TOKEN", null)
    }

    fun getBearer() : String {
        val token = getToken()

        var result : String? = null

        if (token != null) {
            result = "Bearer $token"
        }

        return result.toString()
    }

    fun getUsername() : String {

        val token = getToken()
        var username = ""

        try {
            if (!token.isNullOrEmpty()) {
                val decodedJWT = JWT.decode(token)
                username = decodedJWT.getClaim("username").asString() ?: "Usuário"
            } else {
                username = "Usuário"
            }
        } catch (e: Exception) {
            username = "Usuário"
        }
        return username
    }



    fun clear() {
        sharedPreferences.edit { remove("JWT_TOKEN") }
    }
}