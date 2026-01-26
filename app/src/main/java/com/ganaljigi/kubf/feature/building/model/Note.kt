package com.ganaljigi.kubf.feature.building.model

data class Note(
    val id: Long = 0L,
    val note:String="",
    val imageUrl:List<String> = emptyList()
)