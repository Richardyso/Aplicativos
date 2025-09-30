package com.example

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Product(
    val codigo: String,
    val nome: String,
    val valor: String
)

object ProductManager {
    private var products: List<Product> = emptyList()
    
    fun loadProducts(context: Context) {
        try {
            // Carregar arquivo principal com todos os itens
            val jsonString = context.assets.open("todos_itens.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            
            products = (0 until jsonArray.length()).map { i ->
                val itemArray = jsonArray.getJSONArray(i)
                Product(
                    codigo = itemArray.getString(0),
                    nome = itemArray.getString(1),
                    valor = "" // O arquivo não tem valor
                )
            }
            
            // Debug: verificar se carregou produtos
            android.util.Log.d("ProductManager", "Carregados ${products.size} produtos do arquivo todos_itens.json")
            if (products.isNotEmpty()) {
                android.util.Log.d("ProductManager", "Primeiro produto: ${products.first().codigo} - ${products.first().nome}")
            }
        } catch (e: Exception) {
            android.util.Log.e("ProductManager", "Erro ao carregar produtos: ${e.message}")
            products = emptyList()
        }
    }
    
    fun getProductNameByCode(code: String): String {
        val product = products.find { it.codigo == code }
        val nome = product?.nome ?: "Produto $code"
        android.util.Log.d("ProductManager", "Buscando código $code, encontrado: $nome")
        return nome
    }
    
    fun getAllProducts(): List<Product> = products
}
