package com.example.conexioncomunitaria

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conexioncomunitaria.model.AuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class EncuestaActivity : AppCompatActivity() {

    private lateinit var fabAdmin: FloatingActionButton
    private lateinit var fabPushAdmin: FloatingActionButton
    private lateinit var textView: TextView
    private lateinit var headerView: TextView
    private lateinit var layoutPreguntas: LinearLayout
    private lateinit var layoutResultados: LinearLayout
    private lateinit var btnEnviarRespuestas: Button
    private val respuestas = mutableMapOf<Int, Int>()
    private lateinit var userEmail: String
    private lateinit var currentEncuestaId: String
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encuesta_activity)

        fabAdmin = findViewById(R.id.fabAdmin)
        fabPushAdmin = findViewById(R.id.fabPushAdmin)
        textView = findViewById(R.id.TextViewNohHay)
        layoutPreguntas = findViewById(R.id.layoutPreguntas)
        layoutResultados = findViewById(R.id.layoutResultados)
        btnEnviarRespuestas = findViewById(R.id.btnEnviarRespuestas)
        headerView = findViewById(R.id.textViewHonestidad)

        val authManager = AuthManager()
        userEmail = authManager.auth.currentUser?.email ?: return

        authManager.getUserData(userEmail) { userData ->
            if (userData != null && userData["admin"] == true) {
                fabAdmin.visibility = View.VISIBLE
                fabAdmin.setOnClickListener {
                    val bottomSheet = CrearEncuestaBottomSheetDialogFragment()
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }


            }
            fabPushAdmin.setOnClickListener {
                obtenerResultadosEncuesta()
                fabPushAdmin.visibility = View.GONE
                fabAdmin.visibility = View.VISIBLE // Disable the button after clicking
            }
        }

        db.collection("encuestas").orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    currentEncuestaId = document.id
                    fabAdmin.visibility = View.GONE
                    fabPushAdmin.visibility = View.VISIBLE
                    verificarSiUsuarioRespondio()
                    mostrarEncuesta(document)
                } else {
                    textView.visibility = View.VISIBLE
                    btnEnviarRespuestas.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                textView.visibility = View.VISIBLE
            }

        btnEnviarRespuestas.setOnClickListener {
            enviarRespuestas()
        }
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
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


    private fun verificarSiUsuarioRespondio() {
        db.collection("respuestas_usuarios").document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val respondedEncuestas = document.get("respondedEncuestas") as? List<String> ?: emptyList()
                    if (respondedEncuestas.contains(currentEncuestaId)) {
                        btnEnviarRespuestas.isEnabled = false
                        Toast.makeText(this, "Ya has respondido esta encuesta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al verificar respuesta: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerResultadosEncuesta() {
        db.collection("encuestas").document(currentEncuestaId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    layoutPreguntas.visibility = View.GONE
                    btnEnviarRespuestas.visibility = View.GONE
                    layoutResultados.visibility = View.VISIBLE
                    fabAdmin.setImageResource(R.drawable.ic_add)
                    fabAdmin.tag = R.drawable.ic_add
                    val preguntas = listOf(
                        document.getString("pregunta1"),
                        document.getString("pregunta2"),
                        document.getString("pregunta3"),
                        document.getString("pregunta4")
                    )
                    val votes = document.get("votes") as? Map<String, Long> ?: return@addOnSuccessListener
                    val increments = document.get("increments") as? Map<String, Long> ?: return@addOnSuccessListener
                    val percentagesDouble = document.get("percentages") as? Map<String, Double> ?: return@addOnSuccessListener

                    // Convert to Map<String, Float>
                    val percentages = percentagesDouble.mapValues { it.value.toFloat() }

                    val numVotantes = increments["incremento1"] ?: 0L




                    val results = listOf(
                        "${preguntas[0]} \n\t---> ${percentages["pregunta1"] ?: 0f}% ",
                        "${preguntas[1]} \n\t---> ${percentages["pregunta2"] ?: 0f}% ",
                        "${preguntas[2]} \n\t---> ${percentages["pregunta3"] ?: 0f}% ",
                        "${preguntas[3]} \n\t---> ${percentages["pregunta4"] ?: 0f}% "
                    )
                    headerView.text = "Votaciones actuales por $numVotantes personas"

                    results.forEach { result ->
                        val resultView = LayoutInflater.from(this).inflate(R.layout.result_card_view, null)
                        val tvResult = resultView.findViewById<TextView>(R.id.tvResult)
                        tvResult.text = result
                        val imageView = resultView.findViewById<ImageView>(R.id.calificacion) // Obtén la ImageView

                        // Obtén el porcentaje de la cadena de texto
                        val percentageString = result.substringAfter("--->").replace("%", "").trim()
                        val percentage = percentageString.toFloatOrNull() ?: 0f

                        // Asigna la imagen según el porcentaje
                        val imageResource = when {
                            percentage < 30f -> R.drawable.imagen_calificacion_0
                            percentage < 70f -> R.drawable.imagen_calificacion_5
                            else -> R.drawable.imagen_calificacion_10
                        }
                        imageView.setImageResource(imageResource)

                        layoutPreguntas.visibility = View.GONE
                        layoutResultados.visibility = View.VISIBLE
                        layoutResultados.addView(resultView)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener resultados: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarEncuesta(document: DocumentSnapshot) {
        val preguntas = listOf(
            document.getString("pregunta1"),
            document.getString("pregunta2"),
            document.getString("pregunta3"),
            document.getString("pregunta4")
        )

        preguntas.forEachIndexed { index, pregunta ->
            if (!pregunta.isNullOrEmpty()) {
                val preguntaView = LayoutInflater.from(this).inflate(R.layout.pregunta_card_view, null)
                val tvPregunta = preguntaView.findViewById<TextView>(R.id.tvPregunta)
                tvPregunta.text = "${index + 1}. - $pregunta"

                val buttons = listOf(
                    preguntaView.findViewById<ImageButton>(R.id.btnCalificacion0),
                    preguntaView.findViewById<ImageButton>(R.id.btnCalificacion5),
                    preguntaView.findViewById<ImageButton>(R.id.btnCalificacion10)
                )

                buttons.forEach { button ->
                    button.setOnClickListener {
                        respuestas[index] = when (button.id) {
                            R.id.btnCalificacion0 -> 0
                            R.id.btnCalificacion5 -> 5
                            R.id.btnCalificacion10 -> 10
                            else -> 0
                        }
                    }
                }
                layoutPreguntas.visibility = View.VISIBLE
                layoutPreguntas.addView(preguntaView)
            }
        }
    }

    private fun enviarRespuestas() {
        if (respuestas.size == 4) {
            val encuestaRef = db.collection("encuestas").document(currentEncuestaId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(encuestaRef)
                val currentVotes = snapshot.get("votes") as? Map<String, Long> ?: mapOf(
                    "pregunta1" to 0L,
                    "pregunta2" to 0L,
                    "pregunta3" to 0L,
                    "pregunta4" to 0L
                )

                val currentIncrements = snapshot.get("increments") as? Map<String, Long> ?: mapOf(
                    "incremento1" to 0L,
                    "incremento2" to 0L,
                    "incremento3" to 0L,
                    "incremento4" to 0L
                )

                val updatedVotes = mapOf(
                    "pregunta1" to currentVotes["pregunta1"]!! + respuestas[0]!!,
                    "pregunta2" to currentVotes["pregunta2"]!! + respuestas[1]!!,
                    "pregunta3" to currentVotes["pregunta3"]!! + respuestas[2]!!,
                    "pregunta4" to currentVotes["pregunta4"]!! + respuestas[3]!!
                )

                val updatedIncrements = mapOf(
                    "incremento1" to currentIncrements["incremento1"]!! + 1,
                    "incremento2" to currentIncrements["incremento2"]!! + 1,
                    "incremento3" to currentIncrements["incremento3"]!! + 1,
                    "incremento4" to currentIncrements["incremento4"]!! + 1
                )

                val updatedPercentages = mapOf(
                    "pregunta1" to (updatedVotes["pregunta1"]!!.toFloat() * 100 / (updatedIncrements["incremento1"]!! * 10)),
                    "pregunta2" to (updatedVotes["pregunta2"]!!.toFloat() * 100 / (updatedIncrements["incremento2"]!! * 10)),
                    "pregunta3" to (updatedVotes["pregunta3"]!!.toFloat() * 100 / (updatedIncrements["incremento3"]!! * 10)),
                    "pregunta4" to (updatedVotes["pregunta4"]!!.toFloat() * 100 / (updatedIncrements["incremento4"]!! * 10))
                )

                transaction.update(encuestaRef, "votes", updatedVotes)
                transaction.update(encuestaRef, "increments", updatedIncrements)
                transaction.update(encuestaRef, "percentages", updatedPercentages)

                val userResponsesRef = db.collection("respuestas_usuarios").document(userEmail)
                transaction.update(userResponsesRef, "respondedEncuestas", FieldValue.arrayUnion(currentEncuestaId))
            }.addOnSuccessListener {
                Toast.makeText(this, "Respuestas enviadas", Toast.LENGTH_SHORT).show()
                btnEnviarRespuestas.isEnabled = false
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al enviar respuestas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Por favor, responde todas las preguntas", Toast.LENGTH_SHORT).show()
        }
    }
}
