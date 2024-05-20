package com.example.conexioncomunitaria

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.conexioncomunitaria.model.AuthManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class MainScreenActivity : AppCompatActivity() {

    private lateinit var btnForums: Button
    private lateinit var btnResourcesDirectory: Button
    private lateinit var btnReportProblem: Button
    private lateinit var btnTakeSurvey: Button
    private lateinit var showReports: FloatingActionButton
    private lateinit var userEmail: String
    private lateinit var authManager: AuthManager
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        btnForums = findViewById(R.id.btnForums)
        btnResourcesDirectory = findViewById(R.id.btnResourcesDirectory)
        btnReportProblem = findViewById(R.id.btnReportProblem)
        btnTakeSurvey = findViewById(R.id.btnTakeSurvey)
        showReports = findViewById(R.id.btnReports)

        val authManager = AuthManager()
        userEmail = authManager.auth.currentUser?.email ?: return

        authManager.getUserData(userEmail) { userData ->
            if (userData != null && userData["admin"] == true) {
                showReports.visibility = View.VISIBLE
                showReports.setOnClickListener {
                    mostrarReportes()
                }
            }

        }

        btnForums.setOnClickListener {
            startActivity(Intent(this, ForumsActivity::class.java))
        }

        btnResourcesDirectory.setOnClickListener {
            startActivity(Intent(this, DirectorioActivity::class.java))
        }

        btnReportProblem.setOnClickListener {
            startActivity(Intent(this, ReportProblemActivity::class.java))
        }

        btnTakeSurvey.setOnClickListener {
            startActivity(Intent(this, EncuestaActivity::class.java))
        }


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.action_back -> {
                    Toast.makeText(this, "Ya te encuentras en Inicio!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.actionLogout -> {
                    // Cerrar sesión de Firebase
                    authManager.logout()

                    // Redirigir a MainScreenActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    true
                }
                else -> onOptionsItemSelected(menuItem)
            }
        }

    }
    @SuppressLint("SetTextI18n")
    private fun mostrarReportes() {
        firestore.collection("reports").get()
            .addOnSuccessListener { documents ->
                val builder = AlertDialog.Builder(this)
                val dialogView = layoutInflater.inflate(R.layout.dialog_reports, null) // Crea un layout para mostrar reportes
                builder.setView(dialogView)

                val reportsContainer = dialogView.findViewById<LinearLayout>(R.id.reportsContainer) //Contenedor para los reportes

                for (document in documents) {
                    val reportView = layoutInflater.inflate(R.layout.report_item, null) // Crea un layout para cada reporte

                    val emailTextView = reportView.findViewById<TextView>(R.id.reportEmail)
                    emailTextView.text = "Contacto: ${document.getString("userEmail")}"

                    val dateTextView = reportView.findViewById<TextView>(R.id.reportDate)
                    dateTextView.text = "Acontecido el ${document.getString("date")}"

                    //Alinear el texto de la fecha hacia la derecha (gravity end)
                    dateTextView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END

                    val categoryTextView = reportView.findViewById<TextView>(R.id.reportCategory)
                    categoryTextView.text = "Categoria: ${document.getString("category")}"

                    val contentTextView = reportView.findViewById<TextView>(R.id.reportContent)
                    contentTextView.text = "Contenido: \n${document.getString("content")}"

                    reportsContainer.addView(reportView)
                }

                builder.setPositiveButton("Cerrar", null) // Botón para cerrar el diálogo
                builder.create().show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al cargar los reportes: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }



    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true) // Minimizar la aplicación
    }
}

