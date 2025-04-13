package es.studium.loginapirest_huellaapp

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AltaActivity : AppCompatActivity() {

    private lateinit var txt_NombreAlta : EditText
    private lateinit var txt_Pass1Alta : EditText
    private lateinit var txt_Pass2Alta : EditText
    private lateinit var btn_Alta : Button
    private lateinit var nombreAltaIntroducido : String
    private lateinit var pass1Introducida : String
    private lateinit var pass2Introducida : String

    //Variables consulta BD
    private lateinit var result : JSONArray
    private lateinit var jsonObject : JSONObject
    var idUsuarioBD: String = "0"
    private lateinit var nombreUsuarioBD : String
    private lateinit var claveUsuarioBD : String
    private lateinit var tipoUsuarioBD: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alta)

        txt_NombreAlta = findViewById(R.id.txt_nombreUsuarioAlta)
        txt_Pass1Alta = findViewById(R.id.txt_pass1Alta)
        txt_Pass2Alta = findViewById(R.id.txt_pass2Alta)
        btn_Alta = findViewById(R.id.btn_AceptarAlta)

        btn_Alta.setOnClickListener {

            nombreAltaIntroducido = txt_NombreAlta.text.toString()
            pass1Introducida = txt_Pass1Alta.text.toString()
            pass2Introducida = txt_Pass2Alta.text.toString()

            //Control de errores
            if(nombreAltaIntroducido.isEmpty() or pass1Introducida.isEmpty() or pass2Introducida.isEmpty()){
                Toast.makeText(this,"Los campos de texto con * no se pueden dejar en blanco",Toast.LENGTH_SHORT).show()
            }
            else if((pass1Introducida.length<6) || (pass2Introducida.length<6)){
                Toast.makeText(this,"La contraseña ha de tener más de 6 caracteres",Toast.LENGTH_SHORT).show()
            }
            else if (pass1Introducida != pass2Introducida){
                Toast.makeText(this,"La contraseñas introducidas han de coincidir",Toast.LENGTH_SHORT).show()
            }
            else{
                //1. Comprobación de que no existe ya el usuario en la base de datos
                if(consultarExistenciaUsuario(nombreAltaIntroducido)){
                    Toast.makeText(this,"El usuario ya existe en la BBDD",Toast.LENGTH_SHORT).show()
                }
                else{
                    var altaUsuario = AltaRemotaUsuarios()
                    var verificarAlta : Boolean = altaUsuario.darAltaUsuarioEnBD(nombreAltaIntroducido,pass1Introducida)
                    if(verificarAlta){
                        Toast.makeText(this,"ALTA EXITOSA, USUARIO VÁLIDO",Toast.LENGTH_SHORT).show()
                        val intentVuelta = Intent(this, MainActivity::class.java)
                        startActivity(intentVuelta)
                    }
                    else{
                        Toast.makeText(this,"ERROR EN EL ALTA",Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

    }

    fun consultarExistenciaUsuario(nombreUsuario : String) : Boolean{
        var existeUsuario : Boolean = false

        if(android.os.Build.VERSION.SDK_INT > 9){
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        var accesoRemotoUsuarios = AccesoRemotoUsuarios()
        result = accesoRemotoUsuarios.obtenerListado()


        //Verificamos que result no está vacío
        try{
            if(result.length() > 0){
                for (i in 0 until result.length()) {
                    jsonObject = result.getJSONObject(i)
                    idUsuarioBD = jsonObject.getString("idUsuario")
                    nombreUsuarioBD = jsonObject.getString("nombreUsuario")
                    claveUsuarioBD = jsonObject.getString("claveUsuario")
                    tipoUsuarioBD = jsonObject.getString("tipoUsuario")

                    if (nombreUsuarioBD==nombreUsuario) {
                        existeUsuario = true
                        break //<-- salimos del bucle
                    }
                }
            }
            else{
                Log.e("MainActivity", "El JSONObject está vacío")
            }
        }
        catch(e : JSONException){
            Log.e("MainActivity", "Error al procesar el JSON", e)
        }

        return existeUsuario
    }
}