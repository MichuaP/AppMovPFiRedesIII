package com.example.noticias

import android.content.Context
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.io.FileWriter
import java.util.concurrent.Executors

class PostXmlManager(private val context: Context) {
    private val fileName = "posts.txt"
    private val file: File = File(context.filesDir, fileName)

    // Inicializar DropboxManager con el token
    private val dropboxManager: DropboxManager = DropboxManager("sl.CCZe_1ohBXYIinSZcuDDhTITPrDOa2ez_92Ol-S-o_PJtu-CqgEkZ2sFXr8BIZn4SrToMsAXetuGyasRVTQlyotXphKOKFDfTmYWrydv7EGCmtrlwZ8TyCdO54opvKNamciiwMIoIK4SRCaVnWEm")

    // ExecutorService para manejar tareas en segundo plano
    private val executorService = Executors.newSingleThreadExecutor()

    init {
        if (!file.exists()) {
            createInitialXmlFile()
        }
    }

    private fun createInitialXmlFile() {
        // Asegurarse de que el contenido XML esté correctamente formateado
        val xmlContent = """<?xml version="1.0" encoding="UTF-8"?>
<posts>
</posts>""".trimIndent()

        try {
            // Usar FileWriter para asegurar que se escriba correctamente
            FileWriter(file).use { writer ->
                writer.write(xmlContent)
                writer.flush()
            }

            // Subir el archivo inicial a Dropbox
            executorService.submit {
                dropboxManager.uploadFile(file, "/posts.txt")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun savePost(post: Post): Boolean {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)

            val postElement = document.createElement("post")

            val usernameElement = document.createElement("username")
            usernameElement.textContent = post.username

            val titleElement = document.createElement("title")
            titleElement.textContent = post.title

            val contentElement = document.createElement("content")
            contentElement.textContent = post.content

            val timestampElement = document.createElement("timestamp")
            timestampElement.textContent = post.timestamp.toString()

            postElement.appendChild(usernameElement)
            postElement.appendChild(titleElement)
            postElement.appendChild(contentElement)
            postElement.appendChild(timestampElement)

            document.documentElement.appendChild(postElement)

            val transformer = TransformerFactory.newInstance().newTransformer()
            val source = DOMSource(document)
            val writer = FileWriter(file)
            val result = StreamResult(writer)
            transformer.transform(source, result)

            // Subir el archivo actualizado a Dropbox de manera asíncrona
            executorService.submit {
                dropboxManager.uploadFile(file, "/posts.txt")
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getAllPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        try {
            // Intentar descargar la última versión desde Dropbox
            executorService.submit {
                try {
                    dropboxManager.downloadFile("/posts.txt", file)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)

            val postNodes = document.getElementsByTagName("post")
            for (i in 0 until postNodes.length) {
                val postElement = postNodes.item(i) as Element
                val username = postElement.getElementsByTagName("username").item(0).textContent
                val title = postElement.getElementsByTagName("title").item(0).textContent
                val content = postElement.getElementsByTagName("content").item(0).textContent
                val timestamp = postElement.getElementsByTagName("timestamp").item(0).textContent.toLong()

                posts.add(Post(username, title, content, timestamp))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return posts.sortedByDescending { it.timestamp }
    }

    // Asegurarse de liberar los recursos cuando ya no se necesiten
    fun cleanup() {
        executorService.shutdown()
    }
}