// Sidebar JavaScript - Lógica completa
console.log('📱 Sidebar carregada!');

// Estado da aplicação
let appState = {
  contacts: [],
  message: '',
  settings: {
    delay: 8000,
    randomDelay: false,
    minDelay: 5000,
    maxDelay: 12000
  },
  templates: [],
  reports: []
};

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
  initializeApp();
  loadStoredData();
  setupEventListeners();
  setupTabs();
});

// Inicializa aplicação
function initializeApp() {
  console.log('✅ Inicializando aplicação...');
  updateUI();
}

// Carrega dados do storage
async function loadStoredData() {
  try {
    const stored = await chrome.storage.local.get(['templates', 'settings']);
    
    if (stored.templates) {
      appState.templates = stored.templates;
      renderTemplates();
    }
    
    if (stored.settings) {
      appState.settings = stored.settings;
      applySettings();
    }
    
    loadReports();
  } catch (error) {
    console.error('Erro ao carregar dados:', error);
  }
}

// Aplica configurações salvas
function applySettings() {
  document.getElementById('delayInput').value = appState.settings.delay / 1000;
  document.getElementById('randomDelayCheck').checked = appState.settings.randomDelay;
  
  if (appState.settings.randomDelay) {
    document.getElementById('randomDelayConfig').style.display = 'block';
    document.getElementById('minDelayInput').value = appState.settings.minDelay / 1000;
    document.getElementById('maxDelayInput').value = appState.settings.maxDelay / 1000;
  }
}

// Setup de Event Listeners
function setupEventListeners() {
  // Fechar sidebar
  document.getElementById('closeBtn').onclick = closeSidebar;
  
  // Mensagem
  const messageInput = document.getElementById('messageInput');
  messageInput.oninput = (e) => {
    appState.message = e.target.value;
    updateCharCount();
    validateForm();
  };
  
  // Contatos
  document.getElementById('pasteContactsBtn').onclick = pasteContacts;
  document.getElementById('importFileBtn').onclick = () => {
    document.getElementById('fileInput').click();
  };
  document.getElementById('fileInput').onchange = handleFileImport;
  document.getElementById('clearContactsBtn').onclick = clearContacts;
  
  // Configurações
  document.getElementById('delayInput').onchange = (e) => {
    appState.settings.delay = parseInt(e.target.value) * 1000;
    saveSettings();
  };
  
  document.getElementById('randomDelayCheck').onchange = (e) => {
    appState.settings.randomDelay = e.target.checked;
    document.getElementById('randomDelayConfig').style.display = 
      e.target.checked ? 'block' : 'none';
    saveSettings();
  };
  
  document.getElementById('minDelayInput').onchange = (e) => {
    appState.settings.minDelay = parseInt(e.target.value) * 1000;
    saveSettings();
  };
  
  document.getElementById('maxDelayInput').onchange = (e) => {
    appState.settings.maxDelay = parseInt(e.target.value) * 1000;
    saveSettings();
  };
  
  // Ações
  document.getElementById('startBtn').onclick = startBulkSend;
  document.getElementById('pauseBtn').onclick = pauseBulkSend;
  document.getElementById('stopBtn').onclick = stopBulkSend;
  
  // Templates
  document.getElementById('newTemplateBtn').onclick = () => {
    document.getElementById('templateModal').style.display = 'flex';
  };
  
  document.getElementById('closeTemplateModal').onclick = closeTemplateModal;
  document.getElementById('cancelTemplateBtn').onclick = closeTemplateModal;
  document.getElementById('saveTemplateBtn').onclick = saveTemplate;
  
  // Escuta atualizações do background
  chrome.runtime.onMessage.addListener(handleBackgroundMessage);
}

// Setup de Tabs
function setupTabs() {
  const tabs = document.querySelectorAll('.tab');
  const contents = document.querySelectorAll('.tab-content');
  
  tabs.forEach(tab => {
    tab.onclick = () => {
      const targetTab = tab.dataset.tab;
      
      // Remove active de todos
      tabs.forEach(t => t.classList.remove('active'));
      contents.forEach(c => c.classList.remove('active'));
      
      // Adiciona active no clicado
      tab.classList.add('active');
      document.querySelector(`[data-content="${targetTab}"]`).classList.add('active');
      
      // Carrega dados se necessário
      if (targetTab === 'reports') {
        loadReports();
      }
    };
  });
}

// Cola contatos da área de transferência
async function pasteContacts() {
  try {
    const text = await navigator.clipboard.readText();
    const numbers = extractNumbers(text);
    
    if (numbers.length === 0) {
      alert('Nenhum número válido encontrado na área de transferência!');
      return;
    }
    
    addContacts(numbers);
    alert(`${numbers.length} contato(s) adicionado(s)!`);
    
  } catch (error) {
    console.error('Erro ao colar contatos:', error);
    alert('Erro ao ler área de transferência. Verifique as permissões.');
  }
}

