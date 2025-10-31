// Popup JavaScript
console.log('📱 Popup carregado!');

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
  initPopup();
  setupEventListeners();
  checkSendingState();
});

function initPopup() {
  console.log('✅ Popup inicializado');
}

// Event Listeners
function setupEventListeners() {
  document.getElementById('openWhatsAppBtn').onclick = openWhatsApp;
  document.getElementById('howToUseBtn').onclick = showHowToUse;
  document.getElementById('githubBtn').onclick = openGitHub;
  
  // Escuta atualizações do background
  chrome.runtime.onMessage.addListener((request) => {
    if (request.action === 'STATE_UPDATE') {
      updatePopupState(request.state);
    }
  });
}

// Abre WhatsApp Web
function openWhatsApp() {
  chrome.tabs.create({
    url: 'https://web.whatsapp.com'
  });
}

// Verifica estado do envio
async function checkSendingState() {
  try {
    const response = await chrome.runtime.sendMessage({
      action: 'GET_SENDING_STATE'
    });
    
    if (response && response.state) {
      updatePopupState(response.state);
    }
  } catch (error) {
    console.error('Erro ao verificar estado:', error);
  }
}

// Atualiza estado do popup
function updatePopupState(state) {
  if (!state || !state.isRunning) {
    showIdleState();
    return;
  }
  
  showSendingState(state);
  updateStats(state);
  updateProgress(state);
}

// Mostra estado idle
function showIdleState() {
  document.getElementById('statusTitle').textContent = 'Pronto para usar';
  document.getElementById('statusDescription').textContent = 
    'Abra o WhatsApp Web e clique no botão verde para começar';
  
  document.getElementById('statsGrid').style.display = 'none';
  document.getElementById('progressContainer').style.display = 'none';
}

// Mostra estado enviando
function showSendingState(state) {
  const status = state.isPaused ? 'Pausado' : 'Enviando...';
  const description = state.isPaused 
    ? 'O envio está pausado. Volte ao WhatsApp para retomar.'
    : `Enviando mensagens (${state.sent} de ${state.totalMessages})`;
  
  document.getElementById('statusTitle').textContent = status;
  document.getElementById('statusDescription').textContent = description;
  
  document.getElementById('statsGrid').style.display = 'grid';
  document.getElementById('progressContainer').style.display = 'block';
}

// Atualiza estatísticas
function updateStats(state) {
  document.getElementById('sentStat').textContent = state.sent || 0;
  document.getElementById('failedStat').textContent = state.failed || 0;
  document.getElementById('pendingStat').textContent = state.pending || 0;
}

// Atualiza progresso
function updateProgress(state) {
  const progress = ((state.sent + state.failed) / state.totalMessages) * 100;
  
  document.getElementById('progressFill').style.width = `${progress}%`;
  document.getElementById('progressPercent').textContent = `${Math.round(progress)}%`;
}

// Como usar
function showHowToUse() {
  const howToUse = `
📱 COMO USAR:

1. Abra o WhatsApp Web
2. Clique no botão verde flutuante
3. Cole ou importe sua lista de números
4. Digite sua mensagem (use variáveis como {nome})
5. Configure o intervalo entre mensagens
6. Clique em "Iniciar Envio"

💡 DICAS:
• Use intervalo de 8-12 segundos
• Ative "intervalo aleatório" para mais segurança
• Salve mensagens frequentes como templates
• Acompanhe os envios nos relatórios

⚠️ IMPORTANTE:
• Não envie spam
• Respeite as regras do WhatsApp
• Use com responsabilidade
  `;
  
  alert(howToUse);
}

// Abre GitHub
function openGitHub() {
  chrome.tabs.create({
    url: 'https://github.com/your-repo/whatsapp-bulk-sender'
  });
}

console.log('✅ Popup JS pronto!');

