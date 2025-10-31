# 🗺️ Dido - Extrator de Leads de Mapas (Portugal 🇵🇹)

**Versão 100% Gratuita e Sem Limitações!**

Dido é uma extensão para navegadores que extrai automaticamente dados de negócios **portugueses** do Google Maps e Bing Maps. Especializado em números portugueses (DDI +351) com filtros automáticos de qualidade.

## ✨ Características

- ✅ **100% Gratuito** - Sem limitações ou bloqueios
- ✅ **Números Portugueses** - Detecta e formata números com DDI +351
- ✅ **Filtro Inteligente** - Remove automaticamente registros sem número
- ✅ **Anti-Duplicatas** - Elimina nomes repetidos e registros duplicados
- ✅ **Google Maps** - Extrai dados de listagens do Google Maps
- ✅ **Bing Maps** - Extrai dados de listagens do Bing Maps
- ✅ **Automático** - Coleta dados enquanto você navega
- ✅ **Exportação Excel** - Baixa dados em planilha Excel (.xlsx)
- ✅ **Interface Simples** - Fácil de usar

## 📊 Dados Coletados

Para cada negócio encontrado, o Dido coleta:

- 📝 Nome da empresa *(obrigatório para exportação)*
- 📞 Número de telefone português (+351) *(obrigatório para exportação)*
- 📍 Endereço completo (sem o telefone misturado)
- 🌐 Website
- ⭐ Avaliações e ratings
- 🏷️ Categorias de negócio
- 📍 Coordenadas geográficas
- 🔗 URLs das listagens
- 📅 Data de coleta

**⚠️ Importante:** Apenas registros com **nome válido E telefone português** são exportados. Duplicatas (por nome ou conteúdo) são automaticamente removidas!

## 🚀 Como Instalar

### Método 1: Instalação Manual (Recomendado)

1. **Baixe o projeto**
   - Clone ou baixe este repositório
   - Extraia os arquivos para uma pasta

2. **Abra o Chrome/Edge**
   - Digite `chrome://extensions/` na barra de endereços
   - Ou `edge://extensions/` no Microsoft Edge

3. **Ative o Modo Desenvolvedor**
   - Clique no botão "Modo do desenvolvedor" no canto superior direito

4. **Carregue a Extensão**
   - Clique em "Carregar sem compactação"
   - Selecione a pasta `Dido` que contém o `manifest.json`
   - Clique em "Selecionar pasta"

5. **Pronto!**
   - A extensão Dido aparecerá na sua lista de extensões
   - O ícone 🗺️ aparecerá na barra de ferramentas

### Método 2: Instalação via Arquivo ZIP

1. **Compacte a pasta Dido**
   - Crie um arquivo ZIP com todos os arquivos da pasta Dido

2. **Instale no Chrome/Edge**
   - Vá para `chrome://extensions/` ou `edge://extensions/`
   - Ative o "Modo do desenvolvedor"
   - Arraste o arquivo ZIP para a página de extensões

## 📖 Como Usar

### 1. Navegação Automática
- Acesse [Google Maps](https://maps.google.com) ou [Bing Maps](https://bing.com/maps)
- Faça buscas por negócios em Portugal (ex: "restaurantes em Lisboa", "lojas em Porto")
- Navegue pelos resultados clicando nas listagens
- Os dados serão coletados automaticamente
- **Apenas negócios com telefones portugueses (+351) serão exportados**

### 2. Interface da Extensão
- Clique no ícone 🗺️ na barra de ferramentas
- Veja quantos registros foram coletados
- Use os botões para:
  - **📊 Exportar Excel**: Baixa planilha Excel (.xlsx) com dados filtrados
  - **🗑️ Limpar Dados**: Remove todos os registros coletados
  - **🔄 Remover Duplicatas**: Limpa manualmente duplicatas da base
  - **🔧 Testar Extração**: Verifica se os telefones portugueses estão sendo detectados
  - **❓ Como Usar**: Mostra instruções detalhadas

### 3. Exportação de Dados
- Clique em "Exportar Excel"
- Uma planilha Excel (.xlsx) será baixada com todos os dados coletados
- **Apenas registros com nome e telefone português válidos são incluídos**
- **Nomes duplicados são automaticamente removidos**
- A planilha contém colunas organizadas: Nome, Telefone (+351), Categoria, Endereço, Website, etc.
- Formato do telefone: `+351 9XX XXX XXX`

## 🔧 Estrutura do Projeto

```
Dido/
├── manifest.json          # Configuração da extensão
├── html/
│   └── popup.html         # Interface da extensão
├── js/
│   ├── popup.js           # Lógica da interface
│   ├── background.js      # Script em segundo plano
│   └── content.js         # Script das páginas
├── css/
│   ├── popup.css          # Estilos da interface
│   └── content.css        # Estilos das páginas
├── icons/
│   └── icon-128.png       # Ícone da extensão
├── _locales/
│   └── en/
│       └── messages.json  # Textos da extensão
└── README.md              # Este arquivo
```

## 🛠️ Desenvolvimento

### Tecnologias
- **Manifest V3** - Versão mais recente de extensões
- **Chrome Extensions API** - Para funcionalidades do navegador
- **JavaScript ES6+** - Código moderno
- **CSS3** - Estilos responsivos


## 🔍 Filtros de Qualidade

O Dido aplica automaticamente filtros inteligentes para garantir qualidade dos dados:

### Filtros Aplicados na Exportação:
1. ✅ **Telefone Obrigatório**: Apenas registros com número português válido (+351 9XX XXX XXX)
2. ✅ **Nome Obrigatório**: Apenas registros com nome válido (mínimo 3 caracteres)
3. ✅ **Sem Duplicatas de Nome**: Remove automaticamente registros com nomes repetidos
4. ✅ **Sem Duplicatas de Conteúdo**: Remove registros com mesmo nome + endereço + telefone
5. ✅ **Limpeza de Endereço**: Remove telefones do campo de endereço automaticamente

### Padrões de Telefone Detectados:
- `+351 912 345 678`
- `00351 912 345 678`
- `912 345 678`
- Variações com ou sem espaços

## 📞 Suporte

Se encontrar problemas:
1. Verifique este README
2. Use o botão "🔧 Testar Extração" para verificar detecção de números
3. Teste em uma nova aba do navegador
4. Recarregue a extensão em `chrome://extensions/`
5. Verifique o console do navegador para erros (F12)

## 🎯 Casos de Uso em Portugal

- **Pesquisa de Mercado**: Mapear concorrentes em Lisboa, Porto, Braga, etc.
- **Vendas B2B**: Coletar leads de empresas portuguesas
- **Análise Comercial**: Estudar distribuição de negócios por região
- **Marketing Direto**: Criar listas de contatos com telefones portugueses
- **Prospecção de Clientes**: Identificar potenciais clientes em áreas específicas
- **Estudos Acadêmicos**: Pesquisas sobre comércio local em Portugal

---

**🗺️ Dido - Extraindo leads de mapas portugueses de forma gratuita e com qualidade!** 🇵🇹
