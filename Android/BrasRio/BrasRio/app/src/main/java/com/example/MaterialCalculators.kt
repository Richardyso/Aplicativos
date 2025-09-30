package com.example

data class MaterialItem(
    val codigo: String,
    val nome: String,
    val quantidade: Int
)

// Configurações de materiais para parede
object MaterialConfigs {
    val placas = mapOf(
        "280" to MaterialConfig("Drywall ST Branco", 2.16), // 1,80 x 1,20
        "177" to MaterialConfig("Drywall RU (Resistente à Umidade)", 2.16), // 1,80 x 1,20
        "193" to MaterialConfig("Drywall RF (Resistente à fogo)", 2.16), // 1,80 x 1,20
        "181" to MaterialConfig("Placa Cimentícia 8MM", 2.88), // 2,40 x 1,20
        "182" to MaterialConfig("Placa Cimentícia 10MM", 2.88), // 2,40 x 1,20
        "263" to MaterialConfig("Placa Cimentícia 6MM", 2.88), // 2,40 x 1,20
        "1172" to MaterialConfig("Placa Cimentícia 12MM", 2.88) // 2,40 x 1,20
    )
}

data class MaterialConfig(
    val nome: String,
    val area: Double
)

// Classe para cálculo de parede - baseada em m² + pé direito
class ParedeCalculator(
    private val metrosQuadrados: Double,
    private val peDireito: Double,
    private val tipoPlaca: String
) {
    private val comprimento = metrosQuadrados / peDireito // Calcula comprimento automaticamente

    fun calcularArea(): Double = metrosQuadrados

    fun calcularMateriais(): List<MaterialItem> {
        val area = calcularArea()
        val placaConfig = MaterialConfigs.placas[tipoPlaca]
            ?: throw IllegalArgumentException("Tipo de placa não encontrado")

        val materiaisSelecionados = mutableListOf<MaterialItem>()

        // Placa escolhida - cálculo baseado na área real (aumentada em 20% para parede)
        val quantidadePlacas = kotlin.math.ceil((area / placaConfig.area) * 4.45).toInt()
        addMaterialByCode(tipoPlaca, quantidadePlacas, materiaisSelecionados)

        // Parafusos (vêm em mil unidades - código 1521)
        val parafusosNecessarios = calculateParafusos(area, "parede")
        addMaterialByCode("1521", kotlin.math.ceil(parafusosNecessarios / 1000.0).toInt(), materiaisSelecionados)

        // Fita telada (vêm em rolos de 90m - código 1516)
        val fitaNecessaria = calculateFita(area, "parede")
        addMaterialByCode("1516", kotlin.math.ceil(fitaNecessaria / 90.0).toInt(), materiaisSelecionados)

        // Massa para acabamento (vêm em kg - código 698 para 5kg, 431 para 28kg)
        val massaNecessaria = calculateMassa(area, "parede")
        if (massaNecessaria <= 5) {
            addMaterialByCode("698", 1, materiaisSelecionados) // 5kg
        } else {
            val qtd28kg = kotlin.math.ceil(massaNecessaria / 28.0).toInt()
            addMaterialByCode("431", qtd28kg, materiaisSelecionados) // 28kg
        }

        // Perfis para parede - baseado no comprimento calculado e pé direito
        val montantesNecessarios = kotlin.math.ceil(comprimento / 0.6).toInt() + 1 // A cada 60cm + extremidades
        val guiasNecessarias = kotlin.math.ceil((comprimento * 2) / 3.0).toInt() // Guias superior e inferior

        addMaterialByCode("388", kotlin.math.ceil(guiasNecessarias * 3.0).toInt(), materiaisSelecionados) // Guia 48 (aumentada em 200%)
        addMaterialByCode("387", kotlin.math.ceil(montantesNecessarios * 1.1).toInt(), materiaisSelecionados) // Montante 48 (aumentado em 10%)
        addMaterialByCode("192", kotlin.math.ceil((area * 2) / 100.0).toInt(), materiaisSelecionados) // Bucha 6
        addMaterialByCode("173", kotlin.math.ceil((area * 0.5) / 100.0).toInt(), materiaisSelecionados) // Parafuso Frangeado

        return materiaisSelecionados
    }

    fun getResumo(): Map<String, Any> {
        return mapOf(
            "metrosQuadrados" to metrosQuadrados,
            "peDireito" to peDireito,
            "comprimento" to String.format("%.2f", comprimento),
            "tipoPlaca" to tipoPlaca
        )
    }
}

