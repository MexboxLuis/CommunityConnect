package com.example.conexioncomunitaria.model

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class AuthManager {
    var auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onComplete: (Task<AuthResult>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task)
            }
    }

    fun register(name: String, email: String, password: String, codigoPostal: String, onComplete: (Task<AuthResult>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "nombre" to name,
                        "codigoPostal" to codigoPostal,
                        "email" to email,
                        "admin" to false,
                    )
                    firestore.collection("usuarios")
                        .document(user!!.uid)
                        .set(userData)
                        .addOnCompleteListener { firestoreTask ->
                            onComplete(authTask) // Pasa el resultado de la autenticación
                        }
                } else {
                    onComplete(authTask) // Pasa el resultado de la autenticación (error)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserData(email: String, onComplete: (Map<String, Any>?) -> Unit) {
        firestore.collection("usuarios")
            .whereEqualTo("email", email) // Filtrar por correo electrónico
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    // El usuario existe, obtener los datos
                    val document = task.result.documents[0]
                    val userData = document.data
                    onComplete(userData)
                } else {
                    // Usuario no encontrado o error en la consulta
                    onComplete(null)
                }
            })
    }

    fun getCurrentUserData(onComplete: (Map<String, Any>?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            getUserData(user.email!!, onComplete) // Use the current user's email
        } else {
            onComplete(null) // User not logged in
        }
    }
    fun getAuthInstance(): FirebaseAuth {
        return auth
    }


}
