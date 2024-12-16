package com.example.noticias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // ViewHolder para el adaptador
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val content: TextView = itemView.findViewById(R.id.tvContent)
        val category: TextView = itemView.findViewById(R.id.tvCategory)
        val country: TextView = itemView.findViewById(R.id.tvCountry)
        val language: TextView = itemView.findViewById(R.id.tvLanguage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // Inflar el diseño del item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // Obtener el post actual
        val post = posts[position]

        // Asignar los valores a las vistas
        holder.title.text = post.titulo
        holder.content.text = post.contenido
        holder.category.text = post.categoria
        holder.country.text = post.pais
        holder.language.text = post.idioma
    }

    override fun getItemCount(): Int = posts.size
}

