# sova-ide
Software Optimization, Visualization and Analysis IDE

## Cel projektu

Celem projektu jest stworzenie rozszerzalnego środowiska do analizy projektów programistycznych.
SOVA IDE umożliwia:
- przetwarzanie kodu źródłowego,
- trwałe przechowywanie zebranych danych,
- uruchamianie zewnętrznych modułów (pluginów) odpowiedzialnych za analizę, wyliczenia lub wizualizacje.

Docelowo SOVA ma służyć jako fundament dla kolejnych rozszerzeń tworzonych przez innych magistrantów.

## Główne komponenty

1. **Moduł ekstrakcji kodu źródłowego**  
   Parsuje projekt programistyczny (np. w języku Java) i wyodrębnia z niego strukturę oraz metadane (np. klasy, zależności, metody), zapisując je do bazy danych. Projekt przewiduje możliwość parsowania nie tylko całych projektów, ale też np. logów aplikacji.

2. **Baza danych projektu**  
   Trwała struktura danych umożliwiająca wielokrotne przetwarzanie i analizowanie informacji z kodu źródłowego projektu. Typ bazy - grafowa - został wybrany jako najbardziej odpowiedni do wymagań `SOVA IDE`.

3. **Moduły rozszerzające (pluginy)**  
   Każdy plugin działa jako niezależny komponent operujący na danych z bazy – może wizualizować zależności między klasami, obliczać metryki, wykrywać problemy projektowe itp.

## Plan działania

### Zadania na tydzień 1 (21.05):

1. **Wybór finalnej nazwy projektu** (`SOVA IDE` – wstępna propozycja)
2. **Analiza i wybór typu bazy danych**  
   + przygotowanie propozycji schematu danych
3. **Koncepcja architektury pluginów oraz wstępny projekt interfejsu użytkownika** (przeniesione na 28.05, później na 4.06)
   + analiza dostępnych modeli rozszerzalności (np. systemy pluginów w IntelliJ / Eclipse / VSCode)

#### Realizacja zadań
1. `SOVA IDE` nazwą tworzonego narzędzia
2. Wybrana grafowa baza danych (wstępna propozycja - Neo4j)
   + przygotowany wstępny schemat danych
3. Wstępny projekt interfejsu użytkownika na obrazku poniżej
   + [przeniesione na kolejne spotkanie] analiza modeli rozszerzalności

### Zadania na tydzień 2 & 3 (28.05 & 4.06):
1. Analiza modeli rozszerzalności
2. Wybór architektury pluginowej
3. Decyzja architektoniczna w interakcji plugin <-> baza danych
4. Decyzja - Czy pluginy mogą być tworzone w dowolnym języku programowania?

### Zadania na tydzień 4 (10.06):
1. Decyzja, czy narzędzie `SOVA IDE` ma być:
   + osobnym narzędziem, niezależnym, tworzonym od zera
   + pluginem jednego ze znanych IDE (IntelliJ, VS Code)
2. Harmonogram prac - z terminem złożenia pracy 15 września 2025r.
3. Decyzja architektoniczna co do zapisywania danych przez pluginy
4. Analiza dwóch dodatkowych modeli rozszerzalności
   + Jenkins
   + VS Code

---

# Schemat Bazy Danych 
## Typy wierzchołków

- **Project**
- (?) **Module**
- **File**
- **Entity**
- **Member**

---

### Project
- `name`: `String`
- `language`: `String` / `Enum` (e.g., Java, Python, JS, etc.)
- (?) Multi-language support

### File
- `kind`: `Enum` (`SourceFile`, `TestFile`, `Resource`, etc.)
- `path`: `String`
- (?) `content`: `String`

### Entity
- `kind`: `Enum` (`Class`, `Interface`, `Record`, `Enum`, etc.)
- `content`: `String`

### Member
- `kind`: `Enum` (`Field`, `Method`, etc.)
- `content`: `String`

---

## Typy krawędzi

- **has**
  - `Project` has `SourceFile`
  - `SourceFile` has `Class`
  - `Class` has `Field`
  - `Class` has `Method`

- **uses**
  - `Class` uses `Resource`
  - `Method` uses `Field`

- **implements**
  - `Class` implements `Interface`

- **extends**
  - `Class` extends `Class`

- **calls**
  - `Method` calls `Method`

- **is of type**
  - `Field` is of type `Class` / `Interface` / `Record` / `Enum`
  - `Method` is of type `Class` / `Interface` / `Record` / `Enum`

---

# Designy
## Baza Danych
##### Relacja HAS
![db-schema-has-relation](https://github.com/user-attachments/assets/bcda2a24-5d69-491f-815c-9618ef40b48a)
##### Inne relacje
![db-schema-other-relations](https://github.com/user-attachments/assets/a7fa9c44-5513-4b14-ae01-9ae90cff7e4a)

## IDE Overview
##### Flow przykładowego działania IDE
![sova-ide-flow](https://github.com/user-attachments/assets/41ca5331-0d8a-4be9-8ec6-1d264a83657e)
##### Koncepcja architektury
![sova-ide-overview](https://github.com/user-attachments/assets/a70537d7-08f5-42e8-9272-faaa94d3c0fb)


# Na później

- diagram blokowy architektury
- opis przykładowego pluginu
- lista używanych technologii/frameworków
- uzasadnienie projektu: dlaczego ten projekt jest potrzebny? (będzie częścią wstępu pracy magisterskiej)

---

> Dokument ten będzie aktualizowany wraz z postępem prac projektowych.
