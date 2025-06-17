package com.example.noticias

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var userXmlManager: UserXmlManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(SaveSharedPreference.getAlias(this).length == 0) {
            // Se queda en login
        } else { // se va a inicio
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        userXmlManager = UserXmlManager(this)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString() //correo
            val password = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUsuario(correo = username, contrasena = password)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    fun loginUsuario(correo: String, contrasena: String) {
        val client = OkHttpClient()

        // Crear el JSON con los datos para el login
        val jsonObject = JSONObject().apply {
            put("correo", correo)
            put("contrasena", contrasena)
        }

        // Crear el cuerpo de la solicitud
        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        // Construir la solicitud POST
        val request = Request.Builder()
            .url("http://192.168.1.80:3000/login")
            //.url("https://f02d-187-233-92-78.ngrok-free.app/login")
            .post(requestBody)
            .build()

        // Ejecutar la solicitud en un hilo de fondo
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("LOGINP", "ERROR" + e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Error: ${it.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val responseData = it.body?.string()
                    if (!responseData.isNullOrEmpty()) {
                        try{
                            val jsonResponse = JSONObject(responseData)

                            // Validar si fue exitoso
                            val success = jsonResponse.optBoolean("success", false)
                            if (!success) {
                                runOnUiThread {
                                    Toast.makeText(this@LoginActivity, "Login fallido", Toast.LENGTH_SHORT).show()
                                }
                                return
                            }
                            // Extraer solo el usuario
                            val usuario = jsonResponse.getJSONObject("usuario")
                            Log.d("LOGINP", "Usuario: $usuario")

                            // Guardar datos en SharedPreferences
                            SaveSharedPreference.setIdUsuario(this@LoginActivity, usuario.getInt("IdUser"))
                            SaveSharedPreference.setAlias(this@LoginActivity, usuario.getString("Alias"))

                            runOnUiThread {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }

                        }catch (e: Exception) {
                            e.printStackTrace()
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Error al procesar la respuesta.", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Respuesta vacía del servidor.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}