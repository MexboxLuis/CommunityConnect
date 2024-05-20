package com.example.conexioncomunitaria

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conexioncomunitaria.model.AuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUserType: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvCodigoPostal: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)


        tvUserType = findViewById(R.id.tvUserType)
        tvNombre = findViewById(R.id.tvNombre)
        tvEmail = findViewById(R.id.tvEmail)
        tvCodigoPostal = findViewById(R.id.tvCodigoPostal)


        val authManager = AuthManager()
        val userEmail = authManager.auth.currentUser?.email


        if (userEmail != null) {
            authManager.getUserData(userEmail) { userData ->
                if (userData != null) {
                    val isAdmin = userData["admin"] as? Boolean ?: false
                    tvUserType.text = if (isAdmin) "ADMINISTRADOR" else "CIUDADANO"
                    tvUserType.setTextColor(if (isAdmin) Color.parseColor("#FF8C00") else Color.BLUE) // Naranja para admin, Azul para ciudadano
                    tvNombre.text = if (!isAdmin) {userData["nombre"] as? String ?: "" }else "${authManager.auth.currentUser?.uid}"
                    tvEmail.text = userData["email"] as? String ?: ""
                    tvCodigoPostal.text =  if (!isAdmin) {userData["codigoPostal"] as? String ?: "" }else "XXXXXX"
                }
            }
        }
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    Toast.makeText(this, "Ya te encuentras en Perfil!", Toast.LENGTH_SHORT).show()
                    true // <-- Importante devolver true aquí
                }
                R.id.action_back -> {
                    onBackPressed()
                    true // <-- Importante devolver true aquí
                }
                R.id.actionLogout -> {
                    // Cerrar sesión de Firebase
                    authManager.logout()

                    // Redirigir a MainScreenActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Opcional: Cierra la actividad actual para que el usuario no pueda volver atrás

                    true // <-- Importante devolver true aquí
                }
                else -> onOptionsItemSelected(menuItem)
            }
        }

    }




    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainScreenActivity::class.java)
        startActivity(intent)
        finish()
    }
}
