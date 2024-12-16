package com.example.noticias

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PostNewsActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnPost: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var postXmlManager: PostXmlManager
    private var currentUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_news)

        // Obtener el nombre de usuario del Intent
        currentUsername = intent.getStringExtra("username") ?: "anonymous"

        postXmlManager = PostXmlManager(this)

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnPost = findViewById(R.id.btnPost)
        recyclerView = findViewById(R.id.recyclerView)


        recyclerView.layoutManager = LinearLayoutManager(this)
        loadPosts()

        btnPost.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val newPost = Post(currentUsername, title, content)
                if (postXmlManager.savePost(newPost)) {
                    loadPosts() // Recargar los posts
                    etTitle.text.clear()
                    etContent.text.clear()
                    Toast.makeText(this, "Post publicado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al publicar el post", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadPosts() {
        val posts = postXmlManager.getAllPosts()
        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter
    }
}