// Classe para cálculo de placas cimentícias
class CimenticiaCalculator(
    private val metrosQuadrados: Double,
    private val peDireito: Double,
    private val tipoPlaca: String?,
    private val sistema: String // 'teto' ou 'parede'
) {
    private val comprimento = metrosQuadrados / peDireito // Calcula comprimento automaticamente

    fun calcularArea(): Double = metrosQuadrados

    fun calcularMateriais(): List<MaterialItem> {
        val area = calcularArea()
        val materiaisSelecionados = mutableListOf<MaterialItem>()

        // Materiais específicos do sistema cimentícia
        if (sistema == "teto") {
            // Para teto: painel wall + fita e massa (sem placas cimentícias)
            val quantidadePlacas = kotlin.math.ceil(area / 2.88).toInt()
            addMaterialByCode("464", quantidadePlacas, materiaisSelecionados) // Painel wall
            addMaterialByCode("582", kotlin.math.ceil(area / 15.0).toInt(), materiaisSelecionados) // Massa para projeto cimentícia (1 para cada 15m²)
            addMaterialByCode("1518", kotlin.math.ceil(area / 15.0).toInt(), materiaisSelecionados) // Fita cimentícia (1 para cada 15m²)
        } else {
            // Para parede: uma placa + fita e massa (sem bucha e parafuso)
            if (tipoPlaca != null) {
                val quantidadePlacas = kotlin.math.ceil(area / 2.88).toInt()
                val quantidadeComAcrescimo = kotlin.math.ceil(quantidadePlacas * 1.1).toInt() // Aumenta 10%
                addMaterialByCode(tipoPlaca, quantidadeComAcrescimo, materiaisSelecionados) // Placa cimentícia
            }
            addMaterialByCode("582", kotlin.math.ceil(area / 15.0).toInt(), materiaisSelecionados) // Massa para projeto cimentícia (1 para cada 15m²)
            addMaterialByCode("1518", kotlin.math.ceil(area / 15.0).toInt(), materiaisSelecionados) // Fita cimentícia (1 para cada 15m²)
        }

        return materiaisSelecionados
    }

    fun getResumo(): Map<String, Any> {
        return mapOf(
            "metrosQuadrados" to metrosQuadrados,
            "peDireito" to peDireito,
            "comprimento" to String.format("%.2f", comprimento),
            "tipoPlaca" to (tipoPlaca ?: ""),
            "sistema" to sistema
        )
    }
}

// Classe para cálculo de réguas PVC
class PVCCalculator(
    private val metrosQuadrados: Double,
    private val tipoRegua: String
) {
    private val larguraRegua = 0.20 // 20cm = 0.20m

    fun calcularArea(): Double = metrosQuadrados

    fun calcularMateriais(): List<MaterialItem> {
        val area = calcularArea()
        val materiaisSelecionados = mutableListOf<MaterialItem>()

        // Calcula quantas réguas são necessárias
        // Área ÷ largura da régua (0.20m) = comprimento total necessário
        val comprimentoTotal = area / larguraRegua

        // Comprimento total ÷ comprimento da régua escolhida = quantidade de réguas
        val comprimentoRegua = obterComprimentoRegua(tipoRegua)
        val quantidadeReguas = kotlin.math.ceil(comprimentoTotal / comprimentoRegua).toInt()

        // Adiciona 20% de margem
        val quantidadeComMargem = kotlin.math.ceil(quantidadeReguas * 1.2).toInt()

        addMaterialByCode(tipoRegua, quantidadeComMargem, materiaisSelecionados)

        return materiaisSelecionados
    }

    private fun obterComprimentoRegua(codigoRegua: String): Double {
        val comprimentos = mapOf(
            "1138" to 1.00,   // FORRO PVC 1,00 METROS
            "1139" to 2.00,   // FORRO PVC 2,00 METROS
            "740" to 6.50,    // FORRO PVC 6,50 METROS
            "566" to 3.00,    // FORRO PVC 3,00 METROS
            "571" to 6.00,    // FORRO PVC 6,00 METROS
            "567" to 3.50,    // FORRO PVC 3,50 METROS
            "741" to 5.50,    // FORRO PVC 5.50 METROS
            "568" to 4.00,    // FORRO PVC 4,00 METROS
            "570" to 5.00,    // FORRO PVC 5.00 METROS
            "569" to 4.50,    // FORRO PVC 4,50 METROS
            "572" to 7.00,    // FORRO PVC 7,00 METROS
            "573" to 8.00,    // FORRO PVC 8,00 METROS
            "1251" to 8.50    // FORRO PVC 8,50 METROS
        )
        return comprimentos[codigoRegua] ?: 1.00
    }

    fun getResumo(): Map<String, Any> {
        return mapOf(
            "metrosQuadrados" to metrosQuadrados,
            "tipoRegua" to tipoRegua,
            "larguraRegua" to larguraRegua,
            "comprimentoRegua" to obterComprimentoRegua(tipoRegua)
        )
    }
}

