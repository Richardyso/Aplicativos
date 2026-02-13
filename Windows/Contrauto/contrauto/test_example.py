#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script de exemplo para testar os componentes do Propoza
"""

from datetime import datetime
from core.contract_builder import ContractBuilder
from core.proposal_builder import ProposalBuilder
from export.pdf_generator import PDFGenerator


def test_proposal():
    """Testa a geração de uma proposta"""
    print("Testando geração de proposta...")
    
    # Dados de exemplo
    data = {
        'country': 'BR',
        'contractor_name': 'Empresa Exemplo LTDA',
        'contractor_type': 'PJ',
        'contractor_doc': '12.345.678/0001-90',
        'contractor_address': 'Rua das Flores, 123, Centro, São Paulo - SP',
        'contractor_email': 'contato@empresa.com.br',
        'contractor_phone': '(11) 1234-5678',
        
        'contracted_name': 'Consultoria Tech Solutions',
        'contracted_type': 'PJ',
        'contracted_doc': '98.765.432/0001-10',
        'contracted_email': 'comercial@techsolutions.com.br',
        'contracted_phone': '(11) 9876-5432',
        
        'company_specialty': 'desenvolvimento de software e consultoria em TI',
        'company_experience': 'mais de 10 anos',
        
        'service_title': 'Desenvolvimento de Sistema Web',
        'service_description': 'Desenvolvimento de sistema web completo para gestão empresarial, incluindo módulos de vendas, estoque e financeiro.',
        'service_objective': 'automatizar e otimizar os processos de gestão da empresa',
        
        'deliverables': [
            'Sistema web responsivo',
            'Painel administrativo',
            'Relatórios gerenciais',
            'Manual do usuário',
            'Treinamento da equipe'
        ],
        
        'total_value': 25000.00,
        'payment_method': 'Parcelado em 4x',
        'payment_details': '25% na assinatura, 25% na entrega do protótipo, 25% na entrega final, 25% após 30 dias',
        
        'execution_deadline': '90 dias',
        'proposal_validity': '30',
        
        'city': 'São Paulo',
        'created_at': datetime.now()
    }
    
    # Gera a proposta
    builder = ProposalBuilder()
    proposal = builder.build(data)
    
    print(f"Proposta gerada: {proposal['title']}")
    
    # Gera o PDF
    pdf_gen = PDFGenerator()
    pdf_path = pdf_gen.generate(proposal, data)
    
    print(f"PDF gerado em: {pdf_path}")
    return pdf_path


def test_contract():
    """Testa a geração de um contrato"""
    print("\nTestando geração de contrato...")
    
    # Dados de exemplo
    data = {
        'country': 'PT',
        'contractor_name': 'Empresa Portuguesa Unipessoal',
        'contractor_type': 'PJ',
        'contractor_doc': '123456789',
        'contractor_address': 'Avenida da Liberdade, 100, 1250-096 Lisboa',
        
        'contracted_name': 'João Silva',
        'contracted_type': 'PF',
        'contracted_doc': '987654321',
        'contracted_address': 'Rua do Comércio, 50, 4000-123 Porto',
        
        'contract_object': 'Prestação de serviços de consultoria em marketing digital, incluindo gestão de redes sociais e campanhas publicitárias',
        
        'total_value': 5000.00,
        'payment_terms': 'Pagamento mensal de €1.000,00, até ao dia 5 de cada mês',
        
        'contract_duration': '6 meses',
        'start_date': '01/01/2024',
        'end_date': '30/06/2024',
        
        'city': 'Lisboa',
        'created_at': datetime.now()
    }
    
    # Gera o contrato
    builder = ContractBuilder()
    contract = builder.build(data)
    
    print(f"Contrato gerado: {contract['title']}")
    
    # Gera o PDF
    pdf_gen = PDFGenerator()
    pdf_path = pdf_gen.generate(contract, data)
    
    print(f"PDF gerado em: {pdf_path}")
    return pdf_path


if __name__ == "__main__":
    print("=== TESTE DO SISTEMA PROPOZA ===\n")
    
    try:
        # Testa proposta
        test_proposal()
        
        # Testa contrato
        test_contract()
        
        print("\n✅ Testes concluídos com sucesso!")
        print("Verifique os PDFs gerados na pasta 'documentos_gerados'")
        
    except Exception as e:
        print(f"\n❌ Erro durante os testes: {e}")
        import traceback
        traceback.print_exc()
