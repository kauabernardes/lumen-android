package org.lumen.app.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatDate(dataDoBanco: String): String {
    // Se a string contiver o 'Z' ou o offset, o parse abaixo é mais direto:
    val instant = Instant.parse(dataDoBanco)
    val localTime = instant.atZone(ZoneId.systemDefault())

    return localTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"))
}