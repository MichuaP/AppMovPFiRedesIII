package com.example.noticias

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import java.util.concurrent.Executors

class UserXmlManager(private val context: Context) {
    private val fileName = "users.txt"
    private val file: File = File(context.filesDir, fileName)

    // Crea una instancia del cliente de Dropbox
    private val dropboxManager: DropboxManager = DropboxManager("sl.CCZe_1ohBXYIinSZcuDDhTITPrDOa2ez_92Ol-S-o_PJtu-CqgEkZ2sFXr8BIZn4SrToMsAXetuGyasRVTQlyotXphKOKFDfTmYWrydv7EGCmtrlwZ8TyCdO54opvKNamciiwMIoIK4SRCaVnWEm") // Reemplaza con tu token de acceso

    // ExecutorService para manejar tareas en segundo plano
    private val executorService = Executors.newSingleThreadExecutor()

    init {
        if (!file.exists()) {
            createInitialXmlFile()
        }
    }

    // Crear un archivo XML inicial si no existe
    private fun createInitialXmlFile() {
        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <users>
            </users>
        """.trimIndent()
        file.writeText(xmlContent)
    }

    // Guardar un nuevo usuario en el archivo XML y respaldarlo en Dropbox
    fun saveUser(username: String, password: String): Boolean {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)

            // Crear nuevo elemento de usuario
            val userElement = document.createElement("user")

            val usernameElement = document.createElement("username")
            usernameElement.textContent = username

            val passwordElement = document.createElement("password")
            passwordElement.textContent = password

            userElement.appendChild(usernameElement)
            userElement.appendChild(passwordElement)

            // Añadir el nuevo usuario al documento
            document.documentElement.appendChild(userElement)

            // Guardar el documento actualizado en el archivo local
            val transformer = TransformerFactory.newInstance().newTransformer()
            val source = DOMSource(document)
            val writer = FileWriter(file)
            val result = StreamResult(writer)
            transformer.transform(source, result)

            // Usar el Executor para cargar el archivo en Dropbox de manera asíncrona
            executorService.submit {
                dropboxManager.uploadFile(file, "/users.txt")
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    // Validar si un usuario y contraseña coinciden con los datos guardados
    fun validateUser(username: String, password: String): Boolean {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(file)

            val users = document.getElementsByTagName("user")
            for (i in 0 until users.length) {
                val userElement = users.item(i) as Element
                val storedUsername = userElement.getElementsByTagName("username").item(0).textContent
                val storedPassword = userElement.getElementsByTagName("password").item(0).textContent

                if (username == storedUsername && password == storedPassword) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
