@echo off
echo ==============================================
echo       SORA ANGKLUNG - KIOSK STARTUP
echo ==============================================

:: 1. Memulai server Python (Backend) di background
echo [1/2] Menyalakan Backend Server...
start "Angklung Backend" cmd /c "python -m src.api"

:: Beri waktu sejenak agar server FastAPI benar-benar siap
timeout /t 5 /nobreak >nul

:: 2. Membuka Google Chrome dalam Kiosk Mode (Layar penuh, tanpa navbar)
echo [2/2] Membuka antarmuka PWA dalam Mode Kiosk...
:: Asumsi Chrome terinstal di direktori default
start chrome --user-data-dir="%temp%\angklung_chrome_profile" --start-fullscreen --app="http://localhost:8000" --disable-pinch --overscroll-history-navigation=0

echo Selesai!
exit
