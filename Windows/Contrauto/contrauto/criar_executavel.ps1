# Script PowerShell para criar executável do Contrauto

Write-Host "`n====================================`n" -ForegroundColor Cyan
Write-Host "   CONTRAUTO - Criador de Executável" -ForegroundColor Cyan
Write-Host "`n====================================`n" -ForegroundColor Cyan

# Verifica se o Python está instalado
try {
    $pythonVersion = python --version 2>&1
    Write-Host "[OK] Python encontrado: $pythonVersion" -ForegroundColor Green
} catch {
    Write-Host "[ERRO] Python não encontrado!" -ForegroundColor Red
    Write-Host "Por favor, instale o Python antes de continuar." -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "`nInstalando dependências necessárias..." -ForegroundColor Yellow

# Instala as dependências
pip install -r requirements.txt --quiet
pip install pyinstaller pillow --quiet

Write-Host "[OK] Dependências instaladas" -ForegroundColor Green
Write-Host "`nCriando executável..." -ForegroundColor Yellow
Write-Host "Isso pode levar alguns minutos..." -ForegroundColor Gray

# Executa o script de build
python build_exe.py

Write-Host "`nProcesso concluído!" -ForegroundColor Green
Read-Host "`nPressione Enter para sair"
