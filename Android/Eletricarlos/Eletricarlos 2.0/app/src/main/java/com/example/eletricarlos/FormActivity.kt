package com.example.eletricarlos

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eletricarlos.models.Entry
import com.example.eletricarlos.models.FormData
import com.example.eletricarlos.utils.DataManager
import java.util.Calendar

class FormActivity : AppCompatActivity() {
    
    private lateinit var containerEntries: LinearLayout
    private lateinit var dataManager: DataManager
    private lateinit var localName: String
    private lateinit var type: String
    private var canEdit: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        
        supportActionBar?.hide()
        
        localName = intent.getStringExtra("LOCAL_NAME") ?: ""
        type = intent.getStringExtra("TYPE") ?: ""
        canEdit = intent.getBooleanExtra("CAN_EDIT", false)
        
        containerEntries = findViewById(R.id.containerEntries)
        dataManager = DataManager(this)
        
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnRemove = findViewById<Button>(R.id.btnRemove)
        val btnSave = findViewById<Button>(R.id.btnSave)
        
        // Load saved data or initialize with 1 empty entry
        loadData()
        
        // Configure buttons based on edit permissions
        if (canEdit) {
            btnAdd.setOnClickListener {
                addEntry()
            }
            
            btnRemove.setOnClickListener {
                if (containerEntries.childCount > 1) {
                    containerEntries.removeViewAt(containerEntries.childCount - 1)
                } else {
                    Toast.makeText(this, "Deve haver pelo menos uma entrada", Toast.LENGTH_SHORT).show()
                }
            }
            
            btnSave.setOnClickListener {
                saveData()
            }
        } else {
            // View only mode - disable editing
            btnAdd.visibility = View.GONE
            btnRemove.visibility = View.GONE
            btnSave.visibility = View.GONE
        }
    }
    
    private fun loadData() {
        val savedData = dataManager.loadFormData(localName, type)
        
        if (savedData != null && savedData.entries.isNotEmpty()) {
            // Load saved entries
            for (entry in savedData.entries) {
                addEntry(entry)
            }
        } else {
            // Initialize with 1 empty entry
            addEntry()
        }
    }
    
    private fun addEntry(entry: Entry? = null) {
        val inflater = LayoutInflater.from(this)
        val entryView = inflater.inflate(R.layout.entry_item, containerEntries, false)
        
        val etNumero = entryView.findViewById<EditText>(R.id.etNumero)
        val etData = entryView.findViewById<EditText>(R.id.etData)
        val etObservacao = entryView.findViewById<EditText>(R.id.etObservacao)
        
        // If entry data is provided, populate the fields
        if (entry != null) {
            etNumero.setText(entry.numero)
            etData.setText(entry.data)
            etObservacao.setText(entry.observacao)
        }
        
        // Configure based on edit permissions
        if (canEdit) {
            // Configure date picker for the date field
            setupDatePicker(etData)
        } else {
            // View only mode - make fields non-editable
            etNumero.isEnabled = false
            etData.isEnabled = false
            etObservacao.isEnabled = false
        }
        
        containerEntries.addView(entryView)
    }
    
    private fun setupDatePicker(etData: EditText) {
        // Make the field not editable by keyboard, only by date picker
        etData.isFocusable = false
        etData.isClickable = true
        
        etData.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format the date as DD/MM/YYYY
                    val formattedDate = String.format(
                        "%02d/%02d/%04d",
                        selectedDay,
                        selectedMonth + 1, // Month is 0-based
                        selectedYear
                    )
                    etData.setText(formattedDate)
                },
                year,
                month,
                day
            )
            
            datePickerDialog.show()
        }
    }
    
    private fun saveData() {
        val formData = FormData(localName, type)
        
        // Collect data from all entry views (incluindo vazias)
        for (i in 0 until containerEntries.childCount) {
            val entryView = containerEntries.getChildAt(i)
            val etNumero = entryView.findViewById<EditText>(R.id.etNumero)
            val etData = entryView.findViewById<EditText>(R.id.etData)
            val etObservacao = entryView.findViewById<EditText>(R.id.etObservacao)
            
            val entry = Entry(
                numero = etNumero.text.toString(),
                data = etData.text.toString(),
                observacao = etObservacao.text.toString()
            )
            formData.entries.add(entry)
        }
        
        // Salvar sempre, mesmo com entradas vazias
        dataManager.saveFormData(formData)
        Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show()
        // Don't finish - stay on the form
    }
}