// Classe para cálculo de Forro Mineral
class ForroMineralCalculator(
    private val metrosQuadrados: Double,
    private val tipoForro: String
) {
    fun calcularArea(): Double = metrosQuadrados

    fun calcularMateriais(): List<MaterialItem> {
        val area = calcularArea()
        val materiaisSelecionados = mutableListOf<MaterialItem>()

        // Calcula quantidade de placas baseado na área coberta por cada tipo
        val areaPorPlaca = obterAreaPorPlaca(tipoForro)
        val quantidadePlacas = kotlin.math.ceil(area / areaPorPlaca).toInt()

        // Adiciona 10% de margem
        val quantidadeComMargem = kotlin.math.ceil(quantidadePlacas * 1.1).toInt()

        // Adiciona o forro mineral escolhido
        addMaterialByCode(tipoForro, quantidadeComMargem, materiaisSelecionados)

        // Materiais adicionais específicos do Forro Mineral
        addMaterialByCode("19", kotlin.math.ceil((area * 5) / 100.0).toInt(), materiaisSelecionados) // PARAFUSO 13 PONTA BROCA (CENTO)
        addMaterialByCode("267", kotlin.math.ceil((area * 2) / 50.0).toInt(), materiaisSelecionados) // PRESILHA BIGODE 20MM C/50 PECAS
        addMaterialByCode("164", kotlin.math.ceil((area * 0.5) / 100.0).toInt(), materiaisSelecionados) // PINO CLIP 1/4 (CENTO)

        // Aplicado apenas para placas 1250x625 (códigos 161 e 867)
        if (tipoForro == "161" || tipoForro == "867") {
            // Usa função padronizada para grade quadrada de forros 1250x625
            calcularMateriaisForro1250x625(area, materiaisSelecionados)
        } else {
            // Para outros tipos de Forro Mineral (625x625), usa função padronizada para grade quadrada
            calcularMateriaisForro625x625(area, materiaisSelecionados)
        }

        return materiaisSelecionados
    }

    private fun obterAreaPorPlaca(codigoForro: String): Double {
        val areas = mapOf(
            "161" to 9.375,   // FORRO MINERAL ECOMIN 1250 X 625 X 13MM (12UN) 9,375M2
            "194" to 7.03,    // FORRO MINERAL ECOMIN 625 X 625 (18UN) 7,03M2
            "601" to 5.46,    // FORRO MINERAL FEINSTRATOS (NRC 0,60) TEGULAR 625X625 C/14 PÇ (5,46M2)
            "867" to 7.812    // FORRO MINERAL FEINSTRATOS (NRC 0,60) 1250 X 625 X 15MM C/10 PÇ (7,812M2)
        )
        return areas[codigoForro] ?: 7.812 // Default para o maior se não encontrar
    }

    fun getResumo(): Map<String, Any> {
        return mapOf(
            "metrosQuadrados" to metrosQuadrados,
            "tipoForro" to tipoForro,
            "areaPorPlaca" to obterAreaPorPlaca(tipoForro)
        )
    }
}

