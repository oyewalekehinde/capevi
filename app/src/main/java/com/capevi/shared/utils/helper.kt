package com.capevi.shared.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

fun formatTime(time: String): LocalTime {
    val splitTime = time.split(":")
    val time = LocalTime.of(splitTime[0].toInt(), splitTime[1].toInt())
    return time
}

fun formatDate(date: String): LocalDate {
    val splitTime = date.split("/")
    val date = LocalDate.of(splitTime[2].toInt(), splitTime[1].toInt(), splitTime[0].toInt())
    return date
}

fun getDayOfMonthSuffix(day: Int): String {
    if (day in 11..13) {
        return "th"
    }
    return when (day % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

fun formatDateText(date: LocalDateTime): String {
    // Get day of month with suffix
    val dayOfMonth = date.dayOfMonth
    val dayOfMonthWithSuffix = "$dayOfMonth${getDayOfMonthSuffix(dayOfMonth)}"

    // Get month and year
    val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val year = date.year

    // Combine to get the final formatted string
    val formattedDate = "$dayOfMonthWithSuffix $month, $year"
    return formattedDate
}

fun validateEmail(email: String): String? {
    // Basic regex for email validation
    val emailRegex = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
    return if (email.matches(emailRegex.toRegex())) {
        null
    } else {
        "Invalid email address"
    }
}

fun validatePassword(password: String): String? =
    when {
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isDigit() } -> "Password must contain at least one number"
        !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
        !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
        else -> null
    }

fun validateFullName(fullName: String): String? {
    val regex = "^[a-zA-Z]+(?: [a-zA-Z]+)+\$".toRegex()
    return if (fullName.matches(regex)) {
        null
    } else {
        "Invalid name"
    }
}
