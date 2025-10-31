// Content Script - Injeção no WhatsApp Web
console.log('💚 WhatsApp Bulk Sender - Content Script injetado!');

// Espera WhatsApp carregar
waitForWhatsAppLoad().then(() => {
  console.log('✅ WhatsApp Web carregado!');
  initBulkSender();
});

// Aguarda carregamento do WhatsApp
function waitForWhatsAppLoad() {
  return new Promise((resolve) => {
    const checkInterval = setInterval(() => {
      // Verifica se a caixa de texto está disponível
      const inputBox = document.querySelector('[contenteditable="true"][data-tab="10"]');
      if (inputBox) {
        clearInterval(checkInterval);
        resolve();
      }
    }, 1000);
  });
}

// Inicializa o Bulk Sender
function initBulkSender() {
  createSidebarButton();
  listenForMessages();
}

// Cria botão flutuante para abrir sidebar
function createSidebarButton() {
  const button = document.createElement('div');
  button.id = 'bulk-sender-btn';
  button.innerHTML = `
    <svg width="32" height="32" viewBox="0 0 24 24" fill="white">
      <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
      <path d="M7 9h2v2H7zm4 0h2v2h-2zm4 0h2v2h-2z"/>
    </svg>
    <span class="badge" id="bulk-badge">0</span>
  `;
  button.title = 'WhatsApp Bulk Sender';
  document.body.appendChild(button);
  
  button.onclick = toggleSidebar;
}

// Sidebar principal
let sidebarOpen = false;
let sidebarIframe = null;

function toggleSidebar() {
  if (sidebarOpen) {
    closeSidebar();
  } else {
    openSidebar();
  }
}

function openSidebar() {
  if (!sidebarIframe) {
    sidebarIframe = document.createElement('iframe');
    sidebarIframe.id = 'bulk-sender-sidebar';
    sidebarIframe.src = chrome.runtime.getURL('sidebar/sidebar.html');
    document.body.appendChild(sidebarIframe);
    
    setTimeout(() => {
      sidebarIframe.classList.add('open');
    }, 100);
  } else {
    sidebarIframe.classList.add('open');
  }
  
  sidebarOpen = true;
}

function closeSidebar() {
  if (sidebarIframe) {
    sidebarIframe.classList.remove('open');
  }
  sidebarOpen = false;
}

// Escuta mensagens do background
function listenForMessages() {
  chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    console.log('📬 Content recebeu:', request.action);
    
    switch (request.action) {
      case 'SEND_MESSAGE':
        sendWhatsAppMessage(request.data)
          .then(result => sendResponse({ success: true, result }))
          .catch(error => sendResponse({ success: false, error: error.message }));
        return true; // Async
        
      case 'CHECK_NUMBER':
        checkNumberExists(request.data.number)
          .then(exists => sendResponse({ success: true, exists }))
          .catch(error => sendResponse({ success: false, error: error.message }));
        return true;
        
      case 'GET_CONTACTS':
        getWhatsAppContacts()
          .then(contacts => sendResponse({ success: true, contacts }))
          .catch(error => sendResponse({ success: false, error: error.message }));
        return true;
        
      default:
        sendResponse({ success: false, error: 'Ação desconhecida' });
    }
  });
}

// Envia mensagem no WhatsApp
async function sendWhatsAppMessage(data) {
  console.log('📤 Enviando mensagem para:', data.number);
  
  try {
    // Abre conversa
    await openChat(data.number);
    await delay(1500);
    
    // Digita mensagem
    await typeMessage(data.message);
    await delay(800);
    
    // Anexa arquivo se houver
    if (data.attachment) {
      await attachFile(data.attachment);
      await delay(1000);
    }
    
    // Envia
    await clickSendButton();
    await delay(500);
    
    // Notifica sucesso
    chrome.runtime.sendMessage({
      action: 'MESSAGE_SENT',
      data: { index: data.index, number: data.number }
    });
    
    console.log('✅ Mensagem enviada para:', data.number);
    return { sent: true };
    
  } catch (error) {
    console.error('❌ Erro ao enviar mensagem:', error);
    
    // Notifica falha
    chrome.runtime.sendMessage({
      action: 'MESSAGE_FAILED',
      data: { index: data.index, number: data.number, error: error.message }
    });
    
    throw error;
  }
}

