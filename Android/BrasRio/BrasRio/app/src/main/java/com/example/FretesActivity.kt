package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FretesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fretes)

        initializeViews()
        setupClickListeners()
        loadFretesData()
    }

    private fun initializeViews() {
        // Views serão inicializadas no setupClickListeners
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
        
        findViewById<Button>(R.id.btnContatarWhatsApp).setOnClickListener {
            contatarWhatsApp()
        }
    }

    private fun loadFretesData() {
        val tvFretesContent = findViewById<TextView>(R.id.tvFretesContent)
        val fretes = ServiceManager.getAllFretes()
        
        val html = buildString {
            append("Nossos Parceiros de Frete:\n\n")
            fretes.forEachIndexed { index, frete ->
                append("${index + 1}. ${frete.nome}\n")
                append("   Telefone: ${frete.telefone}\n")
                append("   Área: ${frete.areaAtendimento}\n")
                append("   Peso Máximo: ${frete.pesoMaximo}\n")
                append("   [Contatar via WhatsApp]\n\n")
            }
            append("Informações Importantes:\n")
            append("• Valores podem variar conforme peso e distância\n")
            append("• Entrega em até 24h para área urbana\n")
            append("• Atendimento de segunda a sábado")
        }
        
        tvFretesContent.text = html
        
        // Criar botões individuais para cada frete
        createFreteButtons()
    }
    
    private fun createFreteButtons() {
        val fretes = ServiceManager.getAllFretes()
        val buttonContainer = findViewById<LinearLayout>(R.id.buttonContainer)
        
        // Limpar botões existentes (exceto os fixos)
        val buttonsToRemove = mutableListOf<View>()
        for (i in 0 until buttonContainer.childCount) {
            val child = buttonContainer.getChildAt(i)
            if (child is Button && child.id != R.id.btnVoltar) {
                buttonsToRemove.add(child)
            }
        }
        buttonsToRemove.forEach { buttonContainer.removeView(it) }
        
        // Criar botão para cada frete
        fretes.forEach { frete ->
            val button = Button(this).apply {
                text = "Contatar ${frete.nome}"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }
                setBackgroundResource(R.drawable.button_primary)
                setTextColor(resources.getColor(android.R.color.white, null))
                setOnClickListener {
                    contatarFreteWhatsApp(frete.numeroWhatsApp)
                }
            }
            buttonContainer.addView(button, buttonContainer.childCount - 1) // Adicionar antes do botão Voltar
        }
    }

    private fun contatarWhatsApp() {
        // Esta função não será mais usada, pois cada frete terá seu próprio botão
        // Mantida apenas para compatibilidade
    }
    
    fun contatarFreteWhatsApp(numeroWhatsApp: String) {
        val url = "https://wa.me/55$numeroWhatsApp"
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
