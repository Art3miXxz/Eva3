package com.example.sensor_gas

import java.io.Serializable

data class Datos(
    var estado: String = "",
    var fecha: String = "",
    var hora: String = "",
    var numero: Int = 0,
    var usuario: String = ""
) : Serializable {
    override fun toString(): String {
        return "\nEstado: $estado\nFecha: $fecha\nHora: $hora\nUsuario: $usuario\n"
    }
}