// Dido - Background Script (Versão Gratuita)
// Removido sistema de licenciamento e limitações

// Função para validar se um perfil tem dados úteis
function isValidProfile(profile) {
    // Verificar se tem pelo menos nome ou endereço
    const hasName = profile.name && profile.name.trim().length > 0;
    const hasAddress = (profile.fulladdr && profile.fulladdr.trim().length > 0) || 
                      (profile.address && profile.address.trim().length > 0);
    
    // Verificar se não é apenas espaços em branco
    const hasValidName = hasName && profile.name.trim() !== '';
    const hasValidAddress = hasAddress && (profile.fulladdr?.trim() !== '' || profile.address?.trim() !== '');
    
    return hasValidName || hasValidAddress;
}

// Função para gerar UUID único baseado no conteúdo
function generateUniqueId(profile) {
    const content = `${profile.name || ''}_${profile.fulladdr || profile.address || ''}_${profile.phone_number || profile.phone || ''}`;
    const hash = content.split('').reduce((a, b) => {
        a = ((a << 5) - a) + b.charCodeAt(0);
        return a & a;
    }, 0);
    return `profile_${Math.abs(hash)}_${Date.now()}`;
}

// Função para salvar novos perfis encontrados
async function saveNewProfiles(profiles) {
    if (Object.keys(profiles).length > 0) {
        try {
            const result = await chrome.storage.local.get(['found_records']);
            const existingRecords = result.found_records || {};
            
            // Filtrar e validar perfis
            const validProfiles = {};
            let duplicatesCount = 0;
            let invalidCount = 0;
            
            for (const [key, profile] of Object.entries(profiles)) {
                // Validar se o perfil tem dados úteis
                if (!isValidProfile(profile)) {
                    invalidCount++;
                    continue;
                }
                
                // Gerar UUID único baseado no conteúdo
                const uniqueId = generateUniqueId(profile);
                
                // Verificar se já existe um perfil com dados similares
                const isDuplicate = Object.values(existingRecords).some(existing => {
                    const existingContent = `${existing.name || ''}_${existing.fulladdr || existing.address || ''}_${existing.phone_number || existing.phone || ''}`;
                    const newContent = `${profile.name || ''}_${profile.fulladdr || profile.address || ''}_${profile.phone_number || profile.phone || ''}`;
                    return existingContent === newContent && existingContent.trim() !== '';
                });
                
                if (isDuplicate) {
                    duplicatesCount++;
                    continue;
                }
                
                // Atualizar UUID único
                profile.uuid = uniqueId;
                validProfiles[uniqueId] = profile;
            }
            
            // Log de estatísticas
            if (invalidCount > 0 || duplicatesCount > 0) {
                console.log(`📊 Estatísticas de coleta: ${Object.keys(validProfiles).length} novos, ${duplicatesCount} duplicatas ignoradas, ${invalidCount} inválidos ignorados`);
            }
            
            // Salvar apenas perfis válidos e únicos
            if (Object.keys(validProfiles).length > 0) {
                const updatedRecords = { ...existingRecords, ...validProfiles };
                await chrome.storage.local.set({ found_records: updatedRecords });
                
                // Atualizar badge
                const count = Object.keys(updatedRecords).length;
                chrome.action.setBadgeText({ text: count > 0 ? count.toString() : '' });
                chrome.action.setBadgeBackgroundColor({ color: '#1976d2' });
            }
            
        } catch (error) {
            console.error('Erro ao salvar perfis:', error);
        }
    }
}

// Função para parsear dados do Google Maps
function parseGoogleMapsProfile(query, data) {
    try {
        if (!Array.isArray(data) || data.length < 246) return null;
        
        const profile = {
            uuid: data[78] || `google_${Date.now()}_${Math.random()}`,
            query: query,
            name: data[11] || '',
            url: data[7] ? data[7][0] : null,
            domain: data[7] ? data[7][1] : null,
            fulladdr: data[39] || '',
            address: data[39] || '', // Campo adicional para endereço
            addr1: data[2] && data[2][0] ? data[2][0] : null,
            addr2: data[2] && data[2][1] ? data[2][1] : null,
            addr3: data[2] && data[2][2] ? data[2][2] : null,
            addr4: data[2] && data[2][3] ? data[2][3] : null,
            district: data[14] || null,
            timezone: data[30] || null,
            reviews: data[4] ? data[4][8] : 0,
            rating: data[4] ? data[4][7] : 0,
            latitude: data[9] ? data[9][2] : null,
            longitude: data[9] ? data[9][3] : null,
            categories: data[13] || [],
            local_name: data[101] || null,
            local_fulladdr: data[149] || null,
            thumbnail: data[37] && data[37][0] ? data[37][0][0][6][0].split('=')[0] : null,
            phone_numbers: data[178] ? data[178].map(p => p[3]) : [],
            created_at: Date.now(),
            reviews_url: data[4] ? data[4][3][0] : null,
            fid: data[10] || null,
            cid: null,
            listing_url: null,
            phone_number: data[178] && data[178][0] ? data[178][0][1][0][0] : null,
            phone: data[178] && data[178][0] ? data[178][0][1][0][0] : null, // Campo adicional para telefone
            international_phone_number: data[178] && data[178][0] ? data[178][0][1][1][0] : null,
            features: data[100] && data[100][1] ? data[100][1].reduce((acc, item) => acc.concat(item[2]), []).map(f => f[1]) : null,
            claimed: Array.isArray(data[57]) && data[57].length > 2 && data[57][2] ? 'yes' : 'no',
            primary_category: data[13] && data[13][0] ? data[13][0] : null
        };
        
        // Determinar CID
        if (data[37] && data[37][0]) {
            profile.cid = data[37][0][0][29][1];
        } else if (data[51] && data[51][0]) {
            profile.cid = data[51][0][0][29][1];
        } else if (data[72] && data[72][0]) {
            profile.cid = data[72][0][0][29][1];
        }
        
        if (profile.cid) {
            profile.listing_url = `https://www.google.com/maps?cid=${profile.cid}`;
        }
        
        return profile;
    } catch (error) {
        console.error('Erro ao parsear perfil do Google Maps:', error);
        return null;
    }
}

