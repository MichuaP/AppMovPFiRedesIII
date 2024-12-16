package com.example.noticias

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

class RegisterActivity : AppCompatActivity() {
    private lateinit var userXmlManager: UserXmlManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userXmlManager = UserXmlManager(this)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellidoPaterno = findViewById<EditText>(R.id.etApellidoPaterno)
        val etApellidoMaterno = findViewById<EditText>(R.id.etApellidoMaterno)
        val etAlias = findViewById<EditText>(R.id.etAlias)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val nombre = etNombre.text.toString()
            val apellidoPaterno = etApellidoPaterno.text.toString()
            val apellidoMaterno = etApellidoMaterno.text.toString()
            val alias = etAlias.text.toString()
            val correo = etCorreo.text.toString()
            val password = etPassword.text.toString()

            if (nombre.isEmpty() || apellidoPaterno.isEmpty() || apellidoMaterno.isEmpty() ||
                alias.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Realizar el registro de usuario
            registerUsuario(
                nombre = nombre,
                apellidoPaterno = apellidoPaterno,
                apellidoMaterno = apellidoMaterno,
                alias = alias,
                correo = correo,
                contrasena = password
            )

            // DropBox?
            //if (userXmlManager.saveUser(nombre, apellidoPaterno, apellidoMaterno, alias, correo, password)) {
            //    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            //    finish()
            //} else {
            //    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            //}
        }
    }

    private fun registerUsuario(nombre: String, apellidoPaterno: String, apellidoMaterno: String, alias: String, correo: String, contrasena: String) {
        val client = OkHttpClient()

        val jsonObject = JSONObject().apply {
            put("nombre", nombre)
            put("apep", apellidoPaterno)
            put("apem", apellidoMaterno)
            put("alias", alias)
            put("correo", correo)
            put("contrasena", contrasena)
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://192.168.0.6:3000/signupApp")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@RegisterActivity, "Error en el registro: ${it.code}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val responseData = it.body?.string()
                        val data = JSONObject(responseData)
                        runOnUiThread {
                            if (data.getBoolean("success")) {
                                Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@RegisterActivity, data.getString("error"), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        })
    }
}