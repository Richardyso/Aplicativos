package com.example

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var edtNome: EditText
    private lateinit var edtCpfCnpj: EditText
    private lateinit var edtTelefone: EditText
    private lateinit var btnEntrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefs = getSharedPreferences("BRASRIO_PREFS", MODE_PRIVATE)
        
        // Carregar produtos do JSON
        ProductManager.loadProducts(this)
        
        // Carregar serviços (fretes e instaladores)
        ServiceManager.loadServices(this)

        // Pular login se já tiver dados
        if (prefs.contains("nome") && prefs.contains("cpfCnpj") && prefs.contains("telefone")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        initializeViews()
        setupValidators()
        setupClickListeners()
    }

    private fun initializeViews() {
        edtNome = findViewById(R.id.edtNome)
        edtCpfCnpj = findViewById(R.id.edtCpfCnpj)
        edtTelefone = findViewById(R.id.edtTelefone)
        btnEntrar = findViewById(R.id.btnEntrar)
    }

    private fun setupValidators() {
        // Aplicar validadores aos campos - Versão simples sem formatação automática
        edtNome.addTextChangedListener(SimpleNameTextWatcher(edtNome))
        edtCpfCnpj.addTextChangedListener(SimpleCPFCNPJTextWatcher(edtCpfCnpj))
        edtTelefone.addTextChangedListener(SimplePhoneTextWatcher(edtTelefone))
    }

    private fun setupClickListeners() {
        btnEntrar.setOnClickListener {
            hideKeyboard()
            validateAndProceed()
        }

        // Esconder teclado ao clicar fora dos campos
        findViewById<View>(android.R.id.content).setOnClickListener {
            hideKeyboard()
        }
    }

    private fun validateAndProceed() {
        val nome = edtNome.text.toString().trim()
        val cpfCnpj = edtCpfCnpj.text.toString().trim()
        val telefone = edtTelefone.text.toString().trim()

        // Validações
        when {
            nome.isEmpty() -> {
                edtNome.error = "Nome é obrigatório"
                edtNome.requestFocus()
                return
            }
            !Validators.isValidName(nome) -> {
                edtNome.error = "Nome deve conter apenas letras"
                edtNome.requestFocus()
                return
            }
            cpfCnpj.isEmpty() -> {
                edtCpfCnpj.error = "CPF/CNPJ é obrigatório"
                edtCpfCnpj.requestFocus()
                return
            }
            !Validators.isValidCPForCNPJ(cpfCnpj) -> {
                edtCpfCnpj.error = "CPF/CNPJ inválido"
                edtCpfCnpj.requestFocus()
                return
            }
            telefone.isEmpty() -> {
                edtTelefone.error = "Telefone é obrigatório"
                edtTelefone.requestFocus()
                return
            }
            !Validators.isValidPhone(telefone) -> {
                edtTelefone.error = "Telefone inválido"
                edtTelefone.requestFocus()
                return
            }
        }

        // Salvar dados
        prefs.edit()
            .putString("nome", nome)
            .putString("cpfCnpj", cpfCnpj)
            .putString("telefone", telefone)
            .apply()

        // Mostrar mensagem de sucesso
        Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show()

        // Ir para próxima tela
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = currentFocus
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
