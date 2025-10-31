# 🗺️ Dido - Extrator de Leads de Mapas

**Versão 100% Gratuita e Sem Limitações!**

Dido é uma extensão para navegadores que extrai automaticamente dados de negócios do Google Maps e Bing Maps. É uma versão gratuita e ilimitada baseada no PRESTo, removendo todas as limitações e sistemas de licenciamento.

## ✨ Características

- ✅ **100% Gratuito** - Sem limitações ou bloqueios
- ✅ **Sem Licenciamento** - Funciona imediatamente após instalação
- ✅ **Google Maps** - Extrai dados de listagens do Google Maps
- ✅ **Bing Maps** - Extrai dados de listagens do Bing Maps
- ✅ **Automático** - Coleta dados enquanto você navega
- ✅ **Exportação Excel** - Baixa dados em planilha Excel (.xlsx)
- ✅ **Interface Simples** - Fácil de usar

## 📊 Dados Coletados

Para cada negócio encontrado, o Dido coleta:

- 📝 Nome da empresa
- 📍 Endereço completo
- 📞 Número de telefone
- 🌐 Website
- ⭐ Avaliações e ratings
- 🏷️ Categorias de negócio
- 📍 Coordenadas geográficas
- 🔗 URLs das listagens
- 📅 Data de coleta

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
- Faça buscas por negócios (ex: "restaurantes em São Paulo")
- Navegue pelos resultados clicando nas listagens
- Os dados serão coletados automaticamente

### 2. Interface da Extensão
- Clique no ícone 🗺️ na barra de ferramentas
- Veja quantos registros foram coletados
- Use os botões para:
  - **📊 Exportar Excel**: Baixa planilha Excel (.xlsx) com todos os dados
  - **🗑️ Limpar Dados**: Remove todos os registros coletados
  - **❓ Como Usar**: Mostra instruções detalhadas

### 3. Exportação de Dados
- Clique em "Exportar Excel"
- Uma planilha Excel (.xlsx) será baixada com todos os dados coletados
- A planilha contém colunas organizadas com todas as informações dos negócios
- Formato: Nome, Endereço, Telefone, Website, Avaliação, Categoria, etc.

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


## 📞 Suporte

Se encontrar problemas:
1. Verifique este README
2. Teste em uma nova aba do navegador
3. Recarregue a extensão
4. Verifique o console do navegador para erros

## 🎯 Casos de Uso

- **Pesquisa de Mercado**: Mapear concorrentes em uma região
- **Vendas B2B**: Coletar leads de empresas
- **Análise Comercial**: Estudar distribuição de negócios
- **Marketing Direto**: Criar listas de contatos
- **Estudos Acadêmicos**: Pesquisas sobre comércio local

---

**🗺️ Dido - Extraindo leads de mapas de forma gratuita e ilimitada!**
