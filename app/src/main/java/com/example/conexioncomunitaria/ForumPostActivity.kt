package com.example.conexioncomunitaria

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.example.conexioncomunitaria.model.AuthManager
import com.google.firebase.firestore.FirebaseFirestore




class ForumPostActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_post)

        authManager = AuthManager()
        val user = authManager.auth.currentUser
        val userEmail = user?.email

        val textViewUser = findViewById<TextView>(R.id.textViewUser)
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextContent = findViewById<EditText>(R.id.editTextContent)
        val buttonPublish = findViewById<Button>(R.id.buttonPublish)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        if (userEmail != null) {
            authManager.getUserData(userEmail) { userData ->
                if (userData != null && userData["admin"] as? Boolean == true) {
                    val userName = userData["nombre"] as? String ?: "Usuario Administrador"
                    textViewUser.text = userName
                }
                else if (userData != null && userData["admin"] as? Boolean == false) {
                    val userName = userData["nombre"] as? String ?: "Usuario"
                    textViewUser.text = userName
                }
            }
        }
        else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }



        buttonPublish.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (userEmail != null) {
                    authManager.getUserData(userEmail) { userData ->
                        if (userData != null) {

                            val userName = userData["nombre"] as? String ?: "Usuario Administrador"

                            val forumPostData = hashMapOf(
                                "title" to title,
                                "content" to content,
                                "date" to currentDate,
                                "userEmail" to userEmail,
                                "userName" to userName // Add the userName field
                            )

                            firestore.collection("forum_posts")
                                .add(forumPostData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Publicación publicada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, ForumsActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Error al publicar: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Error al obtener datos del usuario",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
}



