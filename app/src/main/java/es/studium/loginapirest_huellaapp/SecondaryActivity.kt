package es.studium.loginapirest_huellaapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondaryActivity : AppCompatActivity() {

    private lateinit var lbl_idAcceso : TextView
    private lateinit var lbl_nombreAceso : TextView
    private lateinit var lbl_claveAcceso : TextView
    private lateinit var lbl_tipoAcceso : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondary)

        lbl_idAcceso = findViewById(R.id.lbl_idAcceso)
        lbl_nombreAceso = findViewById(R.id.lbl_usuarioAcceso)
        lbl_claveAcceso = findViewById(R.id.lbl_claveAcceso)
        lbl_tipoAcceso = findViewById(R.id.lbl_tipoAcceso)

        val extras = intent.extras
        if(extras != null){
            val idRecibido = extras.getString("idUsuario")
            val nombreRecibido = extras.getString("nombreUsuario")
            val claveRecibido = extras.getString("claveUsuario")
            val tipoRecibido = extras.getString("tipoUsuario")

            lbl_idAcceso.text = idRecibido
            lbl_nombreAceso.text = nombreRecibido
            lbl_claveAcceso.text = claveRecibido
            lbl_tipoAcceso.text = tipoRecibido
        }

    }
}