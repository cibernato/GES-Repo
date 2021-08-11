package com.example.safecare

import com.example.safecare.dtos.AlertaDto
import com.example.safecare.dtos.UsuarioDto
import com.google.android.material.textfield.TextInputLayout
import com.google.maps.model.LatLng

object StaticObjects {
    var telefonos = arrayListOf<String>().apply {
        add("991199370")
        add("935420642")
    }

    var usuarios = arrayListOf<UsuarioDto>().apply {
        add( UsuarioDto("usuario1","otrocorreo@asdf.asfd","Renzo","991199370","asdf"))
        add( UsuarioDto("test","asdf1@asdf.asdf","Renzo","935420642","asdf"))
    }

    var alertas = arrayListOf<AlertaDto>().apply {
        add(AlertaDto(LatLng(-16.4138316, -71.5489517),"Se registro un incidente","E1"))
    }

}
fun validateInputField(inputLayout: TextInputLayout, field: String): Boolean {
    if (inputLayout.editText?.text.toString().isEmpty()) {
        inputLayout.error = "$field no puede estar vacio"
        inputLayout.isErrorEnabled = true
        return false
    }
    return true
}