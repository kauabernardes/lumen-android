package org.lumen.app.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun formatDate(dataDoBanco: String): String {
    return try {

        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatoEntrada.timeZone = TimeZone.getTimeZone("UTC")

        val dataObjeto = formatoEntrada.parse(dataDoBanco)


        val formatoSaida = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        formatoSaida.timeZone = TimeZone.getDefault()


        if (dataObjeto != null) {
            formatoSaida.format(dataObjeto)
        } else {
            dataDoBanco
        }
    } catch (e: Exception) {

        "Data inválida"
    }
}