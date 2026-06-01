package org.lumen.app.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))
    .withZone(ZoneId.systemDefault())

fun formatDate(dataDoBanco: String?): String {
    if (dataDoBanco.isNullOrBlank()) return "Data inválida"

    return try {

        val instant = Instant.parse(dataDoBanco)
        formatter.format(instant)
    } catch (e: Exception) {
        "Data inválida"
    }
}