# Harmonogram prac – SOVA IDE (14 tygodni)

## PoC backendu SOVA IDE (2 tygodnie)  
**11 – 25 czerwca**

- ✅ Działająca baza danych Neo4j z prostym schematem
  + ✅ Szkic API jako fasada Neo4j (zabezpieczenie przed zmianą technologii bazy danych)
  + ✅ Abstrakcje na Neo4j – krawędź i wierzchołek
  + ⚠️ rozszerzenie schematu z relacjami między wierzchołkami
  + ✅ Cypher - jak podstawowe rzeczy wyciągać (relacje itd)
- Szkic API dla pluginów
  + [na później] Specyfikacja minimalnego kontraktu pluginu
  + [na później] Extension points
  + ✅ Dostęp do bazy
- ✅ System ładujący pluginy (pliki `.jar`)
  + ✅ Rejestracja pustego pluginu
  + ✅ Rejestracja pluginów do pamięci i odpalenie ich po parserze
  + ✅ Implementacja output plugin'a – liczenie liczby plików w projekcie (output w konsoli póki co)
- Plugin parsera JAR (zapis do bazy)
  + ✅ Logika parsowania
  + ✅ Logika zapisywania w bazie
  + [na później] Implementacja parsera jako plugin
- Przemyślenie sposobu udostępniania API głównej aplikacji dla pluginów (np. przez GitHub Packages jako alternatywa dla Maven Central)
  + ✅ Budowanie plugin-api do maven local
  + [na później] Budowanie plugin-api do GitHub Packages

### Pisanie (1 tydzień)  
**25 czerwca – 2 lipca**

- [ready] Szkic struktury pracy magisterskiej (LaTeX)
- [ready] Opis architektury backendu + diagramy
- [ready] Schemat danych w bazie + uzasadnienie wyboru grafowej bazy danych
  + będzie wymienne np. na SQLa
- [w trakcie pisania, wedle potrzeby] Zbieranie literatury (BiBTeX)

---

## PoC frontendu SOVA IDE (2 tygodnie)  
**2 – 16 lipca**

- Szkielet frontendu
- Wrzucanie pliku `.jar` przez GUI
- Widok listy zainstalowanych pluginów
- Możliwość ręcznego odpalenia pluginu z GUI
- Mock output pluginu – liczba klas
  + Komponent React dla outputu wyświetlany w GUI
- Dynamiczne renderowanie komponentu React dostarczonego przez plugin

### Pisanie (1 tydzień)  
**16 – 23 lipca**

- Integracja frontend + backend
- Opis architektury GUI + diagramy
- Koncepcja osadzania komponentów pluginowych

---

## Rozwijanie backendu do MVP (2 tygodnie)  
**23 lipca – 6 sierpnia**

- Drugi parser plugin – np. projekt z GitHuba w Javie lub plik `.log`
- Drugi output plugin – np. wizualizacja relacji importów między pakietami

### Pisanie (1 tydzień)  
**6 – 13 sierpnia**

- Opis rozszerzalności
- Instrukcja tworzenia i dodawania pluginów (jako dodatek w pracy magisterskiej)

---

## Rozwijanie frontendu do MVP (1 tydzień)  
**13 – 20 sierpnia**

- Wsparcie wielu projektów
- Widok listy pluginów per projekt

### Pisanie

- Opis flow użytkownika

---

## Dopracowanie i testy (1 tydzień)  
**20 – 27 sierpnia**

- Ogólne poprawki
- Przykładowe testy jednostkowe i integracyjne

### Pisanie (1 tydzień)  
**27 sierpnia – 3 września**

- Testowalność architektury pluginowej
- Rozdział ewaluacyjny – potwierdzenie działania koncepcji
- Porównanie z innymi IDE
- Pozostałe elementy pracy

---

## Bufor i końcowe poprawki (1,5–2 tygodnie)  
**3 – 15 września**

- Poprawki końcowe
- Ewentualne usprawnienia GUI / backendu

### Pisanie

- Rozdział podsumowujący
- Wnioski, trudności, dalsze kierunki rozwoju
