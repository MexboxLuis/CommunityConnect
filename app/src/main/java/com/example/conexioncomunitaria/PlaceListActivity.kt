package com.example.conexioncomunitaria


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conexioncomunitaria.model.AuthManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView


class PlaceListActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_place_list)

        val authManager = AuthManager()
        val categoryIndex = intent.getIntExtra("CATEGORY_INDEX", -1)

        val backgroundColor = when (categoryIndex) {
            0 -> ContextCompat.getColor(this, R.color.color1)
            1 -> ContextCompat.getColor(this, R.color.color2)
            2 -> ContextCompat.getColor(this, R.color.color3)
            3 -> ContextCompat.getColor(this, R.color.color4)
            else -> ContextCompat.getColor(this, R.color.ultra_pastel_yellow)
        }

        val title = when (categoryIndex) {
            0 -> "Parques"
            1 -> "Oficinas"
            2 -> "Hospitales"
            3 -> "Museos"
            else -> ""
        }

        val upperCaseTitle = title.toUpperCase()

        val titleTextView: TextView = findViewById(R.id.textViewTitle)
        titleTextView.text = upperCaseTitle

        val places = when (categoryIndex) {
            0 -> getParqueData()
            1 -> getOficinaData()
            2 -> getHospitalData()
            3 -> getMuseoData()
            else -> emptyList()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlaceListAdapter(places, backgroundColor)

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


    private fun getParqueData(): List<Place> {
        return listOf(
            Place(
                R.drawable.parque,
                "Parque Sanrio",
                "Calle Puroland #234 Col. Centro",
                "Ideal para andar en bici",
                null
            ),
            Place(
                R.drawable.parque,
                "Parque RockPark",
                "Calle Reforma Col. Centro",
                "Presentaciones musicales todos los jueves",
                null
            ),
            Place(R.drawable.parque, "Parque Mayor", "Calle Tromso, Col. NW", "Pet Friendly", null),
            Place(
                R.drawable.parque,
                "Parque Bol",
                "Calle Madril, Col. PM",
                "Ideal para picnic",
                null
            ),
        )
    }

    private fun getOficinaData(): List<Place> {
        return listOf(
            Place(
                R.drawable.oficina,
                "Alcaldía principal",
                "Calle Reforma #234, Col. Centro",
                "Viernes a Domingo 8:00 - 16hrs",
                null
            ),
            Place(
                R.drawable.oficina,
                "DIF Kuromiland",
                "Calle Kuromi #74, Col. Centro",
                "Viernes a Domingo 8:00 - 16hrs",
                null
            ),
            Place(
                R.drawable.oficina,
                "Centro Juventud",
                "Calle Bratz #23, Col. Centro",
                "Viernes a Domingo 8:00 - 16hrs",
                null
            ),
            Place(
                R.drawable.oficina,
                "Tesorería",
                "Calle lol #24, Col. Centro",
                "Viernes a Domingo 8:00 - 16hrs",
                null
            )


        )
    }

    private fun getHospitalData(): List<Place> {
        return listOf(
            Place(
                R.drawable.hospital,
                "Hospital principal",
                "Calle Hospitales #234, Col.Centro",
                "Servicio 24hrs",
                "Urgencias 987654321"
            ),
            Place(
                R.drawable.hospital,
                "Hospital Kuromiland",
                "Calle Hospitales #235, Col.Centro",
                "Servicio 24hrs",
                "Urgencias 555123456"
            ),
            Place(
                R.drawable.hospital,
                "Hospital Santillanal",
                "Calle Hospitales #237, Col.Centro",
                "Servicio 24hrs",
                "Urgencias 246810135"
            ),

            )
    }

    private fun getMuseoData(): List<Place> {
        return listOf(
            Place(
                R.drawable.museo,
                "Museo de las Maravillas Locas",
                "Calle de las Fantasías 123, Ciudad de las Risas",
                "De lunes a viernes: 10:00 - 18:00, Sábados y domingos: 11:00 - 20:00",
                "+123 456 789"
            ),
            Place(
                R.drawable.museo,
                "Museo de la Ciencia Absurda",
                "Avenida de los Sueños Raros 456, Metrópolis Cómica",
                "Martes a sábado: 11:00 - 17:00, Domingos: 12:00 - 16:00",
                "+987 654 321"
            ),
            Place(
                R.drawable.museo,
                "Museo de Historias Fantásticas",
                "Calle de los Enigmas 789, Ciudad de las Bromas",
                "Lunes a viernes: 9:00 - 16:30, Sábados: 10:00 - 13:00",
                "+345 678 912"
            ),
            Place(
                R.drawable.museo,
                "Museo de Arte Surrealista",
                "Plaza de los Sueños Loco 567, Villa de las Carcajadas",
                "Miércoles a domingo: 12:00 - 20:00",
                "+789 123 456"
            ),
            Place(
                R.drawable.museo,
                "Museo de las Aventuras Extravagantes",
                "Calle de las Locuras 321, Ciudad de las Ocurrencias",
                "De lunes a viernes: 10:30 - 17:30, Sábados y domingos: 12:00 - 18:00",
                "+111 222 333"
            ),
            Place(
                R.drawable.museo,
                "Museo de la Tecnología Chiflada",
                "Avenida de los Inventos Locos 654, Metrópolis Cómica",
                "Martes a sábado: 10:00 - 16:00, Domingos: 11:00 - 15:00",
                "+444 555 666"
            ),
            Place(
                R.drawable.museo,
                "Museo de Misterios y Carcajadas",
                "Calle de las Risas 987, Ciudad de las Sorpresas",
                "Lunes a viernes: 9:00 - 15:30, Sábados: 10:30 - 14:00",
                "+777 888 999"
            ),
            Place(
                R.drawable.museo,
                "Museo de Arte Frenético",
                "Plaza de las Locuras 753, Villa de las Carcajadas",
                "Miércoles a domingo: 11:00 - 19:00",
                "+123 456 789"
            ),
            Place(
                R.drawable.museo,
                "Museo de las Risas Sin Fin",
                "Calle de las Ocurrencias 567, Ciudad de las Carcajadas",
                "De lunes a viernes: 9:00 - 17:00, Sábados y domingos: 10:00 - 18:00",
                "+111 222 333"
            ),
            Place(
                R.drawable.museo,
                "Museo de la Imaginación Desbordante",
                "Avenida de los Sueños Locos 888, Metrópolis Divertida",
                "Martes a sábado: 10:00 - 16:00, Domingos: 11:00 - 15:00",
                "+444 555 666"
            ),
            Place(
                R.drawable.museo,
                "Museo de Misterios y Risas",
                "Calle de las Sorpresas 999, Ciudad de las Bromas",
                "Lunes a viernes: 9:00 - 15:30, Sábados: 10:30 - 14:00",
                "+777 888 999"
            ),
            Place(
                R.drawable.museo,
                "Museo de Arte Fantabuloso",
                "Plaza de las Locuras 753, Villa de las Sonrisas",
                "Miércoles a domingo: 11:00 - 19:00",
                "+123 456 789"
            ),
        )

    }

    data class Place(
        val imageRes: Int,
        val name: String,
        val address: String,
        val schedule: String,
        val contact: String?
    )
}

class PlaceListAdapter(private val places: List<PlaceListActivity.Place>, private val backgroundColor: Int) :
    RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.place_item_list, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place, backgroundColor)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        private val scheduleTextView: TextView = itemView.findViewById(R.id.scheduleTextView)
        private val contactTextView: TextView = itemView.findViewById(R.id.contactTextView)

        fun bind(place: PlaceListActivity.Place, backgroundColor: Int) {
            imageView.setImageResource(place.imageRes)
            nameTextView.text = place.name
            addressTextView.text = place.address
            scheduleTextView.text = place.schedule
            contactTextView.text = place.contact ?: ""

            itemView.setBackgroundColor(backgroundColor)
        }
    }
}






