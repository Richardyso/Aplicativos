# -*- coding: utf-8 -*-
"""
Janela principal da aplicação Contrauto
Gerencia a navegação e exibição dos diferentes módulos
"""

import customtkinter as ctk
from typing import Optional
from .wizard import DocumentWizard


class MainWindow:
    """Classe responsável pela janela principal da aplicação"""
    
    def __init__(self, root: ctk.CTk):
        """
        Inicializa a janela principal
        
        Args:
            root: Janela raiz do CustomTkinter
        """
        self.root = root
        self.current_frame: Optional[ctk.CTkFrame] = None
        
        # Configuração inicial
        self._setup_ui()
        
        # Mostra a tela inicial
        self._show_home_screen()
        
    def _setup_ui(self):
        """Configura a interface da janela principal"""
        # Frame principal que conterá todo o conteúdo
        self.main_container = ctk.CTkFrame(self.root)
        self.main_container.pack(fill="both", expand=True, padx=0, pady=0)
        
        # Barra superior
        self._create_top_bar()
        
        # Container para o conteúdo
        self.content_frame = ctk.CTkFrame(self.main_container)
        self.content_frame.pack(fill="both", expand=True, padx=0, pady=0)
        
    def _create_top_bar(self):
        """Cria a barra superior com logo e navegação"""
        top_bar = ctk.CTkFrame(self.main_container, height=60)
        top_bar.pack(fill="x", padx=0, pady=0)
        top_bar.pack_propagate(False)
        
        # Logo/Título
        logo_label = ctk.CTkLabel(
            top_bar,
            text="CONTRAUTO",
            font=ctk.CTkFont(size=24, weight="bold")
        )
        logo_label.pack(side="left", padx=20, pady=10)
        
        # Subtítulo
        subtitle = ctk.CTkLabel(
            top_bar,
            text="Sistema de Geração de Propostas e Contratos",
            font=ctk.CTkFont(size=12)
        )
        subtitle.pack(side="left", padx=10, pady=10)
        
        # Botão Home (lado direito)
        home_btn = ctk.CTkButton(
            top_bar,
            text="🏠 Início",
            width=100,
            command=self._show_home_screen
        )
        home_btn.pack(side="right", padx=20, pady=10)
        
    def _clear_content(self):
        """Limpa o conteúdo atual do frame principal"""
        if self.current_frame:
            self.current_frame.destroy()
            self.current_frame = None
            
    def _show_home_screen(self):
        """Exibe a tela inicial com opções"""
        self._clear_content()
        
        # Frame da tela inicial
        home_frame = ctk.CTkFrame(self.content_frame)
        home_frame.pack(fill="both", expand=True, padx=20, pady=20)
        self.current_frame = home_frame
        
        # Título de boas-vindas
        welcome_label = ctk.CTkLabel(
            home_frame,
            text="Bem-vindo ao Contrauto",
            font=ctk.CTkFont(size=32, weight="bold")
        )
        welcome_label.pack(pady=(50, 20))
        
        # Descrição
        desc_label = ctk.CTkLabel(
            home_frame,
            text="Escolha o tipo de documento que deseja criar:",
            font=ctk.CTkFont(size=16)
        )
        desc_label.pack(pady=(0, 40))
        
        # Container para os botões
        buttons_frame = ctk.CTkFrame(home_frame, fg_color="transparent")
        buttons_frame.pack(pady=20)
        
        # Botão para criar proposta
        proposal_btn = ctk.CTkButton(
            buttons_frame,
            text="📄 Nova Proposta Comercial",
            width=300,
            height=80,
            font=ctk.CTkFont(size=16),
            command=lambda: self._start_wizard("proposal")
        )
        proposal_btn.pack(pady=10)
        
        # Botão para criar contrato
        contract_btn = ctk.CTkButton(
            buttons_frame,
            text="📋 Novo Contrato",
            width=300,
            height=80,
            font=ctk.CTkFont(size=16),
            command=lambda: self._start_wizard("contract")
        )
        contract_btn.pack(pady=10)
        
        # Botão para modelos salvos (futuro)
        templates_btn = ctk.CTkButton(
            buttons_frame,
            text="📁 Modelos Salvos",
            width=300,
            height=80,
            font=ctk.CTkFont(size=16),
            state="disabled",  # Desabilitado por enquanto
            command=self._show_templates
        )
        templates_btn.pack(pady=10)
        
        # Rodapé com informações
        footer_frame = ctk.CTkFrame(home_frame, fg_color="transparent")
        footer_frame.pack(side="bottom", pady=20)
        
        footer_label = ctk.CTkLabel(
            footer_frame,
            text="💡 Dica: O Contrauto funciona 100% offline e seus dados ficam seguros em seu computador",
            font=ctk.CTkFont(size=12),
            text_color="gray"
        )
        footer_label.pack()
        
    def _start_wizard(self, document_type: str):
        """
        Inicia o wizard para criação de documento
        
        Args:
            document_type: Tipo de documento ('proposal' ou 'contract')
        """
        self._clear_content()
        
        # Cria o wizard
        wizard_frame = ctk.CTkFrame(self.content_frame)
        wizard_frame.pack(fill="both", expand=True)
        self.current_frame = wizard_frame
        
        # Inicializa o wizard
        self.wizard = DocumentWizard(wizard_frame, document_type, self._on_wizard_complete)
        
    def _on_wizard_complete(self, document_data: dict):
        """
        Callback chamado quando o wizard é concluído
        
        Args:
            document_data: Dados do documento gerado
        """
        print(f"Documento gerado: {document_data}")
        # Aqui podemos mostrar uma tela de sucesso ou voltar ao início
        self._show_home_screen()
        
    def _show_templates(self):
        """Mostra a tela de modelos salvos (implementação futura)"""
        pass
