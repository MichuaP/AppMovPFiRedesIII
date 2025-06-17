package com.example.noticias

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class MyNewsActivity : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var currentUsername: String = ""
    private var currentIdUser: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_news)

        recyclerView = findViewById(R.id.recyclerView)

        // Obtener el nombre de usuario
        currentUsername = SaveSharedPreference.getAlias(this)
        //Obtener id del usuario
        currentIdUser = SaveSharedPreference.getIdUsuario(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        loadPosts(currentIdUser)
    }

    //cargar mis post desde api
    private fun loadPosts(idUs: Int) {
        val client = OkHttpClient()

        // Construir la URL con el parámetro idUsuario
        val url = "http://192.168.1.82:3000/myNews?idUsuario=$idUs"

        // Construir la solicitud
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        // Ejecutar la solicitud en un hilo de fondo
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MyNewsActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("POSTS", "ERROR" + e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@MyNewsActivity, "Error: ${it.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val responseData = it.body?.string()
                    if (!responseData.isNullOrEmpty()) {
                        // Parsear el JSON como un array
                        val noticiasArray = JSONArray(responseData)
                        val posts : MutableList<Post> = mutableListOf()

                        for (i in 0 until noticiasArray.length()) {
                            val noticia = noticiasArray.getJSONObject(i)
                            posts.add(
                                Post(
                                    titulo = noticia.getString("Titulo"),
                                    contenido = noticia.getString("Contenido"),
                                    pais = noticia.getString("Pais"),
                                    categoria = noticia.getString("Categoria"),
                                    idioma = noticia.getString("Idioma")
                                )
                            )
                        }
                        runOnUiThread {
                            postAdapter = PostAdapter(posts.toList())
                            recyclerView.adapter = postAdapter
                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(this@MyNewsActivity, "Respuesta vacía del servidor.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

}