// Abre chat com número
function openChat(number) {
  return new Promise((resolve, reject) => {
    // Remove formatação do número
    const cleanNumber = number.replace(/\D/g, '');
    
    // URL do WhatsApp
    const url = `https://web.whatsapp.com/send?phone=${cleanNumber}&text=`;
    
    // Abre chat
    window.location.href = url;
    
    // Aguarda chat abrir
    const checkInterval = setInterval(() => {
      const inputBox = document.querySelector('[contenteditable="true"][data-tab="10"]');
      if (inputBox && inputBox.offsetParent !== null) {
        clearInterval(checkInterval);
        resolve();
      }
    }, 500);
    
    // Timeout 15s
    setTimeout(() => {
      clearInterval(checkInterval);
      reject(new Error('Timeout ao abrir chat'));
    }, 15000);
  });
}

// Digita mensagem
function typeMessage(message) {
  return new Promise((resolve, reject) => {
    const inputBox = document.querySelector('[contenteditable="true"][data-tab="10"]');
    
    if (!inputBox) {
      reject(new Error('Caixa de texto não encontrada'));
      return;
    }
    
    // Processa variáveis
    const processedMessage = processMessageVariables(message);
    
    // Insere texto
    inputBox.focus();
    document.execCommand('insertText', false, processedMessage);
    
    // Dispara eventos
    inputBox.dispatchEvent(new Event('input', { bubbles: true }));
    inputBox.dispatchEvent(new Event('change', { bubbles: true }));
    
    resolve();
  });
}

// Processa variáveis na mensagem
function processMessageVariables(message) {
  const now = new Date();
  const variables = {
    '{nome}': getCurrentContactName(),
    '{data}': now.toLocaleDateString('pt-BR'),
    '{hora}': now.toLocaleTimeString('pt-BR'),
    '{dia}': now.getDate(),
    '{mes}': now.getMonth() + 1,
    '{ano}': now.getFullYear()
  };
  
  let processed = message;
  Object.keys(variables).forEach(key => {
    processed = processed.replace(new RegExp(key, 'gi'), variables[key]);
  });
  
  return processed;
}

// Pega nome do contato atual
function getCurrentContactName() {
  const nameElement = document.querySelector('[data-testid="conversation-info-header-chat-title"]');
  return nameElement ? nameElement.innerText : 'Cliente';
}

// Clica no botão enviar
function clickSendButton() {
  return new Promise((resolve, reject) => {
    const sendButton = document.querySelector('[data-testid="send"]');
    
    if (!sendButton) {
      reject(new Error('Botão enviar não encontrado'));
      return;
    }
    
    sendButton.click();
    resolve();
  });
}

// Anexa arquivo
async function attachFile(fileData) {
  // TODO: Implementar anexo de arquivos
  console.log('📎 Anexando arquivo...');
}

// Verifica se número existe
async function checkNumberExists(number) {
  try {
    await openChat(number);
    await delay(2000);
    
    // Verifica se há erro
    const errorElement = document.querySelector('[data-testid="alert-phone-number-error"]');
    return !errorElement;
    
  } catch (error) {
    return false;
  }
}

// Pega contatos do WhatsApp
function getWhatsAppContacts() {
  return new Promise((resolve) => {
    const contacts = [];
    const contactElements = document.querySelectorAll('[data-testid^="cell-frame-title"]');
    
    contactElements.forEach(el => {
      const name = el.innerText;
      if (name) {
        contacts.push({ name });
      }
    });
    
    resolve(contacts);
  });
}

// Utilitário: delay
function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

// Atualiza badge do botão
chrome.runtime.onMessage.addListener((request) => {
  if (request.action === 'STATE_UPDATE') {
    const badge = document.getElementById('bulk-badge');
    if (badge && request.state) {
      badge.textContent = request.state.pending || 0;
      badge.style.display = request.state.isRunning ? 'block' : 'none';
    }
  }
});

console.log('✅ Content Script pronto!');

