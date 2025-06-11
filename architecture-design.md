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

