#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para criar executável do Contrauto usando PyInstaller
"""

import os
import sys
import subprocess
import shutil
from pathlib import Path


def install_pyinstaller():
    """Instala o PyInstaller se não estiver instalado"""
    try:
        import PyInstaller
        print("✓ PyInstaller já está instalado")
    except ImportError:
        print("Instalando PyInstaller...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", "pyinstaller"])
        print("✓ PyInstaller instalado com sucesso")


def create_spec_file():
    """Cria arquivo de especificação customizado para o PyInstaller"""
    spec_content = """# -*- mode: python ; coding: utf-8 -*-

import os
from pathlib import Path

block_cipher = None

# Diretório base do projeto
BASE_DIR = Path(SPECPATH).parent

a = Analysis(
    ['main.py'],
    pathex=[str(BASE_DIR)],
    binaries=[],
    datas=[
        # Inclui os modelos de documentos
        ('models/*.txt', 'models'),
        # Inclui o README
        ('README.md', '.'),
    ],
    hiddenimports=[
        'customtkinter',
        'PIL',
        'PIL._tkinter_finder',
        'reportlab',
        'reportlab.graphics',
        'reportlab.lib',
        'reportlab.pdfbase',
        'reportlab.pdfbase._fontdata',
        'reportlab.pdfgen',
        'reportlab.platypus',
    ],
    hookspath=[],
    hooksconfig={},
    runtime_hooks=[],
    excludes=[
        'matplotlib',
        'numpy',
        'pandas',
        'scipy',
        'pytest',
        'black',
        'flake8',
    ],
    win_no_prefer_redirects=False,
    win_private_assemblies=False,
    cipher=block_cipher,
    noarchive=False,
)

pyz = PYZ(a.pure, a.zipped_data, cipher=block_cipher)

exe = EXE(
    pyz,
    a.scripts,
    a.binaries,
    a.zipfiles,
    a.datas,
    [],
    name='Contrauto',
    debug=False,
    bootloader_ignore_signals=False,
    strip=False,
    upx=True,
    upx_exclude=[],
    runtime_tmpdir=None,
    console=False,  # False para não mostrar console
    disable_windowed_traceback=False,
    target_arch=None,
    codesign_identity=None,
    entitlements_file=None,
    icon='icon.ico' if os.path.exists('icon.ico') else None,
    version='version_info.txt' if os.path.exists('version_info.txt') else None,
    uac_admin=False,
    uac_uiaccess=False,
)
"""
    
    with open("Contrauto.spec", "w", encoding="utf-8") as f:
        f.write(spec_content)
    
    print("✓ Arquivo de especificação criado: Contrauto.spec")


def create_version_info():
    """Cria arquivo de informações de versão para Windows"""
    version_content = """# UTF-8
#
# Para mais detalhes sobre este arquivo, veja:
# https://docs.microsoft.com/en-us/windows/win32/menurc/versioninfo-resource

