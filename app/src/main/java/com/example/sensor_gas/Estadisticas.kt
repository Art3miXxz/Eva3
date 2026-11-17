package com.example.sensor_gas

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections

class Estadisticas : AppCompatActivity() {

    private lateinit var tvusuestadisticas: TextView
    private lateinit var btnvolest: Button

    // Variables para la lista y el botón de refrescar
    private lateinit var lvdatos: ListView
    private lateinit var btn1: Button

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estadisticas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusuestadisticas = findViewById(R.id.tvusuestadisticas)
        btnvolest = findViewById(R.id.btnvolest)

        // Enlazar los IDs del XML
        lvdatos = findViewById(R.id.lvdatos)
        btn1 = findViewById(R.id.btn1)

        usu = intent.getSerializableExtra("usu") as? Usuario

        tvusuestadisticas.setText("Bienvenido(a) : " + usu?.nombre)

        listar_datos()
        btn_refrescar()

        volver()
        desactivarFlecha()
    }


    fun listar_datos() {
        // IMPORTANTE: Cambia "Sensor" por el nombre exacto del nodo en tu Firebase
        // donde la ESP32 receptora guarda las lecturas del sensor de gas.
        var dbref = FirebaseDatabase.getInstance().getReference("Sensor")

        var lisdat: ArrayList<LecturaGas?> = ArrayList<LecturaGas?>()
        var ada: ArrayAdapter<LecturaGas?> = ArrayAdapter<LecturaGas?>(this@Estadisticas, android.R.layout.simple_list_item_1, lisdat)
        lvdatos.setAdapter(ada)

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (x in snapshot.children) {
                    // Lee el nivel de gas ("numero" según la estructura Msg de tu ESP32)
                    val numeroStr = (x.child("numero").getValue() ?: x.child("nivelGas").getValue())?.toString() ?: "0"
                    val nivelGas = numeroStr.toIntOrNull() ?: 0

                    // Lee "texto" ("FUGA DE GAS" / "Ambiente seguro") o el booleano "estado"
                    val textoFirebase = x.child("texto").getValue()?.toString()
                    val estadoBool = x.child("estado").getValue()?.toString()
                    val estadoTexto = when {
                        !textoFirebase.isNullOrEmpty() && textoFirebase != "null" -> textoFirebase
                        estadoBool == "true" -> "FUGA DE GAS"
                        else -> "Ambiente seguro"
                    }

                    val fecha = x.child("fecha").getValue()?.toString() ?: "Sin fecha"
                    val hora = x.child("hora").getValue()?.toString() ?: "Sin hora"

                    val lectura = LecturaGas(nivelGas, estadoTexto, fecha, hora)
                    lisdat.add(lectura)
                }

                // Invertir el orden para mostrar las lecturas más recientes arriba
                Collections.reverse(lisdat)
                lvdatos.setAdapter(ada)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@Estadisticas,
                    "¡ Error al Obtener Datos !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    fun btn_refrescar() {
        btn1.setOnClickListener {
            listar_datos()
            Toast.makeText(this@Estadisticas, "¡ Pantalla Refrescada !", Toast.LENGTH_SHORT).show()
        }
    }


    private fun volver() {
        btnvolest.setOnClickListener {
            val intent = Intent(this@Estadisticas, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {}
    }
}

// Clase modelo para dar formato a las mediciones del sensor de gas en el ListView
data class LecturaGas(
    var nivelGas: Int,
    var estado: String,
    var fecha: String,
    var hora: String
) {
    override fun toString(): String {
        return "\nNivel de Gas: $nivelGas\nEstado: $estado\nFecha: $fecha\nHora: $hora\n"
    }
}