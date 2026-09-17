# Estructura de archivos

Descripción de cada paquete y archivo relevante del proyecto.

---

## Raíz

```
AmiminAlarm/
├── app/                      # Módulo de la aplicación
├── gradle/                   # Wrapper y catálogo de versiones
├── docs/                     # Documentación (este directorio)
├── screenshots/              # Capturas para el README
├── build.gradle.kts          # Plugins raíz
├── settings.gradle.kts       # Módulos y repositorios
├── gradle.properties         # Propiedades de Gradle
├── gradlew / gradlew.bat     # Wrapper
├── local.properties          # Ruta del SDK (no versionado)
└── README.md · LICENSE · ...
```

---

## `app/src/main/java/com/amimin/app/`

### Raíz

| Archivo | Descripción |
|---------|-------------|
| `MainActivity.kt` | Punto de entrada. Aplica idioma, tema global, wallpaper y navegación |
| `AmiminApp.kt` | `Application` de Hilt. Crea los canales de notificación |

### `data/`

| Archivo | Descripción |
|---------|-------------|
| `model/Models.kt` | Entidades `Alarm`, `CalendarEvent` |
| `database/AmiminDatabase.kt` | Base de datos Room |
| `database/DatabaseMigrations.kt` | Migraciones que preservan datos |
| `database/DAOs.kt` | `AlarmDao`, `CalendarEventDao` |
| `database/AlarmScheduler.kt` | Programa alarmas, recordatorios y snooze |
| `database/AlarmService.kt` | Servicio en primer plano que suena |
| `database/AlarmReceiver.kt` | Dispara la alarma |
| `database/AlarmReminderReceiver.kt` | Recordatorio previo (1h/30m/10m) |
| `database/EventReminderReceiver.kt` | Recordatorio de eventos |
| `database/AlarmNotificationReceiver.kt` | Acciones Dismiss / Snooze |
| `database/BootReceiver.kt` | Reagenda tras reiniciar |
| `repository/Repositories.kt` | `AlarmRepository`, `CalendarRepository` |
| `repository/SettingsRepository.kt` | Ajustes en DataStore |
| `backup/BackupManager.kt` | Exportar / importar copia de seguridad |

### `di/`

| Archivo | Descripción |
|---------|-------------|
| `DatabaseModule.kt` | Provee Room y los DAOs a Hilt |

### `notifications/`

| Archivo | Descripción |
|---------|-------------|
| `NotificationHelper.kt` | Canales y contexto localizado |

### `ui/`

| Paquete | Descripción |
|---------|-------------|
| `theme/` | `Color`, `Type`, `Shape`, `Theme` |
| `animations/` | `Particles`, `LiveWallpaper`, `VideoLiveWallpaper`, `Transitions`, `Animations` |
| `components/` | `StickerPicker`, `SoundEffects` |
| `navigation/` | `Navigation.kt` (NavHost y rutas) |
| `screens/home/` | `HomeScreen`, `HomeViewModel`, `Greetings` |
| `screens/alarm/` | `AlarmScreen`, `AlarmViewModel`, `AlarmRingingActivity` |
| `screens/calendar/` | `CalendarScreen`, `CalendarViewModel` |
| `screens/settings/` | `SettingsScreen`, `SettingsViewModel` |

---

## `app/src/main/res/`

| Carpeta | Contenido |
|---------|-----------|
| `values/` | Español (base) |
| `values-en/`... | Traducciones (inglés, japonés, coreano, chino, ruso, francés, alemán) |
| `drawable/` | Iconos vectoriales (`ic_alarm`, `ic_snooze`) |
| `mipmap-anydpi-v26/` | Icono adaptativo |
| `xml/` | `file_paths`, `backup_rules`, `data_extraction_rules` |

> **Nota**: el idioma base (`values/`) es español; los textos por defecto del
> sistema se completan con los archivos de cada idioma.

---

## Archivos clave que conviene conocer

| Necesitas… | Mira… |
|-----------|-------|
| Añadir una pantalla | `ui/navigation/Navigation.kt` |
| Cambiar colores o tipografía | `ui/theme/` |
| Modificar el saludo | `ui/screens/home/Greetings.kt` |
| Tocar la programación de alarmas | `data/database/AlarmScheduler.kt` |
| Añadir un ajuste | `data/repository/SettingsRepository.kt` + `SettingsScreen` |
| Traducir | `res/values-*/strings.xml` |