// Classe para cálculo de Forro Boreal
class ForroBorealCalculator(
    private val metrosQuadrados: Double,
    private val tipoForro: String
) {
    fun calcularArea(): Double = metrosQuadrados

    fun calcularMateriais(): List<MaterialItem> {
        val area = calcularArea()
        val materiaisSelecionados = mutableListOf<MaterialItem>()

        // Calcula quantidade de placas baseado na área coberta por cada tipo
        val areaPorPlaca = obterAreaPorPlaca(tipoForro)
        val quantidadePlacas = kotlin.math.ceil(area / areaPorPlaca).toInt()

        // Adiciona o forro boreal escolhido (sem margem adicional, igual ao Isopor)
        addMaterialByCode(tipoForro, quantidadePlacas, materiaisSelecionados)

        // Materiais adicionais específicos do Forro Boreal (usando as mesmas métricas do Isopor)
        addMaterialByCode("19", kotlin.math.ceil((area * 5) / 100.0).toInt(), materiaisSelecionados) // PARAFUSO 13 PONTA BROCA (CENTO)
        addMaterialByCode("267", kotlin.math.ceil((area * 2) / 50.0).toInt(), materiaisSelecionados) // PRESILHA BIGODE 20MM C/50 PECAS
        addMaterialByCode("164", kotlin.math.ceil((area * 0.5) / 100.0).toInt(), materiaisSelecionados) // PINO CLIP 1/4 (CENTO)

        // Usa função padronizada para grade quadrada de forros 1250x625
        calcularMateriaisForro1250x625(area, materiaisSelecionados)

        return materiaisSelecionados
    }

    private fun obterAreaPorPlaca(codigoForro: String): Double {
        val areas = mapOf(
            "368" to 18.75    // FORRO BOREAL PLUS 1250X625X15 MM (C/24 PÇS - 18,75 M2)
        )
        return areas[codigoForro] ?: 18.75 // Default para 18.75 se não encontrar
    }

    fun getResumo(): Map<String, Any> {
        return mapOf(
            "metrosQuadrados" to metrosQuadrados,
            "tipoForro" to tipoForro,
            "areaPorPlaca" to obterAreaPorPlaca(tipoForro)
        )
    }
}

// Função padronizada para calcular materiais de forros 1250x625
private fun calcularMateriaisForro1250x625(area: Double, materiaisSelecionados: MutableList<MaterialItem>) {
    // Dimensões das placas: 1,25m x 0,625m
    val larguraPlaca = 1.25 // 1250mm
    val comprimentoPlaca = 0.625 // 625mm

    // Calcula dimensões do ambiente (aproximação quadrada)
    val ladoAmbiente = kotlin.math.sqrt(area)

    // Perfis principais: espaçados a cada 0,625m (comprimento da placa)
    // Para ambiente quadrado: lado ÷ 0,625 + 1 para margem
    val perfisNecessarios = kotlin.math.ceil(ladoAmbiente / comprimentoPlaca).toInt() + 1

    // Travessas 1250mm: uma para cada linha de placas
    // Para ambiente quadrado: lado ÷ 1,25 + 1 para margem
    val travessas1250 = kotlin.math.ceil(ladoAmbiente / larguraPlaca).toInt() + 1

    // Travessas 625mm: duas para cada linha de placas (metade da largura)
    // Para ambiente quadrado: (lado ÷ 1,25) × 2 + 2 para margem
    val travessas625 = kotlin.math.ceil(ladoAmbiente / larguraPlaca).toInt() * 2 + 2

    // Cantoneira: quantidade baseada no perímetro do ambiente
    // Para ambiente quadrado: (lado × 4) ÷ 3 + margem
    val cantoneiras = kotlin.math.ceil((ladoAmbiente * 4) / 3.0).toInt() + 1

    // Adiciona os materiais com quantidades mínimas garantidas
    addMaterialByCode("1364", kotlin.math.max(4, perfisNecessarios), materiaisSelecionados) // PERFIL CLICADO 3125 MM
    addMaterialByCode("1365", kotlin.math.max(4, travessas1250), materiaisSelecionados) // TRAVESSA CLICADA 1250 MM
    addMaterialByCode("1366", kotlin.math.max(6, travessas625), materiaisSelecionados) // TRAVESSA CLICADA 625 MM
    addMaterialByCode("1363", kotlin.math.max(4, cantoneiras), materiaisSelecionados) // CANTONEIRA BRANCA 3000 MM
}