// Importa arquivo
function handleFileImport(e) {
  const file = e.target.files[0];
  if (!file) return;
  
  const reader = new FileReader();
  reader.onload = (event) => {
    const text = event.target.result;
    const numbers = extractNumbers(text);
    
    if (numbers.length === 0) {
      alert('Nenhum número válido encontrado no arquivo!');
      return;
    }
    
    addContacts(numbers);
    alert(`${numbers.length} contato(s) importado(s)!`);
  };
  
  reader.readAsText(file);
}

// Extrai números de um texto
function extractNumbers(text) {
  // Regex para números de telefone (aceita vários formatos)
  const regex = /(?:\+?55\s?)?(?:\(?0?[1-9]{2}\)?\s?)?(?:9\s?)?\d{4}[-\s]?\d{4}/g;
  const matches = text.match(regex) || [];
  
  // Remove duplicatas e limpa números
  const cleaned = [...new Set(matches.map(num => cleanNumber(num)))];
  
  return cleaned.filter(num => num.length >= 10);
}

// Limpa número (remove formatação)
function cleanNumber(number) {
  return number.replace(/\D/g, '');
}

// Adiciona contatos
function addContacts(numbers) {
  numbers.forEach(number => {
    if (!appState.contacts.find(c => c.number === number)) {
      appState.contacts.push({
        number,
        name: `Contato ${appState.contacts.length + 1}`
      });
    }
  });
  
  renderContacts();
  validateForm();
}

// Remove contato
function removeContact(number) {
  appState.contacts = appState.contacts.filter(c => c.number !== number);
  renderContacts();
  validateForm();
}

// Limpa todos os contatos
function clearContacts() {
  if (!confirm('Deseja realmente limpar todos os contatos?')) return;
  
  appState.contacts = [];
  renderContacts();
  validateForm();
}

// Renderiza lista de contatos
function renderContacts() {
  const contactList = document.getElementById('contactList');
  const contactSummary = document.getElementById('contactSummary');
  const totalContacts = document.getElementById('totalContacts');
  
  if (appState.contacts.length === 0) {
    contactList.innerHTML = `
      <div class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="#ccc">
          <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
        </svg>
        <p>Nenhum contato adicionado</p>
        <p class="tip">Cole uma lista de números ou importe um arquivo</p>
      </div>
    `;
    contactSummary.style.display = 'none';
  } else {
    contactList.innerHTML = appState.contacts.map(contact => `
      <div class="contact-item">
        <div>
          <span class="phone">${formatPhone(contact.number)}</span>
        </div>
        <button class="remove-btn" onclick="removeContact('${contact.number}')">×</button>
      </div>
    `).join('');
    
    contactSummary.style.display = 'flex';
    totalContacts.textContent = appState.contacts.length;
  }
}

// Formata número de telefone
function formatPhone(number) {
  if (number.length === 11) {
    return `(${number.substr(0, 2)}) ${number.substr(2, 5)}-${number.substr(7)}`;
  }
  return number;
}

// Atualiza contagem de caracteres
function updateCharCount() {
  const count = appState.message.length;
  document.getElementById('charCount').textContent = `${count} caracteres`;
}

// Valida formulário
function validateForm() {
  const startBtn = document.getElementById('startBtn');
  const isValid = appState.message.trim().length > 0 && appState.contacts.length > 0;
  
  startBtn.disabled = !isValid;
}

// Inicia envio em massa
async function startBulkSend() {
  console.log('🚀 Iniciando envio em massa...');
  
  if (!confirm(`Deseja enviar mensagem para ${appState.contacts.length} contato(s)?`)) {
    return;
  }
  
  // Prepara dados
  const sendData = {
    contacts: appState.contacts.map(contact => ({
      ...contact,
      message: appState.message
    })),
    settings: appState.settings
  };
  
  // Envia para background
  chrome.runtime.sendMessage({
    action: 'START_BULK_SEND',
    data: sendData
  }, (response) => {
    if (response.success) {
      // Mostra progresso
      document.getElementById('progressSection').style.display = 'block';
      document.getElementById('actionBar').style.display = 'none';
    }
  });
}

// Pausa envio
function pauseBulkSend() {
  chrome.runtime.sendMessage({
    action: 'PAUSE_BULK_SEND'
  }, (response) => {
    if (response.success) {
      const pauseBtn = document.getElementById('pauseBtn');
      pauseBtn.innerHTML = `
        <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
          <path d="M8 5v14l11-7z"/>
        </svg>
        Retomar
      `;
      pauseBtn.onclick = resumeBulkSend;
    }
  });
}

// Retoma envio
function resumeBulkSend() {
  chrome.runtime.sendMessage({
    action: 'RESUME_BULK_SEND'
  }, (response) => {
    if (response.success) {
      const pauseBtn = document.getElementById('pauseBtn');
      pauseBtn.innerHTML = `
        <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
          <path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z"/>
        </svg>
        Pausar
      `;
      pauseBtn.onclick = pauseBulkSend;
    }
  });
}

// Para envio
function stopBulkSend() {
  if (!confirm('Deseja realmente parar o envio?')) return;
  
  chrome.runtime.sendMessage({
    action: 'STOP_BULK_SEND'
  }, (response) => {
    if (response.success) {
      document.getElementById('progressSection').style.display = 'none';
      document.getElementById('actionBar').style.display = 'block';
    }
  });
}

