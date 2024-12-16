package com.example.noticias

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var userXmlManager: UserXmlManager
    private val ENCRYPTION_KEYC = "35224252703265875843711068151088"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

            Log.d("LOGINP", "Correo y contraseña"+username+" "+password)


            loginUsuario(
                correo = username,
                contrasena = password
            )

            //Lo de dropbox?
//            if (userXmlManager.validateUser(username, password)) {
//                val intent = Intent(this, MainActivity::class.java)
//                intent.putExtra("username", username)
//                startActivity(intent)
//                finish()
//            } else {
//                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
//            }
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
            .url("http://192.168.1.82:3000/loginApp")
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
                            Toast.makeText(this@LoginActivity, "Error en el inicio de sesión: ${it.code}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Si la respuesta es exitosa
                        val responseData = it.body?.string()
                        runOnUiThread {
                            // Cambiar a la MainActivity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("username", correo)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        })
    }
}