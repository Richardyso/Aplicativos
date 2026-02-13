# -*- coding: utf-8 -*-
"""
Builder para construção de propostas comerciais
Monta o documento final a partir dos dados coletados
"""

from typing import Dict, List, Optional
from datetime import datetime, timedelta
import os
from pathlib import Path
from .placeholders import PlaceholderManager
from .language_selector import LanguageSelector


class ProposalBuilder:
    """Constrói propostas comerciais a partir de templates e dados coletados"""
    
    def __init__(self):
        """Inicializa o builder de propostas"""
        self.placeholder_manager = PlaceholderManager()
        self.language_selector = LanguageSelector()
        self.models_path = Path(__file__).parent.parent / "models"
        
    def build(self, data: Dict) -> Dict:
        """
        Constrói a proposta com base nos dados fornecidos
        
        Args:
            data: Dicionário com todos os dados coletados no wizard
            
        Returns:
            Dict com o conteúdo da proposta e metadados
        """
        # Seleciona o template baseado no país
        template = self._load_template(data.get('country', 'BR'))
        
        # Prepara os dados para substituição
        prepared_data = self._prepare_data(data)
        
        # Substitui os placeholders
        content = self.placeholder_manager.replace(template, prepared_data)
        
        # Adiciona seções personalizadas
        content = self._add_custom_sections(content, data)
        
        # Formata o documento final
        formatted_content = self._format_document(content, data)
        
        return {
            'content': formatted_content,
            'title': self._generate_title(data),
            'type': 'proposal',
            'country': data.get('country', 'BR'),
            'metadata': self._generate_metadata(data)
        }
        
    def _load_template(self, country: str) -> str:
        """
        Carrega o template de proposta apropriado
        
        Args:
            country: Código do país (BR ou PT)
            
        Returns:
            Conteúdo do template
        """
        filename = f"proposta_{country.lower()}.txt"
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
        """Retorna um template padrão de proposta"""
        if country == 'BR':
            return """
PROPOSTA COMERCIAL

{city}, {date_full}

À
{contractor_name}
{contractor_address}
{contractor_doc_type}: {contractor_doc}

Prezado(a) Cliente,

Conforme solicitado, apresentamos nossa proposta comercial para {service_title}.

1. APRESENTAÇÃO DA EMPRESA

{contracted_name} é uma empresa especializada em {company_specialty}, com ampla experiência no mercado.

2. OBJETO DA PROPOSTA

{service_description}

3. ESCOPO DOS SERVIÇOS

{service_scope}

4. ENTREGÁVEIS

{deliverables}

5. INVESTIMENTO

Valor Total: R$ {total_value}

Forma de Pagamento: {payment_method}
{payment_details}

6. PRAZO DE EXECUÇÃO

{execution_deadline}

7. VALIDADE DA PROPOSTA

Esta proposta tem validade de {proposal_validity} dias a partir desta data.

8. CONDIÇÕES GERAIS

{general_conditions}

9. CONCLUSÃO

Colocamo-nos à disposição para quaisquer esclarecimentos que se façam necessários.

Atenciosamente,

_________________________________
{contracted_name}
{contracted_doc_type}: {contracted_doc}
{contracted_contact}
"""
        else:  # PT
            return """
PROPOSTA COMERCIAL

{city}, {date_full}

Exmo(a). Sr(a).
{contractor_name}
{contractor_address}
NIF: {contractor_doc}

Assunto: Proposta Comercial - {service_title}

Estimado(a) Cliente,

Em resposta à vossa solicitação, vimos por este meio apresentar a nossa proposta comercial.

1. APRESENTAÇÃO

{contracted_name}, com o NIF {contracted_doc}, é uma empresa especializada em {company_specialty}.

2. OBJETO

{service_description}

3. ÂMBITO DOS SERVIÇOS

{service_scope}

4. ENTREGÁVEIS

{deliverables}

5. VALOR DO INVESTIMENTO

Valor Total: € {total_value}

Condições de Pagamento: {payment_method}
{payment_details}

6. PRAZO DE EXECUÇÃO

{execution_deadline}

7. VALIDADE DA PROPOSTA

A presente proposta é válida por {proposal_validity} dias a contar da data acima indicada.

8. CONDIÇÕES GERAIS

{general_conditions}

9. CONSIDERAÇÕES FINAIS

Esperamos que a presente proposta vá ao encontro das vossas expectativas.
Ficamos ao dispor para qualquer esclarecimento adicional.

Com os melhores cumprimentos,

_________________________________
{contracted_name}
NIF: {contracted_doc}
{contracted_contact}
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
        
        # Dados do cliente (contratante)
        prepared['contractor_name'] = data.get('contractor_name', '')
        prepared['contractor_doc'] = data.get('contractor_doc', '')
        prepared['contractor_doc_type'] = self._get_doc_type(data.get('contractor_type'), data.get('country'))
        prepared['contractor_address'] = data.get('contractor_address', '')
        
        # Dados da empresa (contratado)
        prepared['contracted_name'] = data.get('contracted_name', '')
        prepared['contracted_doc'] = data.get('contracted_doc', '')
        prepared['contracted_doc_type'] = self._get_doc_type(data.get('contracted_type'), data.get('country'))
        prepared['contracted_contact'] = self._format_contact_info(data)
        prepared['company_specialty'] = data.get('company_specialty', 'prestação de serviços')
        prepared['company_experience'] = data.get('company_experience', 'vários anos')
        
        # Serviço
        prepared['service_title'] = data.get('service_title', '')
        prepared['service_objective'] = data.get('service_objective', 'atender às necessidades do cliente')
        prepared['service_description'] = data.get('service_description', '')
        prepared['service_scope'] = data.get('service_scope', data.get('service_description', ''))
        
        # Entregáveis
        deliverables = data.get('deliverables', [])
        if isinstance(deliverables, list):
            prepared['deliverables'] = self._format_deliverables(deliverables)
        else:
            prepared['deliverables'] = deliverables
        
        # Valores e pagamento
        prepared['total_value'] = self._format_currency(data.get('total_value', 0), data.get('country'))
        prepared['payment_method'] = data.get('payment_method', '')
        prepared['payment_details'] = data.get('payment_details', '')
        
        # Prazos
        prepared['execution_deadline'] = data.get('execution_deadline', '30 dias')
        prepared['proposal_validity'] = data.get('proposal_validity', '30')
        
        # Condições gerais
        prepared['general_conditions'] = data.get('general_conditions', self._get_default_conditions(data.get('country')))
        
        # Local e data
        prepared['city'] = data.get('city', '')
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
        months_pt = {
            1: 'janeiro', 2: 'fevereiro', 3: 'março', 4: 'abril',
            5: 'maio', 6: 'junho', 7: 'julho', 8: 'agosto',
            9: 'setembro', 10: 'outubro', 11: 'novembro', 12: 'dezembro'
        }
        
        month = months_pt[date.month]
        return f"{date.day} de {month} de {date.year}"
        
    def _format_contact_info(self, data: Dict) -> str:
        """Formata as informações de contato"""
        contact_parts = []
        
        if data.get('contracted_email'):
            contact_parts.append(f"E-mail: {data['contracted_email']}")
            
        if data.get('contracted_phone'):
            contact_parts.append(f"Telefone: {data['contracted_phone']}")
            
        return ' | '.join(contact_parts)
        
    def _format_deliverables(self, deliverables: List[str]) -> str:
        """Formata a lista de entregáveis"""
        if not deliverables:
            return "A definir conforme necessidade do projeto"
            
        formatted = []
        for i, deliverable in enumerate(deliverables, 1):
            formatted.append(f"{i}. {deliverable}")
            
        return '\n'.join(formatted)
        
    def _format_currency(self, value: float, country: str) -> str:
        """Formata valor monetário de acordo com o país"""
        try:
            value = float(value)
            if country == 'BR':
                # Formato brasileiro: R$ 1.234,56
                return f"{value:,.2f}".replace(',', '_').replace('.', ',').replace('_', '.')
            else:
                # Formato português: 1.234,56 €
                return f"{value:,.2f}".replace(',', '_').replace('.', ',').replace('_', '.')
        except:
            return str(value)
            
    def _get_default_conditions(self, country: str) -> str:
        """Retorna condições gerais padrão"""
        if country == 'BR':
            return """- Os valores apresentados não incluem impostos, que serão adicionados conforme legislação vigente.
