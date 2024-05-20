package com.example.conexioncomunitaria

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conexioncomunitaria.model.AuthManager
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class RegisterActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etCodigoPostal: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_screen)

        // Inicializa las referencias a los views
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etCodigoPostal = findViewById(R.id.etCodigoPostal)
        btnRegister = findViewById(R.id.btnRegister)

        authManager = AuthManager() // Inicializar AuthManager

        btnRegister.setOnClickListener {
            if (validateInputs()) { // Validar los campos
                val name = etNombre.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val codigoPostal = etCodigoPostal.text.toString()

                authManager.register(name, email, password, codigoPostal) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainScreenActivity::class.java))
                        finish() // Cerrar la actividad de registro
                    } else {
                        handleRegistrationError(task.exception) // Manejar errores de registro
                    }
                }
            }
        }
    }

    fun redirectToLogin(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateInputs(): Boolean {
        // Validación simple de campos (puedes agregar más validaciones según tus requisitos)
        if (etNombre.text.isEmpty() || etEmail.text.isEmpty() || etPassword.text.isEmpty() || etCodigoPostal.text.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validación adicional del correo electrónico (opcional)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun handleRegistrationError(exception: Exception?) {
        // Manejo de errores específico de Firebase Authentication (puedes personalizar los mensajes)
        when (exception) {
            is FirebaseAuthWeakPasswordException ->
                Toast.makeText(this, "La contraseña es muy débil", Toast.LENGTH_SHORT).show()
            is FirebaseAuthInvalidCredentialsException ->
                Toast.makeText(this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show()
            is FirebaseAuthUserCollisionException ->
                Toast.makeText(this, "Este correo electrónico ya está registrado", Toast.LENGTH_SHORT).show()
            else ->
                Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
        }
    }
}

