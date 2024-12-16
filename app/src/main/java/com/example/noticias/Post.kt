package com.example.noticias



data class Post(
    val username: String,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)