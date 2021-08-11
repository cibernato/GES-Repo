package com.example.safecare.dtos

import com.google.maps.model.LatLng
import java.util.*

class AlertaDto(
    var ubicacion : LatLng,
    var text : String,
    var titulo : String,
    var fecha : Date = Date()
) {
}