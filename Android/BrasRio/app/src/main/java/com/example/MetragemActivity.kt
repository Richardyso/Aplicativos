package com.example

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MetragemActivity : AppCompatActivity() {

    private lateinit var material: String
    private var subtype: String? = null
    private lateinit var etMetragem: EditText
    private lateinit var etMetrosQuadrados: EditText
    private lateinit var etPeDireito: EditText
    private lateinit var tvComprimentoCalculado: TextView
    private lateinit var spPlaca: Spinner
    private lateinit var spCorPiso: Spinner
    private lateinit var etQuantidadeJanelas: EditText
    private lateinit var etQuantidadePortas: EditText
    private lateinit var llMetragemSimples: LinearLayout
    private lateinit var llDimensoesParede: LinearLayout
    private lateinit var llPlacaContainer: LinearLayout
    private lateinit var llCorPisoContainer: LinearLayout
    private lateinit var llJanelasContainer: LinearLayout
    private lateinit var llPortasContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metragem)

        material = intent.getStringExtra("material") ?: ""
        subtype = intent.getStringExtra("subtype")
        
        initializeViews()
        setupClickListeners()
        updateUI()
    }

    private fun initializeViews() {
        etMetragem = findViewById(R.id.etMetragem)
        etMetrosQuadrados = findViewById(R.id.etMetrosQuadrados)
        etPeDireito = findViewById(R.id.etPeDireito)
        tvComprimentoCalculado = findViewById(R.id.tvComprimentoCalculado)
        spPlaca = findViewById(R.id.spPlaca)
        spCorPiso = findViewById(R.id.spCorPiso)
        etQuantidadeJanelas = findViewById(R.id.etQuantidadeJanelas)
        etQuantidadePortas = findViewById(R.id.etQuantidadePortas)
        llMetragemSimples = findViewById(R.id.llMetragemSimples)
        llDimensoesParede = findViewById(R.id.llDimensoesParede)
        llPlacaContainer = findViewById(R.id.llPlacaContainer)
        llCorPisoContainer = findViewById(R.id.llCorPisoContainer)
        llJanelasContainer = findViewById(R.id.llJanelasContainer)
        llPortasContainer = findViewById(R.id.llPortasContainer)
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnCalcular).setOnClickListener {
            calcular()
        }
        
        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }

        // Listeners para cálculo automático do comprimento
        etMetrosQuadrados.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                atualizarComprimento()
            }
        })

        etPeDireito.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                atualizarComprimento()
            }
        })
    }

    private fun updateUI() {
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvMaterial = findViewById<TextView>(R.id.tvMaterial)
        
        tvTitle.text = "Informe a metragem (m²)"
        tvMaterial.text = if (subtype != null) "Material: $material - $subtype" else "Material: $material"

        when (material) {
            "Drywall" -> setupDrywallUI()
            "Cimenticia" -> setupCimenticiaUI()
            "PVC" -> setupPVCUI()
            "Piso" -> setupPisoUI()
            "Painel" -> setupPainelUI()
            "Forro Mineral" -> setupForroMineralUI()
            "Forro Boreal" -> setupForroBorealUI()
            "Isopor" -> setupIsoporUI()
        }
    }

    private fun setupDrywallUI() {
        if (subtype == "Parede") {
            findViewById<TextView>(R.id.tvTitle).text = "Informe a metragem e pé direito da parede"
            llMetragemSimples.visibility = View.GONE
            llDimensoesParede.visibility = View.VISIBLE
        }
        
        llPlacaContainer.visibility = View.VISIBLE
        setupDrywallSpinner()
    }

    private fun setupCimenticiaUI() {
        if (subtype == "Parede") {
            findViewById<TextView>(R.id.tvTitle).text = "Informe a metragem e pé direito da parede"
            llMetragemSimples.visibility = View.GONE
            llDimensoesParede.visibility = View.VISIBLE
            llPlacaContainer.visibility = View.VISIBLE
            setupCimenticiaSpinner()
        } else if (subtype == "Teto") {
            llPlacaContainer.visibility = View.GONE
        }
    }

    private fun setupPVCUI() {
        if (subtype == "Regua") {
            llPlacaContainer.visibility = View.VISIBLE
            setupPVCSpinner()
        } else {
            llPlacaContainer.visibility = View.GONE
        }
    }

    private fun setupPisoUI() {
        llCorPisoContainer.visibility = View.VISIBLE
        setupPisoSpinner()
    }

    private fun setupPainelUI() {
        llJanelasContainer.visibility = View.VISIBLE
        llPortasContainer.visibility = View.VISIBLE
    }

    private fun setupForroMineralUI() {
        llPlacaContainer.visibility = View.VISIBLE
        setupForroMineralSpinner()
    }

    private fun setupForroBorealUI() {
        llPlacaContainer.visibility = View.VISIBLE
        setupForroBorealSpinner()
    }

    private fun setupIsoporUI() {
        // Isopor não precisa de seleções especiais
    }

    private fun setupDrywallSpinner() {
        val opcoes = listOf(
            "Selecione o tipo de placa...",
            "Drywall ST Branco 1,80 x 1,20",
            "Drywall RU (Resistente à Umidade) 1,80 x 1,20",
            "Drywall RF (Resistente à fogo) 1,80 x 1,20"
        )
        val codigos = listOf("", "280", "177", "193")
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPlaca.adapter = adapter
    }

    private fun setupCimenticiaSpinner() {
        val opcoes = listOf(
            "Selecione o tipo de placa...",
            "PLACA CIMENTICIA 2.40 X 1.20 8MM DECORLIT",
            "PLACA CIMENTICIA 2.40 X 1.20 10MM DECORLIT",
            "PLACA CIMENTICIA 2.40 X 1.20 6MM DECORLIT",
            "PLACA CIMENTICIA 2.40 X 1.20 12MM DECORLIT"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPlaca.adapter = adapter
    }

    private fun setupPVCSpinner() {
        val opcoes = listOf(
            "Selecione o tipo de régua...",
            "FORRO PVC 1,00 METROS ( 7MM )",
            "FORRO PVC 2,00 METROS ( 7MM )",
            "FORRO PVC 6,50 METROS ( 7MM )",
            "FORRO PVC 3,00 METROS ( 7MM )",
            "FORRO PVC 6,00 METROS ( 7MM )",
            "FORRO PVC 3,50 METROS ( 7MM )",
            "FORRO PVC 5.50 METROS ( 7MM )",
            "FORRO PVC 4,00 METROS ( 7MM )",
            "FORRO PVC 5.00 METROS ( 7MM )",
            "FORRO PVC 4,50 METROS ( 7MM )",
            "FORRO PVC 7,00 METROS ( 7MM )",
            "FORRO PVC 8,00 METROS ( 7MM )",
            "FORRO PVC 8,50 METROS ( 7MM )"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPlaca.adapter = adapter
    }

    private fun setupPisoSpinner() {
        val opcoes = if (subtype == "Vinílico") {
            listOf(
                "Selecione a cor do material...",
                "[1574] PISO VINÍLICO RUFFINO - SOFISTICATO CARAMBOLA - 18 REGUAS - 2MM - 3,90 M2",
                "[1570] PISO VINÍLICO RUFFINO - SOFISTICATO SAPUCAIA - 18 REGUAS - 2MM - 3,90 M2",
                "[1599] PISO VINILICO RUFFINO BRAVO COR ANGELIM - 3MM - 2,6 M2",
                "[1575] PISO VINILICO RUFFINO NOBILE COLADO BAOBA 2MM - 3,90M2",
                "[1576] PISO VINILICO RUFFINO NOBILE COLADO DAMASCO 2MM - 3,90M2"
            )
        } else {
            listOf(
                "Selecione a cor do material...",
                "[1102] PISO LAMINADO GRAN ELEGANCE STONE CLICK 8MM - CAIXA C/ 2,41M2",
                "[1236] PISO LAMINADO CLICADO DURAFLOOR NATURE BELGRADO CX C/ 2,51M2",
                "[1401] PISO LAMINADO QUICK STEP PREMIERE MOCHA - 2,84M2"
            )
        }
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCorPiso.adapter = adapter
    }

    private fun setupForroMineralSpinner() {
        val opcoes = listOf(
            "Selecione o tipo de forro...",
            "FORRO MINERAL ECOMIN 1250 X 625 X 13MM (12UN) 9,375M2",
            "FORRO MINERAL ECOMIN 625 X 625 (18UN) 7,03M2",
            "FORRO MINERAL FEINSTRATOS (NRC 0,60) TEGULAR 625X625 C/14 PÇ (5,46M2)",
            "FORRO MINERAL FEINSTRATOS (NRC 0,60) 1250 X 625 X 15MM C/10 PÇ (7,812M2)"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPlaca.adapter = adapter
    }

    private fun setupForroBorealSpinner() {
        val opcoes = listOf(
            "Selecione o tipo de forro...",
            "FORRO BOREAL PLUS 1250X625X15 MM (C/24 PÇS - 18,75 M2)"
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPlaca.adapter = adapter
    }

    private fun atualizarComprimento() {
        val m2 = etMetrosQuadrados.text.toString().toDoubleOrNull() ?: 0.0
        val peDireito = etPeDireito.text.toString().toDoubleOrNull() ?: 0.0
        
        if (m2 > 0 && peDireito > 0) {
            val comprimento = m2 / peDireito
            tvComprimentoCalculado.text = "Comprimento calculado: ${String.format("%.2f", comprimento)} m"
        } else {
            tvComprimentoCalculado.text = "Comprimento calculado: 0 m"
        }
    }

    private fun calcular() {
        val m2: Double
        val peDireito: Double?
        val quantidadeJanelas: Int
        val quantidadePortas: Int
        val placaSel: String
        val corPiso: String

        // Determina qual forma de entrada usar
        if ((material == "Drywall" && subtype == "Parede") || (material == "Cimenticia" && subtype == "Parede")) {
            m2 = etMetrosQuadrados.text.toString().toDoubleOrNull() ?: 0.0
            peDireito = etPeDireito.text.toString().toDoubleOrNull() ?: 0.0
            
            if (m2 <= 0) {
                Toast.makeText(this, "Digite a metragem em metros quadrados!", Toast.LENGTH_SHORT).show()
                return
            }
            if (peDireito <= 0) {
                Toast.makeText(this, "Digite o pé direito (altura)!", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            m2 = etMetragem.text.toString().toDoubleOrNull() ?: 0.0
            peDireito = null
            
            if (m2 <= 0) {
                Toast.makeText(this, "Digite uma metragem válida!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Captura quantidade de janelas e portas para Divisória Naval
        quantidadeJanelas = etQuantidadeJanelas.text.toString().toIntOrNull() ?: 0
        quantidadePortas = etQuantidadePortas.text.toString().toIntOrNull() ?: 0

        // Validações específicas por material
        placaSel = when {
            material == "Drywall" -> {
                if (spPlaca.selectedItemPosition == 0) {
                    Toast.makeText(this, "Selecione o tipo de placa de Drywall.", Toast.LENGTH_SHORT).show()
                    return
                }
                val codigos = listOf("", "280", "177", "193")
                codigos[spPlaca.selectedItemPosition]
            }
            material == "Cimenticia" && subtype == "Parede" -> {
                if (spPlaca.selectedItemPosition == 0) {
                    Toast.makeText(this, "Selecione o tipo de placa cimentícia.", Toast.LENGTH_SHORT).show()
                    return
                }
                val codigos = listOf("", "181", "182", "263", "1172")
                codigos[spPlaca.selectedItemPosition]
            }
            material == "PVC" && subtype == "Regua" -> {
                if (spPlaca.selectedItemPosition == 0) {
                    Toast.makeText(this, "Selecione o tipo de régua PVC.", Toast.LENGTH_SHORT).show()
                    return
                }
                val codigos = listOf("", "1138", "1139", "740", "566", "571", "567", "741", "568", "570", "569", "572", "573", "1251")
                codigos[spPlaca.selectedItemPosition]
            }
            material == "Forro Mineral" -> {
                if (spPlaca.selectedItemPosition == 0) {
                    Toast.makeText(this, "Selecione o tipo de Forro Mineral.", Toast.LENGTH_SHORT).show()
                    return
                }
                val codigos = listOf("", "161", "194", "601", "867")
                codigos[spPlaca.selectedItemPosition]
            }
            material == "Forro Boreal" -> {
                if (spPlaca.selectedItemPosition == 0) {
                    Toast.makeText(this, "Selecione o tipo de Forro Boreal.", Toast.LENGTH_SHORT).show()
                    return
                }
                "368"
            }
            else -> ""
        }

        corPiso = if (material == "Piso") {
            if (spCorPiso.selectedItemPosition == 0) {
                Toast.makeText(this, "Selecione a cor do material.", Toast.LENGTH_SHORT).show()
                return
            }
            val codigosVinilico = listOf("", "1574", "1570", "1599", "1575", "1576")
            val codigosLaminado = listOf("", "1102", "1236", "1401")
            if (subtype == "Vinílico") {
                codigosVinilico[spCorPiso.selectedItemPosition]
            } else {
                codigosLaminado[spCorPiso.selectedItemPosition]
            }
        } else ""

        // Navegar para a tela de resultado
        val intent = Intent(this, ResultadoActivity::class.java).apply {
            putExtra("material", material)
            putExtra("subtype", subtype)
            putExtra("m2", m2)
            putExtra("peDireito", peDireito)
            putExtra("placa", placaSel)
            putExtra("cor", corPiso)
            putExtra("janelas", quantidadeJanelas)
            putExtra("portas", quantidadePortas)
        }
        startActivity(intent)
    }
}
