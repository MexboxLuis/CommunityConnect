package com.example.conexioncomunitaria

import android.content.Intent
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conexioncomunitaria.model.AuthManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencia a los elementos de la UI
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val btnRegister = findViewById<TextView>(R.id.btnRegister)

        val authManager = AuthManager()

        if (authManager.isLoggedIn()) {
            // El usuario ya está autenticado, redirigir a la pantalla principal
            startActivity(Intent(this, MainScreenActivity::class.java))
            finish()
        } else {

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                if (email.isNotEmpty() || password.isNotEmpty()) {
                    authManager.login(email, password) { task ->
                        if (task.isSuccessful) {
                            // Inicio de sesión exitoso
                            startActivity(Intent(this, MainScreenActivity::class.java))
                            finish()
                        } else {
                            // Aquí podrías añadir lógica adicional para mostrar un mensaje de error más específico al usuario
                            val exception = task.exception
                            if (exception is FirebaseAuthInvalidUserException) {
                                // El usuario no existe o ha sido deshabilitado
                                Toast.makeText(baseContext, "Usuario inválido", Toast.LENGTH_SHORT)
                                    .show()
                            } else if (exception is FirebaseAuthInvalidCredentialsException) {
                                // Credenciales incorrectas
                                Toast.makeText(
                                    baseContext,
                                    "Contraseña incorrecta",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                // Otro tipo de error
                                Toast.makeText(
                                    baseContext,
                                    "Error en la autenticación",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else
                    Toast.makeText(
                        baseContext,
                        "Por favor, ingrese su correo y contraseña",
                        Toast.LENGTH_SHORT
                    ).show()
            }


            // Manejo del evento click del texto olvidé mi contraseña
            tvForgotPassword.setOnClickListener {
                // Aquí puedes iniciar la actividad de recuperación de contraseña
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
            }

            // Manejo del evento click del botón de registrarse
            btnRegister.setOnClickListener {
                // Aquí puedes iniciar la actividad de registro
                startActivity(Intent(this, RegisterActivity::class.java))
            }


        }


    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true) // Minimizar la aplicación
    }

}