// Atualiza progresso
function updateProgress(state) {
  if (!state) return;
  
  const total = state.totalMessages;
  const sent = state.sent;
  const failed = state.failed;
  const pending = state.pending;
  const progress = ((sent + failed) / total) * 100;
  
  document.getElementById('sentCount').textContent = sent;
  document.getElementById('failedCount').textContent = failed;
  document.getElementById('pendingCount').textContent = pending;
  document.getElementById('progressFill').style.width = `${progress}%`;
  document.getElementById('progressText').textContent = `${Math.round(progress)}%`;
  
  // Se finalizado
  if (!state.isRunning && !state.isPaused) {
    setTimeout(() => {
      document.getElementById('progressSection').style.display = 'none';
      document.getElementById('actionBar').style.display = 'block';
      alert(`Envio finalizado!\n\nEnviadas: ${sent}\nFalhas: ${failed}`);
    }, 2000);
  }
}

// Recebe mensagens do background
function handleBackgroundMessage(request, sender, sendResponse) {
  if (request.action === 'STATE_UPDATE') {
    updateProgress(request.state);
  }
}

// Salva configurações
async function saveSettings() {
  await chrome.storage.local.set({ settings: appState.settings });
}

// Templates
async function saveTemplate() {
  const name = document.getElementById('templateName').value;
  const message = document.getElementById('templateMessage').value;
  
  if (!name || !message) {
    alert('Preencha nome e mensagem!');
    return;
  }
  
  const template = {
    id: Date.now(),
    name,
    message,
    createdAt: new Date().toISOString()
  };
  
  appState.templates.push(template);
  await chrome.storage.local.set({ templates: appState.templates });
  
  renderTemplates();
  closeTemplateModal();
  alert('Template salvo com sucesso!');
}

function closeTemplateModal() {
  document.getElementById('templateModal').style.display = 'none';
  document.getElementById('templateName').value = '';
  document.getElementById('templateMessage').value = '';
}

function renderTemplates() {
  const templatesList = document.getElementById('templatesList');
  
  if (appState.templates.length === 0) {
    templatesList.innerHTML = `
      <div class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="#ccc">
          <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
        </svg>
        <p>Nenhum template salvo</p>
      </div>
    `;
    return;
  }
  
  templatesList.innerHTML = appState.templates.map(template => `
    <div class="template-card" onclick="useTemplate(${template.id})">
      <h4>${template.name}</h4>
      <p>${template.message}</p>
      <div class="template-actions">
        <button class="btn btn-sm btn-primary" onclick="event.stopPropagation(); useTemplate(${template.id})">Usar</button>
        <button class="btn btn-sm btn-danger" onclick="event.stopPropagation(); deleteTemplate(${template.id})">Excluir</button>
      </div>
    </div>
  `).join('');
}

function useTemplate(id) {
  const template = appState.templates.find(t => t.id === id);
  if (template) {
    document.getElementById('messageInput').value = template.message;
    appState.message = template.message;
    updateCharCount();
    validateForm();
    
    // Volta para tab de envio
    document.querySelector('[data-tab="send"]').click();
  }
}

async function deleteTemplate(id) {
  if (!confirm('Deseja excluir este template?')) return;
  
  appState.templates = appState.templates.filter(t => t.id !== id);
  await chrome.storage.local.set({ templates: appState.templates });
  renderTemplates();
}

// Relatórios
async function loadReports() {
  const stored = await chrome.storage.local.get('reports');
  appState.reports = stored.reports || [];
  renderReports();
}

function renderReports() {
  const reportsList = document.getElementById('reportsList');
  
  if (appState.reports.length === 0) {
    reportsList.innerHTML = `
      <div class="empty-state">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="#ccc">
          <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
        </svg>
        <p>Nenhum relatório disponível</p>
      </div>
    `;
    return;
  }
  
  reportsList.innerHTML = appState.reports.map(report => `
    <div class="report-card">
      <div class="report-header">
        <span class="report-date">${new Date(report.startTime).toLocaleString('pt-BR')}</span>
        <span class="report-duration">${formatDuration(report.duration)}</span>
      </div>
      <div class="report-stats">
        <span style="color: #27ae60">✓ ${report.sent} enviadas</span>
        <span style="color: #e74c3c">✗ ${report.failed} falhas</span>
        <span>Total: ${report.total}</span>
      </div>
    </div>
  `).join('');
}

function formatDuration(ms) {
  const seconds = Math.floor(ms / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  
  if (hours > 0) return `${hours}h ${minutes % 60}m`;
  if (minutes > 0) return `${minutes}m ${seconds % 60}s`;
  return `${seconds}s`;
}

// Fecha sidebar
function closeSidebar() {
  window.parent.postMessage({ action: 'CLOSE_SIDEBAR' }, '*');
}

function updateUI() {
  renderContacts();
  validateForm();
}

console.log('✅ Sidebar JS pronta!');

