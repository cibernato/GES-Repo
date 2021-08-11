package com.example.safecare.dtos

class UsuarioDto(
    var nombre: String,
    var email: String,
    var supervisor: String,
    var telefono: String,
    var password: String,
    var isSupervisor: Boolean = false
) {
}