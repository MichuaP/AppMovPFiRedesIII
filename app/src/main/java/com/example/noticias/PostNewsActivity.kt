package com.example.noticias

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PostNewsActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var etLugar: EditText
    private lateinit var btnPost: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var spCategoria: Spinner
    private lateinit var spIdioma: Spinner
    private var currentUsername: String = ""
    private var currentIdUser: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_news)

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnPost = findViewById(R.id.btnPost)
        recyclerView = findViewById(R.id.recyclerView)
        etLugar = findViewById(R.id.etLugar)

        //Categoria
        spCategoria = findViewById(R.id.spinner4)
        val dataCat = listOf("Arte", "Negocios", "Moda", "Comida","Salud","Hogar","Películas","Política","Ciencia","Deportes","Tecnología","Teatro","Viajes","Mundo")
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataCat)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria.adapter = adapter1

        spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Obtener el valor seleccionado
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(this@PostNewsActivity, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Acción cuando no se selecciona nada
            }
        }

        //Idioma
        spIdioma = findViewById(R.id.spinner)
        val dataId = listOf("Español", "Inglés")
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataId)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spIdioma.adapter = adapter2

        spIdioma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Obtener el valor seleccionado
                val selectedItem = parent.getItemAtPosition(position).toString()
                Toast.makeText(this@PostNewsActivity, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Acción cuando no se selecciona nada
            }
        }

        // Obtener el nombre de usuario
        currentUsername = SaveSharedPreference.getAlias(this)
        //Obtener id del usuario
        currentIdUser = SaveSharedPreference.getIdUsuario(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        loadPosts(currentIdUser)

        btnPost.setOnClickListener {
            val currentDate = LocalDate.now()
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val title = etTitle.text.toString()
            val content = etContent.text.toString()
            val lugar = etLugar.text.toString()
            val categoria = spCategoria.selectedItemPosition+1
            val idioma = spIdioma.selectedItemPosition+1
            val imagen = "/img/default3.jpg"

            if (title.isEmpty() || content.isEmpty() || lugar.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val client = OkHttpClient()

                // Crear el JSON con los datos para la noticia
                val jsonObject = JSONObject().apply {
                    put("titulo", title)
                    put("contenido", content)
                    put("fechaPublic", formattedDate)
                    put("idCategoria", categoria)
                    put("idIdioma", idioma)
                    put("idPais", 1)
                    put("idEstado", 1)
                    put("lugar", lugar)
                    put("idUser", currentIdUser)
                    put("imagen", imagen)
                }

                // Crear el cuerpo de la solicitud
                val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                // Construir la solicitud POST
                val request = Request.Builder()
                    .url("http://192.168.1.82:3000/subirNoticia")
                    .post(requestBody)
                    .build()

                // Ejecutar la solicitud en un hilo de fondo
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@PostNewsActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.d("POSTS", "ERROR" + e.message)
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(this@PostNewsActivity, "Noticia subida con éxito", Toast.LENGTH_SHORT).show()
                                Log.d("POSTS", "EXITO" )
                                // Limpiar los campos
                                etTitle.setText("")
                                etContent.setText("")
                                etLugar.setText("")
                                spCategoria.setSelection(0)
                                spIdioma.setSelection(0)
                                loadPosts(currentIdUser)
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(this@PostNewsActivity, "Error al subir la noticia", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            }
        }
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
                    Toast.makeText(this@PostNewsActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("POSTS", "ERROR" + e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@PostNewsActivity, "Error: ${it.code}", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this@PostNewsActivity, "Respuesta vacía del servidor.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}