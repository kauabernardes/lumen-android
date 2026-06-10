package org.lumen.app.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatDate(dataDoBanco: String): String {
    val instant = Instant.parse(dataDoBanco)
    val localTime = instant.atZone(ZoneId.systemDefault())

    return localTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"))
}