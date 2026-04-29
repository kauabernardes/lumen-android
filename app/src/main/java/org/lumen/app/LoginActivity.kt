package org.lumen.app

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val btnEntrar = findViewById<MaterialButton>(R.id.btnEntrar)
        val btnCriarConta = findViewById<TextView>(R.id.btnCriarConta)
        val btnRecuperar = findViewById<TextView>(R.id.btnRecuperarSenha)

        btnEntrar.setOnClickListener {
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()

            if (validarDados(email, senha)) {
                processarLogin(email, senha)
            }
        }

        // Lógica para ir para a tela de Cadastro
        btnCriarConta.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        btnRecuperar.setOnClickListener {
            Toast.makeText(this, "Indo para Recuperação de Senha...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarDados(email: String, senha: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Insira seu email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (senha.length < 6) {
            Toast.makeText(this, "Insira a sua senha", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun processarLogin(email: String, senha: String) {
        if (email == "admin@teste" && senha == "123456") {
            Toast.makeText(this, "Bem-vindo!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "E-mail ou senha incorretos!", Toast.LENGTH_LONG).show()
        }
    }
}