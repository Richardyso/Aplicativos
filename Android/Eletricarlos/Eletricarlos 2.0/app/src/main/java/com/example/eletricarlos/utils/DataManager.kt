package com.example.eletricarlos.utils

import android.util.Log
import com.example.eletricarlos.models.Entry
import com.example.eletricarlos.models.FormData
import com.google.firebase.firestore.FirebaseFirestore

/**
 * DataManager que usa Firestore como backend.
 * Coleção: dados
 * Documento: {localName}_{type}
 * Campos: entries (array de {numero, data, observacao})
 */
class DataManager {
    
    private val TAG = "DataManager"
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "dados"
    
    /**
     * Salva dados no Firestore (upload para o banco de dados)
     */
    fun saveFormData(formData: FormData, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val docId = "${formData.localName}_${formData.type}"
        val data = hashMapOf(
            "entries" to formData.entries.map { entry ->
                hashMapOf(
                    "numero" to entry.numero,
                    "data" to entry.data,
                    "observacao" to entry.observacao
                )
            }
        )
        
        db.collection(collectionName).document(docId)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "✓ Dados salvos no Firestore: $docId (${formData.entries.size} entradas)")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro ao salvar no Firestore: ${e.message}")
                onError(e.message ?: "Erro ao salvar")
            }
    }
    
    /**
     * Carrega dados do Firestore
     */
    fun loadFormData(localName: String, type: String, onResult: (FormData?) -> Unit) {
        val docId = "${localName}_${type}"
        
        db.collection(collectionName).document(docId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val formData = FormData(localName, type)
                    @Suppress("UNCHECKED_CAST")
                    val entriesList = document.get("entries") as? List<Map<String, Any>>
                    
                    entriesList?.forEach { entryMap ->
                        val entry = Entry(
                            numero = (entryMap["numero"] as? String) ?: "",
                            data = (entryMap["data"] as? String) ?: "",
                            observacao = (entryMap["observacao"] as? String) ?: ""
                        )
                        formData.entries.add(entry)
                    }
                    
                    Log.d(TAG, "✓ Dados carregados do Firestore: $docId (${formData.entries.size} entradas)")
                    onResult(formData)
                } else {
                    Log.d(TAG, "Documento não encontrado: $docId")
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro ao carregar do Firestore: ${e.message}")
                onResult(null)
            }
    }
}
