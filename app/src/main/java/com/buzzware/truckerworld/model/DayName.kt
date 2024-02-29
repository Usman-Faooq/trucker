package com.buzzware.truckerworld.model

import java.util.UUID

data class DayName(
    val dayName: String,
    val dayNumber: String,
    var isSelected: Boolean = false,
    var selectedMilliseconds: Long = 0,
    val id: String = UUID.randomUUID().toString()
)
