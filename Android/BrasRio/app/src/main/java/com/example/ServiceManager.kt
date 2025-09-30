package com.example

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Frete(
    val nome: String,
    val telefone: String,
    val numeroWhatsApp: String,
    val areaAtendimento: String,
    val pesoMaximo: String
)

data class Instalador(
    val nome: String,
    val telefone: String,
    val numeroWhatsApp: String,
    val especializacao: String
)

object ServiceManager {
    private var fretes: List<Frete> = emptyList()
    private var instaladores: List<Instalador> = emptyList()
    
    fun loadServices(context: Context) {
        loadFretes(context)
        loadInstaladores(context)
    }
    
    private fun loadFretes(context: Context) {
        try {
            val jsonString = context.assets.open("fretes.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("fretes")
            
            fretes = (0 until jsonArray.length()).map { i ->
                val item = jsonArray.getJSONObject(i)
                Frete(
                    nome = item.getString("nome"),
                    telefone = item.getString("telefone"),
                    numeroWhatsApp = item.getString("numeroWhatsApp"),
                    areaAtendimento = item.getString("areaAtendimento"),
                    pesoMaximo = item.getString("pesoMaximo")
                )
            }
            
            android.util.Log.d("ServiceManager", "Carregados ${fretes.size} fretes")
        } catch (e: Exception) {
            android.util.Log.e("ServiceManager", "Erro ao carregar fretes: ${e.message}")
            fretes = emptyList()
        }
    }
    
    private fun loadInstaladores(context: Context) {
        try {
            val jsonString = context.assets.open("instaladores.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray = jsonObject.getJSONArray("instaladores")
            
            instaladores = (0 until jsonArray.length()).map { i ->
                val item = jsonArray.getJSONObject(i)
                Instalador(
                    nome = item.getString("nome"),
                    telefone = item.getString("telefone"),
                    numeroWhatsApp = item.getString("numeroWhatsApp"),
                    especializacao = item.getString("especializacao")
                )
            }
            
            android.util.Log.d("ServiceManager", "Carregados ${instaladores.size} instaladores")
        } catch (e: Exception) {
            android.util.Log.e("ServiceManager", "Erro ao carregar instaladores: ${e.message}")
            instaladores = emptyList()
        }
    }
    
    fun getAllFretes(): List<Frete> = fretes
    
    fun getAllInstaladores(): List<Instalador> = instaladores
    
    fun getFretesByArea(area: String): List<Frete> {
        return fretes.filter { it.areaAtendimento.contains(area, ignoreCase = true) }
    }
    
    fun getInstaladoresBySpecialization(especializacao: String): List<Instalador> {
        return instaladores.filter { it.especializacao.contains(especializacao, ignoreCase = true) }
    }
}
