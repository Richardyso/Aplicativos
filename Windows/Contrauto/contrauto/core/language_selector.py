# -*- coding: utf-8 -*-
"""
Seletor de idioma e adaptações regionais
Gerencia diferenças entre português brasileiro e europeu
"""

from typing import Dict, Optional


class LanguageSelector:
    """Gerencia adaptações de linguagem para diferentes países"""
    
    def __init__(self):
        """Inicializa o seletor de idioma"""
        self.country = 'BR'  # Padrão Brasil
        self.translations = self._load_translations()
        
    def set_country(self, country: str):
        """
        Define o país para adaptação de linguagem
        
        Args:
            country: Código do país (BR ou PT)
        """
        if country in ['BR', 'PT']:
            self.country = country
        else:
            raise ValueError(f"País não suportado: {country}")
            
    def get_term(self, key: str) -> str:
        """
        Retorna o termo apropriado para o país selecionado
        
        Args:
            key: Chave do termo
            
        Returns:
            Termo traduzido/adaptado
        """
        if key in self.translations:
            return self.translations[key].get(self.country, key)
        return key
        
    def adapt_text(self, text: str) -> str:
        """
        Adapta um texto completo para o país selecionado
        
        Args:
            text: Texto a ser adaptado
            
        Returns:
            Texto adaptado
        """
        # Lista de substituições comuns
        if self.country == 'PT':
            replacements = {
                # Ortografia
                'ação': 'acção',
                'ações': 'acções',
                'direção': 'direcção',
                'objetivo': 'objectivo',
                'projeto': 'projecto',
                'elétrico': 'eléctrico',
                'ótimo': 'óptimo',
                
                # Vocabulário
                'usuário': 'utilizador',
                'deletar': 'eliminar',
                'salvar': 'guardar',
                'arquivo': 'ficheiro',
                'relatório': 'relatório',
                
                # Termos jurídicos
                'fatura': 'factura',
                'recibo': 'recibo',
                
                # Moeda
                'R$': '€',
                'reais': 'euros',
                'real': 'euro'
            }
            
            for br_term, pt_term in replacements.items():
                text = text.replace(br_term, pt_term)
                # Também substitui com inicial maiúscula
                text = text.replace(br_term.capitalize(), pt_term.capitalize())
                
        return text
        
    def _load_translations(self) -> Dict[str, Dict[str, str]]:
        """Carrega o dicionário de traduções/adaptações"""
        return {
            # Interface
            'save': {'BR': 'Salvar', 'PT': 'Guardar'},
            'delete': {'BR': 'Deletar', 'PT': 'Eliminar'},
            'file': {'BR': 'Arquivo', 'PT': 'Ficheiro'},
            'user': {'BR': 'Usuário', 'PT': 'Utilizador'},
            'report': {'BR': 'Relatório', 'PT': 'Relatório'},
            
            # Documentos
            'invoice': {'BR': 'Fatura', 'PT': 'Factura'},
            'receipt': {'BR': 'Recibo', 'PT': 'Recibo'},
            'proposal': {'BR': 'Proposta', 'PT': 'Proposta'},
            'contract': {'BR': 'Contrato', 'PT': 'Contrato'},
            
            # Termos comerciais
            'client': {'BR': 'Cliente', 'PT': 'Cliente'},
            'supplier': {'BR': 'Fornecedor', 'PT': 'Fornecedor'},
            'service': {'BR': 'Serviço', 'PT': 'Serviço'},
            'product': {'BR': 'Produto', 'PT': 'Produto'},
            
            # Campos de formulário
            'name': {'BR': 'Nome', 'PT': 'Nome'},
            'address': {'BR': 'Endereço', 'PT': 'Morada'},
            'zip_code': {'BR': 'CEP', 'PT': 'Código Postal'},
            'state': {'BR': 'Estado', 'PT': 'Distrito'},
            'city': {'BR': 'Cidade', 'PT': 'Cidade'},
            'phone': {'BR': 'Telefone', 'PT': 'Telefone'},
            'mobile': {'BR': 'Celular', 'PT': 'Telemóvel'},
            
            # Documentos de identificação
            'tax_id_company': {'BR': 'CNPJ', 'PT': 'NIPC'},
            'tax_id_person': {'BR': 'CPF', 'PT': 'NIF'},
            'id_card': {'BR': 'RG', 'PT': 'Cartão de Cidadão'},
            
            # Moeda
            'currency': {'BR': 'R$', 'PT': '€'},
            'currency_name': {'BR': 'Real', 'PT': 'Euro'},
            'currency_plural': {'BR': 'Reais', 'PT': 'Euros'},
            
            # Datas
            'date_format': {'BR': 'DD/MM/AAAA', 'PT': 'DD/MM/AAAA'},
            
            # Termos legais
            'contractor': {'BR': 'Contratante', 'PT': 'Primeiro Outorgante'},
            'contracted': {'BR': 'Contratado', 'PT': 'Segundo Outorgante'},
            'clause': {'BR': 'Cláusula', 'PT': 'Cláusula'},
            'court': {'BR': 'Foro', 'PT': 'Tribunal'},
            'jurisdiction': {'BR': 'Comarca', 'PT': 'Comarca'},
            
            # Expressões formais
            'dear': {'BR': 'Prezado(a)', 'PT': 'Exmo(a). Sr(a).'},
            'sincerely': {'BR': 'Atenciosamente', 'PT': 'Com os melhores cumprimentos'},
            'regards': {'BR': 'Cordialmente', 'PT': 'Cumprimentos'},
            
            # Ações
            'agree': {'BR': 'Concordar', 'PT': 'Concordar'},
            'sign': {'BR': 'Assinar', 'PT': 'Assinar'},
            'approve': {'BR': 'Aprovar', 'PT': 'Aprovar'},
            'cancel': {'BR': 'Cancelar', 'PT': 'Cancelar'},
            
            # Status
            'pending': {'BR': 'Pendente', 'PT': 'Pendente'},
            'approved': {'BR': 'Aprovado', 'PT': 'Aprovado'},
            'rejected': {'BR': 'Rejeitado', 'PT': 'Rejeitado'},
            'completed': {'BR': 'Concluído', 'PT': 'Concluído'}
        }
        
    def format_currency(self, value: float) -> str:
        """
        Formata valor monetário de acordo com o país
        
        Args:
            value: Valor numérico
            
        Returns:
            Valor formatado com símbolo da moeda
        """
        if self.country == 'BR':
            # Formato brasileiro: R$ 1.234,56
            formatted = f"{value:,.2f}".replace(',', '_').replace('.', ',').replace('_', '.')
            return f"R$ {formatted}"
        else:
            # Formato português: 1.234,56 €
            formatted = f"{value:,.2f}".replace(',', '_').replace('.', ',').replace('_', '.')
            return f"{formatted} €"
            
    def get_date_format(self) -> str:
        """Retorna o formato de data apropriado"""
        # Ambos os países usam DD/MM/AAAA
        return "%d/%m/%Y"
        
    def get_document_terms(self) -> Dict[str, str]:
        """Retorna termos específicos para documentos legais"""
        terms = {
            'contractor': self.get_term('contractor'),
            'contracted': self.get_term('contracted'),
            'clause': self.get_term('clause'),
            'court': self.get_term('court'),
            'tax_id_company': self.get_term('tax_id_company'),
            'tax_id_person': self.get_term('tax_id_person'),
            'currency': self.get_term('currency')
        }
        return terms
