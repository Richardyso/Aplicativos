# 📘 Documento de Regras e Descrição do Projeto

## **Contrauto – SaaS Local para Geração de Propostas e Contratos Comerciais**

---

## **1. Função do Propoza**

O **Propoza** é um sistema local que gera **propostas comerciais** e **contratos personalizados**, construídos a partir de perguntas guiadas. Ele monta automaticamente documentos completos, profissionais e prontos para assinatura digital.

---

## **2. Ideia e Descrição Geral**

* O Contrauto funciona como um **assistente interativo offline**.
* Através de um conjunto estruturado de perguntas, ele monta:

  * Propostas comerciais
  * Contratos de prestação de serviços
  * Contratos de venda
  * Contratos para produtos físicos ou digitais
* O sistema deve permitir personalização total dos documentos, incluindo:

  * Dados das partes envolvidas
  * Valores e condições de pagamento
  * Descrição do serviço ou produto
  * Prazos
  * Termos adicionais
* A saída final será um **PDF pronto para assinatura digital**.

---

## **3. Validade Legal e Considerações Jurídicas**

O Contrauto deve respeitar normas jurídicas de **Brasil** e **Portugal**.

### **🇧🇷 Brasil**

* Código Civil
* Normas de contratos comerciais
* LGPD (quando aplicável)
* Regras sobre assinatura digital (incluindo ICP-Brasil)

### **🇵🇹 Portugal**

* Código Civil Português
* Normas de contratação comercial e prestação de serviços
* RGPD (quando aplicável)
* Regras contratuais usuais no país

### **Importante sobre a Linguagem**

Quando o documento for destinado a **Portugal**, a linguagem utilizada deve ser **português europeu**, considerando que tanto contratante quanto contratado podem ser portugueses.
Da mesma forma, para documentos destinados ao Brasil, deverá ser usado **português brasileiro**.

---

## **4. Tecnologias Utilizadas**

* **Linguagem:** Python
* **Interface:** CustomTkinter
* **Exportação:** PDF (via ReportLab, FPDF ou similar)
* **Execução:** Totalmente offline e portátil
* **Armazenamento:** Modelos e documentos gerados localmente

---

## **5. Requisitos Funcionais**

### **5.1 Interface**

* Interface amigável, moderna e responsiva
* Fluxo de perguntas passo a passo (wizard)
* Seleção de país (Brasil ou Portugal)
* Validação de campos obrigatórios
* Pré-visualização do documento antes da exportação
* Possibilidade de salvar modelos personalizados

### **5.2 Geração de Documentos**

* Texto montado dinamicamente via placeholders
* Suporte a modelos extensos e cláusulas combináveis
* Personalização de:

  * Cláusulas
  * Títulos
  * Termos adicionais
* Tratamento diferente de linguagem conforme o país selecionado

### **5.3 Exportação**

* PDF final profissional
* Compatível com assinatura digital
* Metadados inseridos automaticamente (autor, data, empresa)

---

## **6. Objetivos Secundários (Opcional)**

* Biblioteca interna de cláusulas padrão
* Editor de texto antes de exportar
* Modo escuro/claro
* Armazenamento de histórico de documentos
* Futuras integrações com APIs de assinatura digital

---

## **7. Regras do Projeto**

1. O Contrauto deve sempre ser **local, portátil e independente de internet**.
2. Código modular, organizado e amplamente documentado.
3. Sistema deve permitir expansão futura para novos países, idiomas e modelos.
4. Os textos legais devem ser facilmente editáveis.
5. A interface deve seguir boas práticas de UX, facilitando o uso por pessoas não técnicas.
6. A linguagem jurídica deve ser diferente para:

   * Brasil (português brasileiro)
   * Portugal (português europeu)
7. Todo o projeto deve ser construído com foco em simplicidade, eficiência e confiabilidade.
