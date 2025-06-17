package com.example.noticias



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.noticias.R
import com.example.noticias.NewsItem
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapter(private val newsList: List<NewsItem>,
    private val onItemClick: (NewsItem) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.titleTextView.text = newsItem.title
        holder.abstractTextView.text = newsItem.abstract
        holder.dateTextView.text = formatDate(newsItem.published_date)

        val highResImage = newsItem.multimedia?.find { it.format == "Large" }?.url
        if (highResImage != null) {
            Glide.with(holder.imageView.context)
                .load(highResImage)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.error)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background)
        }

        val imageUrl = newsItem.multimedia?.firstOrNull()?.url
        Glide.with(holder.imageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background) // Imagen predeterminada
            .error(R.drawable.error) // Imagen de error
            .into(holder.imageView)

        //Click en la noticia
        holder.itemView.setOnClickListener{
            onItemClick(newsItem)
        }


    }
    fun formatDate(dateString: String): String {
        return try {
            // Convertir la fecha del formato de la API al formato deseado
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString // En caso de error, devolver la fecha original
        }
    }
    override fun getItemCount(): Int = newsList.size

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.news_title)
        val abstractTextView: TextView = view.findViewById(R.id.news_abstract)
        val dateTextView: TextView = view.findViewById(R.id.news_date)
        val imageView: ImageView = view.findViewById(R.id.news_image)
    }
}
