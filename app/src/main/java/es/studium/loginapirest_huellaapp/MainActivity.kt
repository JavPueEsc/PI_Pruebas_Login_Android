package es.studium.loginapirest_huellaapp

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    //declaramos botones var= variable que puede cambiar, lateinit= permite no asignar
    //un valor en el momento
    private lateinit var btnLoging : Button
    private lateinit var btnAlta : Button
    private lateinit var btnBiometrico : Button
    private lateinit var txtUsu : EditText
    private lateinit var txtPass : EditText
    private lateinit var usuario : String
    private lateinit var password: String
    private lateinit var chkMostrar : CheckBox
    private lateinit var result : JSONArray
    private lateinit var jsonObject : JSONObject

    var idUsuarioBD: String = "0"
    private lateinit var nombreUsuarioBD : String
    private lateinit var claveUsuarioBD : String
    private lateinit var tipoUsuarioBD: String

    private lateinit var datosUsuarioCorrecto : ModeloUsuario
    private var credencialesCorrectas : Boolean = false

    private lateinit var lbl_comprobacionusuario : TextView
    private lateinit var lbl_comprobacionPass : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Asociación de botones con vistas
        btnLoging = findViewById(R.id.btn_login)
        btnAlta = findViewById(R.id.btn_alta)
        btnBiometrico = findViewById(R.id.btn_biometrico)
        txtUsu = findViewById(R.id.txt_usuario)
        txtPass = findViewById(R.id.txt_pass)
        chkMostrar = findViewById(R.id.ckb_mostrar)
        lbl_comprobacionusuario = findViewById(R.id.lbl_usuarioBD)
        lbl_comprobacionPass = findViewById(R.id.lbl_PassBD)

        //Añadir los listeners y gestión directa
        btnLoging.setOnClickListener {
            usuario = txtUsu.text.toString();
            password = txtPass.text.toString()

            //------------------------------Comunicación con la API-------------------------------------
            if(android.os.Build.VERSION.SDK_INT > 9){
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
            }
            var accesoRemotoUsuarios = AccesoRemotoUsuarios()
            result = accesoRemotoUsuarios.obtenerListado()


            //Verificamos que result no está vacío
            try{
                //Reseteo los textviews desede el principio por si se ha realizado
                //Un acceso correcto y se quieren realizar otros incorrectos
                lbl_comprobacionusuario.text = "Acceso denegado"
                lbl_comprobacionPass.text = "Acceso denegado"

                if(result.length() > 0){
                    for (i in 0 until result.length()) {
                        jsonObject = result.getJSONObject(i)
                        idUsuarioBD = jsonObject.getString("idUsuario")
                        nombreUsuarioBD = jsonObject.getString("nombreUsuario")
                        claveUsuarioBD = jsonObject.getString("claveUsuario")
                        tipoUsuarioBD = jsonObject.getString("tipoUsuario")

                        if ((nombreUsuarioBD==usuario) and (claveUsuarioBD == convertirASHA256(password))){
                            lbl_comprobacionusuario.text = nombreUsuarioBD
                            lbl_comprobacionPass.text = claveUsuarioBD
                            datosUsuarioCorrecto = ModeloUsuario(idUsuarioBD.toInt(), nombreUsuarioBD,claveUsuarioBD, tipoUsuarioBD.toInt())
                            credencialesCorrectas = true
                            break //<-- salimos del bucle
                        }
                    }
                    Log.d("DEBUG", "HASH ENVIADO: ${convertirASHA256(password)}")
                    Log.d("DEBUG", "HASH BD: $claveUsuarioBD")
                }
                else{
                    Log.e("MainActivity", "El JSONObject está vacío")
                }
            }
            catch(e : JSONException){
                Log.e("MainActivity", "Error al procesar el JSON", e)
            }
            //------------------------------------------------------------------------------------------

            //-------> CONTROL DE ERRORES IRÍA AQUÍ: Campos vacíos <-----------

            if(credencialesCorrectas){
               Toast.makeText(this,"CREDENCIALES CORRECTAS", Toast.LENGTH_SHORT).show()
               //Reseteo de credenciales correctas
               credencialesCorrectas = false

                val bundle = Bundle()
                bundle.putString("idUsuario", idUsuarioBD)
                bundle.putString("nombreUsuario", nombreUsuarioBD)
                bundle.putString("claveUsuario", claveUsuarioBD)
                bundle.putString("tipoUsuario", tipoUsuarioBD)

                val intent = Intent(this, SecondaryActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
           }
            else{
               Toast.makeText(this,"CREDENCIALES INCORRECTAS", Toast.LENGTH_SHORT).show()
           }
        }

        btnAlta.setOnClickListener {
            Toast.makeText(this,"Botón Alta presionado",Toast.LENGTH_SHORT).show()
            val intentAlta = Intent(this, AltaActivity::class.java)
            startActivity(intentAlta)
        }

        btnBiometrico.setOnClickListener {
            Toast.makeText(this,"Botón Biométrico presionado",Toast.LENGTH_SHORT).show()
        }

        //Gestíón Mostrar/ocultar contraseña
        chkMostrar.setOnClickListener {
            if(chkMostrar.isChecked){
                txtPass.inputType = InputType.TYPE_CLASS_TEXT
                txtPass.setSelection(txtPass.text.length)
            }
            else{
                //Hace que el edit text admita texto (1) y por otro lado que ese texto se convierta en contraseña (2)
                txtPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                txtPass.setSelection(txtPass.text.length)
            }
        }

        // Hacer que la aplicación se ejecute en modo inmersivo
       // hideSystemUI()

    }

    //----------------------------------------------------------------------------------------------
    //Hacer que desaparezcan los botones de navegacion que aparecen por defecto
    private fun hideSystemUI() {
        // Esconde tanto la barra de estado como los botones de navegación
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Cuando la actividad recibe el foco, aseguramos que se mantenga el modo inmersivo
            hideSystemUI()
        }
    }
    //----------------------------------------------------------------------------------------------

    //Convertir lo que escribe el usuario a SHA2
    fun convertirASHA256(input: String): String {
        // Obtener la instancia del algoritmo SHA-256
        val digest = MessageDigest.getInstance("SHA-256")

        // Obtener el hash de la entrada
        val hashBytes = digest.digest(input.toByteArray())

        // Convertir los bytes a una cadena hexadecimal
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}