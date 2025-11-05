# ⚡ Eletricarlos 2.0

Sistema de Anotações de Manutenção Elétrica

## 📱 Sobre o Aplicativo

Eletricarlos é um aplicativo Android para gerenciamento de anotações de manutenção elétrica em diferentes estabelecimentos. Desenvolvido especialmente para Carlos e sua equipe.

## 🆕 Versão 2.0 - Novidades

### ✨ Recursos Principais

#### 1. Sistema de Login e Permissões
- **Carlos** (Administrador)
  - Acesso total a todos os locais
  - Pode editar e visualizar tudo
  
- **Dorian** (Visualização)
  - Acesso apenas a Dorys Prime
  - Somente visualização
  
- **Romario** (Visualização)
  - Acesso apenas a Pousada Paraíso
  - Somente visualização
  
- **Hotel** (Visualização)
  - Acesso a Hotel JR e Hotel Guarany
  - Somente visualização

#### 2. Locais Disponíveis
- 🏨 Pousada Paraíso
- 🏨 Dorys Prime
- 🏨 Hotel JR
- 🏨 Hotel Guarany

#### 3. Tipos de Manutenção
- 🔧 Manutenção Preventiva
- 🔧 Manutenção Corretiva
- 📝 Observação

#### 4. Funcionalidades
- ✅ Adicionar linhas dinamicamente (+)
- ✅ Remover linhas (-)
- ✅ Seleção de data por calendário
- ✅ Salvamento local em JSON
- ✅ **Sincronização automática com MongoDB Atlas** ☁️
- ✅ **Botão "Sincronizar com Nuvem"** para backup manual
- ✅ **Funciona offline** - sincroniza quando voltar online
- ✅ Campos editáveis com hints
- ✅ Botão Sair para trocar usuário
- ✅ **Migração automática de dados da v1.0**

## 🔄 Migração de Dados (v1.0 → v2.0)

### Importante! 
O Eletricarlos 2.0 **preserva automaticamente** todos os dados da versão anterior!

- ✅ Detecção automática de dados antigos
- ✅ Migração com confirmação do usuário
- ✅ Relatório detalhado antes de importar
- ✅ Dados originais preservados como backup
- ✅ Processo não destrutivo

**Mais detalhes:** Veja [MIGRATION.md](MIGRATION.md)

## 🔐 Segurança

### Medidas Implementadas
- 🔒 Network Security Config (HTTPS obrigatório)
- 🔒 ProGuard (ofuscação de código)
- 🔒 Backup criptografado
- 🔒 Dados sensíveis protegidos
- 🔒 Build de release seguro
- 🔒 Controle de acesso por usuário

**Mais detalhes:** Veja [SECURITY.md](SECURITY.md)

## 🏗️ Estrutura do Projeto

```
app/
├── src/main/
│   ├── java/com/example/eletricarlos/
│   │   ├── models/
│   │   │   ├── Entry.kt              # Modelo de entrada
│   │   │   ├── FormData.kt           # Dados do formulário
│   │   │   ├── User.kt               # Modelo de usuário
│   │   │   └── UserSession.kt        # Sessão do usuário
│   │   ├── utils/
│   │   │   ├── AuthManager.kt        # Autenticação
│   │   │   ├── DataManager.kt        # Gerenciamento de dados
│   │   │   └── LegacyDataMigration.kt # Migração v1.0 → v2.0
│   │   ├── LoginActivity.kt          # Tela de login
│   │   ├── MainActivity.kt           # Tela principal
│   │   ├── LocalActivity.kt          # Tela de local
│   │   └── FormActivity.kt           # Tela de formulário
│   └── res/
│       ├── layout/                    # Layouts XML
│       ├── values/                    # Strings e cores
│       └── xml/                       # Configurações de segurança
└── build.gradle.kts                   # Configurações do app
```

## 💾 Armazenamento de Dados

### 🏠 Armazenamento Local
- **SharedPreferences** (dados locais)
- Chave: `${localName}_${type}`
- Formato: JSON com Gson
- **Funciona offline**

### ☁️ Armazenamento em Nuvem (NOVO!)
- **MongoDB Atlas** (sincronização automática)
- Database: `eletricarlos_db`
- Collection: `maintenance_data`
- **Backup automático na nuvem**
- **Multi-dispositivo**

### Formato JSON
```json
{
  "localName": "Pousada Paraíso",
  "type": "Manutenção Preventiva",
  "entries": [
    {
      "numero": "123",
      "data": "04/11/2025",
      "observacao": "Troca de disjuntor"
    }
  ]
}
```

**Mais detalhes:** Veja [MONGODB_SETUP.md](MONGODB_SETUP.md)

## 🛠️ Tecnologias Utilizadas

