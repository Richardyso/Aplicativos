package com.example.eletricarlos.models

data class FormData(
    val localName: String,
    val type: String,
    val entries: MutableList<Entry> = mutableListOf()
)

