package com.example.conexioncomunitaria

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings

class CrearEncuestaBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.crear_encuesta_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPublicarEncuesta = view.findViewById<Button>(R.id.btnPublicarEncuesta)

        btnPublicarEncuesta.setOnClickListener {
            val pregunta1 = view.findViewById<EditText>(R.id.etPregunta).text.toString().trim()
            val pregunta2 = view.findViewById<EditText>(R.id.etPregunta2).text.toString().trim()
            val pregunta3 = view.findViewById<EditText>(R.id.etPregunta3).text.toString().trim()
            val pregunta4 = view.findViewById<EditText>(R.id.etPregunta4).text.toString().trim()

            if (pregunta1.isNotEmpty() && pregunta2.isNotEmpty() && pregunta3.isNotEmpty() && pregunta4.isNotEmpty()) {

                val db = Firebase.firestore
                db.firestoreSettings = firestoreSettings { isPersistenceEnabled = true }

                // Generar un nuevo ID para la encuesta
                val encuestaId = db.collection("encuestas").document().id
                val timestamp = System.currentTimeMillis()

                val encuestaData = hashMapOf(
                    "pregunta1" to pregunta1,
                    "pregunta2" to pregunta2,
                    "pregunta3" to pregunta3,
                    "pregunta4" to pregunta4,
                    "timestamp" to timestamp
                )

                db.collection("encuestas")
                    .document(encuestaId)
                    .set(encuestaData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Encuesta publicada", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, EncuestaActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context?.startActivity(intent)
                        dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al publicar la encuesta: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            else{
                Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()

            }

        }
    }
}
