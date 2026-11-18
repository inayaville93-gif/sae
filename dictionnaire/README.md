# jdict : gestionnaire de dictionnaire (module L1 bis)

Auteur : Inaya Hamdaoui

Extrait les définitions d'un dump Wiktionary (français ou anglais), les range dans un
fichier de définitions (JSONL) accompagné d'un index trié, et permet de les rechercher
rapidement par dichotomie.

## Installation (Ubuntu / WSL)

```
sudo apt install -y openjdk-21-jdk maven
```

## Compilation

```
mvn package                      # compile, lance les tests, crée target/jdict.jar (fat jar)
export CLASSPATH=$PWD/target/jdict.jar
```

## Utilisation

```
# 1. créer definitions.dict + definitions.index à partir du dump
cat frwiktionary-latest-pages-articles.xml.bz2 | java fr.univ_eiffel.DictionaryMaker fr definitions
#    version compressée (zlib)
cat frwiktionary-latest-pages-articles.xml.bz2 | java fr.univ_eiffel.DictionaryMaker fr compressed:definitions

# 2. rechercher
java fr.univ_eiffel.jdict.DictionarySearcher definitions congres        # mot exact
java fr.univ_eiffel.jdict.DictionarySearcher definitions CONGRES        # forme normalisée
java fr.univ_eiffel.jdict.DictionarySearcher definitions yaml:CONGRES   # sortie YAML

# 3. formes normalisées (pour le moteur C : dictionnary_build words.txt words.lex)
java fr.univ_eiffel.jdict.NormalizedExtractor definitions > words.txt
java fr.univ_eiffel.jdict.NormalizedExtractor definitions "nom propre,adjectif" > words.txt

# 4. table de fréquences des lettres (pour le moteur C : grid_build chartable.txt 4 4)
java fr.univ_eiffel.jdict.CharTableMaker definitions > chartable.txt

# 5. mode interactif
java fr.univ_eiffel.jdict.InteractiveUI definitions
```

Le dossier `exemples/` contient de petits extraits de dumps (français et anglais) pour tester :

```
cat exemples/frwiktionary-extrait.xml.bz2 | java fr.univ_eiffel.DictionaryMaker fr test
java fr.univ_eiffel.jdict.DictionarySearcher test CONGRES
```

Les dumps complets se trouvent sur https://dumps.wikimedia.org/frwiktionary/ et
https://dumps.wikimedia.org/enwiktionary/ (fichier `XXwiktionary-latest-pages-articles.xml.bz2`).

## Formats de fichiers

**definitions.dict** : une ligne JSON d'en-tête
(`{"description":"definition file","created_on":"...","language":"fr"}`, plus
`"compression":"zlib"` si les entrées sont compressées), puis une entrée par mot :
une ligne JSON `{"title":...,"definitions":{"nom":[...],...}}`, ou le même texte compressé
avec zlib (Deflater) si la compression est activée.

**definitions.index** : `DICTINDX` (8 octets ASCII), puis pour chaque mot, dans l'ordre trié,
la position du premier octet et du dernier octet de son entrée dans le .dict
(2 entiers de 4 octets en grand-boutiste). Le saut de ligne qui suit une entrée non compressée
n'est pas compris dans l'intervalle.

Ordre de tri : forme normalisée (majuscules, sans diacritiques), puis ordre naturel des String
en cas d'égalité (`Congrès` < `congres` < `congrès`).

## Architecture

| Paquetage | Rôle |
|---|---|
| `fr.univ_eiffel.jdict` | programmes (`DictionaryMaker`, `DictionarySearcher`, `NormalizedExtractor`, `CharTableMaker`, `InteractiveUI`), `Entry`, JSON |
| `...jdict.wiki` | lecture du dump avec StAX (`DumpReader`), extraction des définitions (`DefinitionExtractor`) |
| `...jdict.lang` | ce qui dépend de la langue du Wiktionary : `Language`, `FrenchLanguage`, `EnglishLanguage` |
| `...jdict.storage` | écriture (`DictionaryWriter`) et lecture (`Dictionary`, avec `RandomAccessFile` et recherche par dichotomie) ; compression (`Compression`, `NoCompression`, `ZlibCompression`) |
| `...jdict.format` | formats d'affichage (`EntryFormatter`, `JsonFormatter`, `YamlFormatter`) |
| `...jdict.text` | normalisation et ordre des mots (`WordNormalizer`), comptage des lettres (`LetterCounter`) |

### Extensibilité

- **Nouvelle langue** : écrire une classe qui implémente `Language` (comment reconnaître la
  section de la langue et les sections de types de mots) et l'ajouter dans `Languages`.
- **Nouvel algorithme de compression** : implémenter `Compression` et l'ajouter dans
  `Compressions`. Son nom est écrit dans l'en-tête du .dict, donc la lecture choisit
  automatiquement le bon décompresseur.
- **Nouveau format d'affichage** : implémenter `EntryFormatter` et l'ajouter dans `Formatters`
  (utilisable aussitôt avec le préfixe `nom:mot`).

## Bibliothèques

- Apache Commons Compress (décompression bzip2 du dump)
- Jackson (JSON, compatible avec les records)
- JUnit 5 (tests)