- **Kotlin** - Linguagem principal
- **Android SDK** - Framework
- **SharedPreferences** - Armazenamento local
- **MongoDB Atlas** - Banco de dados na nuvem ☁️
- **MongoDB Kotlin Driver** - Cliente MongoDB
- **Coroutines** - Programação assíncrona
- **Gson** - Serialização JSON
- **Material Design** - Interface
- **ProGuard** - Ofuscação
- **Network Security Config** - Segurança de rede

## 📦 Build e Instalação

### Modo Debug
```bash
./gradlew assembleDebug
./gradlew installDebug
```

### Modo Release (Para Produção)
```bash
# 1. Gerar keystore (primeira vez)
keytool -genkey -v -keystore eletricarlos-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias eletricarlos

# 2. Descomentar signingConfig no build.gradle.kts

# 3. Gerar APK assinado
./gradlew assembleRelease

# 4. Instalar
./gradlew installRelease
```

## 📱 Como Usar

### 1. Login
- Selecione seu usuário no dropdown
- Digite a senha
- Clique em "Entrar"

### 2. Selecionar Local
- Escolha um dos locais disponíveis
- (Apenas locais permitidos para seu usuário serão exibidos)

### 3. Selecionar Tipo de Manutenção
- Manutenção Preventiva
- Manutenção Corretiva
- Observação

### 4. Preencher Dados
- **Nº**: Digite o número
- **Data**: Clique para abrir calendário
- **Observação**: Digite a observação
- Clique em **+** para adicionar mais linhas
- Clique em **-** para remover última linha
- Clique em **Salvar** para salvar os dados
- ☁️ **Sincronização automática** com MongoDB Atlas

### 5. Sincronizar com Nuvem
- Clique no botão **"Sincronizar com Nuvem"** na tela principal
- Envia todos os dados locais para MongoDB Atlas
- Mostra quantos conjuntos de dados foram sincronizados
- ☁️ **Backup automático na nuvem!**

### 6. Sair
- Clique no botão "Sair" na tela principal
- Permite trocar de usuário

## 👥 Credenciais Padrão

| Usuário | Senha | Tipo | Locais |
|---------|-------|------|--------|
| Carlos | olavo3043 | Admin | Todos |
| Dorian | dorys | Visualização | Dorys Prime |
| Romario | paraiso | Visualização | Pousada Paraíso |
| Hotel | hotel | Visualização | Hotel JR, Hotel Guarany |

## 🎨 Interface

- Fundo branco clean
- Botões roxos/lavanda arredondados
- Campos grandes e clicáveis
- Hints que desaparecem ao digitar
- Calendário nativo do Android
- Design intuitivo e simples

## 📋 Campos do Formulário

- **Nº**: Campo numérico (máx 6 dígitos)
- **Data**: Seleção por calendário (DD/MM/AAAA)
- **Observação**: Texto livre

## 🔄 Compatibilidade

- **Versão Mínima**: Android 7.0 (API 24)
- **Versão Alvo**: Android 14 (API 36)
- **Migração**: v1.0 → v2.0 automática

## 📝 Changelog

### v2.0 (Novembro 2025)
- ✨ Sistema de login e permissões
- ✨ Controle de acesso por usuário
- ✨ **Sincronização automática com MongoDB Atlas** ☁️
- ✨ **Botão "Sincronizar com Nuvem"** para backup manual
- ✨ **Funciona offline** - sincroniza quando voltar online
- ✨ Interface modernizada com fundo branco
- ✨ Campos maiores e mais clicáveis
- ✨ Botão Sair
- ✨ Segurança aprimorada
- ✨ Migração automática de dados da v1.0
- ✨ Modo visualização para usuários não-admin
- ✨ ProGuard para ofuscação
- ✨ Network Security Config

### v1.0 (Versão Original)
- Funcionalidades básicas de anotação
- 4 locais
- 3 tipos de manutenção
- Salvamento local

## 🚀 Próximos Passos (Futuro)

- [x] Sincronização na nuvem (MongoDB Atlas) ✅
- [ ] Sincronização bidirecional (baixar da nuvem)
- [ ] Biometria para login
- [ ] Exportar relatórios em PDF
- [ ] Gráficos e estatísticas
- [ ] Notificações de manutenção
- [ ] Fotos das manutenções
- [ ] Dashboard web para visualizar dados
- [ ] Sincronização em tempo real

## 📞 Suporte

Para dúvidas ou problemas:
1. Verificar [MIGRATION.md](MIGRATION.md) para questões de migração
2. Verificar [SECURITY.md](SECURITY.md) para questões de segurança
3. Verificar [MONGODB_SETUP.md](MONGODB_SETUP.md) para MongoDB/sincronização
4. Contatar o desenvolvedor

## 📄 Licença

Aplicativo desenvolvido para uso interno da Eletricarlos.

---

**Eletricarlos 2.0** - Gestão Profissional de Manutenção Elétrica ⚡

