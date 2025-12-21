@echo off
echo Lancement du simulateur motobatata...
echo.

cd /d "%~dp0"

REM Vérifier que les fichiers sont compilés
if not exist "bin\motobatata" (
    echo Erreur: Les fichiers ne sont pas compiles!
    echo Veuillez d'abord executer compile.bat
    pause
    exit /b 1
)

REM Lancer l'application
echo Demarrage de SimulatorFrame...
java -cp bin motobatata.gui.SimulatorFrame

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Erreur lors du lancement de l'application!
    pause
    exit /b 1
)
