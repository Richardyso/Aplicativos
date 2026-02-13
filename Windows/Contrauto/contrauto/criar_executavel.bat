@echo off
chcp 65001 > nul
echo.
echo ====================================
echo   CONTRAUTO - Criador de Executável
echo ====================================
echo.

REM Verifica se o Python está instalado
python --version > nul 2>&1
if errorlevel 1 (
    echo [ERRO] Python não encontrado!
    echo Por favor, instale o Python antes de continuar.
    echo.
    pause
    exit /b 1
)

echo [OK] Python encontrado
echo.

REM Instala as dependências se necessário
echo Instalando dependências necessárias...
pip install -r requirements.txt > nul 2>&1
pip install pyinstaller pillow > nul 2>&1

echo [OK] Dependências instaladas
echo.

REM Executa o script de build
echo Criando executável...
echo Isso pode levar alguns minutos...
echo.

python build_exe.py

echo.
echo Processo concluído!
echo.
pause
