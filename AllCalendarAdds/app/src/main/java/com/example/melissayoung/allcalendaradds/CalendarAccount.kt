package com.example.melissayoung.allcalendaradds

data class CalendarAccount(
        val calendarId: String,
        val accountName: String,
        val ownerName: String,
        val accountType: String,
        var isSelected: Boolean = false
)