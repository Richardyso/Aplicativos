# -*- coding: utf-8 -*-
"""
Builder para construção de contratos
Monta o documento final a partir dos dados coletados
"""

from typing import Dict, List, Optional
from datetime import datetime
import os
from pathlib import Path
from .placeholders import PlaceholderManager
from .language_selector import LanguageSelector


class ContractBuilder:
    """Constrói contratos a partir de templates e dados coletados"""
    
    def __init__(self):
        """Inicializa o builder de contratos"""
        self.placeholder_manager = PlaceholderManager()
        self.language_selector = LanguageSelector()
        self.models_path = Path(__file__).parent.parent / "models"
        
    def build(self, data: Dict) -> Dict:
        """
        Constrói o contrato com base nos dados fornecidos
        
        Args:
            data: Dicionário com todos os dados coletados no wizard
            
        Returns:
            Dict com o conteúdo do contrato e metadados
        """
        # Seleciona o template baseado no país
        template = self._load_template(data.get('country', 'BR'))
        
        # Prepara os dados para substituição
        prepared_data = self._prepare_data(data)
        
        # Substitui os placeholders
        content = self.placeholder_manager.replace(template, prepared_data)
        
        # Adiciona cláusulas selecionadas
        content = self._add_selected_clauses(content, data)
        
        # Formata o documento final
        formatted_content = self._format_document(content, data)
        
        return {
            'content': formatted_content,
            'title': self._generate_title(data),
            'type': 'contract',
            'country': data.get('country', 'BR'),
            'metadata': self._generate_metadata(data)
        }
        
    def _load_template(self, country: str) -> str:
        """
        Carrega o template de contrato apropriado
        
        Args:
            country: Código do país (BR ou PT)
            
        Returns:
            Conteúdo do template
        """
        filename = f"contrato_{country.lower()}.txt"
        template_path = self.models_path / filename
        
        # Se o arquivo não existir, retorna um template básico
        if not template_path.exists():
            return self._get_default_template(country)
            
        try:
            with open(template_path, 'r', encoding='utf-8') as f:
                return f.read()
        except Exception as e:
            print(f"Erro ao carregar template: {e}")
            return self._get_default_template(country)
            
    def _get_default_template(self, country: str) -> str:
        """Retorna um template padrão de contrato"""
        if country == 'BR':
            return """
CONTRATO DE PRESTAÇÃO DE SERVIÇOS

Pelo presente instrumento particular, de um lado:

CONTRATANTE: {contractor_name}, {contractor_type}, inscrito(a) no {contractor_doc_type} sob o nº {contractor_doc}, 
com sede/endereço em {contractor_address}, neste ato representado(a) na forma de seu estatuto/contrato social,

E de outro lado:

CONTRATADO: {contracted_name}, {contracted_type}, inscrito(a) no {contracted_doc_type} sob o nº {contracted_doc}, 
com sede/endereço em {contracted_address}, neste ato representado(a) na forma de seu estatuto/contrato social,

Têm entre si justo e contratado o que segue:

CLÁUSULA PRIMEIRA - DO OBJETO
{contract_object}

CLÁUSULA SEGUNDA - DO VALOR E FORMA DE PAGAMENTO
{payment_terms}

CLÁUSULA TERCEIRA - DO PRAZO
{contract_duration}

CLÁUSULA QUARTA - DAS OBRIGAÇÕES DO CONTRATANTE
{contractor_obligations}

CLÁUSULA QUINTA - DAS OBRIGAÇÕES DO CONTRATADO
{contracted_obligations}

{additional_clauses}

CLÁUSULA ÚLTIMA - DO FORO
Fica eleito o foro da comarca de {city}, {state}, para dirimir quaisquer dúvidas oriundas do presente contrato.

E por estarem assim justos e contratados, assinam o presente instrumento em 2 (duas) vias de igual teor.

{city}, {date_full}

_________________________________
{contractor_name}
CONTRATANTE

_________________________________
{contracted_name}
CONTRATADO
"""
        else:  # PT
            return """
CONTRATO DE PRESTAÇÃO DE SERVIÇOS

Entre:

PRIMEIRO OUTORGANTE: {contractor_name}, {contractor_type}, com o NIF {contractor_doc}, 
com sede em {contractor_address}, adiante designado por CONTRATANTE,

E

SEGUNDO OUTORGANTE: {contracted_name}, {contracted_type}, com o NIF {contracted_doc}, 
com sede em {contracted_address}, adiante designado por CONTRATADO,

É celebrado o presente contrato de prestação de serviços que se rege pelas seguintes cláusulas:

CLÁUSULA PRIMEIRA - OBJETO
{contract_object}

CLÁUSULA SEGUNDA - PREÇO E CONDIÇÕES DE PAGAMENTO
{payment_terms}

CLÁUSULA TERCEIRA - PRAZO
{contract_duration}

CLÁUSULA QUARTA - OBRIGAÇÕES DO CONTRATANTE
{contractor_obligations}

CLÁUSULA QUINTA - OBRIGAÇÕES DO CONTRATADO
{contracted_obligations}

{additional_clauses}

CLÁUSULA ÚLTIMA - FORO COMPETENTE
Para a resolução de todos os litígios emergentes do presente contrato fica estipulada a competência do Tribunal da Comarca de {city}.

Feito em {city}, aos {date_full}, em dois exemplares de igual valor.

O CONTRATANTE                           O CONTRATADO

_____________________                   _____________________
{contractor_name}                       {contracted_name}
"""
            
    def _prepare_data(self, data: Dict) -> Dict:
        """
        Prepara os dados para substituição nos templates
        
        Args:
            data: Dados brutos do wizard
            
        Returns:
            Dados formatados para placeholders
        """
        prepared = {}
        
        # Dados do contratante
        prepared['contractor_name'] = data.get('contractor_name', '')
        prepared['contractor_type'] = 'pessoa jurídica' if data.get('contractor_type') == 'PJ' else 'pessoa física'
        prepared['contractor_doc'] = data.get('contractor_doc', '')
        prepared['contractor_doc_type'] = self._get_doc_type(data.get('contractor_type'), data.get('country'))
        prepared['contractor_address'] = data.get('contractor_address', '')
        
        # Dados do contratado
        prepared['contracted_name'] = data.get('contracted_name', '')
        prepared['contracted_type'] = 'pessoa jurídica' if data.get('contracted_type') == 'PJ' else 'pessoa física'
        prepared['contracted_doc'] = data.get('contracted_doc', '')
        prepared['contracted_doc_type'] = self._get_doc_type(data.get('contracted_type'), data.get('country'))
        prepared['contracted_address'] = data.get('contracted_address', '')
        
        # Objeto e condições
        prepared['contract_object'] = data.get('contract_object', '')
        prepared['payment_terms'] = data.get('payment_terms', '')
        prepared['contract_duration'] = data.get('contract_duration', '')
        
        # Obrigações
        prepared['contractor_obligations'] = data.get('contractor_obligations', 'Conforme acordado entre as partes.')
        prepared['contracted_obligations'] = data.get('contracted_obligations', 'Conforme acordado entre as partes.')
        
        # Local e data
        prepared['city'] = data.get('city', '')
        prepared['state'] = data.get('state', '') if data.get('country') == 'BR' else ''
        prepared['date_full'] = self._format_date(data.get('created_at', datetime.now()), data.get('country'))
        
        return prepared
        
    def _get_doc_type(self, person_type: str, country: str) -> str:
        """Retorna o tipo de documento apropriado"""
        if country == 'BR':
            return 'CNPJ' if person_type == 'PJ' else 'CPF'
        else:
            return 'NIPC' if person_type == 'PJ' else 'NIF'
            
    def _format_date(self, date: datetime, country: str) -> str:
        """Formata a data de acordo com o país"""
        if country == 'BR':
            return date.strftime('%d de %B de %Y')
        else:
            return date.strftime('%d de %B de %Y')
            
    def _add_selected_clauses(self, content: str, data: Dict) -> str:
        """Adiciona cláusulas adicionais selecionadas"""
        additional_clauses = data.get('additional_clauses', [])
        
        if not additional_clauses:
            return content.replace('{additional_clauses}', '')
            
        # Aqui seria construída a lista de cláusulas adicionais
        clauses_text = "\n\n".join(additional_clauses)
        
        return content.replace('{additional_clauses}', clauses_text)
        
    def _format_document(self, content: str, data: Dict) -> str:
        """Formata o documento final"""
        # Remove placeholders não utilizados
        content = self.placeholder_manager.clean_unused(content)
        
        # Ajusta formatação
        content = content.strip()
        
        # Adiciona numeração de páginas se necessário
        # (implementação futura)
        
        return content
        
    def _generate_title(self, data: Dict) -> str:
        """Gera o título do documento"""
        contractor = data.get('contractor_name', 'Contratante')
        contracted = data.get('contracted_name', 'Contratado')
        
        return f"Contrato - {contractor} x {contracted}"
        
    def _generate_metadata(self, data: Dict) -> Dict:
        """Gera metadados do documento"""
        return {
            'created_at': data.get('created_at', datetime.now()).isoformat(),
            'country': data.get('country', 'BR'),
            'contractor': data.get('contractor_name', ''),
            'contracted': data.get('contracted_name', ''),
            'value': data.get('total_value', ''),
            'duration': data.get('contract_duration', '')
        }
