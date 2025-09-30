package com.example

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class SimpleNameTextWatcher(private val editText: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Remove caracteres especiais e números, mantém apenas letras e espaços
        val cleanText = s?.toString()?.replace(Regex("[^a-zA-ZÀ-ÿ\\s]"), "") ?: ""
        if (s.toString() != cleanText) {
            editText.setText(cleanText)
            editText.setSelection(cleanText.length)
        }
    }
    
    override fun afterTextChanged(s: Editable?) {
        editText.error = null
    }
}

class SimpleCPFCNPJTextWatcher(private val editText: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Apenas remove caracteres não numéricos, sem formatação automática
        val cleanText = s?.toString()?.replace(Regex("[^0-9]"), "") ?: ""
        if (s.toString() != cleanText) {
            editText.setText(cleanText)
            editText.setSelection(cleanText.length)
        }
    }
    
    override fun afterTextChanged(s: Editable?) {
        editText.error = null
    }
}

class SimplePhoneTextWatcher(private val editText: EditText) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Apenas remove caracteres não numéricos, sem formatação automática
        val cleanText = s?.toString()?.replace(Regex("[^0-9]"), "") ?: ""
        if (s.toString() != cleanText) {
            editText.setText(cleanText)
            editText.setSelection(cleanText.length)
        }
    }
    
    override fun afterTextChanged(s: Editable?) {
        editText.error = null
    }
}
