# Fichiers 

- Le dossier *src* contient les classes java :
    - Algorithms : contient les algorithmes de construction d'automates.
    - Automaton : classe représentant un automate.
    - PatterSearch : permet de lancer une recherche de motif sur un fichier texte.
    - RegEx : classe fournie permettant de construire un AST. 
    - Main : lancement du projet.
    - Tests : lancement des tests.
- Le dossier *tests* contient le fichier sur lequel les tests sont fait.
- Le répertoire racine contient le sujet du projet, le rapport, le README et le fichier config.plot servant pour les tests. 

# Execution

## avec ant 

Le projet utilise java 8.

Pour lancer le projet, on ce place à la racine de celui-ci puis on build en ligne de commande avec ant.

```
$ ant build
```

Le dossier ./bin avec les binaires est créer, on peut ensuite executer le projet avec les commandes suivantes : 

```
$ cd bin
$ java Main "S(a|g|r)+on" "../tests/babylone.txt"
```

Le premier argument est l'expression régulière à chercher, le deuxieme est le chemin du fichier de test.

# Tests 

## avec ant 

On ce place à la racine du projet et on le build : 

```
$ ant build
```
Le dossier ./bin avec les binaires est créer, on peut ensuite executer les tests avec les commandes suivantes : 

```
$ cd bin 
$ java Tests
```

le fichier results.dat est généré à la racine du projet. Pour générer le .pdf contenant l'affichage des tests on se replace à la 
racine puis lance la commande gnuplot. 

```
$ cd ..
$ gnuplot config.plot
```

Le fichier performances.pdf est généré. 