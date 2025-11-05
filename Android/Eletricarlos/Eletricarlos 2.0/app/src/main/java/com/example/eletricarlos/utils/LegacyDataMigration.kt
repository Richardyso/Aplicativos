package com.example.eletricarlos.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.eletricarlos.models.Entry
import com.example.eletricarlos.models.FormData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Classe responsável por migrar dados do aplicativo antigo (v1.0) para o novo (v2.0)
 * 
 * Mapeamento:
 * App Antigo -> App Novo
 * - botao1 = Pousada Paraíso
 * - botao2 = Dorys Prime
 * - botao3 = Hotel JR
 * - botao4 = Hotel Guarany
 * 
 * Tipos:
 * - MpActivityData = Manutenção Preventiva
 * - McActivityData = Manutenção Corretiva
 * - ObsActivityData = Observação
 */
class LegacyDataMigration(private val context: Context) {
    
    private val legacyPrefs = context.getSharedPreferences("legacy_migration", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // Estrutura de dados antiga
    data class LegacyRowData(
        val number: String,
        val date: String,
        val observation: String
    )
    
    // Mapeamento de botões antigos para locais novos
    private val locationMapping = mapOf(
        "botao1" to "Pousada Paraíso",
        "botao2" to "Dorys Prime",
        "botao3" to "Hotel JR",
        "botao4" to "Hotel Guarany"
    )
    
    // Mapeamento de tipos de atividade
    private val typeMapping = mapOf(
        "Mp" to "Manutenção Preventiva",
        "Mc" to "Manutenção Corretiva",
        "Obs" to "Observação"
    )
    
    /**
     * Verifica se a migração já foi executada
     */
    fun isMigrationCompleted(): Boolean {
        return legacyPrefs.getBoolean("migration_completed", false)
    }
    
    /**
     * Marca a migração como completa
     */
    private fun markMigrationCompleted() {
        legacyPrefs.edit().putBoolean("migration_completed", true).apply()
    }
    
    /**
     * Migra todos os dados do app antigo para o novo formato
     */
    fun migrateAllData(): Int {
        if (isMigrationCompleted()) {
            return 0 // Já migrado
        }
        
        var migratedCount = 0
        
        // Para cada local (botao1, botao2, botao3, botao4)
        for ((oldButtonId, newLocalName) in locationMapping) {
            // Para cada tipo (Mp, Mc, Obs)
            for ((oldType, newType) in typeMapping) {
                val migrated = migrateLegacyData(oldButtonId, oldType, newLocalName, newType)
                if (migrated) {
                    migratedCount++
                }
            }
        }
        
        markMigrationCompleted()
        return migratedCount
    }
    
    /**
     * Migra dados de uma combinação específica de local e tipo
     */
    private fun migrateLegacyData(
        oldButtonId: String,
        oldType: String,
        newLocalName: String,
        newType: String
    ): Boolean {
        // Nome do SharedPreferences antigo
        val legacyPrefName = "${oldType}ActivityData_$oldButtonId"
        
        val oldPrefs = context.getSharedPreferences(legacyPrefName, Context.MODE_PRIVATE)
        val jsonString = oldPrefs.getString("rowDataList", null) ?: return false
        
        try {
            // Ler dados antigos
            val type = object : TypeToken<List<LegacyRowData>>() {}.type
            val legacyDataList: List<LegacyRowData> = gson.fromJson(jsonString, type)
            
            if (legacyDataList.isEmpty()) return false
            
            // Converter para novo formato
            val newEntries = legacyDataList.map { legacyData ->
                Entry(
                    numero = legacyData.number,
                    data = legacyData.date,
                    observacao = legacyData.observation
                )
            }
            
            // Salvar no novo formato usando DataManager
            val dataManager = DataManager(context)
            val formData = FormData(newLocalName, newType)
            formData.entries.addAll(newEntries)
            dataManager.saveFormData(formData) // Salva localmente em JSON
            
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Limpa os dados antigos (use com cuidado!)
     * Deve ser chamado apenas após confirmar que a migração foi bem-sucedida
     */
    fun cleanupLegacyData() {
        for ((oldButtonId, _) in locationMapping) {
            for ((oldType, _) in typeMapping) {
                val legacyPrefName = "${oldType}ActivityData_$oldButtonId"
                try {
                    val oldPrefs = context.getSharedPreferences(legacyPrefName, Context.MODE_PRIVATE)
                    oldPrefs.edit().clear().apply()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    /**
     * Obtém relatório de dados disponíveis para migração
     */
    fun getMigrationReport(): String {
        val report = StringBuilder()
        report.appendLine("=== Relatório de Migração Eletricarlos v1.0 -> v2.0 ===\n")
        
        var totalDataSets = 0
        var totalEntries = 0
        
        for ((oldButtonId, newLocalName) in locationMapping) {
            for ((oldType, newType) in typeMapping) {
                val legacyPrefName = "${oldType}ActivityData_$oldButtonId"
                val oldPrefs = context.getSharedPreferences(legacyPrefName, Context.MODE_PRIVATE)
                val jsonString = oldPrefs.getString("rowDataList", null)
                
                if (jsonString != null) {
                    try {
                        val type = object : TypeToken<List<LegacyRowData>>() {}.type
                        val legacyDataList: List<LegacyRowData> = gson.fromJson(jsonString, type)
                        if (legacyDataList.isNotEmpty()) {
                            report.appendLine("✓ $newLocalName - $newType: ${legacyDataList.size} entradas")
                            totalDataSets++
                            totalEntries += legacyDataList.size
                        }
                    } catch (e: Exception) {
                        report.appendLine("✗ Erro ao ler: $newLocalName - $newType")
                    }
                }
            }
        }
        
        report.appendLine("\nTotal: $totalDataSets conjuntos de dados com $totalEntries entradas")
        report.appendLine("Status: ${if (isMigrationCompleted()) "✓ Migração concluída" else "⚠ Migração pendente"}")
        
        return report.toString()
    }
}

