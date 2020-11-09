# Execution

## avec ant 

Le projet utilise java 8.

Pour lancer le projet, on ce place à la racine de celui-ci puis on build en ligne de commande avec ant.

```
ant build
```

Le dossier ./bin avec les binaires est créer, on peut ensuite executer le projet avec les commandes suivantes : 

```
cd bin
java Main "S(a|g|r)+on" "../tests/babylone.txt"
```

Le premier argument est l'expression régulière à chercher, le deuxieme est le chemin du fichier de test.

## avec le jar

Depuis la racine du projet on lancera la commande suivante : 

```
java -jar bello_gomez.jar "S(a|g|r)+on" "./tests/babylone.txt
```

Le premier argument est l'expression régulière à chercher, le deuxieme est le chemin du fichier de test.