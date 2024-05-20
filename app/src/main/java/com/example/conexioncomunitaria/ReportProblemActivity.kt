package com.example.conexioncomunitaria

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.conexioncomunitaria.model.AuthManager
import com.google.firebase.firestore.FirebaseFirestore

class ReportProblemActivity : AppCompatActivity() {

    private lateinit var spinnerDay: Spinner
    private lateinit var spinnerMonth: Spinner
    private lateinit var authManager: AuthManager
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_problem)

        // Referencias a los elementos de la interfaz
        val spinnerCategory: Spinner = findViewById(R.id.spinner_category)
        spinnerDay = findViewById(R.id.spinner_day)
        spinnerMonth = findViewById(R.id.spinner_month)
        authManager = AuthManager()
        val spinnerYear: Spinner = findViewById(R.id.spinner_year)
        val editTextReport: EditText = findViewById(R.id.edit_text_report)
        val buttonSend: Button = findViewById(R.id.button_send)

        // Configuración del spinner de categorías
        val categories = arrayOf("Ambiental", "Seguridad", "Otro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(R.layout.dropdown_spinner)
        spinnerCategory.adapter = adapter

        // Configuración de los spinners de fecha
        val months = arrayOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        val years: Array<String> = Array(30) { i -> (i + 1995).toString() }
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(R.layout.dropdown_spinner)
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(R.layout.dropdown_spinner)
        spinnerMonth.adapter = monthAdapter
        spinnerYear.adapter = yearAdapter

        // Listener para el cambio de mes
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = monthAdapter.getItem(position).toString()
                updateDays(selectedMonth, spinnerYear.selectedItem.toString().toInt())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacemos nada si no se selecciona nada
            }
        }

        // Listener para el cambio de año
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMonth = spinnerMonth.selectedItem.toString()
                updateDays(selectedMonth, spinnerYear.selectedItem.toString().toInt())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacemos nada si no se selecciona nada
            }
        }

        // Configuración del botón de enviar
        buttonSend.setOnClickListener {
            val category = spinnerCategory.selectedItem.toString()
            val day = spinnerDay.selectedItem.toString()
            val month = spinnerMonth.selectedItem.toString()
            val year = spinnerYear.selectedItem.toString()
            val report = editTextReport.text.toString()

            if (report.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa un reporte", Toast.LENGTH_SHORT).show()
            } else {
                val user = authManager.auth.currentUser
                val userEmail = user?.email

                if (userEmail != null) {
                    val reportData = hashMapOf(
                        "category" to category,
                        "date" to "$day de $month de $year",
                        "content" to report,
                        "userEmail" to userEmail
                    )

                    firestore.collection("reports") // Nueva colección en Firestore
                        .add(reportData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Reporte enviado con exito, espera a que un administrador lo revise", Toast.LENGTH_LONG).show()
                            editTextReport.text.clear()

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error al enviar reporte: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, MainScreenActivity::class.java))
                            finish()
                        }
                } else {
                    Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateDays(month: String, year: Int) {
        val daysInMonth = when (month) {
            "Febrero" -> if (isLeapYear(year)) Array(29) { i -> (i + 1).toString() } else Array(28) { i -> (i + 1).toString() }
            "Abril", "Junio", "Septiembre", "Noviembre" -> Array(30) { i -> (i + 1).toString() }
            else -> Array(31) { i -> (i + 1).toString() }
        }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysInMonth)
        dayAdapter.setDropDownViewResource(R.layout.dropdown_spinner)
        spinnerDay.adapter = dayAdapter
    }

    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

}

