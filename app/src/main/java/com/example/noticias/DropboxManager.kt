package com.example.noticias

    import com.dropbox.core.v2.DbxClientV2
    import com.dropbox.core.DbxRequestConfig
    import com.dropbox.core.v2.files.WriteMode
    import java.io.File
    import java.io.FileInputStream
    import android.util.Log

    class DropboxManager(private val accessToken: String) {

        private val client: DbxClientV2

        init {
            val config = DbxRequestConfig.newBuilder("UsuarioRespaldo").build() // Nombre de tu app de Dropbox
            client = DbxClientV2(config, accessToken)
        }

        // Función para subir un archivo a Dropbox
        fun uploadFile(localFile: File, dropboxPath: String) {
            try {
                FileInputStream(localFile).use { inputStream ->
                    client.files().uploadBuilder(dropboxPath)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream)
                }
                Log.i("Dropbox", "Archivo subido exitosamente a Dropbox.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DropboxError", "Error al subir el archivo: ${e.message}", e)
            }
        }
        private fun getClient(): DbxClientV2 {
            return client
        }

        fun downloadFile(dropboxPath: String, localFile: File) {
            try {
                val client = getClient()
                val outputStream = localFile.outputStream()
                client.files().downloadBuilder(dropboxPath)
                    .download(outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }
