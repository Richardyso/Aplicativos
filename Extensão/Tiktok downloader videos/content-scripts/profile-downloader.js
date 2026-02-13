// Profile Downloader - Baixa todos os vídeos de um perfil do TikTok
(function() {
    'use strict';

    const chrome = globalThis.chrome || globalThis.browser;
    
    // Detecta se estamos em uma página de perfil
    function isProfilePage() {
        const path = window.location.pathname;
        return path.startsWith('/@') && !path.includes('/video/') && !path.includes('/photo/');
    }

    // Cria o botão de download em lote
    function createBulkDownloadButton() {
        const button = document.createElement('button');
        button.id = 'tiktok-bulk-downloader';
        button.innerHTML = `
            <svg width="24" height="24" viewBox="0 0 24 24" fill="white">
                <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
            </svg>
            <span style="margin-left: 8px;">Baixar Todos os Vídeos</span>
        `;
        button.style.cssText = `
            position: fixed;
            top: 100px;
            right: 20px;
            z-index: 9999;
            background: linear-gradient(135deg, #fe2c55 0%, #ff6b6b 100%);
            color: white;
            border: none;
            border-radius: 25px;
            padding: 12px 24px;
            font-size: 14px;
            font-weight: bold;
            cursor: pointer;
            display: flex;
            align-items: center;
            box-shadow: 0 4px 15px rgba(254, 44, 85, 0.4);
            transition: all 0.3s ease;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
        `;
        
        button.addEventListener('mouseenter', () => {
            button.style.transform = 'translateY(-2px)';
            button.style.boxShadow = '0 6px 20px rgba(254, 44, 85, 0.5)';
        });
        
        button.addEventListener('mouseleave', () => {
            button.style.transform = 'translateY(0)';
            button.style.boxShadow = '0 4px 15px rgba(254, 44, 85, 0.4)';
        });
        
        return button;
    }

    // Cria o painel de progresso
    function createProgressPanel() {
        const panel = document.createElement('div');
        panel.id = 'tiktok-download-progress';
        panel.style.cssText = `
            position: fixed;
            top: 160px;
            right: 20px;
            width: 320px;
            background: white;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            z-index: 9998;
            display: none;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
        `;
        
        panel.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                <h3 style="margin: 0; font-size: 16px; color: #333;">Progresso do Download</h3>
                <button id="close-progress" style="background: none; border: none; font-size: 20px; cursor: pointer; color: #666;">&times;</button>
            </div>
            <div id="progress-status" style="font-size: 13px; color: #666; margin-bottom: 10px;"></div>
            <div style="background: #f0f0f0; border-radius: 10px; height: 8px; overflow: hidden; margin-bottom: 10px;">
                <div id="progress-bar" style="background: linear-gradient(90deg, #fe2c55, #ff6b6b); height: 100%; width: 0%; transition: width 0.3s ease;"></div>
            </div>
            <div id="progress-details" style="font-size: 12px; color: #999;"></div>
            <div id="video-list" style="max-height: 300px; overflow-y: auto; margin-top: 15px; font-size: 12px;"></div>
        `;
        
        panel.querySelector('#close-progress').addEventListener('click', () => {
            panel.style.display = 'none';
        });
        
        return panel;
    }

    // Atualiza o progresso
    function updateProgress(current, total, status) {
        const progressPanel = document.getElementById('tiktok-download-progress');
        const progressBar = document.getElementById('progress-bar');
        const progressStatus = document.getElementById('progress-status');
        const progressDetails = document.getElementById('progress-details');
        
        if (progressPanel) {
            progressPanel.style.display = 'block';
            const percentage = total > 0 ? (current / total * 100).toFixed(1) : 0;
            progressBar.style.width = `${percentage}%`;
            progressStatus.textContent = status;
            progressDetails.textContent = `${current} de ${total} vídeos`;
        }
    }

    // Adiciona item à lista de vídeos
    function addVideoToList(videoId, status, isSuccess = true) {
        const videoList = document.getElementById('video-list');
        if (videoList) {
            const item = document.createElement('div');
            item.style.cssText = `
                padding: 8px;
                margin-bottom: 5px;
                background: ${isSuccess ? '#e8f5e9' : '#ffebee'};
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: space-between;
            `;
            item.innerHTML = `
                <span style="color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1;">${videoId}</span>
                <span style="color: ${isSuccess ? '#4caf50' : '#f44336'}; font-size: 18px; margin-left: 10px;">${isSuccess ? '✓' : '✗'}</span>
            `;
            videoList.appendChild(item);
            videoList.scrollTop = videoList.scrollHeight;
        }
    }

    // Coleta todos os IDs de vídeos do perfil
    async function collectVideoIds() {
        const videoIds = new Set();
        let lastHeight = 0;
        let scrollAttempts = 0;
        const maxScrollAttempts = 5; // Tentativas sem novos vídeos antes de parar
        
        updateProgress(0, 0, 'Coletando vídeos do perfil...');
        
        while (scrollAttempts < maxScrollAttempts) {
            // Procura por links de vídeo na página
            const videoLinks = document.querySelectorAll('a[href*="/video/"]');
            
            videoLinks.forEach(link => {
                const href = link.getAttribute('href');
                if (href) {
                    const match = href.match(/\/video\/(\d+)/);
                    if (match && match[1]) {
                        videoIds.add(match[1]);
                    }
                }
            });
            
            // Scroll para carregar mais vídeos
            window.scrollTo(0, document.body.scrollHeight);
            await new Promise(resolve => setTimeout(resolve, 2000));
            
            const currentHeight = document.body.scrollHeight;
            if (currentHeight === lastHeight) {
                scrollAttempts++;
            } else {
                scrollAttempts = 0;
                lastHeight = currentHeight;
            }
            
            updateProgress(videoIds.size, '?', `Encontrados ${videoIds.size} vídeos...`);
        }
        
        // Scroll de volta para o topo
        window.scrollTo(0, 0);
        
        return Array.from(videoIds);
    }

    // Obtém o nome do autor do perfil
    function getProfileAuthor() {
        const pathname = window.location.pathname;
        const match = pathname.match(/\/@([^\/]+)/);
        return match ? '@' + match[1] : null;
    }

    // Baixa um vídeo individual
    async function downloadVideo(videoId, author) {
        try {
            const response = await chrome.runtime.sendMessage({
                type: 'DOWNLOAD_VIDEO_REQ',
                data: {
                    vid: videoId,
                    author: author,
                    page: 'user',
                    url: window.location.href
                }
            });
            
            return response && response.type === 'DOWNLOAD_RESP';
        } catch (error) {
            console.error('Erro ao baixar vídeo:', error);
            return false;
        }
    }

    // Função principal de download em lote
    async function startBulkDownload() {
        const button = document.getElementById('tiktok-bulk-downloader');
        if (button) {
            button.disabled = true;
            button.style.opacity = '0.6';
            button.style.cursor = 'not-allowed';
        }
        
        const videoList = document.getElementById('video-list');
        if (videoList) {
            videoList.innerHTML = '';
        }
        
        try {
            // Coleta todos os IDs de vídeos
            const videoIds = await collectVideoIds();
            
            if (videoIds.length === 0) {
                updateProgress(0, 0, 'Nenhum vídeo encontrado!');
                alert('Nenhum vídeo encontrado neste perfil.');
                return;
            }
            
            // Obtém o nome do autor
            const author = getProfileAuthor();
            if (!author) {
                alert('Não foi possível identificar o autor do perfil.');
                return;
            }
            
            // Baixa cada vídeo com um pequeno delay
            let successCount = 0;
            let failCount = 0;
            
            for (let i = 0; i < videoIds.length; i++) {
                const videoId = videoIds[i];
                updateProgress(i + 1, videoIds.length, `Baixando vídeo ${i + 1}/${videoIds.length}...`);
                
                const success = await downloadVideo(videoId, author);
                
                if (success) {
                    successCount++;
                    addVideoToList(videoId, 'Sucesso', true);
                } else {
                    failCount++;
                    addVideoToList(videoId, 'Falha', false);
                }
                
                // Delay entre downloads para não sobrecarregar
                await new Promise(resolve => setTimeout(resolve, 1000));
            }
            
            updateProgress(
                videoIds.length,
                videoIds.length,
                `Concluído! ${successCount} sucesso(s), ${failCount} falha(s)`
            );
            
        } catch (error) {
            console.error('Erro no download em lote:', error);
            updateProgress(0, 0, 'Erro ao baixar vídeos!');
            alert('Ocorreu um erro durante o download. Verifique o console para mais detalhes.');
        } finally {
            if (button) {
                button.disabled = false;
                button.style.opacity = '1';
                button.style.cursor = 'pointer';
            }
        }
    }

    // Inicializa a interface
    function init() {
        if (!isProfilePage()) {
            return;
        }
        
        // Remove elementos anteriores se existirem
        const oldButton = document.getElementById('tiktok-bulk-downloader');
        const oldPanel = document.getElementById('tiktok-download-progress');
        if (oldButton) oldButton.remove();
        if (oldPanel) oldPanel.remove();
        
        // Cria e adiciona o botão
        const button = createBulkDownloadButton();
        document.body.appendChild(button);
        
        button.addEventListener('click', startBulkDownload);
        
        // Cria e adiciona o painel de progresso
        const panel = createProgressPanel();
        document.body.appendChild(panel);
    }

    // Observa mudanças de URL (TikTok é uma SPA)
    let lastUrl = location.href;
    new MutationObserver(() => {
        const url = location.href;
        if (url !== lastUrl) {
            lastUrl = url;
            setTimeout(init, 1000);
        }
    }).observe(document, { subtree: true, childList: true });

    // Inicializa quando a página carregar
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
    
    // Reinicializa após um delay para garantir que a página carregou
    setTimeout(init, 2000);
})();

