package com.example

object Validators {
    
    fun isValidName(name: String): Boolean {
        return name.matches(Regex("^[a-zA-ZÀ-ÿ\\s]+$")) && name.length >= 2
    }
    
    fun isValidCPF(cpf: String): Boolean {
        val cleanCpf = cpf.replace(Regex("[^0-9]"), "")
        if (cleanCpf.length != 11) return false
        
        // Verifica se todos os dígitos são iguais
        if (cleanCpf.all { it == cleanCpf[0] }) return false
        
        // Validação do primeiro dígito verificador
        var sum = 0
        for (i in 0..8) {
            sum += Character.getNumericValue(cleanCpf[i]) * (10 - i)
        }
        var digit1 = 11 - (sum % 11)
        if (digit1 >= 10) digit1 = 0
        
        if (Character.getNumericValue(cleanCpf[9]) != digit1) return false
        
        // Validação do segundo dígito verificador
        sum = 0
        for (i in 0..9) {
            sum += Character.getNumericValue(cleanCpf[i]) * (11 - i)
        }
        var digit2 = 11 - (sum % 11)
        if (digit2 >= 10) digit2 = 0
        
        return Character.getNumericValue(cleanCpf[10]) == digit2
    }
    
    fun isValidCNPJ(cnpj: String): Boolean {
        val cleanCnpj = cnpj.replace(Regex("[^0-9]"), "")
        if (cleanCnpj.length != 14) return false
        
        // Verifica se todos os dígitos são iguais
        if (cleanCnpj.all { it == cleanCnpj[0] }) return false
        
        // Validação do primeiro dígito verificador
        val weights1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        var sum = 0
        for (i in 0..11) {
            sum += Character.getNumericValue(cleanCnpj[i]) * weights1[i]
        }
        var digit1 = sum % 11
        digit1 = if (digit1 < 2) 0 else 11 - digit1
        
        if (Character.getNumericValue(cleanCnpj[12]) != digit1) return false
        
        // Validação do segundo dígito verificador
        val weights2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        sum = 0
        for (i in 0..12) {
            sum += Character.getNumericValue(cleanCnpj[i]) * weights2[i]
        }
        var digit2 = sum % 11
        digit2 = if (digit2 < 2) 0 else 11 - digit2
        
        return Character.getNumericValue(cleanCnpj[13]) == digit2
    }
    
    fun isValidCPForCNPJ(cpfCnpj: String): Boolean {
        val clean = cpfCnpj.replace(Regex("[^0-9]"), "")
        return when (clean.length) {
            11 -> isValidCPF(cpfCnpj)
            14 -> isValidCNPJ(cpfCnpj)
            else -> false
        }
    }
    
    fun isValidPhone(phone: String): Boolean {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        return cleanPhone.length >= 10 && cleanPhone.length <= 11
    }
}
