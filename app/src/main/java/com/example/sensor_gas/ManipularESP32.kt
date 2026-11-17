package com.example.sensor_gas

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManipularESP32 : AppCompatActivity() {


    private lateinit var tvusuanipularesp32: TextView
    private lateinit var btnon: Button
    private lateinit var btnoff: Button
    private lateinit var btnvolesp32: Button

    private var usu: Usuario? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manipular_esp32)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Asignación de IDs actualizados
        tvusuanipularesp32 = findViewById(R.id.tvusuanipularesp32)
        btnon = findViewById(R.id.btnon)
        btnoff = findViewById(R.id.btnoff)
        btnvolesp32 = findViewById(R.id.btnvolesp32)

        usu = intent.getParcelableExtra("usu", Usuario::class.java)

        tvusuanipularesp32.setText("Bienvenido Usuario : " + usu?.nombre)

        encender()
        apagar()
        volver()
        desactivarFlecha()

    } // Cierra el "onCreate".


    fun encender(){
        btnon.setOnClickListener {

            var estado = "Encendido"
            var fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            var hora  = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            // variable llamada "json" que contiene los datos formateados en JSON
            // Debe estar así, sin espacios adicionales, de lo contrario,
            // firebase no reconocerá el formato cuando ESP32 intente enviarlos.
            val json = """
            {
              "estado" : "$estado",
              "fecha"  : "$fecha",
              "hora"   : "$hora",
              "numero" : 1,
              "usuario" : "${usu?.nombre?.uppercase()}"
            }
            """.trimIndent()

            // La IP es la que muestra ESP32 a través del monitor serial.
            val url = "http://10.217.157.66/guardar"

            //----------------------------------------------------------------------------------------------------------------------
            //--- "Thread" siempre queda igual, ya que, se le entrega la variable "json"                                         ---
            //--- y el código se lo entrega al ESP32                                                                             ---
            //----------------------------------------------------------------------------------------------------------------------
            Thread {
                try {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.outputStream.write(json.toByteArray())

                    val response = conn.inputStream.bufferedReader().readText()
                    runOnUiThread {
                        Toast.makeText(this, "Enviado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
            //----------------------------------------------------------------------------------------------------------------------
        }
    } // Cierra la función "encender".


    fun apagar(){
        btnoff.setOnClickListener {

            var estado = "Apagado"
            var fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            var hora  = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            // variable llamada "json" que contiene los datos formateados en JSON
            // Debe estar así, sin espacios adicionales, de lo contrario,
            // firebase no reconocerá el formato cuando ESP32 intente enviarlos.
            val json = """
                {
                  "estado" : "$estado",
                  "fecha"  : "$fecha",
                  "hora"   : "$hora",
                  "numero" : 0,
                  "usuario" : "${usu?.nombre?.uppercase()}"
                }
                """.trimIndent()

            // La IP es la que muestra ESP32 a través del monitor serial.
            val url = "http://10.217.157.66/guardar"

            //----------------------------------------------------------------------------------------------------------------------
            //--- "Thread" siempre queda igual, ya que, se le entrega la variable "json"                                         ---
            //--- y el código se lo entrega al ESP32                                                                             ---
            //----------------------------------------------------------------------------------------------------------------------
            Thread {
                try {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.outputStream.write(json.toByteArray())

                    val response = conn.inputStream.bufferedReader().readText()
                    runOnUiThread {
                        Toast.makeText(this, "Enviado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
            //----------------------------------------------------------------------------------------------------------------------
        }
    } // Cierra la función "apagar".


    fun volver(){
        // Usando el botón actualizado
        btnvolesp32.setOnClickListener {
            var intent = Intent(this@ManipularESP32, Menu::class.java)
            intent.putExtra("usu",usu)
            startActivity(intent)
        }
    } // Cierra la función "volver".


    fun desactivarFlecha(){
        onBackPressedDispatcher.addCallback(this){

        }
    } // Cierra la función "desactivarFlecha".

} // Cierra la clase "ManipularESP32".
