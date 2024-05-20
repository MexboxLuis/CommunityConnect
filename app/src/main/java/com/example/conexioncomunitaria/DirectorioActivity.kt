package com.example.conexioncomunitaria
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.conexioncomunitaria.model.AuthManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView

class DirectorioActivity : AppCompatActivity() {

    private lateinit var imageViewImagen: ImageView
    private lateinit var textViewTitulo: TextView
    private lateinit var buttonBack: Button
    private lateinit var buttonNext: Button
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var bottomNavigationView: BottomNavigationView

    private val items = listOf(
        Item(R.drawable.parque, "Parques"),
        Item(R.drawable.oficina, "Oficinas"),
        Item(R.drawable.hospital, "Hospitales"),
        Item(R.drawable.museo, "Museos")
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.directorio_screen)
        val authManager = AuthManager()
        imageViewImagen = findViewById(R.id.imageViewImagen)
        textViewTitulo = findViewById(R.id.textViewIcon)
        buttonBack = findViewById(R.id.buttonBack)
        buttonNext = findViewById(R.id.buttonNext)
        bottomAppBar = findViewById(R.id.bottomAppBar)
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

        updateUI()

        imageViewImagen.setOnClickListener {
            val intent = Intent(this, PlaceListActivity::class.java)
            intent.putExtra("CATEGORY_INDEX", currentIndex)
            startActivity(intent)
        }


        buttonNext.setOnClickListener {
            currentIndex = (currentIndex + 1) % items.size
            updateUI()
        }

        buttonBack.setOnClickListener {
            currentIndex = (currentIndex - 1 + items.size) % items.size
            updateUI()
        }
    }




    private fun updateUI() {
        val currentItem = items[currentIndex]
        imageViewImagen.setImageResource(currentItem.imageRes)
        textViewTitulo.text = currentItem.title
    }

    data class Item(val imageRes: Int, val title: String)

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

}

