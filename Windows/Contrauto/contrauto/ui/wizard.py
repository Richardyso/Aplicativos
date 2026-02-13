# -*- coding: utf-8 -*-
"""
Wizard para criação guiada de documentos
Gerencia o fluxo de perguntas e coleta de dados
"""

import customtkinter as ctk
from typing import Dict, List, Callable, Optional
from datetime import datetime
import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from core.language_selector import LanguageSelector
from core.contract_builder import ContractBuilder
from core.proposal_builder import ProposalBuilder
from export.pdf_generator import PDFGenerator


class DocumentWizard:
    """Wizard para criação passo a passo de documentos"""
    
    def __init__(self, parent: ctk.CTkFrame, document_type: str, on_complete: Callable):
        """
        Inicializa o wizard
        
        Args:
            parent: Frame pai onde o wizard será exibido
            document_type: Tipo de documento ('proposal' ou 'contract')
            on_complete: Callback chamado ao completar o wizard
        """
        self.parent = parent
        self.document_type = document_type
        self.on_complete = on_complete
        
        # Dados coletados
        self.data: Dict = {
            'document_type': document_type,
            'country': None,
            'created_at': datetime.now()
        }
        
        # Controle de etapas
        self.current_step = 0
        self.steps: List[Dict] = []
        
        # Builders
        self.language_selector = LanguageSelector()
        self.contract_builder = ContractBuilder()
        self.proposal_builder = ProposalBuilder()
        self.pdf_generator = PDFGenerator()
        
        # Interface
        self._setup_ui()
        self._define_steps()
        self._show_current_step()
        
    def _setup_ui(self):
        """Configura a interface do wizard"""
        # Container principal
        self.wizard_container = ctk.CTkFrame(self.parent)
        self.wizard_container.pack(fill="both", expand=True, padx=20, pady=20)
        
        # Cabeçalho com título e progresso
        self._create_header()
        
        # Rodapé com botões de navegação - criado ANTES do conteúdo
        self._create_footer()
        
        # Área de conteúdo - criada DEPOIS do rodapé para não sobrepor
        content_container = ctk.CTkFrame(self.wizard_container)
        content_container.pack(fill="both", expand=True, pady=(10, 0))
        
        self.content_frame = ctk.CTkFrame(content_container)
        self.content_frame.pack(fill="both", expand=True, padx=0, pady=0)
        
    def _create_header(self):
        """Cria o cabeçalho do wizard"""
        header_frame = ctk.CTkFrame(self.wizard_container)
        header_frame.pack(fill="x", pady=(0, 20))
        
        # Título do documento
        title_text = "Nova Proposta Comercial" if self.document_type == "proposal" else "Novo Contrato"
        self.title_label = ctk.CTkLabel(
            header_frame,
            text=title_text,
            font=ctk.CTkFont(size=24, weight="bold")
        )
        self.title_label.pack(pady=10)
        
        # Barra de progresso
        self.progress_bar = ctk.CTkProgressBar(header_frame, width=600)
        self.progress_bar.pack(pady=10)
        self.progress_bar.set(0)
        
        # Label de etapa
        self.step_label = ctk.CTkLabel(
            header_frame,
            text="Etapa 1 de X",
            font=ctk.CTkFont(size=12)
        )
        self.step_label.pack()
        
    def _create_footer(self):
        """Cria o rodapé com botões de navegação"""
        footer_frame = ctk.CTkFrame(self.wizard_container, height=60)
        footer_frame.pack(side="bottom", fill="x", pady=(10, 0))
        footer_frame.pack_propagate(False)  # Mantém altura fixa
        
        # Container interno para os botões
        button_container = ctk.CTkFrame(footer_frame, fg_color="transparent")
        button_container.pack(expand=True, fill="both", padx=10, pady=10)
        
        # Botão Voltar
        self.back_btn = ctk.CTkButton(
            button_container,
            text="← Voltar",
            width=120,
            command=self._previous_step
        )
        self.back_btn.pack(side="left", padx=5)
        
        # Botão Cancelar
        self.cancel_btn = ctk.CTkButton(
            button_container,
            text="Cancelar",
            width=120,
            fg_color="gray",
            hover_color="darkgray",
            command=self._cancel_wizard
        )
        self.cancel_btn.pack(side="left", padx=5)
        
        # Botão Próximo/Finalizar
        self.next_btn = ctk.CTkButton(
            button_container,
            text="Próximo →",
            width=120,
            command=self._next_step
        )
        self.next_btn.pack(side="right", padx=5)
        
    def _define_steps(self):
        """Define as etapas do wizard baseado no tipo de documento"""
        # Etapas comuns - CONTRATADO primeiro (sua empresa)
        common_steps = [
            {
                'title': 'Seleção de País',
                'description': 'Escolha o país para adequar o documento às normas locais',
                'builder': self._build_country_selection
            },
            {
                'title': 'Dados do Contratado',
                'description': 'Informações da sua empresa ou seus dados (quem presta o serviço)',
                'builder': self._build_contracted_info
            },
            {
                'title': 'Dados do Contratante',
                'description': 'Informações do cliente que está contratando',
                'builder': self._build_contractor_info
            }
        ]
        
        # Etapas específicas por tipo
        if self.document_type == "proposal":
            specific_steps = [
                {
                    'title': 'Descrição do Serviço',
                    'description': 'Detalhe o serviço ou produto oferecido',
                    'builder': self._build_service_description
                },
                {
                    'title': 'Valores e Pagamento',
                    'description': 'Defina valores e condições de pagamento',
                    'builder': self._build_payment_info
                },
                {
                    'title': 'Prazos',
                    'description': 'Estabeleça prazos de execução e validade',
                    'builder': self._build_deadlines
                }
            ]
        else:  # contract
            specific_steps = [
                {
                    'title': 'Objeto do Contrato',
                    'description': 'Defina o que está sendo contratado',
                    'builder': self._build_contract_object
                },
                {
                    'title': 'Valores e Pagamento',
                    'description': 'Defina valores e condições de pagamento',
                    'builder': self._build_payment_info
                },
                {
                    'title': 'Cláusulas e Condições',
                    'description': 'Selecione cláusulas aplicáveis',
                    'builder': self._build_clauses
                },
                {
                    'title': 'Vigência e Prazos',
                    'description': 'Defina período de vigência e prazos',
                    'builder': self._build_contract_duration
                }
            ]
        
        # Etapa final comum
        final_step = {
            'title': 'Revisão e Finalização',
            'description': 'Revise os dados e gere o documento',
            'builder': self._build_review
        }
        
        self.steps = common_steps + specific_steps + [final_step]
        
    def _show_current_step(self):
        """Exibe a etapa atual do wizard"""
        # Limpa o conteúdo anterior
        for widget in self.content_frame.winfo_children():
            widget.destroy()
            
        # Atualiza progresso
        progress = (self.current_step + 1) / len(self.steps)
        self.progress_bar.set(progress)
        self.step_label.configure(text=f"Etapa {self.current_step + 1} de {len(self.steps)}")
        
        # Atualiza botões
        self.back_btn.configure(state="normal" if self.current_step > 0 else "disabled")
        is_last_step = self.current_step == len(self.steps) - 1
        self.next_btn.configure(text="Finalizar ✓" if is_last_step else "Próximo →")
        
        # Exibe conteúdo da etapa
        step = self.steps[self.current_step]
        
        # Título da etapa
        step_title = ctk.CTkLabel(
            self.content_frame,
            text=step['title'],
            font=ctk.CTkFont(size=20, weight="bold")
        )
        step_title.pack(pady=(20, 10))
        
        # Descrição da etapa
        step_desc = ctk.CTkLabel(
            self.content_frame,
            text=step['description'],
            font=ctk.CTkFont(size=14),
            text_color="gray"
        )
        step_desc.pack(pady=(0, 30))
        
        # Conteúdo específico da etapa
        step['builder']()
        
    def _next_step(self):
        """Avança para a próxima etapa"""
        # Valida dados da etapa atual
        if not self._validate_current_step():
            return
            
        # Salva dados da etapa atual
        self._save_current_step_data()
        
        # Avança ou finaliza
        if self.current_step < len(self.steps) - 1:
            self.current_step += 1
            self._show_current_step()
        else:
            self._finish_wizard()
            
    def _previous_step(self):
        """Volta para a etapa anterior"""
        if self.current_step > 0:
            self.current_step -= 1
            self._show_current_step()
            
    def _cancel_wizard(self):
        """Cancela o wizard"""
        # Aqui poderia ter um diálogo de confirmação
        self.on_complete(None)
        
    def _finish_wizard(self):
        """Finaliza o wizard e gera o documento"""
        # Gera o documento
        try:
            # Seleciona o builder apropriado
            if self.document_type == "proposal":
                document_content = self.proposal_builder.build(self.data)
            else:
                document_content = self.contract_builder.build(self.data)
                
            # Gera o PDF
            pdf_path = self.pdf_generator.generate(document_content, self.data)
            
            # Adiciona o caminho do PDF aos dados
            self.data['pdf_path'] = pdf_path
            
            # Chama callback de conclusão
            self.on_complete(self.data)
            
        except Exception as e:
            print(f"Erro ao gerar documento: {e}")
            # Aqui deveria mostrar um diálogo de erro
            
    def _validate_current_step(self) -> bool:
        """Valida os dados da etapa atual"""
        # Implementação básica - cada builder específico deve validar seus campos
        return True
        
    def _save_current_step_data(self):
        """Salva os dados da etapa atual"""
        step_title = self.steps[self.current_step]['title']
        
        if step_title == 'Seleção de País':
            self.data['country'] = self.country_var.get()
            
        elif step_title == 'Dados do Contratante':
            self.data['contractor_type'] = self.contractor_type_var.get()
            self.data['contractor_name'] = self.contractor_name_entry.get()
            self.data['contractor_doc'] = self.contractor_doc_entry.get()
            self.data['contractor_address'] = self.contractor_address_entry.get()
            self.data['contractor_email'] = self.contractor_email_entry.get()
            self.data['contractor_phone'] = self.contractor_phone_entry.get()
            
        elif step_title == 'Dados do Contratado':
            self.data['contracted_type'] = self.contracted_type_var.get()
            self.data['contracted_name'] = self.contracted_name_entry.get()
            self.data['contracted_doc'] = self.contracted_doc_entry.get()
            self.data['contracted_address'] = self.contracted_address_entry.get()
            self.data['contracted_email'] = self.contracted_email_entry.get()
            self.data['contracted_phone'] = self.contracted_phone_entry.get()
            
        elif step_title == 'Descrição do Serviço':
            self.data['service_title'] = self.service_title_entry.get()
            self.data['service_description'] = self.service_desc_text.get("1.0", "end-1c")
            deliverables = self.deliverables_text.get("1.0", "end-1c").strip().split('\n')
            self.data['deliverables'] = [d.strip() for d in deliverables if d.strip()]
            
        elif step_title == 'Valores e Pagamento':
            self.data['total_value'] = self.total_value_entry.get()
            self.data['payment_method'] = self.payment_method_var.get()
            self.data['payment_details'] = self.payment_details_text.get("1.0", "end-1c")
            
        elif step_title == 'Prazos':
            self.data['execution_deadline'] = self.execution_deadline_entry.get()
            self.data['proposal_validity'] = self.proposal_validity_entry.get()
            self.data['city'] = self.city_entry.get()
            if hasattr(self, 'state_entry'):
                self.data['state'] = self.state_entry.get()
                
        elif step_title == 'Objeto do Contrato':
            self.data['contract_title'] = self.contract_title_entry.get() if hasattr(self, 'contract_title_entry') else ''
            self.data['contract_object'] = self.contract_object_text.get("1.0", "end-1c")
            self.data['contractor_obligations'] = self.contractor_obligations_text.get("1.0", "end-1c")
            self.data['contracted_obligations'] = self.contracted_obligations_text.get("1.0", "end-1c")
            
        elif step_title == 'Cláusulas e Condições':
            selected_clauses = []
            for key, var in self.clause_vars.items():
                if var.get():
                    selected_clauses.append(key)
            self.data['selected_clauses'] = selected_clauses
            
        elif step_title == 'Vigência e Prazos':
            self.data['contract_duration'] = self.contract_duration_entry.get()
            self.data['start_date'] = self.start_date_entry.get()
            self.data['end_date'] = self.end_date_entry.get()
            self.data['renewable'] = self.renewable_var.get()
            self.data['city'] = self.city_entry.get()
            if hasattr(self, 'state_entry'):
                self.data['state'] = self.state_entry.get()
        
    # Builders para cada etapa
    
    def _build_country_selection(self):
        """Constrói a interface de seleção de país"""
        frame = ctk.CTkFrame(self.content_frame, fg_color="transparent")
        frame.pack(fill="both", expand=True)
        
        # Radio buttons para seleção
        self.country_var = ctk.StringVar(value="BR")
        
        brazil_radio = ctk.CTkRadioButton(
            frame,
            text="🇧🇷 Brasil",
            variable=self.country_var,
            value="BR",
            font=ctk.CTkFont(size=16)
        )
        brazil_radio.pack(pady=10)
        
        portugal_radio = ctk.CTkRadioButton(
            frame,
            text="🇵🇹 Portugal",
            variable=self.country_var,
            value="PT",
            font=ctk.CTkFont(size=16)
        )
        portugal_radio.pack(pady=10)
        
        # Informação sobre a diferença
        info_frame = ctk.CTkFrame(frame)
        info_frame.pack(pady=30, padx=50, fill="x")
        
        info_label = ctk.CTkLabel(
            info_frame,
            text="ℹ️ O documento será adaptado às normas e linguagem do país selecionado",
            font=ctk.CTkFont(size=12),
            wraplength=400
        )
        info_label.pack(pady=20, padx=20)
        
    def _build_contractor_info(self):
        """Constrói a interface para dados do contratante"""
        frame = ctk.CTkFrame(self.content_frame, fg_color="transparent")
        frame.pack(fill="both", expand=True)
        
        # Tipo de pessoa
        person_type_label = ctk.CTkLabel(frame, text="Tipo de Pessoa:")
        person_type_label.grid(row=0, column=0, sticky="w", padx=20, pady=5)
        
        self.contractor_type_var = ctk.StringVar(value="PF")
        
        pf_radio = ctk.CTkRadioButton(frame, text="Pessoa Física", variable=self.contractor_type_var, value="PF")
        pf_radio.grid(row=0, column=1, padx=10, pady=5)
        
        pj_radio = ctk.CTkRadioButton(frame, text="Pessoa Jurídica", variable=self.contractor_type_var, value="PJ")
        pj_radio.grid(row=0, column=2, padx=10, pady=5)
        
        # Nome/Razão Social
        name_label = ctk.CTkLabel(frame, text="Nome/Razão Social:")
        name_label.grid(row=1, column=0, sticky="w", padx=20, pady=5)
        
        self.contractor_name_entry = ctk.CTkEntry(frame, width=400)
        self.contractor_name_entry.grid(row=1, column=1, columnspan=2, padx=10, pady=5)
        
        # Documento (CNPJ/CPF/NIF)
        doc_label = ctk.CTkLabel(frame, text="CNPJ/CPF/NIF:")
        doc_label.grid(row=2, column=0, sticky="w", padx=20, pady=5)
        
        self.contractor_doc_entry = ctk.CTkEntry(frame, width=400)
        self.contractor_doc_entry.grid(row=2, column=1, columnspan=2, padx=10, pady=5)
        
        # Endereço
        address_label = ctk.CTkLabel(frame, text="Endereço Completo:")
        address_label.grid(row=3, column=0, sticky="w", padx=20, pady=5)
        
        self.contractor_address_entry = ctk.CTkEntry(frame, width=400)
        self.contractor_address_entry.grid(row=3, column=1, columnspan=2, padx=10, pady=5)
        
        # Email
        email_label = ctk.CTkLabel(frame, text="E-mail:")
        email_label.grid(row=4, column=0, sticky="w", padx=20, pady=5)
        
        self.contractor_email_entry = ctk.CTkEntry(frame, width=400)
        self.contractor_email_entry.grid(row=4, column=1, columnspan=2, padx=10, pady=5)
        
        # Telefone
        phone_label = ctk.CTkLabel(frame, text="Telefone:")
        phone_label.grid(row=5, column=0, sticky="w", padx=20, pady=5)
        
        self.contractor_phone_entry = ctk.CTkEntry(frame, width=400)
        self.contractor_phone_entry.grid(row=5, column=1, columnspan=2, padx=10, pady=5)
        
    def _build_contracted_info(self):
        """Constrói a interface para dados do contratado"""
        frame = ctk.CTkFrame(self.content_frame, fg_color="transparent")
        frame.pack(fill="both", expand=True)
        
        # Tipo de pessoa
        person_type_label = ctk.CTkLabel(frame, text="Tipo de Pessoa:")
        person_type_label.grid(row=0, column=0, sticky="w", padx=20, pady=5)
        
        self.contracted_type_var = ctk.StringVar(value="PF")
        
        pf_radio = ctk.CTkRadioButton(frame, text="Pessoa Física", variable=self.contracted_type_var, value="PF")
        pf_radio.grid(row=0, column=1, padx=10, pady=5)
        
        pj_radio = ctk.CTkRadioButton(frame, text="Pessoa Jurídica", variable=self.contracted_type_var, value="PJ")
        pj_radio.grid(row=0, column=2, padx=10, pady=5)
        
        # Nome/Razão Social
        name_label = ctk.CTkLabel(frame, text="Nome/Razão Social:")
        name_label.grid(row=1, column=0, sticky="w", padx=20, pady=5)
        
        self.contracted_name_entry = ctk.CTkEntry(frame, width=400)
        self.contracted_name_entry.grid(row=1, column=1, columnspan=2, padx=10, pady=5)
        
        # Documento (CNPJ/CPF/NIF)
        doc_label = ctk.CTkLabel(frame, text="CNPJ/CPF/NIF:")
        doc_label.grid(row=2, column=0, sticky="w", padx=20, pady=5)
        
        self.contracted_doc_entry = ctk.CTkEntry(frame, width=400)
        self.contracted_doc_entry.grid(row=2, column=1, columnspan=2, padx=10, pady=5)
        
        # Endereço
        address_label = ctk.CTkLabel(frame, text="Endereço Completo:")
        address_label.grid(row=3, column=0, sticky="w", padx=20, pady=5)
        
        self.contracted_address_entry = ctk.CTkEntry(frame, width=400)
        self.contracted_address_entry.grid(row=3, column=1, columnspan=2, padx=10, pady=5)
        
        # Email
        email_label = ctk.CTkLabel(frame, text="E-mail:")
        email_label.grid(row=4, column=0, sticky="w", padx=20, pady=5)
        
        self.contracted_email_entry = ctk.CTkEntry(frame, width=400)
        self.contracted_email_entry.grid(row=4, column=1, columnspan=2, padx=10, pady=5)
        
        # Telefone
        phone_label = ctk.CTkLabel(frame, text="Telefone:")
        phone_label.grid(row=5, column=0, sticky="w", padx=20, pady=5)
        
        self.contracted_phone_entry = ctk.CTkEntry(frame, width=400)
        self.contracted_phone_entry.grid(row=5, column=1, columnspan=2, padx=10, pady=5)
        
    def _build_service_description(self):
        """Constrói a interface para descrição do serviço (proposta)"""
        # Frame com scroll para o conteúdo
        scroll_frame = ctk.CTkScrollableFrame(self.content_frame, fg_color="transparent")
        scroll_frame.pack(fill="both", expand=True, padx=20, pady=(0, 20))
        
        # Título do serviço
        title_label = ctk.CTkLabel(scroll_frame, text="Título do Serviço/Produto:")
        title_label.pack(anchor="w", pady=5)
        
        self.service_title_entry = ctk.CTkEntry(scroll_frame, width=600)
        self.service_title_entry.pack(fill="x", pady=(0, 20))
        
        # Descrição detalhada
        desc_label = ctk.CTkLabel(scroll_frame, text="Descrição Detalhada:")
        desc_label.pack(anchor="w", pady=5)
        
        self.service_desc_text = ctk.CTkTextbox(scroll_frame, height=150, width=600)
        self.service_desc_text.pack(fill="x", pady=(0, 20))
        
        # Entregáveis
        deliverables_label = ctk.CTkLabel(scroll_frame, text="Entregáveis (um por linha):")
        deliverables_label.pack(anchor="w", pady=5)
        
        self.deliverables_text = ctk.CTkTextbox(scroll_frame, height=100, width=600)
        self.deliverables_text.pack(fill="x", pady=(0, 20))
        
    def _build_contract_object(self):
        """Constrói a interface para objeto do contrato"""
        scroll_frame = ctk.CTkScrollableFrame(self.content_frame, fg_color="transparent")
        scroll_frame.pack(fill="both", expand=True, padx=20, pady=(0, 10))
        
        # Título do contrato
        title_label = ctk.CTkLabel(scroll_frame, text="Título do Contrato:")
        title_label.pack(anchor="w", pady=5)
        
        self.contract_title_entry = ctk.CTkEntry(scroll_frame, width=600, 
                                               placeholder_text="Ex: Contrato de Prestação de Serviços")
        self.contract_title_entry.pack(fill="x", pady=(0, 20))
        
        # Objeto do contrato
        object_label = ctk.CTkLabel(scroll_frame, text="Objeto do Contrato (descreva detalhadamente):")
        object_label.pack(anchor="w", pady=5)
        
        self.contract_object_text = ctk.CTkTextbox(scroll_frame, height=150, width=600)
        self.contract_object_text.pack(fill="x", pady=(0, 20))
        
        # Obrigações do contratante
        contractor_oblig_label = ctk.CTkLabel(scroll_frame, text="Obrigações do Contratante (opcional):")
        contractor_oblig_label.pack(anchor="w", pady=5)
        
        self.contractor_obligations_text = ctk.CTkTextbox(scroll_frame, height=100, width=600)
        self.contractor_obligations_text.pack(fill="x", pady=(0, 20))
        
        # Obrigações do contratado
        contracted_oblig_label = ctk.CTkLabel(scroll_frame, text="Obrigações do Contratado (opcional):")
        contracted_oblig_label.pack(anchor="w", pady=5)
        
        self.contracted_obligations_text = ctk.CTkTextbox(scroll_frame, height=100, width=600)
        self.contracted_obligations_text.pack(fill="x")
        
    def _build_payment_info(self):
        """Constrói a interface para informações de pagamento"""
        frame = ctk.CTkFrame(self.content_frame, fg_color="transparent")
        frame.pack(fill="both", expand=True, padx=20)
        
        # Valor total
        value_label = ctk.CTkLabel(frame, text="Valor Total:")
        value_label.grid(row=0, column=0, sticky="w", pady=5)
        
        self.total_value_entry = ctk.CTkEntry(frame, width=200)
        self.total_value_entry.grid(row=0, column=1, padx=10, pady=5)
        
        # Forma de pagamento
        payment_label = ctk.CTkLabel(frame, text="Forma de Pagamento:")
        payment_label.grid(row=1, column=0, sticky="w", pady=5)
        
        self.payment_method_var = ctk.StringVar(value="vista")
        
        payment_options = ["À vista", "Parcelado", "Por etapa"]
        self.payment_menu = ctk.CTkOptionMenu(
            frame,
            variable=self.payment_method_var,
            values=payment_options,
            width=200
        )
        self.payment_menu.grid(row=1, column=1, padx=10, pady=5)
        
        # Detalhes do pagamento
        details_label = ctk.CTkLabel(frame, text="Detalhes do Pagamento:")
        details_label.grid(row=2, column=0, sticky="nw", pady=5)
        
        self.payment_details_text = ctk.CTkTextbox(frame, height=100, width=400)
        self.payment_details_text.grid(row=2, column=1, columnspan=2, padx=10, pady=5)
        
    def _build_deadlines(self):
        """Constrói a interface para prazos (proposta)"""
        frame = ctk.CTkFrame(self.content_frame, fg_color="transparent")
        frame.pack(fill="both", expand=True, padx=20)
        
        # Prazo de execução
        execution_label = ctk.CTkLabel(frame, text="Prazo de Execução:")
        execution_label.grid(row=0, column=0, sticky="w", pady=10)
        
        self.execution_deadline_entry = ctk.CTkEntry(frame, width=300, placeholder_text="Ex: 30 dias")
        self.execution_deadline_entry.grid(row=0, column=1, padx=10, pady=10)
        
        # Validade da proposta
        validity_label = ctk.CTkLabel(frame, text="Validade da Proposta (dias):")
        validity_label.grid(row=1, column=0, sticky="w", pady=10)
        
        self.proposal_validity_entry = ctk.CTkEntry(frame, width=300, placeholder_text="Ex: 30")
        self.proposal_validity_entry.grid(row=1, column=1, padx=10, pady=10)
        
        # Cidade
        city_label = ctk.CTkLabel(frame, text="Cidade:")
        city_label.grid(row=2, column=0, sticky="w", pady=10)
        
        self.city_entry = ctk.CTkEntry(frame, width=300, placeholder_text="Ex: São Paulo")
        self.city_entry.grid(row=2, column=1, padx=10, pady=10)
        
        # Estado (apenas para Brasil)
        if hasattr(self, 'country_var') and self.country_var.get() == 'BR':
            state_label = ctk.CTkLabel(frame, text="Estado:")
            state_label.grid(row=3, column=0, sticky="w", pady=10)
            
            self.state_entry = ctk.CTkEntry(frame, width=300, placeholder_text="Ex: SP")
            self.state_entry.grid(row=3, column=1, padx=10, pady=10)
        
    def _build_clauses(self):
        """Constrói a interface para seleção de cláusulas (contrato)"""
        scroll_frame = ctk.CTkScrollableFrame(self.content_frame, fg_color="transparent")
        scroll_frame.pack(fill="both", expand=True, padx=20, pady=(0, 10))
        
        info_label = ctk.CTkLabel(
            scroll_frame,
            text="Selecione as cláusulas adicionais que deseja incluir:",
            font=ctk.CTkFont(size=14)
        )
        info_label.pack(pady=(0, 20))
        
        # Cláusulas padrão com checkboxes
        self.clause_vars = {}
        
        clauses = [
            ("confidencialidade", "Cláusula de Confidencialidade", 
             "As partes se comprometem a manter sigilo sobre informações confidenciais"),
            ("multa", "Cláusula de Multa por Descumprimento", 
             "Multa de 10% sobre o valor total em caso de descumprimento"),
            ("reajuste", "Cláusula de Reajuste", 
             "Reajuste anual pelo IPCA ou índice acordado"),
            ("exclusividade", "Cláusula de Exclusividade", 
             "Exclusividade na prestação dos serviços durante a vigência"),
            ("propriedade_intelectual", "Propriedade Intelectual", 
             "Direitos sobre o trabalho desenvolvido")
        ]
        
        for key, title, description in clauses:
            # Frame para cada cláusula
            clause_frame = ctk.CTkFrame(scroll_frame)
            clause_frame.pack(fill="x", pady=5)
            
            # Checkbox
            var = ctk.BooleanVar(value=True if key in ["confidencialidade", "multa"] else False)
            self.clause_vars[key] = var
            
            checkbox = ctk.CTkCheckBox(
                clause_frame,
                text=title,
                variable=var,
                font=ctk.CTkFont(size=14, weight="bold")
            )
            checkbox.pack(anchor="w", padx=10, pady=(10, 5))
            
            # Descrição
            desc_label = ctk.CTkLabel(
                clause_frame,
                text=description,
                font=ctk.CTkFont(size=12),
                text_color="gray"
            )
            desc_label.pack(anchor="w", padx=35, pady=(0, 10))
        
    def _build_contract_duration(self):
        """Constrói a interface para vigência do contrato"""
        frame = ctk.CTkFrame(self.content_frame, fg_color="transparent")
        frame.pack(fill="both", expand=True, padx=20)
        
        # Duração do contrato
        duration_label = ctk.CTkLabel(frame, text="Duração do Contrato:")
        duration_label.grid(row=0, column=0, sticky="w", pady=10)
        
        self.contract_duration_entry = ctk.CTkEntry(frame, width=300, 
                                                   placeholder_text="Ex: 12 meses, 6 meses, indeterminado")
        self.contract_duration_entry.grid(row=0, column=1, padx=10, pady=10)
        
        # Data de início
        start_label = ctk.CTkLabel(frame, text="Data de Início:")
        start_label.grid(row=1, column=0, sticky="w", pady=10)
        
        self.start_date_entry = ctk.CTkEntry(frame, width=300, 
                                            placeholder_text="DD/MM/AAAA")
        self.start_date_entry.grid(row=1, column=1, padx=10, pady=10)
        
        # Data de término
        end_label = ctk.CTkLabel(frame, text="Data de Término:")
        end_label.grid(row=2, column=0, sticky="w", pady=10)
        
        self.end_date_entry = ctk.CTkEntry(frame, width=300, 
                                          placeholder_text="DD/MM/AAAA ou deixe vazio se indeterminado")
        self.end_date_entry.grid(row=2, column=1, padx=10, pady=10)
        
        # Renovação automática
        self.renewable_var = ctk.BooleanVar(value=False)
        renewable_check = ctk.CTkCheckBox(
            frame,
            text="Renovação automática",
            variable=self.renewable_var
        )
        renewable_check.grid(row=3, column=0, columnspan=2, pady=20)
        
        # Cidade e Estado
        city_label = ctk.CTkLabel(frame, text="Cidade:")
        city_label.grid(row=4, column=0, sticky="w", pady=10)
        
        self.city_entry = ctk.CTkEntry(frame, width=300, placeholder_text="Ex: São Paulo")
        self.city_entry.grid(row=4, column=1, padx=10, pady=10)
        
        if hasattr(self, 'country_var') and self.country_var.get() == 'BR':
            state_label = ctk.CTkLabel(frame, text="Estado:")
            state_label.grid(row=5, column=0, sticky="w", pady=10)
            
            self.state_entry = ctk.CTkEntry(frame, width=300, placeholder_text="Ex: SP")
            self.state_entry.grid(row=5, column=1, padx=10, pady=10)
        
    def _build_review(self):
        """Constrói a interface de revisão final"""
        # Frame com scroll para garantir que os botões fiquem visíveis
        scroll_frame = ctk.CTkScrollableFrame(self.content_frame, fg_color="transparent")
        scroll_frame.pack(fill="both", expand=True, padx=20, pady=(0, 10))
        
        # Resumo dos dados
        summary_label = ctk.CTkLabel(
            scroll_frame,
            text="Resumo do Documento",
            font=ctk.CTkFont(size=18, weight="bold")
        )
        summary_label.pack(pady=10)
        
        # Área de texto com preview - altura reduzida
        self.preview_text = ctk.CTkTextbox(scroll_frame, height=300, width=700)
        self.preview_text.pack(fill="x", padx=20, pady=(0, 20))
        
        # Preenche com um resumo dos dados coletados
        preview_content = self._generate_preview()
        self.preview_text.insert("1.0", preview_content)
        self.preview_text.configure(state="disabled")
        
    def _generate_preview(self) -> str:
        """Gera um preview do documento com os dados coletados"""
        doc_type_text = "PROPOSTA COMERCIAL" if self.document_type == "proposal" else "CONTRATO"
        country_text = "Brasil" if self.data.get('country', 'BR') == 'BR' else "Portugal"
        
        preview = f"""
RESUMO DO DOCUMENTO

Tipo: {doc_type_text}
País: {country_text}
Data: {datetime.now().strftime('%d/%m/%Y')}

CONTRATANTE:
{self.data.get('contractor_name', 'Não informado')}
{self.data.get('contractor_doc', 'Documento não informado')}

CONTRATADO:
{self.data.get('contracted_name', 'Não informado')}
{self.data.get('contracted_doc', 'Documento não informado')}

"""
        
        if self.document_type == "proposal":
            preview += f"""
SERVIÇO:
{self.data.get('service_title', 'Não informado')}

VALOR:
{self.data.get('total_value', 'Não informado')}

PRAZO:
{self.data.get('execution_deadline', 'Não informado')}
"""
        
        preview += "\nClique em 'Finalizar' para gerar o PDF do documento."
        
        return preview.strip()
