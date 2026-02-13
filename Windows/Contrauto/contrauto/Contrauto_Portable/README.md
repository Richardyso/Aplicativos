# Contrauto - Sistema de Geração de Propostas e Contratos

Sistema local e offline para geração de propostas comerciais e contratos personalizados, com suporte para Brasil e Portugal.

## 🚀 Instalação

1. Clone o repositório ou extraia os arquivos
2. Instale as dependências:
```bash
pip install -r requirements.txt
```

## 💻 Executando o Sistema

```bash
cd contrauto
python main.py
```

## 📁 Estrutura do Projeto

```
contrauto/
├── main.py              # Arquivo principal da aplicação
├── requirements.txt     # Dependências do projeto
├── ui/                  # Interface gráfica
│   ├── main_window.py   # Janela principal
│   ├── wizard.py        # Wizard de criação de documentos
│   └── components/      # Componentes reutilizáveis
├── core/                # Lógica de negócio
│   ├── contract_builder.py    # Construtor de contratos
│   ├── proposal_builder.py    # Construtor de propostas
│   ├── language_selector.py   # Seletor de idioma BR/PT
│   └── placeholders.py        # Gerenciador de placeholders
├── models/              # Templates de documentos
│   ├── contrato_br.txt  # Template de contrato brasileiro
│   ├── contrato_pt.txt  # Template de contrato português
│   ├── proposta_br.txt  # Template de proposta brasileira
│   └── proposta_pt.txt  # Template de proposta portuguesa
└── export/              # Exportação de documentos
    └── pdf_generator.py # Gerador de PDF
```

## 🛠️ Funcionalidades

- ✅ Criação de propostas comerciais personalizadas
- ✅ Criação de contratos de prestação de serviços
- ✅ Suporte para Brasil (português brasileiro) e Portugal (português europeu)
- ✅ Interface gráfica intuitiva com wizard passo a passo
- ✅ Geração de PDF profissional
- ✅ 100% offline - seus dados ficam seguros em seu computador
- ✅ Templates customizáveis
- ✅ Sistema de placeholders avançado

## 📝 Como Usar

1. **Inicie o aplicativo** executando `python main.py`
2. **Escolha o tipo de documento** (Proposta ou Contrato)
3. **Selecione o país** (Brasil ou Portugal)
4. **Preencha os dados** seguindo o wizard passo a passo
5. **Revise o documento** na tela de preview
6. **Gere o PDF** clicando em Finalizar

Os documentos gerados são salvos na pasta `documentos_gerados/`.

## 🔧 Personalização

### Modificando Templates

Os templates estão na pasta `models/`. Você pode editar os arquivos `.txt` para personalizar o formato dos documentos. Use placeholders no formato `{nome_do_campo}` para campos que serão substituídos.

### Placeholders Especiais

- `{campo}` - Placeholder simples
- `{?condicao:texto}` - Texto condicional
- `{#lista:item}` - Iteração sobre listas

## 📋 Requisitos do Sistema

- Python 3.8 ou superior
- Windows, macOS ou Linux
- 100MB de espaço em disco

## 📦 Criando Executável Windows (.exe)

Para criar um executável que funciona sem Python instalado:

### Método 1: Usando o arquivo .bat (Recomendado)
```cmd
criar_executavel.bat
```

### Método 2: Usando PowerShell
```powershell
.\criar_executavel.ps1
```

### Método 3: Manualmente
```bash
pip install pyinstaller
python build_exe.py
```

Após a execução, você encontrará:
- `Contrauto.exe` na pasta `dist/`
- Uma pasta `Contrauto_Portable` pronta para distribuição

O executável gerado:
- ✅ Funciona sem Python instalado
- ✅ Inclui todas as dependências
- ✅ Mantém os templates de documentos
- ✅ É totalmente portátil

## 🤝 Suporte

Para questões ou sugestões, consulte a documentação ou abra uma issue no repositório.
