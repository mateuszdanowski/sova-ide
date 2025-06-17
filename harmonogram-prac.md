# Harmonogram prac – SOVA IDE (14 tygodni)

## PoC backendu SOVA IDE (2 tygodnie)  
**11 – 25 czerwca**

- ✅ Działająca baza danych Neo4j z prostym schematem
  + ✅ Szkic API jako fasada Neo4j (zabezpieczenie przed zmianą technologii bazy danych)
  + ✅ Abstrakcje na Neo4j – krawędź i wierzchołek
- Szkic API dla pluginów (extension points + dostęp do bazy)
  + Specyfikacja minimalnego kontraktu pluginu
- System ładujący pluginy (pliki `.jar`)
  + ✅ Rejestracja pustego pluginu
- Plugin parsera JAR (zapis do bazy)
  - ✅ Logika parsowania
  - ✅ Logika zapisywania w bazie
- Output plugin – liczenie klas (output w konsoli)
- ✅ Dodatkowo: Przemyślenie sposobu udostępniania API głównej aplikacji dla pluginów (np. przez GitHub Packages jako alternatywa dla Maven Central)

### Pisanie (1 tydzień)  
**25 czerwca – 2 lipca**

- Szkic struktury pracy magisterskiej (LaTeX)
- Opis architektury backendu + diagramy
- Schemat danych w bazie + uzasadnienie wyboru grafowej bazy danych
- Zbieranie literatury (BiBTeX)

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
