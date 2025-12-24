@echo off
echo Compilation du projet motorola...
echo.

cd /d "%~dp0"

if not exist "bin" mkdir bin

echo Nettoyage des anciens fichiers compiles...
if exist "bin\motorola" rmdir /s /q "bin\motorola"

echo.
echo Compilation de tous les fichiers Java...
echo.

setlocal enabledelayedexpansion
set "javafiles="

REM Créer la liste de tous les fichiers .java
for /r src %%f in (*.java) do (
    set "javafiles=!javafiles! "%%f""
)

REM Compiler tous les fichiers en une seule commande
javac -d bin -sourcepath src -encoding UTF-8 !javafiles!

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Copie des ressources...
    if exist "resources" (
        if not exist "bin\resources" mkdir bin\resources
        xcopy /S /Y "resources\*.*" "bin\resources\"
        echo ✓ Ressources copiees
    )
    
    echo.
    echo ========================================
    echo Compilation reussie!
    echo Les fichiers compiles sont dans: bin\motorola\
    echo ========================================
    pause
) else (
    echo.
    echo ========================================
    echo Erreur lors de la compilation!
    echo ========================================
    pause
    exit /b 1
)
