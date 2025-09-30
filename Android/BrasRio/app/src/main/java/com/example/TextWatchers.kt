package com.example

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class NameTextWatcher(private val editText: EditText) : TextWatcher {
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

class CPFCNPJTextWatcher(private val editText: EditText) : TextWatcher {
    private var isFormatting = false
    
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isFormatting) return
        
        val currentText = s?.toString() ?: ""
        val cleanText = currentText.replace(Regex("[^0-9]"), "")
        
        if (cleanText.isNotEmpty() && cleanText.length <= 14) {
            val formatted = when {
                cleanText.length <= 11 -> formatCPF(cleanText)
                else -> formatCNPJ(cleanText)
            }
            
            if (currentText != formatted) {
                isFormatting = true
                try {
                    editText.removeTextChangedListener(this)
                    editText.setText(formatted)
                    editText.setSelection(formatted.length)
                    editText.addTextChangedListener(this)
                } catch (e: Exception) {
                    // Se der erro, pelo menos adiciona o listener de volta
                    editText.addTextChangedListener(this)
                } finally {
                    isFormatting = false
                }
            }
        }
    }
    
    override fun afterTextChanged(s: Editable?) {
        editText.error = null
    }
    
    private fun formatCPF(cpf: String): String {
        return when {
            cpf.length <= 3 -> cpf
            cpf.length <= 6 -> "${cpf.substring(0, 3)}.${cpf.substring(3)}"
            cpf.length <= 9 -> "${cpf.substring(0, 3)}.${cpf.substring(3, 6)}.${cpf.substring(6)}"
            else -> "${cpf.substring(0, 3)}.${cpf.substring(3, 6)}.${cpf.substring(6, 9)}-${cpf.substring(9, 11)}"
        }
    }
    
    private fun formatCNPJ(cnpj: String): String {
        return when {
            cnpj.length <= 2 -> cnpj
            cnpj.length <= 5 -> "${cnpj.substring(0, 2)}.${cnpj.substring(2)}"
            cnpj.length <= 8 -> "${cnpj.substring(0, 2)}.${cnpj.substring(2, 5)}.${cnpj.substring(5)}"
            cnpj.length <= 12 -> "${cnpj.substring(0, 2)}.${cnpj.substring(2, 5)}.${cnpj.substring(5, 8)}/${cnpj.substring(8)}"
            else -> "${cnpj.substring(0, 2)}.${cnpj.substring(2, 5)}.${cnpj.substring(5, 8)}/${cnpj.substring(8, 12)}-${cnpj.substring(12, 14)}"
        }
    }
}

class PhoneTextWatcher(private val editText: EditText) : TextWatcher {
    private var isFormatting = false
    
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isFormatting) return
        
        val currentText = s?.toString() ?: ""
        val cleanText = currentText.replace(Regex("[^0-9]"), "")
        
        if (cleanText.isNotEmpty() && cleanText.length <= 11) {
            val formatted = formatPhone(cleanText)
            
            if (currentText != formatted) {
                isFormatting = true
                try {
                    editText.removeTextChangedListener(this)
                    editText.setText(formatted)
                    editText.setSelection(formatted.length)
                    editText.addTextChangedListener(this)
                } catch (e: Exception) {
                    // Se der erro, pelo menos adiciona o listener de volta
                    editText.addTextChangedListener(this)
                } finally {
                    isFormatting = false
                }
            }
        }
    }
    
    override fun afterTextChanged(s: Editable?) {
        editText.error = null
    }
    
    private fun formatPhone(phone: String): String {
        return when {
            phone.length <= 2 -> phone
            phone.length <= 6 -> "(${phone.substring(0, 2)}) ${phone.substring(2)}"
            phone.length <= 10 -> "(${phone.substring(0, 2)}) ${phone.substring(2, 6)}-${phone.substring(6)}"
            else -> "(${phone.substring(0, 2)}) ${phone.substring(2, 7)}-${phone.substring(7, 11)}"
        }
    }
}