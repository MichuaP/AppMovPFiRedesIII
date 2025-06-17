package com.example.noticias

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiKey = "jdoTM3Fht31XR9OnFbI8a3MVZ9jTmKAG"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener el username del intent
        currentUsername = intent.getStringExtra("username") ?: ""

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefreshLayout.setOnRefreshListener {
            loadNews() // Función para cargar las noticias
        }
        // Cargar las noticias al iniciar
        loadNews()
    }

    private fun loadNews() {
        // Mostrar el refresco
        swipeRefreshLayout.isRefreshing = true

        val service = RetrofitClient.instance.create(TimesWireService::class.java)
        service.getLatestNews(apiKey).enqueue(object : Callback<NYTResponse> {
            override fun onResponse(call: Call<NYTResponse>, response: Response<NYTResponse>) {
                if (response.isSuccessful) {
                    // Log para inspeccionar el cuerpo completo de la respuesta
                    Log.d("NYT API", "Response Body: ${response.body()}")
                    // Log para imprimir el JSON crudo de la respuesta si es necesario
                    Log.d("NYT API Raw", "Raw Response: ${response.raw()}")
                    // Log para verificar el estado HTTP
                    Log.d("NYT API", "HTTP Status Code: ${response.code()}")
                    // Obtener la lista de noticias desde la respuesta
                    val newsList = response.body()?.results ?: emptyList()
                    // Log para inspeccionar específicamente la lista de noticias
                    Log.d("NYT API", "News List: $newsList")
                    // Configurar el adaptador del RecyclerView
                    adapter = NewsAdapter(newsList) { newsItem ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.url))
                        startActivity(intent)
                    }
                    recyclerView.adapter = adapter
                } else {
                    // Log de error para el cuerpo de la respuesta si no es exitoso
                    Log.e("NYT API", "Error Body: ${response.errorBody()?.string()}")
                }
                // Detener el SwipeRefreshLayout
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<NYTResponse>, t: Throwable) {
                Log.e("NYT API", "Failed: ${t.message}")
                Toast.makeText(this@MainActivity, "Failed to load news", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false // Detener el refresco
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                //Click en cerrar sesión, borramos info de sharedPreferences
                SaveSharedPreference.clearAll(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            R.id.action_post_news -> {
                val intent = Intent(this, PostNewActivity::class.java)
                intent.putExtra("username", currentUsername)
                startActivity(intent)
                true
            }
            R.id.action_mynews -> {
                val intent = Intent(this, MyNewsActivity::class.java)
                intent.putExtra("username", currentUsername)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
