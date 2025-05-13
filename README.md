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
   Parsuje projekt programistyczny (np. w języku Java) i wyodrębnia z niego strukturę oraz metadane (np. klasy, zależności, metody), zapisując je do bazy danych.

2. **Baza danych projektu**  
   Trwała struktura danych umożliwiająca wielokrotne przetwarzanie i analizowanie informacji z kodu źródłowego projektu. Typ bazy (relacyjna / grafowa / dokumentowa) zostanie dobrany zależnie od wymagań.

3. **Moduły rozszerzające (pluginy)**  
   Każdy plugin działa jako niezależny komponent operujący na danych z bazy – może wizualizować zależności między klasami, obliczać metryki, wykrywać problemy projektowe itp.

## Plan działania

### Zadania na tydzień 1 (21.05):

1. **Wybór finalnej nazwy projektu** (`SOVA IDE` – wstępna propozycja)
2. **Analiza i wybór typu bazy danych**  
   + przygotowanie propozycji schematu danych
3. **Koncepcja architektury pluginów oraz wstępny projekt interfejsu użytkownika**  
   + analiza dostępnych modeli rozszerzalności (np. systemy pluginów w IntelliJ / Eclipse / VSCode)

## Na później

- diagram blokowy architektury
- opis przykładowego pluginu
- lista używanych technologii/frameworków
- uzasadnienie projektu: dlaczego ten projekt jest potrzebny? (będzie częścią wstępu pracy magisterskiej)

---

> Dokument ten będzie aktualizowany wraz z postępem prac projektowych.
