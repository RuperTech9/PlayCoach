# PlayCoach ⚽📱

Una aplicación Android desarrollada en Kotlin con Jetpack Compose, orientada a la gestión deportiva de equipos de fútbol base. Diseñada con una arquitectura MVVM moderna y persistencia con Room.

---

## 🚀 Tecnologías y Arquitectura

- **Kotlin + Jetpack Compose**
- **Arquitectura en capas MVVM (Room → Repository → ViewModel → UI Compose)**
- **StateFlow y viewModelScope para estado reactivo**
- **Navegación con Navigation Compose + rutas parametrizadas**
- **Inyección de dependencias con Hilt**
- **Material Design 3**
- **Persistencia con Room y DAOs personalizados**

---

## 🧠 Funcionalidades principales

- Gestión de jugadores, entrenadores y equipos
- Creación y edición de jornadas
- Registro de asistencia y convocatorias
- Estadísticas de jugadores y equipos
- Vista calendario con eventos y partidos
- Editor de formaciones tácticas
- Detalles completos por jornada y jugador

---

## 🎥 Capturas de pantalla

| Portada                                  | Selección de equipo                             | Calendario                            |
|------------------------------------------|-------------------------------------------------|---------------------------------------|
| ![portada](images/01_splash.png)         | ![seleccion](images/02_select_team.png)         | ![calendario](images/03_calendar.png) |

| Estadísticas Equipo                      | Estadísticas Jugador                            | Jornadas                              |
|------------------------------------------|-------------------------------------------------|---------------------------------------|
| ![estadisticas](images/09_teamStats.png) | ![detalles jugador](images/11_playerDetail.png) | ![partidos](images/08_matchday.png)   |

| Eventos                               | Asistencia                              | Pizarra táctica                        |
|---------------------------------------|-----------------------------------------|----------------------------------------|
| ![portada](images/04_eventDetail.png) | ![asistencia](images/05_absence_ok.png) | ![formacion](images/12_formations.png) |
---

## 🏁 Cómo empezar

```bash
git clone https://github.com/TuUsuario/PlayCoach.git
---