// Função padronizada para calcular materiais de forros quadrados 625x625
private fun calcularMateriaisForro625x625(area: Double, materiaisSelecionados: MutableList<MaterialItem>) {
    // Dimensões das placas: 0,625m x 0,625m (quadradas)
    val ladoPlaca = 0.625 // 625mm

    // Calcula dimensões do ambiente (aproximação quadrada)
    val ladoAmbiente = kotlin.math.sqrt(area)

    // Perfis principais: espaçados a cada 0,625m (lado da placa)
    // Para ambiente quadrado: lado ÷ 0,625 + 1 para margem
    val perfisNecessarios = kotlin.math.ceil(ladoAmbiente / ladoPlaca).toInt() + 1

    // Travessas 1250mm: uma para cada linha de placas
    // Para ambiente quadrado: lado ÷ 1,25 + 1 para margem
    val travessas1250 = kotlin.math.ceil(ladoAmbiente / 1.25).toInt() + 1

    // Travessas 625mm: duas para cada linha de placas (metade da largura)
    // Para ambiente quadrado: (lado ÷ 1,25) × 2 + 2 para margem
    val travessas625 = kotlin.math.ceil(ladoAmbiente / 1.25).toInt() * 2 + 2

    // Cantoneira: quantidade baseada no perímetro do ambiente
    // Para ambiente quadrado: (lado × 4) ÷ 3 + margem
    val cantoneiras = kotlin.math.ceil((ladoAmbiente * 4) / 3.0).toInt() + 1

    // Adiciona os materiais com quantidades mínimas garantidas
    addMaterialByCode("1364", kotlin.math.max(4, perfisNecessarios), materiaisSelecionados) // PERFIL CLICADO 3125 MM
    addMaterialByCode("1365", kotlin.math.max(4, travessas1250), materiaisSelecionados) // TRAVESSA CLICADA 1250 MM
    addMaterialByCode("1366", kotlin.math.max(6, travessas625), materiaisSelecionados) // TRAVESSA CLICADA 625 MM
    addMaterialByCode("1363", kotlin.math.max(4, cantoneiras), materiaisSelecionados) // CANTONEIRA BRANCA 3000 MM
}

// Funções auxiliares
private fun calculateParafusos(area: Double, sistema: String): Double {
    val quantidades = mapOf(
        "parede" to 12.0,
        "forro" to 15.0,
        "divisoria" to 14.0
    )
    return kotlin.math.ceil(area * (quantidades[sistema] ?: 12.0) * 1.15)
}

private fun calculateFita(area: Double, sistema: String): Double {
    val quantidades = mapOf(
        "parede" to 1.25, // Reduzido pela metade (era 2.5)
        "forro" to 1.4,   // Reduzido pela metade (era 2.8)
        "divisoria" to 1.3 // Reduzido pela metade (era 2.6)
    )
    return kotlin.math.ceil(area * (quantidades[sistema] ?: 1.25) * 1.08)
}

private fun calculateMassa(area: Double, sistema: String): Double {
    val quantidades = mapOf(
        "parede" to 0.4, // Reduzido pela metade (era 0.8)
        "forro" to 0.45, // Reduzido pela metade (era 0.9)
        "divisoria" to 0.425 // Reduzido pela metade (era 0.85)
    )
    return kotlin.math.round((area * (quantidades[sistema] ?: 0.4) * 1.12) * 10.0) / 10.0
}

private fun addMaterialByCode(code: String, quantidade: Int, materiaisSelecionados: MutableList<MaterialItem>) {
    if (quantidade > 0) {
        val nome = getProductNameByCode(code)
        materiaisSelecionados.add(MaterialItem(code, nome, quantidade))
    }
}

private fun getProductNameByCode(code: String): String {
    return ProductManager.getProductNameByCode(code)
}

// Funções para cálculo de pisos
private fun getAreaPisoVinilico(codigo: String): Double {
    val areas = mapOf(
        "1574" to 3.90,
        "1570" to 3.90,
        "1599" to 2.6,
        "1575" to 3.90,
        "1576" to 3.90
    )
    return areas[codigo] ?: 3.90 // Default para 3.90 se não encontrar
}

private fun getAreaPisoLaminado(codigo: String): Double {
    val areas = mapOf(
        "1102" to 2.41,
        "1236" to 2.51,
        "1401" to 2.84
    )
    return areas[codigo] ?: 2.41 // Default para 2.41 se não encontrar
}

