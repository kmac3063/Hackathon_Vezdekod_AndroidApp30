package com.example.androidapp



//
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.lang.Exception

object Utils {
    fun formatDate(originalDate: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                .withZone(ZoneId.of("UTC"))
            val date: LocalDateTime = LocalDateTime.parse(originalDate, formatter)
            val formatter1 = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            val formatted1 = date.format(formatter1)

            formatted1.toString()
        } catch (e: Exception) {
            originalDate
        }
    }
}