package com.example.noticias

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_page)

        // Espera 2 segundos y luego inicia la MainActivity
        Handler().postDelayed({
            val intent = Intent(this@SplashScreen, LoginActivity::class.java)
            startActivity(intent)
            finish() // Cierra la actividad de splash para que no pueda regresar a ella
        }, 2000) // 2000 ms = 2 segundos
    }
}