private fun escolherMelhorPisoVinilico(m2: Double): PisoResult {
    val opcoes = listOf(
        PisoOption("1574", 3.90, "PISO VINÍLICO RUFFINO - SOFISTICATO CARAMBOLA"),
        PisoOption("1570", 3.90, "PISO VINÍLICO RUFFINO - SOFISTICATO SAPUCAIA"),
        PisoOption("1599", 2.6, "PISO VINILICO RUFFINO BRAVO COR ANGELIM"),
        PisoOption("1575", 3.90, "PISO VINILICO RUFFINO NOBILE COLADO BAOBA"),
        PisoOption("1576", 3.90, "PISO VINILICO RUFFINO NOBILE COLADO DAMASCO")
    )
    
    var melhor: PisoOption? = null
    var menorSobra = Double.MAX_VALUE
    
    opcoes.forEach { op ->
        val quantidade = kotlin.math.ceil(m2 / op.area).toInt()
        val coberta = quantidade * op.area
        val sobra = coberta - m2
        
        if (sobra < menorSobra) {
            menorSobra = sobra
            melhor = op
        }
    }
    
    val quantidade = kotlin.math.ceil(m2 / (melhor?.area ?: 3.90)).toInt()
    return PisoResult(melhor?.codigo ?: "1574", quantidade)
}

private fun escolherMelhorPisoLaminado(m2: Double): PisoResult {
    val opcoes = listOf(
        PisoOption("1102", 2.41, "PISO LAMINADO GRAN ELEGANCE STONE CLICK 8MM"),
        PisoOption("1236", 2.51, "PISO LAMINADO CLICADO DURAFLOOR NATURE BELGRADO"),
        PisoOption("1401", 2.84, "PISO LAMINADO QUICK STEP PREMIERE MOCHA")
    )
    
    var melhor: PisoOption? = null
    var menorSobra = Double.MAX_VALUE
    
    opcoes.forEach { op ->
        val quantidade = kotlin.math.ceil(m2 / op.area).toInt()
        val coberta = quantidade * op.area
        val sobra = coberta - m2
        
        if (sobra < menorSobra) {
            menorSobra = sobra
            melhor = op
        }
    }
    
    val quantidade = kotlin.math.ceil(m2 / (melhor?.area ?: 2.41)).toInt()
    return PisoResult(melhor?.codigo ?: "1102", quantidade)
}

data class PisoOption(
    val codigo: String,
    val area: Double,
    val nome: String
)

data class PisoResult(
    val codigo: String,
    val quantidade: Int
)

// Classe para cálculo de Isopor
class IsoporCalculator(
    private val metrosQuadrados: Double,
    private val tipoForro: String
) {
    fun calcularArea(): Double = metrosQuadrados

    fun calcularMateriais(): List<MaterialItem> {
        val area = calcularArea()
        val materiaisSelecionados = mutableListOf<MaterialItem>()

        // Forro isopor: cada pacote cobre 19,2m²
        val quantidadePaineis = kotlin.math.ceil(area / 19.2).toInt()
        addMaterialByCode("68", quantidadePaineis, materiaisSelecionados) // Forro isopor

        // Materiais adicionais específicos do Isopor
        addMaterialByCode("19", kotlin.math.ceil((area * 5) / 100.0).toInt(), materiaisSelecionados) // PARAFUSO 13 PONTA BROCA (CENTO)
        addMaterialByCode("267", kotlin.math.ceil((area * 2) / 50.0).toInt(), materiaisSelecionados) // PRESILHA BIGODE 20MM C/50 PECAS
        addMaterialByCode("164", kotlin.math.ceil((area * 0.5) / 100.0).toInt(), materiaisSelecionados) // PINO CLIP 1/4 (CENTO)

        // Usa função padronizada para grade quadrada de forros 1250x625
        calcularMateriaisForro1250x625(area, materiaisSelecionados)

        return materiaisSelecionados
    }

    fun getResumo(): Map<String, Any> {
        return mapOf(
            "metrosQuadrados" to metrosQuadrados,
            "tipoForro" to tipoForro
        )
    }
}

