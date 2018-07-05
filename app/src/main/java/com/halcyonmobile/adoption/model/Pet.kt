package com.halcyonmobile.adoption.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Pet(
        var id: String = "",
        var userId: String = "",
        var name: String = "",
        var species: String = "",
        var description: String = "",
        var imageMain: String = "",
        var imageBackground: String = "",
        var age: Long = 0,
        var adopted: Boolean = false
) {
    fun getAgeString(): String {
        val present = Calendar.getInstance()
        val petDate = Calendar.getInstance()
        petDate.time = Date(age)
        val years = present.get(Calendar.YEAR) - petDate.get(Calendar.YEAR)
        if (years == 1) {
            return "1 year"
        }
        if (years < 1) {
            val months = present.get(Calendar.MONTH) - petDate.get(Calendar.MONTH)
            if (months == 1) {
                return "1 month"
            }
            if (months < 1) {
                val weeks = present.get(Calendar.WEEK_OF_YEAR) - petDate.get(Calendar.WEEK_OF_YEAR)
                if (weeks == 1) {
                    return "1 week"
                }
                return "$weeks weeks"
            }
            return "$months months"
        }
        return "$years years"
    }
}
