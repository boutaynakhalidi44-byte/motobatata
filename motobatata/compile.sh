#!/bin/bash
echo "Compilation du projet motobatata..."
echo

cd "$(dirname "$0")/motobatata"

if [ ! -d "bin" ]; then
    mkdir -p bin
fi

echo "Compilation des fichiers Java..."
javac -d bin -sourcepath src src/motobatata/**/*.java

if [ $? -eq 0 ]; then
    echo
    echo "Compilation réussie!"
    echo "Les fichiers compilés sont dans le dossier bin/"
else
    echo
    echo "Erreur lors de la compilation!"
    exit 1
fi

