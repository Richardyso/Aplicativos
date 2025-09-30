package com.example

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ResultadoActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var material: String
    private var subtype: String? = null
    private var m2: Double = 0.0
    private var peDireito: Double? = null
    private lateinit var placa: String
    private lateinit var cor: String
    private var janelas: Int = 0
    private var portas: Int = 0
    private lateinit var materiaisSelecionados: List<MaterialItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        prefs = getSharedPreferences("BRASRIO_PREFS", MODE_PRIVATE)
        
        getIntentData()
        initializeViews()
        setupClickListeners()
        processarResultado()
    }

    private fun getIntentData() {
        material = intent.getStringExtra("material") ?: ""
        subtype = intent.getStringExtra("subtype")
        m2 = intent.getDoubleExtra("m2", 0.0)
        peDireito = if (intent.hasExtra("peDireito")) intent.getDoubleExtra("peDireito", 0.0) else null
        placa = intent.getStringExtra("placa") ?: ""
        cor = intent.getStringExtra("cor") ?: ""
        janelas = intent.getIntExtra("janelas", 0)
        portas = intent.getIntExtra("portas", 0)
    }

    private fun initializeViews() {
        // Views serão inicializadas no setupClickListeners
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnNovoOrcamento).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        findViewById<Button>(R.id.btnFazerPedidoWhatsApp).setOnClickListener {
            fazerPedidoWhatsApp()
        }
    }

    private fun processarResultado() {
        val tvMaterialInfo = findViewById<TextView>(R.id.tvMaterialInfo)
        
        // Monta informações do material
        val infoText = buildString {
            if (material.isNotEmpty() && subtype != null) {
                append("Material: $material - $subtype")
            } else if (material.isNotEmpty()) {
                append("Material: $material")
            }
            
            // Adiciona informações específicas dependendo do tipo
            if ((material == "Drywall" && subtype == "Parede" && peDireito != null) || 
                (material == "Cimenticia" && subtype == "Parede" && peDireito != null)) {
                val comprimento = m2 / peDireito!!
                append(" | ${m2}m² - Pé direito: ${peDireito}m (Comprimento: ${String.format("%.2f", comprimento)}m)")
            } else {
                append(" | Metragem: ${m2}m²")
            }
        }
        
        tvMaterialInfo.text = infoText
        
        try {
            // Calcula materiais baseado no tipo
            materiaisSelecionados = when {
                material == "Drywall" && subtype == "Parede" && peDireito != null -> {
                    val calculator = ParedeCalculator(m2, peDireito!!, placa)
                    calculator.calcularMateriais()
                }
                material == "Cimenticia" && subtype == "Parede" && peDireito != null -> {
                    val calculator = CimenticiaCalculator(m2, peDireito!!, placa, "parede")
                    calculator.calcularMateriais()
                }
                material == "Cimenticia" && subtype == "Teto" -> {
                    val calculator = CimenticiaCalculator(m2, 2.7, null, "teto")
                    calculator.calcularMateriais()
                }
                material == "PVC" && subtype == "Regua" -> {
                    val calculator = PVCCalculator(m2, placa)
                    calculator.calcularMateriais()
                }
                material == "Forro Mineral" -> {
                    val calculator = ForroMineralCalculator(m2, placa)
                    calculator.calcularMateriais()
                }
                material == "Forro Boreal" -> {
                    val calculator = ForroBorealCalculator(m2, placa)
                    calculator.calcularMateriais()
                }
                material == "Isopor" -> {
                    val calculator = IsoporCalculator(m2, placa)
                    calculator.calcularMateriais()
                }
                material == "Painel" -> {
                    val calculator = PainelCalculator(m2, portas)
                    calculator.calcularMateriais()
                }
                material == "Piso" && subtype == "Vinílico" -> {
                    val calculator = PisoCalculator(m2, "Vinílico", cor)
                    calculator.calcularMateriais()
                }
                material == "Piso" && subtype == "Laminado" -> {
                    val calculator = PisoCalculator(m2, "Laminado", cor)
                    calculator.calcularMateriais()
                }
                else -> {
                    // Para outros materiais, usar cálculo genérico
                    calcularMateriaisGenerico()
                }
            }
        } catch (error: Exception) {
            Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            return
        }
        
        mostrarResultado()
    }

    private fun calcularMateriaisGenerico(): List<MaterialItem> {
        // Implementação genérica para materiais não cobertos pelas classes específicas
        return listOf(
            MaterialItem("000", "Material genérico", kotlin.math.ceil(m2).toInt())
        )
    }

    private fun mostrarResultado() {
        val tvResultContent = findViewById<TextView>(R.id.tvResultContent)
        
        val html = buildString {
            append("Lista de materiais:\n\n")
            materiaisSelecionados.forEach { mat ->
                append("• [${mat.codigo}] ${mat.quantidade}x ${mat.nome}\n")
            }
            
            append("\n")
            append("Total de itens: ${materiaisSelecionados.size}\n\n")
            append("⚠️ AVISO IMPORTANTE:\n")
            append("Este cálculo é apenas uma estimativa e não considera características específicas do local de instalação nem possíveis perdas.\n")
            append("Utilize-o apenas como referência. Para informações precisas, recomenda-se consultar um profissional de confiança.")
        }

        tvResultContent.text = html
    }

    private fun fazerPedidoWhatsApp() {
        if (materiaisSelecionados.isEmpty()) {
            Toast.makeText(this, "Nenhum material selecionado!", Toast.LENGTH_SHORT).show()
            return
        }

        // Obter dados do usuário
        val nome = prefs.getString("nome", "")
        val cpfCnpj = prefs.getString("cpfCnpj", "")
        val telefone = prefs.getString("telefone", "")

        val dataAtual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())

        val mensagem = buildString {
            append("*PEDIDO DE COMPRA APP - BRASRIO*\n\n")
            
            // Dados do cliente
            append("*Dados do Cliente:*\n")
            append("Nome: $nome\n")
            append("CPF/CNPJ: $cpfCnpj\n")
            append("Telefone: $telefone\n\n")
            
            // Informações do orçamento
            append("*Informações do Orçamento:*\n")
            if (material.isNotEmpty() && subtype != null) {
                append("Material: $material - $subtype\n")
            } else if (material.isNotEmpty()) {
                append("Material: $material\n")
            }
            
            if ((material == "Drywall" && subtype == "Parede" && peDireito != null) || 
                (material == "Cimenticia" && subtype == "Parede" && peDireito != null)) {
                val comprimento = m2 / peDireito!!
                append("Metragem: ${m2}m² - Pé direito: ${peDireito}m (Comprimento: ${String.format("%.2f", comprimento)}m)\n")
            } else {
                append("Metragem: ${m2}m²\n")
            }
            
            if (janelas > 0) append("Janelas: $janelas\n")
            if (portas > 0) append("Portas: $portas\n")
            append("Data: $dataAtual\n\n")
            
            // Lista de materiais
            append("*Materiais Solicitados:*\n")
            materiaisSelecionados.forEach { mat ->
                append("• [${mat.codigo}] ${mat.quantidade}x ${mat.nome}\n")
            }

            append("\n*Total de itens:* ${materiaisSelecionados.size}\n\n")
            append("*Observação:* Este é um cálculo estimado, para maior precisão contatar um profissional de confiança.")
        }

        val numeroWhatsApp = "5521971252304"
        val url = "https://wa.me/$numeroWhatsApp?text=${Uri.encode(mensagem)}"
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