VSVersionInfo(
  ffi=FixedFileInfo(
    # filevers e prodvers devem ser tuplas de 4 inteiros: (1, 2, 3, 4)
    filevers=(1, 0, 0, 0),
    prodvers=(1, 0, 0, 0),
    # Contém uma máscara de bits que especifica os atributos booleanos do arquivo.
    mask=0x3f,
    # Contém uma máscara de bits que especifica os atributos booleanos válidos do arquivo.
    flags=0x0,
    # O sistema operacional para o qual este arquivo foi projetado.
    # 0x4 - NT e versões posteriores do Windows
    OS=0x4,
    # O tipo geral de arquivo.
    # 0x1 - o arquivo é uma aplicação.
    fileType=0x1,
    # O subtipo de arquivo.
    # 0x0 - o arquivo não tem subtipo.
    subtype=0x0,
    # Data de criação e modificação.
    date=(0, 0)
  ),
  kids=[
    StringFileInfo(
      [
      StringTable(
        u'040904B0',
        [StringStruct(u'CompanyName', u'Contrauto'),
        StringStruct(u'FileDescription', u'Sistema de Geração de Propostas e Contratos'),
        StringStruct(u'FileVersion', u'1.0.0.0'),
        StringStruct(u'InternalName', u'Contrauto'),
        StringStruct(u'LegalCopyright', u'Copyright © 2024 Contrauto'),
        StringStruct(u'OriginalFilename', u'Contrauto.exe'),
        StringStruct(u'ProductName', u'Contrauto - Gerador de Documentos'),
        StringStruct(u'ProductVersion', u'1.0.0.0')])
      ]), 
    VarFileInfo([VarStruct(u'Translation', [1033, 1200])])
  ]
)
"""
    
    with open("version_info.txt", "w", encoding="utf-8") as f:
        f.write(version_content)
    
    print("✓ Arquivo de versão criado: version_info.txt")


def build_executable():
    """Compila o executável usando PyInstaller"""
    print("\nIniciando compilação do executável...")
    
    # Remove builds anteriores
    for folder in ['build', 'dist', '__pycache__']:
        if os.path.exists(folder):
            shutil.rmtree(folder)
            print(f"✓ Pasta {folder} removida")
    
    # Remove spec anterior se existir
    if os.path.exists("Contrauto.spec") and not os.path.exists("Contrauto_custom.spec"):
        os.remove("Contrauto.spec")
    
    # Cria o spec file customizado
    create_spec_file()
    
    # Cria informações de versão
    create_version_info()
    
    # Executa o PyInstaller
    print("\nExecutando PyInstaller...")
    try:
        subprocess.check_call([
            sys.executable, 
            "-m", 
            "PyInstaller",
            "Contrauto.spec",
            "--clean",
            "--noconfirm"
        ])
        print("\n✓ Compilação concluída com sucesso!")
    except subprocess.CalledProcessError as e:
        print(f"\n❌ Erro durante a compilação: {e}")
        return False
    
    return True


def create_icon():
    """Cria um ícone básico para o aplicativo (opcional)"""
    try:
        from PIL import Image, ImageDraw, ImageFont
        
        # Cria uma imagem 256x256 com fundo azul
        img = Image.new('RGB', (256, 256), color='#2c3e50')
        draw = ImageDraw.Draw(img)
        
        # Adiciona texto "P" no centro
        try:
            # Tenta usar uma fonte grande
            font = ImageFont.truetype("arial.ttf", 120)
        except:
            # Usa fonte padrão se não encontrar
            font = ImageFont.load_default()
        
        # Desenha o P centralizado
        draw.text((128, 128), "P", fill='white', font=font, anchor="mm")
        
        # Salva como ICO
        img.save('icon.ico', format='ICO', sizes=[(256, 256)])
        print("✓ Ícone criado: icon.ico")
        
    except ImportError:
        print("⚠ PIL não encontrado, continuando sem ícone personalizado")
    except Exception as e:
        print(f"⚠ Não foi possível criar ícone: {e}")


def post_build_cleanup():
    """Limpa arquivos temporários após a compilação"""
    print("\nLimpando arquivos temporários...")
    
    # Remove arquivos temporários
    temp_files = ['Contrauto.spec', 'version_info.txt']
    for file in temp_files:
        if os.path.exists(file):
            os.remove(file)
            print(f"✓ Removido: {file}")
    
    # Remove pasta build
    if os.path.exists('build'):
        shutil.rmtree('build')
        print("✓ Pasta build removida")


def main():
    """Função principal"""
    print("=== CRIADOR DE EXECUTÁVEL CONTRAUTO ===\n")
    
    # Verifica se está no diretório correto
    if not os.path.exists('main.py'):
        print("❌ Erro: Execute este script dentro da pasta 'propoza'")
        return
    
    # Instala PyInstaller
    install_pyinstaller()
    
    # Cria ícone (opcional)
    create_icon()
    
    # Compila o executável
    if build_executable():
        print("\n✅ SUCESSO!")
        print(f"Executável criado em: {os.path.abspath('dist/Contrauto.exe')}")
        print("\nO arquivo Contrauto.exe pode ser distribuído e executado")
        print("em qualquer computador Windows sem precisar instalar Python!")
        
        # Limpa arquivos temporários
        post_build_cleanup()
        
        # Cria pasta de distribuição final
        dist_folder = Path("Contrauto_Portable")
        if dist_folder.exists():
            shutil.rmtree(dist_folder)
        
        dist_folder.mkdir()
        
        # Copia o executável
        shutil.copy2("dist/Contrauto.exe", dist_folder / "Contrauto.exe")
        
        # Copia o README
        if os.path.exists("README.md"):
            shutil.copy2("README.md", dist_folder / "README.md")
        
        # Cria um arquivo de instruções
        with open(dist_folder / "INSTRUÇÕES.txt", "w", encoding="utf-8") as f:
            f.write("""CONTRAUTO - Sistema de Geração de Propostas e Contratos

COMO USAR:
1. Execute o arquivo Contrauto.exe
2. Escolha o tipo de documento (Proposta ou Contrato)
3. Siga o assistente passo a passo
4. Os documentos serão salvos na pasta "documentos_gerados"

REQUISITOS:
- Windows 7 ou superior
- Não precisa instalar Python!

PROBLEMAS COMUNS:
- Se o antivírus bloquear, adicione uma exceção
- Se aparecer "Windows protegeu seu PC", clique em "Mais informações" e depois "Executar mesmo assim"

Desenvolvido com ❤️ por Contrauto Team
""")
        
        print(f"\n📦 Pasta de distribuição criada: {dist_folder.absolute()}")
        print("   Você pode compactar esta pasta e distribuir!")
        
    else:
        print("\n❌ Falha na criação do executável")


if __name__ == "__main__":
    main()
