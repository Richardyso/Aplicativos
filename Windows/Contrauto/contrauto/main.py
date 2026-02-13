#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Contrauto - Sistema Local para Geração de Propostas e Contratos Comerciais
Arquivo principal da aplicação
"""

import sys
import os
from pathlib import Path

# Adiciona o diretório raiz ao path para importações
sys.path.insert(0, str(Path(__file__).parent))

import customtkinter as ctk
from ui.main_window import MainWindow


class ContrautoApp:
    """Classe principal da aplicação Contrauto"""
    
    def __init__(self):
        """Inicializa a aplicação"""
        # Configurações do CustomTkinter
        ctk.set_appearance_mode("light")  # Modes: "System" (standard), "Dark", "Light"
        ctk.set_default_color_theme("blue")  # Themes: "blue" (standard), "green", "dark-blue"
        
        # Cria a janela principal
        self.root = ctk.CTk()
        self.root.title("Contrauto - Gerador de Propostas e Contratos")
        
        # Define tamanho da janela (metade do tamanho anterior)
        window_width = 900
        window_height = 650
        
        # Obtém dimensões da tela
        screen_width = self.root.winfo_screenwidth()
        screen_height = self.root.winfo_screenheight()
        
        # Calcula posição centralizada
        x = (screen_width - window_width) // 2
        y = (screen_height - window_height) // 2
        
        # Define geometria com tamanho e posição
        self.root.geometry(f"{window_width}x{window_height}+{x}+{y}")
        
        # Define tamanho mínimo
        self.root.minsize(800, 600)
        
        # Cria a interface principal
        self.main_window = MainWindow(self.root)
        
    def run(self):
        """Executa a aplicação"""
        print("Iniciando Contrauto...")
        self.root.mainloop()


def main():
    """Função principal"""
    try:
        app = ContrautoApp()
        app.run()
    except Exception as e:
        print(f"Erro ao iniciar a aplicação: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
