// Dido - Content Script (Versão Gratuita)
// Removido sistema de licenciamento e limitações

(function() {
    'use strict';
    
    console.log('🗺️ Dido - Content Script carregado!');
    
    // Função para extrair dados de elementos da página
    function extractBusinessData() {
        const data = {};
        
        // Tentar extrair dados do Google Maps
        if (window.location.hostname.includes('google.com') && window.location.pathname.includes('/maps')) {
            extractGoogleMapsData(data);
        }
        
        // Tentar extrair dados do Bing Maps
        if (window.location.hostname.includes('bing.com') && window.location.pathname.includes('/maps')) {
            extractBingMapsData(data);
        }
        
        return data;
    }
    
    function extractGoogleMapsData(data) {
        try {
            // Procurar por elementos de negócios na página
            const businessElements = document.querySelectorAll('[data-result-index], [jsaction*="pane"]');
            
            businessElements.forEach((element, index) => {
                const businessData = {
                    uuid: `google_${Date.now()}_${index}`,
                    query: getCurrentSearchQuery(),
                    name: extractText(element, '[data-value="Name"], .fontHeadlineSmall, .fontHeadlineMedium'),
                    fulladdr: extractText(element, '[data-value="Address"], .fontBodyMedium'),
                    address: extractText(element, '[data-value="Address"], .fontBodyMedium'),
                    phone_number: extractText(element, '[data-value="Phone"], [data-value="Phone number"]'),
                    phone: extractText(element, '[data-value="Phone"], [data-value="Phone number"]'),
                    url: extractHref(element, '[data-value="Website"], [data-value="Website URL"]'),
                    website: extractHref(element, '[data-value="Website"], [data-value="Website URL"]'),
                    rating: extractText(element, '.fontDisplayLarge, .fontDisplayMedium'),
                    reviews: extractText(element, '.fontBodySmall'),
                    primary_category: extractText(element, '.fontBodyMedium'),
                    category: extractText(element, '.fontBodyMedium'),
                    created_at: Date.now()
                };
                
                // Validar se tem dados úteis antes de adicionar
                if (isValidBusinessData(businessData)) {
                    data[businessData.uuid] = businessData;
                }
            });
        } catch (error) {
            console.error('Erro ao extrair dados do Google Maps:', error);
        }
    }
    
    // Função para validar se os dados do negócio são úteis
    function isValidBusinessData(businessData) {
        // Verificar se tem pelo menos nome ou endereço válido
        const hasValidName = businessData.name && businessData.name.trim().length > 0;
        const hasValidAddress = (businessData.fulladdr && businessData.fulladdr.trim().length > 0) || 
                              (businessData.address && businessData.address.trim().length > 0);
        
        // Verificar se não são apenas espaços ou caracteres especiais
        const nameIsValid = hasValidName && businessData.name.trim() !== '' && 
                           !businessData.name.match(/^[\s\-_\.]+$/);
        const addressIsValid = hasValidAddress && 
                              (businessData.fulladdr?.trim() !== '' || businessData.address?.trim() !== '') &&
                              !businessData.fulladdr?.match(/^[\s\-_\.]+$/) &&
                              !businessData.address?.match(/^[\s\-_\.]+$/);
        
        return nameIsValid || addressIsValid;
    }
    
    function extractBingMapsData(data) {
        try {
            // Procurar por elementos de negócios no Bing Maps
            const businessElements = document.querySelectorAll('.entity-listing, .business-listing, [data-entity]');
            
            businessElements.forEach((element, index) => {
                const businessData = {
                    uuid: `bing_${Date.now()}_${index}`,
                    query: getCurrentSearchQuery(),
                    name: extractText(element, '.entity-title, .business-name, h3'),
                    fulladdr: extractText(element, '.entity-address, .business-address'),
                    address: extractText(element, '.entity-address, .business-address'),
                    phone_number: extractText(element, '.entity-phone, .business-phone'),
                    phone: extractText(element, '.entity-phone, .business-phone'),
                    url: extractHref(element, '.entity-website, .business-website'),
                    website: extractHref(element, '.entity-website, .business-website'),
                    rating: extractText(element, '.entity-rating, .business-rating'),
                    reviews: extractText(element, '.entity-reviews, .business-reviews'),
                    primary_category: extractText(element, '.entity-category, .business-category'),
                    category: extractText(element, '.entity-category, .business-category'),
                    created_at: Date.now()
                };
                
                // Validar se tem dados úteis antes de adicionar
                if (isValidBusinessData(businessData)) {
                    data[businessData.uuid] = businessData;
                }
            });
        } catch (error) {
            console.error('Erro ao extrair dados do Bing Maps:', error);
        }
    }
    
    function extractText(element, selector) {
        try {
            const found = element.querySelector(selector);
            return found ? found.textContent.trim() : '';
        } catch (error) {
            return '';
        }
    }
    
    function extractHref(element, selector) {
        try {
            const found = element.querySelector(selector);
            return found ? found.href || found.getAttribute('href') || '' : '';
        } catch (error) {
            return '';
        }
    }
    
    function getCurrentSearchQuery() {
        try {
            // Tentar obter a query de busca da URL
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get('q') || urlParams.get('query') || '';
        } catch (error) {
            return '';
        }
    }
    
    // Função para enviar dados para o background script
    function sendDataToBackground(data) {
        if (Object.keys(data).length > 0) {
            try {
                chrome.runtime.sendMessage({
                    type: 'new_profiles',
                    data: data
                });
            } catch (error) {
                console.error('Erro ao enviar dados para background:', error);
            }
        }
    }
    
    // Função para monitorar mudanças na página
    function monitorPageChanges() {
        let lastUrl = window.location.href;
        let lastData = '';
        
        // Observer para mudanças no DOM
        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
                    // Aguardar um pouco para a página carregar completamente
                    setTimeout(() => {
                        const currentData = extractBusinessData();
                        const currentDataStr = JSON.stringify(currentData);
                        
                        if (currentDataStr !== lastData && Object.keys(currentData).length > 0) {
                            lastData = currentDataStr;
                            sendDataToBackground(currentData);
                        }
                    }, 1000);
                }
            });
        });
        
        // Observar mudanças no documento
        observer.observe(document.body, {
            childList: true,
            subtree: true
        });
        
        // Monitorar mudanças de URL
        setInterval(() => {
            if (window.location.href !== lastUrl) {
                lastUrl = window.location.href;
                setTimeout(() => {
                    const currentData = extractBusinessData();
                    if (Object.keys(currentData).length > 0) {
                        sendDataToBackground(currentData);
                    }
                }, 2000);
            }
        }, 1000);
    }
    
    // Função para extrair dados de elementos específicos quando clicados
    function addClickListeners() {
        document.addEventListener('click', (event) => {
            // Aguardar um pouco para a página carregar após o clique
            setTimeout(() => {
                const currentData = extractBusinessData();
                if (Object.keys(currentData).length > 0) {
                    sendDataToBackground(currentData);
                }
            }, 1500);
        });
    }
    
    // Inicialização
    function init() {
        // Aguardar a página carregar completamente
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => {
                setTimeout(init, 1000);
            });
            return;
        }
        
        // Extrair dados iniciais
        const initialData = extractBusinessData();
        if (Object.keys(initialData).length > 0) {
            sendDataToBackground(initialData);
        }
        
        // Iniciar monitoramento
        monitorPageChanges();
        addClickListeners();
        
        console.log('🗺️ Dido - Content Script inicializado com sucesso!');
    }
    
    // Iniciar apenas em páginas de mapas
    if (window.location.hostname.includes('google.com') && window.location.pathname.includes('/maps') ||
        window.location.hostname.includes('bing.com') && window.location.pathname.includes('/maps')) {
        init();
    }
    
})();
