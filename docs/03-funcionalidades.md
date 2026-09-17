# Funcionalidades

Detalle de cada característica de AmiminAlarm.

---

## ⏰ Alarma

### Crear una alarma

Desde la pantalla **Alarma** pulsa el botón **+**. Se abre un diálogo con:

- **Hora y minutos** (formato 12 h o 24 h según ajuste).
- **Etiqueta** personalizada.
- **Repetición** por días de la semana.
- **Sticker** anime (5 categorías).
- **Sonido** del sistema o propio.
- **Foto** personalizada.

### Sonar la alarma

Cuando llega la hora:

1. `AlarmReceiver` recibe el intent.
2. Arranca `AlarmService` (servicio en primer plano).
3. Se reproduce el sonido en bucle y se activa la vibración.
4. Se muestra una **notificación a pantalla completa** (`full-screen intent`).
5. Se abre `AlarmRingingActivity` **sobre el bloqueo** con dos acciones:
   - **Dismiss** — detiene la alarma.
   - **Snooze** — la pospone 5 minutos.

### Recordatorios anticipados

En **Configuración → Avisos de Alarma** puedes activar avisos a:

- **1 hora** antes
- **30 minutos** antes
- **10 minutos** antes

Cada aviso es una notificación independiente, localizada en tu idioma.

---

## 📅 Calendario

### Vista mensual

- Rejilla de 6 semanas con el día actual resaltado.
- Navegación **`‹` `›`** para meses y **`«` `»`** para años.
- Los días con eventos muestran un punto.

### Eventos

Cada evento tiene:

- **Título** y **descripción**.
- **Color** (6 predefinidos).
- **Todo el día** (opcional).
- **Repetición**: no se repite, cada semana, cada mes o cada año.
- **Sticker** anime.
- **Foto** personalizada.

### Notificaciones de eventos

Si las **Notificaciones de Eventos** están activas, recibirás un aviso antes de
que empiece el evento. Funciona también con eventos repetidos.

---

## 🎨 Personalización

### Live Wallpaper (Fondo animado)

En **Configuración → Fondo Animado**:

| Estilo | Descripción |
|--------|-------------|
| **Degradado** | Color que fluye suavemente |
| **Sakura** | Pétalos de cerezo cayendo |
| **Estrellas** | Cielo titilante |
| **Aurora** | Ondas de aurora boreal |
| **Video** | Tu propio video en bucle |

Además puedes activar o desactivar el **sonido** del fondo animado.

### Wallpaper estático

En **Configuración → Wallpaper Anime** eliges una imagen. AmiminAlarm:

1. La copia al almacenamiento interno (para que persista).
2. Extrae sus colores con **Palette**.
3. Los aplica a toda la app si el modo de color es *Wallpaper*.

### Modo de color

Tres fuentes mutuamente excluyentes:

1. **Tema de color** — 6 paletas predefinidas.
2. **Colores personalizados** — eliges primario y secundario.
3. **Colores del wallpaper** — extraídos automáticamente.

### Estilos tipográficos

| Estilo | Fuente |
|--------|--------|
| **Kawaii** | Sans redondeada |
| **Elegant** | Serif con espaciado amplio |
| **Cyberpunk** | Monospace |
| **Minimal** | Sans ligera y compacta |
| **Retro** | Cursiva |

---

## 🌍 Idiomas y saludo

AmiminAlarm incluye 8 idiomas completos. El **saludo de la pantalla principal** se
adapta al **país** y a la **hora**:

| Región | Buenos días | Buenas tardes | Buenas noches |
|--------|-------------|---------------|---------------|
| España | 6:00 – 13:59 | 14:00 – 20:59 | 21:00 – 5:59 |
| Latinoamérica | 6:00 – 11:59 | 12:00 – 18:59 | 19:00 – 5:59 |
| Japón / Corea / China | 5:00 – 10:59 | 11:00 – 17:59 | 18:00 – 4:59 |

El saludo se personaliza con tu **nombre de usuario**:
*«Buenos días, Maira»*.

---

## 👤 Perfil y formato de hora

- **Tu Nombre**: la app te saluda con él.
- **Formato de 24 Horas**: activado muestra `13:00`, desactivado `1:00 PM`.

---

## 💾 Copia de seguridad

En **Configuración → Copia de Seguridad**:

| Acción | Resultado |
|--------|-----------|
| **Guardar copia local** | Crea `amiminalarm-copia-de-seguridad-AAAA-MM-DD.json` donde tú elijas |
| **Guardar en Google Drive** | Comparte el archivo para subirlo a la nube |
| **Restaurar copia** | Recupera alarmas, eventos, ajustes y wallpaper |

La copia incluye **todo**: alarmas, eventos, ajustes y el wallpaper (codificado).

---

## 🔔 Notificaciones

- **Canales** independientes: alarma, recordatorios y eventos.
- **Localizadas** en el idioma activo.
- **Sobreviven** al cierre de la app y al reinicio del teléfono.
- **Pantalla completa** sobre el bloqueo para la alarma.