// Classe para cálculo de Painel (Divisória)
class PainelCalculator(
    private val metrosQuadrados: Double,
    private val quantidadePortas: Int = 0
) {
    fun calcularArea(): Double = metrosQuadrados

    fun calcularMateriais(): List<MaterialItem> {
        val area = calcularArea()
        val materiaisSelecionados = mutableListOf<MaterialItem>()

        // Calcula quantidade de painéis baseado nos metros quadrados
        val quantidadePaineis = kotlin.math.ceil(area / 2.53).toInt()

        // Cálculo baseado na quantidade de painéis
        addMaterialByCode("79", quantidadePaineis, materiaisSelecionados) // Painel Eucatex (Divisória Naval)
        addMaterialByCode("89", kotlin.math.ceil(quantidadePaineis * 1.22).toInt(), materiaisSelecionados) // Guia Baixa (U) Branca 3.00 mts
        addMaterialByCode("81", kotlin.math.ceil(quantidadePaineis * 1.0).toInt(), materiaisSelecionados) // NTR Travessa 3M
        addMaterialByCode("87", kotlin.math.ceil(quantidadePaineis * 1.0).toInt(), materiaisSelecionados) // NTR Travessa 1185 M

        // Requadros e Batentes só são incluídos se houver portas
        if (quantidadePortas > 0) {
            addMaterialByCode("102", kotlin.math.ceil(quantidadePortas * 2.0).toInt(), materiaisSelecionados) // Requadro Horizontal 0,81 M
            addMaterialByCode("101", kotlin.math.ceil(quantidadePortas * 2.0).toInt(), materiaisSelecionados) // Requadro Vertical 2,11 M
            addMaterialByCode("107", kotlin.math.ceil(quantidadePortas * 1.0).toInt(), materiaisSelecionados) // Batente Horizontal 0,84 M
            addMaterialByCode("110", kotlin.math.ceil(quantidadePortas * 2.0).toInt(), materiaisSelecionados) // Batente Vertical 2,14 M
        }

        return materiaisSelecionados
    }

    fun getResumo(): Map<String, Any> {
        return mapOf(
            "metrosQuadrados" to metrosQuadrados,
            "quantidadePortas" to quantidadePortas
        )
    }
}

// Classe para cálculo de Pisos
class PisoCalculator(
    private val metrosQuadrados: Double,
    private val tipoPiso: String, // "Vinílico" ou "Laminado"
    private val corPiso: String? = null
) {
    fun calcularArea(): Double = metrosQuadrados

    fun calcularMateriais(): List<MaterialItem> {
        val area = calcularArea()
        val materiaisSelecionados = mutableListOf<MaterialItem>()

        if (tipoPiso == "Vinílico") {
            // Se foi especificado um código de cor específico, usa ele
            if (corPiso != null && listOf("1574", "1570", "1599", "1575", "1576").contains(corPiso)) {
                val areaPiso = getAreaPisoVinilico(corPiso)
                val quantidade = kotlin.math.ceil(area / areaPiso).toInt()
                addMaterialByCode(corPiso, quantidade, materiaisSelecionados)
                addMaterialByCode("947", quantidade, materiaisSelecionados) // MASSA NIVELADORA PISO SC/ 4KG MAPEI
            } else {
                // Senão, usa a lógica de escolha automática
                val melhor = escolherMelhorPisoVinilico(area)
                addMaterialByCode(melhor.codigo, melhor.quantidade, materiaisSelecionados)
                addMaterialByCode("947", melhor.quantidade, materiaisSelecionados) // MASSA NIVELADORA PISO SC/ 4KG MAPEI
            }
        } else if (tipoPiso == "Laminado") {
            // Se foi especificado um código de cor específico, usa ele
            if (corPiso != null && listOf("1102", "1236", "1401").contains(corPiso)) {
                val areaPiso = getAreaPisoLaminado(corPiso)
                val quantidade = kotlin.math.ceil(area / areaPiso).toInt()
                addMaterialByCode(corPiso, quantidade, materiaisSelecionados)
                addMaterialByCode("447", kotlin.math.ceil(area / 1.2).toInt(), materiaisSelecionados) // MANTA P/ PISO LAMINADO 1,20ML
            } else {
                // Senão, usa a lógica de escolha automática
                val melhor = escolherMelhorPisoLaminado(area)
                addMaterialByCode(melhor.codigo, melhor.quantidade, materiaisSelecionados)
                addMaterialByCode("447", kotlin.math.ceil(area / 1.2).toInt(), materiaisSelecionados) // MANTA P/ PISO LAMINADO 1,20ML
            }
        }

        return materiaisSelecionados
    }

    fun getResumo(): Map<String, Any> {
        return mapOf(
            "metrosQuadrados" to metrosQuadrados,
            "tipoPiso" to tipoPiso,
            "corPiso" to (corPiso ?: "Automático")
        )
    }
}
