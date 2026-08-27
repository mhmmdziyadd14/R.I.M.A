@echo off

:: Bikin Shortcut Desktop Otomatis Jika Belum Ada
set "SHORTCUT_PATH=%USERPROFILE%\Desktop\R.I.M.A.lnk"
if not exist "%SHORTCUT_PATH%" (
    echo Mendaftarkan R.I.M.A ke Desktop...
    echo Set oWS = WScript.CreateObject^("WScript.Shell"^) > "%temp%\CreateShortcut.vbs"
    echo sLinkFile = "%SHORTCUT_PATH%" >> "%temp%\CreateShortcut.vbs"
    echo Set oLink = oWS.CreateShortcut^(sLinkFile^) >> "%temp%\CreateShortcut.vbs"
    echo oLink.TargetPath = "%~dp0start_kiosk.bat" >> "%temp%\CreateShortcut.vbs"
    echo oLink.WorkingDirectory = "%~dp0" >> "%temp%\CreateShortcut.vbs"
    echo oLink.IconLocation = "%~dp0rima_icon.ico, 0" >> "%temp%\CreateShortcut.vbs"
    echo oLink.Save >> "%temp%\CreateShortcut.vbs"
    
    cscript /nologo "%temp%\CreateShortcut.vbs"
    del "%temp%\CreateShortcut.vbs"
)

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
start chrome --user-data-dir="%temp%\angklung_chrome_profile" --kiosk "http://localhost:8000" --disable-pinch --overscroll-history-navigation=0

echo Selesai!
exit
    