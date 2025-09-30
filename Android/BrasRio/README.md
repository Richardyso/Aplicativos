# BRASRIO CAMPO GRANDE - App Android

Este é um aplicativo Android que replica a funcionalidade do site de orçamento rápido da BRASRIO CAMPO GRANDE.

## Funcionalidades

### 🔐 Tela de Login Inicial
- Coleta dados do usuário (Nome, CPF/CNPJ, Telefone) na primeira execução
- Validações completas com formatação automática
- Dados salvos localmente para uso posterior
- Pula a tela de login nas próximas execuções

### 🏠 Tela Principal
- Seleção de tipos de materiais:
  - Drywall
  - Placas Cimentícias
  - PVC
  - Isopor
  - Forro Mineral
  - Forro Boreal
  - Divisória Eucatex
  - Pisos
- Opções de indicação:
  - Instaladores Credenciados
  - Fretes

### 📐 Cálculo de Materiais
- **Drywall**: Suporte para Parede e Teto com diferentes tipos de placas
- **Placas Cimentícias**: Cálculo para Parede e Teto
- **PVC**: Opções de Placa e Régua
- **Pisos**: Vinílico e Laminado com seleção de cores
- **Forros**: Mineral e Boreal com tipos específicos
- **Divisórias**: Suporte para janelas e portas

### 📊 Tela de Metragem
- Entrada de metragem simples ou com dimensões (m² + pé direito)
- Cálculo automático de comprimento para paredes
- Seleção de tipos específicos de materiais
- Validações de campos obrigatórios

### 📱 Resultado e WhatsApp
- Exibição detalhada dos materiais calculados
- Envio automático para WhatsApp com:
  - Dados do cliente
  - Informações do orçamento
  - Lista completa de materiais
  - Data e hora da solicitação

## Estrutura do Projeto

### Activities
- `LoginActivity`: Tela inicial de coleta de dados
- `MainActivity`: Tela principal de seleção
- `MaterialTypeActivity`: Seleção de subtipos de materiais
- `MetragemActivity`: Entrada de metragem e cálculos
- `ResultadoActivity`: Exibição do resultado e envio WhatsApp
- `InstaladoresActivity`: Informações sobre instaladores
- `FretesActivity`: Informações sobre fretes

### Classes de Cálculo
- `ParedeCalculator`: Cálculos para paredes Drywall
- `CimenticiaCalculator`: Cálculos para placas cimentícias
- `PVCCalculator`: Cálculos para réguas PVC
- `ForroMineralCalculator`: Cálculos para forro mineral
- `ForroBorealCalculator`: Cálculos para forro boreal

### Validações
- `Validators`: Validação de CPF, CNPJ, telefone e nome
- `TextWatchers`: Formatação automática de campos

## Tecnologias Utilizadas

- **Kotlin**: Linguagem principal
- **Material Design**: Componentes de UI
- **SharedPreferences**: Armazenamento local de dados
- **Intent**: Navegação entre telas e integração WhatsApp

## Cores e Design

O aplicativo segue a identidade visual do site:
- **Cor primária**: #ff6600 (laranja)
- **Cor secundária**: #e05500 (laranja escuro)
- **Fundo**: #ffffff (branco)
- **Texto**: #000000 (preto) e #666666 (cinza)

## Funcionalidades Especiais

### Validações Inteligentes
- CPF/CNPJ com validação matemática
- Formatação automática de campos
- Validação de telefone brasileiro
- Nome apenas com letras

### Cálculos Precisos
- Baseados nas mesmas fórmulas do site
- Consideração de perdas e margens
- Cálculo automático de comprimento
- Suporte para diferentes tipos de materiais

### Integração WhatsApp
- Mensagem formatada com dados do cliente
- Informações completas do orçamento
- Lista detalhada de materiais
- Timestamp da solicitação

## Como Usar

1. **Primeira execução**: Preencha seus dados na tela de login
2. **Seleção**: Escolha o tipo de material desejado
3. **Configuração**: Selecione subtipo e especificações
4. **Metragem**: Informe as dimensões necessárias
5. **Resultado**: Visualize os materiais calculados
6. **WhatsApp**: Envie o pedido diretamente para a BRASRIO

## Requisitos

- Android 5.0 (API 21) ou superior
- Conexão com internet para envio WhatsApp
- Aplicativo WhatsApp instalado

## Desenvolvimento

O aplicativo foi desenvolvido seguindo as melhores práticas do Android:
- Arquitetura MVVM
- Material Design Guidelines
- Validações robustas
- Interface responsiva
- Código limpo e documentado
