# Arquitectura

AmiminAlarm sigue una arquitectura **MVVM** (Model–View–ViewModel) con una clara
separación por capas, inspirada en los principios de *Clean Architecture*.

---

## Visión general

```
┌─────────────────────────────────────────────────────────────┐
│                        UI  (Jetpack Compose)                 │
│                                                              │
│   Screens ──► ViewModels ──► StateFlow ──► Compose UI        │
│                                                              │
│   Theme · Animations · Components                            │
└───────────────────────────┬─────────────────────────────────┘
                            │  observa Flow
┌───────────────────────────▼─────────────────────────────────┐
│                        Repositories                          │
│                                                              │
│   AlarmRepository · CalendarRepository · SettingsRepository  │
│   BackupManager                                              │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                          Data                                │
│                                                              │
│   Room  ──────────────►  Alarms / CalendarEvents             │
│   DataStore  ─────────►  Ajustes (Preferences)               │
│                                                              │
│   AlarmScheduler · Receivers · AlarmService                  │
└─────────────────────────────────────────────────────────────┘
```

---

## Capas

### 1. UI (`ui/`)

- **Screens**: composables sin lógica de negocio. Reciben estado y emiten eventos.
- **ViewModels**: exponen `StateFlow`, ejecutan casos de uso y sobreviven a la
  rotación.
- **Theme**: sistema de color, tipografía y formas; se genera dinámicamente.
- **Animations**: partículas, transiciones y live wallpapers.
- **Components**: elementos reutilizables (selector de stickers, sonidos).

**Regla**: la UI **nunca** accede directamente a la base de datos.

### 2. Repositorios (`data/repository/`)

Única fuente de verdad para la UI. Abstraen el origen de datos:

| Repositorio | Responsabilidad |
|-------------|-----------------|
| `AlarmRepository` | CRUD de alarmas |
| `CalendarRepository` | CRUD de eventos |
| `SettingsRepository` | Ajustes en DataStore |
| `BackupManager` | Exportar / importar copia |

### 3. Datos (`data/`)

- **Room** para datos estructurados (alarmas, eventos) con migraciones.
- **DataStore** para preferencias clave–valor.
- **AlarmScheduler** centraliza `AlarmManager`.
- **Receivers** y **AlarmService** gestionan la ejecución en segundo plano.

---

## Flujo de datos (ejemplo: crear alarma)

```
Usuario toca "Poner Alarma"
        │
        ▼
AlarmScreen ──► AlarmViewModel.addAlarm(...)
                        │
                        ▼
                AlarmRepository.insertAlarm()   ──►  Room
                        │
                        ▼
                AlarmScheduler.schedule()       ──►  AlarmManager
                        │                              │
                        │                        (recordatorios 1h/30m/10m)
                        ▼
                StateFlow<List<Alarm>>  ──►  UI se recompone
```

Cuando llega la hora:

```
AlarmManager ──► AlarmReceiver ──► AlarmService (foreground)
                                        │
                                        ├─► reproduce sonido + vibra
                                        └─► notificación full-screen
                                                  │
                                                  ▼
                                        AlarmRingingActivity
                                        (Dismiss / Snooze)
```

---

## Decisiones de diseño

| Decisión | Motivo |
|----------|--------|
| **StateFlow en lugar de LiveData** | Mejor integración con Compose y corrutinas |
| **Hilt** | Inyección estática, testeable y sin *boilerplate* |
| **DataStore** | API moderna, asíncrona y segura para preferencias |
| **Migraciones explícitas** | Nunca destruir datos del usuario al actualizar |
| **Foreground Service** | Garantiza el sonido aunque el sistema mate la app |
| **Full-screen intent** | Permite mostrar la alarma sobre el bloqueo |
| **Media3 ExoPlayer** | Reproducción de video robusta y actualizada |
| **Partículas con `withFrameNanos`** | Animación fluida sin recomposición excesiva |

---

## Gestión de temas

El color de la app se deriva de una **fuente de color** única:

```
ColorSource = THEME | CUSTOM | WALLPAPER
```

1. `THEME` → paleta predefinida (6 opciones).
2. `CUSTOM` → colores elegidos por el usuario.
3. `WALLPAPER` → colores extraídos del wallpaper con **Palette**.

Los colores se propagan a toda la app mediante `MaterialTheme`, y las tarjetas
del inicio también los consumen a través de `HomeViewModel`.

---

## Concurrencia

- `viewModelScope` para trabajo ligado a la UI.
- `Dispatchers.IO` para base de datos, archivos y extracción de color.
- `goAsync()` en los receivers para trabajo asíncrono corto.
- `runBlocking` **solo** en `attachBaseContext` (aplicación de idioma), nunca en
  el hilo principal durante la ejecución normal.
