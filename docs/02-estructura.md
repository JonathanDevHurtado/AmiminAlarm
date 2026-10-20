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
| `database/AlarmTimeCalculator.kt` | Cálculo puro de la próxima ocurrencia |
| `database/AlarmScheduler.kt` | Programa alarmas, recordatorios y snooze |
| `database/AlarmService.kt` | Servicio en primer plano que suena |
| `database/AlarmReceiver.kt` | Dispara la alarma |
| `database/AlarmReminderReceiver.kt` | Recordatorio previo (1h/30m/10m) |
| `database/EventReminderReceiver.kt` | Recordatorio de eventos |
| `database/AlarmNotificationReceiver.kt` | Acciones Dismiss / Snooze |
| `database/AlarmRescheduler.kt` | Reagenda todo tras boot, actualización o cambio de hora |
| `database/BootReceiver.kt` | Escucha broadcasts y delega en `AlarmRescheduler` |
| `repository/Repositories.kt` | `AlarmRepository`, `CalendarRepository` |
| `repository/SettingsRepository.kt` | Ajustes en DataStore |
| `backup/BackupManager.kt` | Exportar / importar copia de seguridad |
| `backup/BackupValidator.kt` | Valida el JSON antes de modificar la base de datos |

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
| `values/` | Inglés (base) |
| `values-es/`... | Español, japonés, coreano, chino, ruso, francés y alemán |
| `drawable/` | Iconos vectoriales (`ic_alarm`, `ic_snooze`) |
| `drawable-nodpi/` | Logo `amimin_logo.png` usado en la UI |
| `mipmap-anydpi-v26/` | Icono adaptativo |
| `xml/` | `file_paths`, `backup_rules`, `data_extraction_rules` |

> **Nota**: el idioma base (`values/`) es inglés; `values-es/` contiene la
> traducción al español y el resto de carpetas las demás traducciones.

---

## Archivos clave que conviene conocer

| Necesitas… | Mira… |
|-----------|-------|
| Añadir una pantalla | `ui/navigation/Navigation.kt` |
| Cambiar colores o tipografía | `ui/theme/` |
| Modificar el saludo | `ui/screens/home/Greetings.kt` |
| Tocar la programación de alarmas | `AlarmTimeCalculator` y `AlarmScheduler` |
| Añadir un ajuste | `data/repository/SettingsRepository.kt` + `SettingsScreen` |
| Traducir | `res/values-*/strings.xml` |
