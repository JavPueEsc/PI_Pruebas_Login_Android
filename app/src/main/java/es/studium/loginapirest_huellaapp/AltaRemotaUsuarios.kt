package es.studium.loginapirest_huellaapp

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.IOException

class AltaRemotaUsuarios {
    val client = OkHttpClient()

    fun darAltaUsuarioEnBD(nombre:String, clave : String) : Boolean{
        var correcta : Boolean = true;

        //Montamos la peticion POST
        val formBody = FormBody.Builder()
            .add("nombreUsuario",nombre)
            .add("claveUsuario", clave)
            .add("tipoUsuario", "0")
            .build()

        var request : Request = Request.Builder()
            .url("http://192.168.0.217/ApiRestPracticaPruebaLogin/usuarios.php")
            .post(formBody)
            .build()

        val call = client.newCall(request)

        try{
            val response = call.execute()
            Log.d("RESPUESTA", response.body?.string() ?: "Sin cuerpo")
            return response.isSuccessful
        }
        catch(e : IOException){
            return false
        }
    }
}