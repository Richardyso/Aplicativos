// Background Service Worker - Gerenciamento de envios
console.log('🚀 WhatsApp Bulk Sender - Background Worker iniciado');

// Estado global do envio
let sendingState = {
  isRunning: false,
  isPaused: false,
  currentIndex: 0,
  totalMessages: 0,
  sent: 0,
  failed: 0,
  pending: 0,
  queue: [],
  startTime: null,
  tabId: null
};

// Mensagens entre componentes
chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  console.log('📨 Mensagem recebida:', request.action);
  
  switch (request.action) {
    case 'START_BULK_SEND':
      handleStartBulkSend(request.data, sender);
      sendResponse({ success: true });
      break;
      
    case 'PAUSE_BULK_SEND':
      sendingState.isPaused = true;
      sendResponse({ success: true, state: sendingState });
      break;
      
    case 'RESUME_BULK_SEND':
      sendingState.isPaused = false;
      resumeSending();
      sendResponse({ success: true });
      break;
      
    case 'STOP_BULK_SEND':
      stopSending();
      sendResponse({ success: true });
      break;
      
    case 'GET_SENDING_STATE':
      sendResponse({ success: true, state: sendingState });
      break;
      
    case 'MESSAGE_SENT':
      handleMessageSent(request.data);
      sendResponse({ success: true });
      break;
      
    case 'MESSAGE_FAILED':
      handleMessageFailed(request.data);
      sendResponse({ success: true });
      break;
      
    default:
      sendResponse({ success: false, error: 'Ação desconhecida' });
  }
  
  return true; // Mantém canal aberto para resposta assíncrona
});

// Inicia o envio em massa
async function handleStartBulkSend(data, sender) {
  console.log('▶️ Iniciando envio em massa...', data);
  
  sendingState = {
    isRunning: true,
    isPaused: false,
    currentIndex: 0,
    totalMessages: data.contacts.length,
    sent: 0,
    failed: 0,
    pending: data.contacts.length,
    queue: data.contacts.map((contact, index) => ({
      ...contact,
      index,
      status: 'pending',
      attempts: 0,
      sentAt: null
    })),
    settings: data.settings,
    startTime: Date.now(),
    tabId: sender.tab?.id
  };
  
  // Salva estado inicial
  await saveState();
  
  // Inicia processamento
  processQueue();
}

// Processa a fila de mensagens
async function processQueue() {
  if (!sendingState.isRunning || sendingState.isPaused) {
    return;
  }
  
  const currentMessage = sendingState.queue[sendingState.currentIndex];
  
  if (!currentMessage || currentMessage.status !== 'pending') {
    // Verifica se há mais mensagens pendentes
    const nextPending = sendingState.queue.findIndex(msg => msg.status === 'pending');
    
    if (nextPending === -1) {
      // Finalizado!
      await finalizeSending();
      return;
    }
    
    sendingState.currentIndex = nextPending;
    processQueue();
    return;
  }
  
  // Envia mensagem para o content script
  try {
    await sendMessageToWhatsApp(currentMessage);
    
    // Aguarda intervalo configurado
    const delay = sendingState.settings.randomDelay 
      ? getRandomDelay(sendingState.settings.minDelay, sendingState.settings.maxDelay)
      : sendingState.settings.delay;
    
    setTimeout(() => {
      sendingState.currentIndex++;
      processQueue();
    }, delay);
    
  } catch (error) {
    console.error('❌ Erro ao enviar mensagem:', error);
    currentMessage.status = 'failed';
    currentMessage.error = error.message;
    sendingState.failed++;
    sendingState.pending--;
    
    sendingState.currentIndex++;
    processQueue();
  }
  
  // Atualiza UI
  updateUI();
  await saveState();
}

// Envia mensagem para o WhatsApp
function sendMessageToWhatsApp(message) {
  return new Promise((resolve, reject) => {
    if (!sendingState.tabId) {
      reject(new Error('Tab do WhatsApp não encontrada'));
      return;
    }
    
    chrome.tabs.sendMessage(sendingState.tabId, {
      action: 'SEND_MESSAGE',
      data: message
    }, (response) => {
      if (chrome.runtime.lastError) {
        reject(new Error(chrome.runtime.lastError.message));
        return;
      }
      
      if (response && response.success) {
        resolve(response);
      } else {
        reject(new Error(response?.error || 'Erro ao enviar mensagem'));
      }
    });
  });
}

// Marca mensagem como enviada
function handleMessageSent(data) {
  const message = sendingState.queue.find(m => m.index === data.index);
  if (message) {
    message.status = 'sent';
    message.sentAt = Date.now();
    sendingState.sent++;
    sendingState.pending--;
  }
  updateUI();
  saveState();
}

// Marca mensagem como falha
function handleMessageFailed(data) {
  const message = sendingState.queue.find(m => m.index === data.index);
  if (message) {
    message.status = 'failed';
    message.error = data.error;
    message.attempts++;
    sendingState.failed++;
    sendingState.pending--;
  }
  updateUI();
  saveState();
}

// Para o envio
function stopSending() {
  sendingState.isRunning = false;
  sendingState.isPaused = false;
  console.log('⏹️ Envio interrompido');
  updateUI();
  saveState();
}

// Retoma o envio
function resumeSending() {
  if (sendingState.isRunning && sendingState.isPaused) {
    sendingState.isPaused = false;
    console.log('▶️ Envio retomado');
    processQueue();
  }
}

// Finaliza o envio
async function finalizeSending() {
  sendingState.isRunning = false;
  sendingState.endTime = Date.now();
  
  const duration = sendingState.endTime - sendingState.startTime;
  console.log(`✅ Envio finalizado! Tempo total: ${Math.floor(duration / 1000)}s`);
  console.log(`📊 Enviadas: ${sendingState.sent} | Falhas: ${sendingState.failed}`);
  
  // Gera relatório
  await generateReport();
  
  updateUI();
  await saveState();
}

// Gera relatório do envio
async function generateReport() {
  const report = {
    startTime: sendingState.startTime,
    endTime: sendingState.endTime,
    duration: sendingState.endTime - sendingState.startTime,
    total: sendingState.totalMessages,
    sent: sendingState.sent,
    failed: sendingState.failed,
    messages: sendingState.queue
  };
  
  // Salva relatório
  const reports = await chrome.storage.local.get('reports') || { reports: [] };
  reports.reports = reports.reports || [];
  reports.reports.unshift(report);
  
  // Mantém apenas últimos 50 relatórios
  if (reports.reports.length > 50) {
    reports.reports = reports.reports.slice(0, 50);
  }
  
  await chrome.storage.local.set({ reports: reports.reports });
  console.log('📄 Relatório salvo');
}

// Atualiza UI (envia para popup/sidebar)
function updateUI() {
  chrome.runtime.sendMessage({
    action: 'STATE_UPDATE',
    state: sendingState
  }).catch(() => {
    // Popup pode estar fechado
  });
}

// Salva estado no storage
async function saveState() {
  await chrome.storage.local.set({ sendingState });
}

// Restaura estado ao iniciar
async function restoreState() {
  const stored = await chrome.storage.local.get('sendingState');
  if (stored.sendingState && stored.sendingState.isRunning) {
    sendingState = stored.sendingState;
    sendingState.isPaused = true; // Pausa ao restaurar
    console.log('🔄 Estado restaurado do storage');
  }
}

// Calcula delay aleatório
function getRandomDelay(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

// Inicialização
restoreState();

console.log('✅ Background Worker pronto!');

