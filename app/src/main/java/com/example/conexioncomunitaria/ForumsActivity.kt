package com.example.conexioncomunitaria

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.conexioncomunitaria.model.AuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

data class ForumPost(
    val title: String,
    val date: String,
    val content: String,
    val userName: String
)



class ForumsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var forumPostAdapter: ForumPostAdapter
    private lateinit var btnForumsAdd: Button
    private lateinit var btnFilter: Button
    private val firestore = FirebaseFirestore.getInstance()
    private var forumPosts = listOf<ForumPost>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forums)
        btnForumsAdd = findViewById(R.id.btnForumsAdd)
        btnFilter = findViewById(R.id.btnFilter)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val authManager = AuthManager()


        btnForumsAdd.setOnClickListener {
            val intent = Intent(this, ForumPostActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnFilter.setOnClickListener {
            showFilterDialog()
        }

        loadForumPosts()

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

    private fun loadForumPosts() {
        firestore.collection("forum_posts")
            .get()
            .addOnSuccessListener { result ->
                forumPosts = result.map { document ->
                    ForumPost(
                        title = document.getString("title") ?: "",
                        date = document.getString("date") ?: "",
                        content = document.getString("content") ?: "",
                        userName = document.getString("userName") ?: ""
                    )
                }
                forumPostAdapter = ForumPostAdapter(forumPosts)
                recyclerView.adapter = forumPostAdapter
            }
            .addOnFailureListener { exception ->
                // Manejar error
            }
    }

    private fun showFilterDialog() {
        val filterOptions = arrayOf("Usuario", "Fecha", "Título")
        AlertDialog.Builder(this)
            .setTitle("Filtrar por")
            .setItems(filterOptions) { dialog, which ->
                when (which) {
                    0 -> filterBy("userName")
                    1 -> filterBy("date")
                    2 -> filterBy("title")
                }
            }
            .show()
    }

    private fun filterBy(field: String) {
        val sortedPosts = when (field) {
            "userName" -> forumPosts.sortedBy { it.userName }
            "date" -> forumPosts.sortedBy { it.date }
            "title" -> forumPosts.sortedBy { it.title.toLowerCase(Locale.ROOT) }
            else -> forumPosts
        }
        forumPostAdapter.updatePosts(sortedPosts)
    }
}


class ForumPostAdapter(private var forumPosts: List<ForumPost>) :
    RecyclerView.Adapter<ForumPostAdapter.ForumPostViewHolder>() {

    private val colors = listOf(
        R.color.pastel_yellow,
        R.color.ultra_pastel_yellow,
        R.color.pinky,
        R.color.color1,
        R.color.color2,
        R.color.color3,
        R.color.color4,
        R.color.reportHeaderColor,
        R.color.reportPinkColor,
        R.color.greenButton
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_post, parent, false)
        return ForumPostViewHolder(view, parent.context, colors)
    }

    override fun onBindViewHolder(holder: ForumPostViewHolder, position: Int) {
        val forumPost = forumPosts[position]
        holder.bind(forumPost)
    }

    override fun getItemCount() = forumPosts.size

    fun updatePosts(newPosts: List<ForumPost>) {
        forumPosts = newPosts
        notifyDataSetChanged()
    }

    class ForumPostViewHolder(itemView: View, private val context: Context, private val colors: List<Int>) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.text_view_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.text_view_date)
        private val contentTextView: TextView = itemView.findViewById(R.id.text_view_content)
        private val userNameTextView: TextView = itemView.findViewById(R.id.text_view_user_name)

        fun bind(forumPost: ForumPost) {
            titleTextView.text = forumPost.title
            dateTextView.text = forumPost.date
            contentTextView.text = forumPost.content
            userNameTextView.text = forumPost.userName
            val randomColorResId = colors.random()
            val randomColor = ContextCompat.getColor(context, randomColorResId)
            // Apply the random color to the background of the itemView
            itemView.setBackgroundColor(randomColor)
        }
    }
}

