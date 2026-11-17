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

class Acciones : AppCompatActivity() {

    private lateinit var tvusuacciones: TextView
    private lateinit var lvdatos: ListView
    private lateinit var btn1: Button
    private lateinit var btnvolacc: Button

    private var usu: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusuacciones = findViewById(R.id.tvusuacciones)
        lvdatos = findViewById(R.id.lvdatos)
        btn1 = findViewById(R.id.btn1)
        btnvolacc = findViewById(R.id.btnvolacc)

        usu = intent.getSerializableExtra("usu") as? Usuario

        tvusuacciones.setText("Bienvenido(a) : " + usu?.nombre)

        listar_datos()
        btn_refrescar()
        volver()
        desactivarFlecha()
    }

    fun listar_datos() {
        var dbref = FirebaseDatabase.getInstance().getReference("Datos")

        var lisdat: ArrayList<Datos?> = ArrayList<Datos?>()
        var ada: ArrayAdapter<Datos?> = ArrayAdapter<Datos?>(this@Acciones, android.R.layout.simple_list_item_1, lisdat)
        lvdatos.setAdapter(ada)

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (x in snapshot.children) {
                    var estado = x.child("estado").getValue().toString()
                    var fecha = x.child("fecha").getValue().toString()
                    var hora = x.child("hora").getValue().toString()
                    var numeroStr = x.child("numero").getValue().toString()
                    var numero = numeroStr.toIntOrNull() ?: 0
                    var usuario = x.child("usuario").getValue().toString()

                    var datos = Datos(estado, fecha, hora, numero, usuario)
                    lisdat.add(datos)
                }

                Collections.reverse(lisdat)
                lvdatos.setAdapter(ada)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@Acciones,
                    "¡ Error al Obtener Datos !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun btn_refrescar() {
        btn1.setOnClickListener {
            listar_datos()
            Toast.makeText(this@Acciones, "¡ Pantalla Refrescada !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun volver() {
        btnvolacc.setOnClickListener {
            var intent = Intent(this@Acciones, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
        }
    }

    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {}
    }
}