- O prazo de execução será contado a partir da aprovação formal desta proposta.
- Qualquer alteração no escopo poderá impactar nos valores e prazos apresentados.
- Os trabalhos serão realizados em horário comercial."""
        else:
            return """- Aos valores apresentados acresce IVA à taxa legal em vigor.
- O prazo de execução inicia-se após a adjudicação formal.
- Alterações ao âmbito inicial poderão implicar revisão de valores e prazos.
- Os trabalhos serão executados em horário normal de expediente."""
            
    def _add_custom_sections(self, content: str, data: Dict) -> str:
        """Adiciona seções personalizadas à proposta"""
        # Aqui poderiam ser adicionadas seções extras conforme necessidade
        # Por exemplo: casos de sucesso, certificações, etc.
        return content
        
    def _format_document(self, content: str, data: Dict) -> str:
        """Formata o documento final"""
        # Remove placeholders não utilizados
        content = self.placeholder_manager.clean_unused(content)
        
        # Ajusta espaçamento e formatação
        content = content.strip()
        
        # Remove linhas vazias múltiplas
        while '\n\n\n' in content:
            content = content.replace('\n\n\n', '\n\n')
            
        return content
        
    def _generate_title(self, data: Dict) -> str:
        """Gera o título do documento"""
        service = data.get('service_title', 'Serviços')
        contractor = data.get('contractor_name', 'Cliente')
        
        return f"Proposta - {service} - {contractor}"
        
    def _generate_metadata(self, data: Dict) -> Dict:
        """Gera metadados do documento"""
        created_at = data.get('created_at', datetime.now())
        validity_days = int(data.get('proposal_validity', 30))
        valid_until = created_at + timedelta(days=validity_days)
        
        return {
            'created_at': created_at.isoformat(),
            'valid_until': valid_until.isoformat(),
            'country': data.get('country', 'BR'),
            'client': data.get('contractor_name', ''),
            'service': data.get('service_title', ''),
            'value': data.get('total_value', ''),
            'validity_days': validity_days
        }
