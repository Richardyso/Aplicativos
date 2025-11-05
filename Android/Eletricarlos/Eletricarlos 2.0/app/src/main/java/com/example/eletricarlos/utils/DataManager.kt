package com.example.eletricarlos.utils

import android.content.Context
import android.util.Log
import com.example.eletricarlos.models.Entry
import com.example.eletricarlos.models.FormData
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class DataManager(private val context: Context) {
    
    private val TAG = "DataManager"
    private val dataDir = File(context.filesDir, "eletricarlos_data")
    
    init {
        // Criar diretório de dados se não existir
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
    }
    
    /**
     * Salva dados localmente em arquivo JSON
     */
    fun saveFormData(formData: FormData) {
        val key = "${formData.localName}_${formData.type}"
        val jsonFile = File(dataDir, "$key.json")
        
        try {
            // Criar JSON com os dados fornecidos
            val jsonObject = JSONObject()
            jsonObject.put("localName", formData.localName)
            jsonObject.put("type", formData.type)
            
            val entriesArray = JSONArray()
            for (entry in formData.entries) {
                val entryObject = JSONObject()
                entryObject.put("numero", entry.numero)
                entryObject.put("data", entry.data)
                entryObject.put("observacao", entry.observacao)
                entriesArray.put(entryObject)
            }
            jsonObject.put("entries", entriesArray)
            
            // Salvar em arquivo JSON
            jsonFile.writeText(jsonObject.toString(2)) // Indentação para facilitar leitura
            Log.d(TAG, "✓ Dados salvos em: ${jsonFile.name} (${formData.entries.size} entradas)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar dados: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Carrega dados do arquivo JSON local
     */
    fun loadFormData(localName: String, type: String): FormData? {
        val key = "${localName}_${type}"
        val jsonFile = File(dataDir, "$key.json")
        
        if (!jsonFile.exists()) {
            Log.d(TAG, "Arquivo não encontrado: ${jsonFile.name}")
            return null
        }
        
        try {
            val jsonString = jsonFile.readText()
            val jsonObject = JSONObject(jsonString)
            
            val formData = FormData(
                localName = jsonObject.getString("localName"),
                type = jsonObject.getString("type")
            )
            
            val entriesArray = jsonObject.getJSONArray("entries")
            for (i in 0 until entriesArray.length()) {
                val entryObject = entriesArray.getJSONObject(i)
                val entry = Entry(
                    numero = entryObject.getString("numero"),
                    data = entryObject.getString("data"),
                    observacao = entryObject.getString("observacao")
                )
                formData.entries.add(entry)
            }
            
            Log.d(TAG, "✓ Dados carregados: ${jsonFile.name} (${formData.entries.size} entradas)")
            return formData
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar dados: ${e.message}")
            return null
        }
    }
    
    /**
     * Lista todos os arquivos de dados salvos
     */
    fun listAllData(): List<String> {
        val files = dataDir.listFiles { file -> file.extension == "json" }
        return files?.map { it.nameWithoutExtension } ?: emptyList()
    }
    
    /**
     * Obtém o diretório onde os dados são salvos
     */
    fun getDataDirectory(): File {
        return dataDir
    }
}

