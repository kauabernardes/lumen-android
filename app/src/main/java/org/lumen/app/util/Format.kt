package org.lumen.app.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun formatDate(dataDoBanco: String): String {
    val data = LocalDateTime.parse(dataDoBanco, DateTimeFormatter.ISO_DATE_TIME)
    return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"))
}