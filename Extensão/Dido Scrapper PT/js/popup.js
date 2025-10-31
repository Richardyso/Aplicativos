// Dido - Extrator de Leads de Mapas (Versão Gratuita)
// Removido sistema de licenciamento e limitações

document.addEventListener('DOMContentLoaded', function() {
    const root = document.getElementById('root');
    
    // Interface simples e funcional
    root.innerHTML = `
        <div style="padding: 16px; font-family: Arial, sans-serif; width: 300px;">
            <div style="text-align: center; margin-bottom: 20px;">
                <h2 style="color: #1976d2; margin: 0;">🗺️ Dido</h2>
                <p style="color: #666; margin: 5px 0; font-size: 14px;">Extrator de Leads de Mapas</p>
            </div>
            
            <div id="status" style="text-align: center; margin-bottom: 15px;">
                <div id="recordCount" style="font-size: 18px; font-weight: bold; color: #1976d2;">
                    Carregando...
                </div>
                <div id="statusText" style="font-size: 12px; color: #666; margin-top: 5px;">
                    Registros coletados
                </div>
            </div>
            
            <div style="display: flex; flex-direction: column; gap: 10px;">
                <button id="exportBtn" style="
                    background: #1976d2; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    📊 Exportar Excel
                </button>
                
                <button id="clearBtn" style="
                    background: #f44336; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    🗑️ Limpar Dados
                </button>
                
                <button id="helpBtn" style="
                    background: #4caf50; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    ❓ Como Usar
                </button>
                
                <button id="testBtn" style="
                    background: #ff9800; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    🔧 Testar Extração
                </button>
                
                <button id="dedupeBtn" style="
                    background: #9c27b0; 
                    color: white; 
                    border: none; 
                    padding: 10px; 
                    border-radius: 5px; 
                    cursor: pointer;
                    font-size: 14px;
                ">
                    🔄 Remover Duplicatas
                </button>
            </div>
            
            <div id="info" style="
                margin-top: 15px; 
                padding: 10px; 
                background: #f5f5f5; 
                border-radius: 5px; 
                font-size: 12px; 
                color: #666;
            ">
                <strong>💡 Dica:</strong> Navegue pelo Google Maps ou Bing Maps. 
                Os dados dos negócios portugueses (DDI +351) serão coletados automaticamente!
                Apenas registros com número e nome válidos são exportados.
            </div>
        </div>
    `;
    
    // Funções principais
    async function updateRecordCount() {
        try {
            const result = await chrome.storage.local.get(['found_records']);
            const records = result.found_records || {};
            const count = Object.keys(records).length;
            
            document.getElementById('recordCount').textContent = count;
            document.getElementById('statusText').textContent = 
                count === 1 ? 'registro coletado' : 'registros coletados';
                
            // Atualizar badge da extensão
            chrome.action.setBadgeText({ text: count > 0 ? count.toString() : '' });
            chrome.action.setBadgeBackgroundColor({ color: '#1976d2' });
            
        } catch (error) {
            console.error('Erro ao atualizar contagem:', error);
            document.getElementById('recordCount').textContent = 'Erro';
        }
    }
    
    // Função para verificar se a biblioteca XLSX está carregada
    function checkXLSXLibrary() {
        return typeof XLSX !== 'undefined' && XLSX.utils && XLSX.utils.json_to_sheet;
    }
    
    // Função para exportar em CSV como fallback
    function exportToCSV(dataArray) {
        try {
            // Criar cabeçalhos
            const headers = Object.keys(dataArray[0]);
            const csvContent = [
                headers.join(','),
                ...dataArray.map(row => 
                    headers.map(header => {
                        const value = row[header] || '';
                        // Escapar aspas e vírgulas
                        return `"${value.toString().replace(/"/g, '""')}"`;
                    }).join(',')
                )
            ].join('\n');
            
            // Criar arquivo CSV
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `dido_leads_${new Date().toISOString().split('T')[0]}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
            
            return true;
        } catch (error) {
            console.error('Erro ao exportar CSV:', error);
            return false;
        }
    }
    
    async function exportData() {
        try {
            const result = await chrome.storage.local.get(['found_records']);
            const records = result.found_records || {};
            
            if (Object.keys(records).length === 0) {
                alert('Nenhum dado para exportar! Navegue pelo Google Maps ou Bing Maps primeiro.');
                return;
            }
            
            // Remover duplicatas antes de exportar (por nome E por conteúdo completo)
            const uniqueRecords = {};
            const contentKeyMap = new Map();
            const nameMap = new Map(); // Mapa para evitar nomes repetidos
            
            for (const [uuid, record] of Object.entries(records)) {
                // Criar chave de conteúdo normalizada
                const name = (record.name || '').toLowerCase().trim().replace(/\s+/g, ' ');
                const address = ((record.fulladdr || record.address || '').toLowerCase().trim().replace(/\s+/g, ' '));
                const phone = (record.phone_number || record.phone || '').replace(/\D/g, '');
                const contentKey = `${name}|${address}|${phone}`;
                
                // Verificar se tem dados mínimos válidos
                const hasValidName = record.name && record.name.trim().length > 2;
                const hasValidAddress = (record.fulladdr && record.fulladdr.trim().length > 5) || 
                                      (record.address && record.address.trim().length > 5);
                
                // Só adicionar se for válido, único por conteúdo E único por nome
                if ((hasValidName || hasValidAddress) && 
                    contentKey !== '||' && 
                    !contentKeyMap.has(contentKey) && 
                    !nameMap.has(name)) {
                    contentKeyMap.set(contentKey, true);
                    nameMap.set(name, true); // Registrar nome para evitar repetição
                    uniqueRecords[uuid] = record;
                }
            }
            
            // Converter objetos para array de dados
            const dataArray = Object.values(uniqueRecords)
                .map((record, index) => {
                // Função para extrair telefone de diferentes campos (NÚMEROS PORTUGUESES - DDI +351)
                const extractPhone = (record) => {
                    // Tentar diferentes campos de telefone primeiro
                    const phoneFields = [
                        record.phone_number,
                        record.phone,
                        record.international_phone_number,
                        record.phone_numbers && record.phone_numbers[0]
                    ];
                    
                    for (const phone of phoneFields) {
                        if (phone && phone.trim()) {
                            let cleanPhone = phone.trim().replace(/\s+/g, ''); // Remove espaços
                            
                            // Remover +351 ou 00351 se existir
                            cleanPhone = cleanPhone.replace(/^\+351/, '').replace(/^00351/, '');
                            
                            // Remover caracteres não numéricos
                            cleanPhone = cleanPhone.replace(/\D/g, '');
                            
                            // Número português válido: 9 dígitos começando com 9 (celular)
                            if (cleanPhone.length === 9 && cleanPhone.charAt(0) === '9') {
                                // Formatar: +351 9XX XXX XXX
                                return `+351 ${cleanPhone.substring(0, 3)} ${cleanPhone.substring(3, 6)} ${cleanPhone.substring(6)}`;
                            }
                            
                            // Se tiver mais dígitos, tentar extrair os últimos 9
                            if (cleanPhone.length > 9 && cleanPhone.charAt(cleanPhone.length - 9) === '9') {
                                const last9 = cleanPhone.substring(cleanPhone.length - 9);
                                return `+351 ${last9.substring(0, 3)} ${last9.substring(3, 6)} ${last9.substring(6)}`;
                            }
                        }
                    }
                    
                    // Se não encontrou telefone nos campos específicos, procurar no endereço
                    const address = record.fulladdr || record.address || '';
                    if (address) {
                        // Regex para detectar números portugueses: +351 9XX XXX XXX ou variações
                        const patterns = [
                            /\+351\s*(\d{3})\s*(\d{3})\s*(\d{3})/g,
                            /00351\s*(\d{3})\s*(\d{3})\s*(\d{3})/g,
                            /\b(9\d{2})\s*(\d{3})\s*(\d{3})\b/g
                        ];
                        
                        for (const pattern of patterns) {
                            const match = pattern.exec(address);
                            if (match && match[1].charAt(0) === '9') {
                                return `+351 ${match[1]} ${match[2]} ${match[3]}`;
                            }
                        }
                    }
                    
                    return '';
                };
                
                // Função para extrair endereço limpo (sem telefone português)
                const extractAddress = (record) => {
                    let address = record.fulladdr || record.address || '';
                    
                    // Remover números portugueses usando regex
                    // Padrão: +351 9XX XXX XXX
                    address = address.replace(/\+351\s*\d{3}\s*\d{3}\s*\d{3}/g, '');
                    address = address.replace(/00351\s*\d{3}\s*\d{3}\s*\d{3}/g, '');
                    
                    // Remover números portugueses sem DDI: 9XX XXX XXX
                    address = address.replace(/\b9\d{2}\s*\d{3}\s*\d{3}\b/g, '');
                    
                    // Limpar vírgulas extras e espaços
                    address = address.replace(/,\s*,/g, ','); // Vírgulas duplas
                    address = address.replace(/,\s*$/g, ''); // Vírgula no final
                    address = address.replace(/^\s*,/g, ''); // Vírgula no início
                    address = address.replace(/\s+/g, ' '); // Múltiplos espaços
                    
                    return address.trim();
                };
                
                // Extrair dados
                const extractedPhone = extractPhone(record);
                const extractedAddress = extractAddress(record);
                const extractedName = (record.name || '').trim();
                
                return {
                    'Nº': index + 1, // Adicionar numeração
                    'Nome': extractedName,
                    'Telefone': extractedPhone,
                    'Categoria': record.primary_category || (record.categories && record.categories[0]) || '',
                    'Endereço': extractedAddress,
                    'Website': record.url || record.website || '',
                    'Avaliação': record.rating || '',
                    'Avaliações': record.reviews || '',
                    'Categorias': record.categories ? record.categories.join(', ') : '',
                    'Latitude': record.latitude || '',
                    'Longitude': record.longitude || '',
                    'URL da Listagem': record.listing_url || '',
                    'Reivindicado': record.claimed || '',
                    'Data de Coleta': new Date(record.created_at).toLocaleDateString('pt-BR'),
                    'Query de Busca': record.query || ''
                };
            })
            .filter(row => {
                // Filtrar linhas SEM NÚMERO ou com dados inválidos
                const hasPhone = row.Telefone && row.Telefone.length > 0;
                const hasValidName = row.Nome && row.Nome.length > 2;
                
                // OBRIGATÓRIO: ter telefone E ter nome válido
                return hasPhone && hasValidName;
            });
            
            // Verificar se a biblioteca XLSX está disponível
            if (checkXLSXLibrary()) {
                // Exportar para Excel
                const worksheet = XLSX.utils.json_to_sheet(dataArray);
                
                // Ajustar largura das colunas
                const columnWidths = [
                    { wch: 6 },  // Nº
                    { wch: 30 }, // Nome
                    { wch: 20 }, // Telefone
                    { wch: 20 }, // Categoria
                    { wch: 40 }, // Endereço
                    { wch: 30 }, // Website
                    { wch: 10 }, // Avaliação
                    { wch: 10 }, // Avaliações
                    { wch: 30 }, // Categorias
                    { wch: 12 }, // Latitude
                    { wch: 12 }, // Longitude
                    { wch: 40 }, // URL da Listagem
                    { wch: 12 }, // Reivindicado
                    { wch: 15 }, // Data de Coleta
                    { wch: 30 }  // Query de Busca
                ];
                worksheet['!cols'] = columnWidths;
                
                // Criar workbook
                const workbook = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(workbook, worksheet, 'Leads Coletados');
                
                // Gerar arquivo Excel
                const fileName = `dido_leads_${new Date().toISOString().split('T')[0]}.xlsx`;
                XLSX.writeFile(workbook, fileName);
                
            } else {
                // Fallback para CSV se XLSX não estiver disponível
                console.warn('Biblioteca XLSX não encontrada, exportando como CSV');
                if (exportToCSV(dataArray)) {
                    alert('Biblioteca Excel não carregada. Dados exportados como CSV (.csv) que pode ser aberto no Excel.');
                } else {
                    throw new Error('Falha ao exportar dados');
                }
            }
            
        } catch (error) {
            console.error('Erro ao exportar:', error);
            alert('Erro ao exportar dados! Tente recarregar a extensão.');
        }
    }
    
    async function clearData() {
        if (confirm('Tem certeza que deseja limpar todos os dados coletados?')) {
            try {
                await chrome.storage.local.set({ found_records: {} });
                await updateRecordCount();
                alert('Dados limpos com sucesso!');
            } catch (error) {
                console.error('Erro ao limpar dados:', error);
                alert('Erro ao limpar dados!');
            }
        }
    }
    
    function showHelp() {
        alert(`🗺️ Dido - Como Usar:

1. Navegue pelo Google Maps (maps.google.com) ou Bing Maps (bing.com/maps)
2. Faça buscas por negócios (ex: "restaurantes em Lisboa")
3. Clique nos resultados para visualizar os detalhes
4. Os dados serão coletados automaticamente
5. Use "Exportar Excel" para baixar uma planilha Excel (.xlsx)
6. Use "Limpar Dados" para remover todos os registros

✅ 100% Gratuito e Sem Limitações!
✅ Funciona com Google Maps e Bing Maps
✅ Coleta: nome, endereço, telefone (+351), website, avaliações, etc.
✅ Exporta em Excel com colunas organizadas!
✅ Remove automaticamente duplicatas e registros sem número!`);
    }
    
    async function testExtraction() {
        try {
            const result = await chrome.storage.local.get(['found_records']);
            const records = result.found_records || {};
            
            if (Object.keys(records).length === 0) {
                alert('Nenhum dado para testar! Navegue pelo Google Maps ou Bing Maps primeiro.');
                return;
            }
            
            // Pegar o primeiro registro para teste
            const firstRecord = Object.values(records)[0];
            
            // Função de teste (cópia das funções de extração - NÚMEROS PORTUGUESES)
            const extractPhone = (record) => {
                const phoneFields = [
                    record.phone_number,
                    record.phone,
                    record.international_phone_number,
                    record.phone_numbers && record.phone_numbers[0]
                ];
                
                for (const phone of phoneFields) {
                    if (phone && phone.trim()) {
                        let cleanPhone = phone.trim().replace(/\s+/g, '');
                        cleanPhone = cleanPhone.replace(/^\+351/, '').replace(/^00351/, '');
                        cleanPhone = cleanPhone.replace(/\D/g, '');
                        
                        if (cleanPhone.length === 9 && cleanPhone.charAt(0) === '9') {
                            return `+351 ${cleanPhone.substring(0, 3)} ${cleanPhone.substring(3, 6)} ${cleanPhone.substring(6)}`;
                        }
                        
                        if (cleanPhone.length > 9 && cleanPhone.charAt(cleanPhone.length - 9) === '9') {
                            const last9 = cleanPhone.substring(cleanPhone.length - 9);
                            return `+351 ${last9.substring(0, 3)} ${last9.substring(3, 6)} ${last9.substring(6)}`;
                        }
                    }
                }
                
                const address = record.fulladdr || record.address || '';
                if (address) {
                    const patterns = [
                        /\+351\s*(\d{3})\s*(\d{3})\s*(\d{3})/g,
                        /00351\s*(\d{3})\s*(\d{3})\s*(\d{3})/g,
                        /\b(9\d{2})\s*(\d{3})\s*(\d{3})\b/g
                    ];
                    
                    for (const pattern of patterns) {
                        const match = pattern.exec(address);
                        if (match && match[1].charAt(0) === '9') {
                            return `+351 ${match[1]} ${match[2]} ${match[3]}`;
                        }
                    }
                }
                
                return '';
            };
            
            const extractAddress = (record) => {
                let address = record.fulladdr || record.address || '';
                address = address.replace(/\+351\s*\d{3}\s*\d{3}\s*\d{3}/g, '');
                address = address.replace(/00351\s*\d{3}\s*\d{3}\s*\d{3}/g, '');
                address = address.replace(/\b9\d{2}\s*\d{3}\s*\d{3}\b/g, '');
                address = address.replace(/,\s*,/g, ',');
                address = address.replace(/,\s*$/g, '');
                address = address.replace(/^\s*,/g, '');
                address = address.replace(/\s+/g, ' ');
                return address.trim();
            };
            
            const extractedPhone = extractPhone(firstRecord);
            const extractedAddress = extractAddress(firstRecord);
            
            alert(`🔧 Teste de Extração (NÚMEROS PORTUGUESES +351):

📋 Dados Originais:
Nome: ${firstRecord.name || 'N/A'}
Endereço Original: ${firstRecord.fulladdr || firstRecord.address || 'N/A'}

📱 Resultado da Extração:
Telefone Extraído: ${extractedPhone || 'NÃO ENCONTRADO'}
Endereço Limpo: ${extractedAddress || 'N/A'}

${extractedPhone ? '✅ Número português extraído com sucesso!' : '❌ Número português não encontrado'}`);
            
        } catch (error) {
            console.error('Erro no teste:', error);
            alert('Erro ao testar extração!');
        }
    }
    
    async function removeDuplicates() {
        try {
            const result = await chrome.storage.local.get(['found_records']);
            const records = result.found_records || {};
            
            if (Object.keys(records).length === 0) {
                alert('Nenhum dado para processar!');
                return;
            }
            
            const originalCount = Object.keys(records).length;
            const uniqueRecords = {};
            const contentKeyMap = new Map();
            const nameMap = new Map(); // Mapa para evitar nomes repetidos
            const duplicates = [];
            
            // Processar cada registro
            for (const [uuid, record] of Object.entries(records)) {
                // Criar uma chave única baseada no conteúdo normalizado
                const name = (record.name || '').toLowerCase().trim().replace(/\s+/g, ' ');
                const address = ((record.fulladdr || record.address || '').toLowerCase().trim().replace(/\s+/g, ' '));
                const phone = (record.phone_number || record.phone || '').replace(/\D/g, ''); // Remove formatação
                const contentKey = `${name}|${address}|${phone}`;
                
                // Verificar se tem dados mínimos válidos
                const hasValidName = record.name && record.name.trim().length > 2;
                const hasValidAddress = (record.fulladdr && record.fulladdr.trim().length > 5) || 
                                      (record.address && record.address.trim().length > 5);
                
                if (contentKey && contentKey !== '||' && (hasValidName || hasValidAddress)) {
                    // Verificar duplicata por conteúdo OU por nome
                    if (!contentKeyMap.has(contentKey) && !nameMap.has(name)) {
                        contentKeyMap.set(contentKey, true);
                        nameMap.set(name, true); // Registrar nome
                        uniqueRecords[uuid] = record;
                    } else {
                        duplicates.push(record.name || 'Sem nome');
                    }
                }
            }
            
            const finalCount = Object.keys(uniqueRecords).length;
            const removedCount = originalCount - finalCount;
            
            if (removedCount > 0) {
                // Salvar registros únicos
                await chrome.storage.local.set({ found_records: uniqueRecords });
                await updateRecordCount();
                
                alert(`🔄 Duplicatas Removidas!

📊 Estatísticas:
• Registros originais: ${originalCount}
• Registros únicos: ${finalCount}
• Duplicatas removidas: ${removedCount}

✅ Base de dados limpa com sucesso!`);
            } else {
                alert('✅ Nenhuma duplicata encontrada! Todos os registros são únicos.');
            }
            
        } catch (error) {
            console.error('Erro ao remover duplicatas:', error);
            alert('Erro ao remover duplicatas!');
        }
    }
    
    // Event listeners
    document.getElementById('exportBtn').addEventListener('click', exportData);
    document.getElementById('clearBtn').addEventListener('click', clearData);
    document.getElementById('helpBtn').addEventListener('click', showHelp);
    document.getElementById('testBtn').addEventListener('click', testExtraction);
    document.getElementById('dedupeBtn').addEventListener('click', removeDuplicates);
    
    // Atualizar contagem inicial e a cada mudança no storage
    updateRecordCount();
    chrome.storage.onChanged.addListener((changes) => {
        if (changes.found_records) {
            updateRecordCount();
        }
    });
    
    // Atualizar a cada 2 segundos para garantir sincronização
    setInterval(updateRecordCount, 2000);
});
