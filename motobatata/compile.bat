@echo off
echo Compilation du projet motobatata...
echo.

cd /d "%~dp0"

if not exist "bin" mkdir bin

echo Nettoyage des anciens fichiers compiles...
if exist "bin\motobatata" rmdir /s /q "bin\motobatata"

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
    echo ========================================
    echo Compilation reussie!
    echo Les fichiers compiles sont dans: bin\motobatata\
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
