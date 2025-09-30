package com.example.eletricarlos

import java.io.Serializable

data class RowData(
    val number: String,
    val date: String,
    val observation: String
) : Serializable