// Função para parsear dados do Bing Maps
function parseBingMapsDocument(html, query) {
    try {
        const entityMatches = html.match(/data-entity="(.+?)"/g);
        if (!entityMatches) return null;
        
        const entities = entityMatches.map(match => 
            match.replace('data-entity="', '').replace('"', '').replace(/&quot;/g, '"')
        );
        
        const parsedEntities = entities.map(entity => JSON.parse(entity));
        const profiles = {};
        
        for (const item of parsedEntities) {
            const { entity, routablePoint } = item;
            const { id } = entity;
            
            if (id && id.startsWith('ypid:')) {
                const uuid = id.slice(5); // Remove 'ypid:' prefix
                
                profiles[uuid] = {
                    uuid: uuid,
                    query: query,
                    name: entity.title || '',
                    url: entity.website || null,
                    domain: null,
                    fulladdr: entity.address || '',
                    address: entity.address || '', // Campo adicional para endereço
                    addr1: null,
                    addr2: null,
                    addr3: null,
                    addr4: null,
                    district: null,
                    timezone: null,
                    reviews: 0,
                    rating: 0,
                    latitude: routablePoint.latitude,
                    longitude: routablePoint.longitude,
                    categories: entity.primaryCategoryName ? [entity.primaryCategoryName] : [],
                    local_name: entity.title || null,
                    local_fulladdr: entity.address || null,
                    thumbnail: null,
                    phone_numbers: entity.phone ? [entity.phone] : [],
                    created_at: Date.now(),
                    reviews_url: null,
                    fid: null,
                    cid: null,
                    listing_url: `https://www.bing.com/maps?ss=${id}`,
                    phone_number: entity.phone || null,
                    phone: entity.phone || null, // Campo adicional para telefone
                    international_phone_number: entity.phone || null,
                    features: null,
                    claimed: '',
                    primary_category: entity.primaryCategoryName || null
                };
            }
        }
        
        return profiles;
    } catch (error) {
        console.error('Erro ao parsear documento do Bing Maps:', error);
        return null;
    }
}

// Listener para requisições do Google Maps
chrome.webRequest.onCompleted.addListener(async (details) => {
    if (details.url.startsWith('https://www.google.') && 
        details.url.includes('search?tbm=map') && 
        !details.initiator?.startsWith('chrome-extension')) {
        
        try {
            const url = new URL(details.url);
            if (url.searchParams.has('q')) {
                const query = url.searchParams.get('q');
                const response = await fetch(details.url);
                const text = await response.text();
                
                // Parsear resposta do Google Maps
                const jsonMatch = text.match(/^\)\]\}'\n(.+)$/);
                if (jsonMatch) {
                    const jsonData = JSON.parse(jsonMatch[1]);
                    const data = JSON.parse(jsonData.d.substr(4));
                    
                    const profiles = {};
                    const results = [...(data[0]?.[1] || []), ...(data[64] || [])];
                    
                    for (const result of results) {
                        if (Array.isArray(result) && result.length >= 246) {
                            const profile = parseGoogleMapsProfile(query, result);
                            if (profile) {
                                profiles[profile.uuid] = profile;
                            }
                        }
                    }
                    
                    if (Object.keys(profiles).length > 0) {
                        await saveNewProfiles(profiles);
                    }
                }
            }
        } catch (error) {
            console.error('Erro ao processar Google Maps:', error);
        }
    }
}, { urls: ['<all_urls>'], types: ['xmlhttprequest'] });

// Listener para requisições do Bing Maps
chrome.webRequest.onCompleted.addListener(async (details) => {
    if ((details.url.includes('/localoverlaybfpr') || details.url.includes('/overlaybfpr')) &&
        !details.initiator?.startsWith('chrome-extension')) {
        
        try {
            const url = new URL(details.url);
            const query = url.searchParams.get('q') || '';
            
            const response = await fetch(url);
            const html = await response.text();
            
            const profiles = parseBingMapsDocument(html, query);
            if (profiles) {
                await saveNewProfiles(profiles);
            }
        } catch (error) {
            console.error('Erro ao processar Bing Maps:', error);
        }
    }
}, { urls: ['https://www.bing.com/*'], types: ['xmlhttprequest'] });

// Listener para mensagens do content script
chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.type === 'new_profiles') {
        saveNewProfiles(message.data);
    }
});

// Inicialização
chrome.runtime.onInstalled.addListener((details) => {
    if (details.reason === 'install' || details.reason === 'update') {
        // Inicializar storage se necessário
        chrome.storage.local.get(['found_records'], (result) => {
            if (!result.found_records) {
                chrome.storage.local.set({ found_records: {} });
            }
        });
    }
});

console.log('🗺️ Dido - Background Script carregado com sucesso!');
