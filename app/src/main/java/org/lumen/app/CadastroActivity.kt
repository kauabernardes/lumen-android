package org.lumen.app

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val editNome = findViewById<EditText>(R.id.editNome)
        val editEmail = findViewById<EditText>(R.id.editEmailCadastro)
        val editSenha = findViewById<EditText>(R.id.editSenhaCadastro)
        val editConfirmar = findViewById<EditText>(R.id.editConfirmarSenha)
        val btnCadastrar = findViewById<MaterialButton>(R.id.btnCadastrar)
        val btnVoltarLogin = findViewById<TextView>(R.id.btnVoltarLogin)

        btnCadastrar.setOnClickListener {
            val nome = editNome.text.toString()
            val email = editEmail.text.toString()
            val senha = editSenha.text.toString()
            val confirmar = editConfirmar.text.toString()

            if (validarEntradas(nome, email, senha, confirmar)) {
                Toast.makeText(this, "Cadastro de $nome realizado!", Toast.LENGTH_SHORT).show()
                // Fecha a tela de cadastro e volta para o Login
                finish()
            }
        }

        btnVoltarLogin.setOnClickListener {
            finish()
        }
    }

    private fun validarEntradas(nome: String, email: String, s: String, c: String): Boolean {
        if (nome.isEmpty() || email.isEmpty() || s.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (s != c) {
            Toast.makeText(this, "As senhas não conferem!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}