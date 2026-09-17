# Temas y personalización

El sistema visual de **AmiminAlarm**: temas, wallpaper, live wallpaper y tipografías.

---

## Fuente de color

La app tiene **una única fuente de color** que se elige en
**Configuración → Modo de Color**:

```
ColorSource = THEME | CUSTOM | WALLPAPER
```

Las tres opciones son **mutuamente excluyentes** para evitar conflictos:

| Fuente | De dónde salen los colores |
|--------|---------------------------|
| `THEME` | Una de las 6 paletas predefinidas |
| `CUSTOM` | Los colores que elige el usuario |
| `WALLPAPER` | Extraídos del wallpaper con Palette |

### Prioridad de aplicación

```
if (liveWallpaper activo)      → colores del live wallpaper
else if (colorSource == WALLPAPER) → colores del wallpaper
else if (colorSource == CUSTOM)    → colores personalizados
else                                → paleta del tema
```

---

## Temas predefinidos

| Tema | Primario | Secundario |
|------|----------|-----------|
| Sakura Pink | Rosa sakura | Púrpura anime |
| Midnight Purple | Púrpura | Cian |
| Cherry Red | Rojo cereza | Naranja |
| Ocean Blue | Azul | Cian |
| Neon Glow | Verde neón | Magenta |
| Pastel Dreams | Rosa pastel | Menta |

Cada tema define primario, secundario, acento, fondo, superficie y colores *on*.

---

## Extracción de color del wallpaper

Al elegir una imagen, `SettingsRepository` la copia al almacenamiento interno y
se extraen tres colores con **AndroidX Palette**:

```kotlin
val palette = Palette.from(bitmap).generate()
val primary   = palette.getVibrantColor(fallback)
val secondary = palette.getLightVibrantColor(fallback)
val accent    = palette.getDarkVibrantColor(fallback)
```

Los colores se guardan en DataStore y se aplican a `MaterialTheme`.

> **Por qué se copia la imagen:** un `content://` URI es temporal. Al copiarla al
> almacenamiento interno (`filesDir`), el wallpaper persiste entre reinicios.

---

## Live Wallpaper

En **Configuración → Fondo Animado** hay 5 estilos:

| Estilo | Implementación |
|--------|----------------|
| Degradado | Gradiente animado con `rememberInfiniteTransition` |
| Sakura | 40 pétalos con física simple |
| Estrellas | 60 estrellas con parpadeo sinusoidal |
| Aurora | 3 bandas de aurora con ondas |
| Video | Media3 ExoPlayer en bucle con `TextureView` |

### Rendimiento

Las partículas **no** usan estado por partícula (lo que provocaría una
recomposición por cada una). En su lugar:

```kotlin
var frame by remember { mutableLongStateOf(0L) }

LaunchedEffect(Unit) {
    while (true) {
        withFrameNanos { now ->
            val dt = (now - lastNanos) / 1_000_000_000f
            particles.forEach { it.update(dt) }
            frame++          // un único trigger de redibujado
        }
    }
}

Canvas { frame; /* dibuja todas las partículas */ }
```

Esto sincroniza con la tasa de refresco real (60/90/120 Hz) y dibuja todo en una
sola pasada.

### Video

`VideoLiveWallpaper` usa ExoPlayer con:

- `setUseTextureView` implícito vía `setVideoTextureView` (evita problemas de
  z-order de `SurfaceView` en Compose).
- `prepare()` + `playWhenReady` (imprescindible para que cargue).
- `REPEAT_MODE_ALL` para bucle infinito.
- Control de volumen según el ajuste de sonido.
- Fallback a degradado si el video falla.

---

## Estilos tipográficos

| Estilo | Familia | Peso | Escala | Espaciado |
|--------|---------|------|--------|-----------|
| Kawaii | Default | Bold | 1.00 | 0.3 |
| Elegant | Serif | Normal | 1.06 | 2.0 |
| Cyberpunk | Monospace | Bold | 0.94 | 0.8 |
| Minimal | SansSerif | Light | 0.90 | 0.0 |
| Retro | Cursive | Bold | 1.02 | 1.4 |

Se aplican a toda la `Typography` de Material 3.

---

## Modo claro / oscuro

`isDarkMode` controla el `ColorScheme`. En ambos casos el primario, secundario y
acento provienen de la fuente de color activa; solo cambian fondo, superficie y
los colores *on*.

---

## Stickers anime

`AnimeStickers` define 5 categorías con 12 stickers cada una:

| Categoría | Ejemplos |
|-----------|----------|
| Kawaii | 🌸 🌺 🎀 💖 ✨ |
| Emociones | 😊 😍 🥰 😴 |
| Símbolos | ⭐ 💫 🌙 ☀️ |
| Animales | 🐱 🐰 🦊 🐼 |
| Comida | 🍣 🍜 🍡 🍙 |

Se pueden asignar a alarmas y eventos.

---

## Fondos de pantalla de alarma

La pantalla que aparece cuando suena la alarma admite:

1. **Predeterminado** — animación de pétalos sakura.
2. **Imagen** — una foto del usuario.
3. **Video** — un video del usuario.

Se configura en **Configuración → Pantalla de Alarma**.
