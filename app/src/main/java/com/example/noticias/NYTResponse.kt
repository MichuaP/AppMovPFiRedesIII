package com.example.noticias

data class NYTResponse(
    val status: String,
    val results: List<NewsItem>
)

data class NewsItem(
    val title: String,
    val abstract: String,
    val url: String,
    val published_date: String,
    val multimedia: List<Multimedia>?
)

data class Multimedia(
    val url: String,
    val format: String,
    val height: Int,
    val width: Int,
    val type: String,
    val subtype: String,
    val caption: String?,
    val copyright: String?
)