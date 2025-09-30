package com.example.eletricarlos

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MpActivity : AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var addButton: Button
    private lateinit var saveButton: Button
    private lateinit var removeButton: Button
    private val rowDataList = mutableListOf<RowData>()
    private lateinit var buttonId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mp)

        buttonId = intent.getStringExtra("buttonId") ?: "default"

        tableLayout = findViewById(R.id.tableLayout)
        addButton = findViewById(R.id.addButton)
        saveButton = findViewById(R.id.saveButton)
        removeButton = findViewById(R.id.removeButton)

        setupExistingDatePicker()
        loadData()

        addButton.setOnClickListener {
            addNewRow()
        }

        saveButton.setOnClickListener {
            saveData()
        }

        removeButton.setOnClickListener {
            removeLastRow()
        }
    }

    private fun setupDatePicker(dateEditText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dateEditText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dateEditText.isFocusable = false
        dateEditText.isClickable = true
        dateEditText.setOnClickListener {
            datePickerDialog.show()
        }
    }

    private fun setupExistingDatePicker() {
        val dateEditText = tableLayout.getChildAt(1).findViewById<EditText>(R.id.editTextDate)
        dateEditText.isFocusable = false
        dateEditText.isClickable = true
        setupDatePicker(dateEditText)
    }

    private fun addNewRow(rowData: RowData? = null) {
        val tableRow = TableRow(this)
        tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)

        val numberEditText = EditText(this).apply {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.4f)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
            hint = "Nº"
            rowData?.let { setText(it.number) }
        }
        tableRow.addView(numberEditText)

        val dateEditText = EditText(this).apply {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Data"
            isFocusable = false
            isClickable = true
            rowData?.let { setText(it.date) }
        }
        setupDatePicker(dateEditText)
        tableRow.addView(dateEditText)

        val observationEditText = EditText(this).apply {
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f)
            hint = "Observação"
            rowData?.let { setText(it.observation) }
        }
        tableRow.addView(observationEditText)

        tableLayout.addView(tableRow)
    }

    private fun removeLastRow() {
        val rowCount = tableLayout.childCount
        if (rowCount > 2) { // Keep the header row and the first data row
            tableLayout.removeViewAt(rowCount - 1)
        }
    }

    private fun loadData() {
        val sharedPref = getSharedPreferences("MpActivityData_$buttonId", MODE_PRIVATE)
        val jsonString = sharedPref.getString("rowDataList", null)
        if (jsonString != null) {
            val type = object : TypeToken<List<RowData>>() {}.type
            rowDataList.addAll(Gson().fromJson(jsonString, type))
            for (rowData in rowDataList) {
                addNewRow(rowData)
            }
        }
    }

    private fun saveData() {
        rowDataList.clear()
        for (i in 1 until tableLayout.childCount) {
            val tableRow = tableLayout.getChildAt(i) as TableRow
            val numberEditText = tableRow.getChildAt(0) as EditText
            val dateEditText = tableRow.getChildAt(1) as EditText
            val observationEditText = tableRow.getChildAt(2) as EditText

            val rowData = RowData(
                numberEditText.text.toString(),
                dateEditText.text.toString(),
                observationEditText.text.toString()
            )
            rowDataList.add(rowData)
        }

        val jsonString = Gson().toJson(rowDataList)
        val sharedPref = getSharedPreferences("MpActivityData_$buttonId", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("rowDataList", jsonString)
            apply()
        }
        Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show()
    }
}

