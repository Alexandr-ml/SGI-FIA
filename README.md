# SGI-FIA - Entrega

## Aplicacion movil

- APK debug: `apk/SGI-FIA-debug.apk`
- Paquete Android: `com.grupo1.sgi_fia`
- Usuario: `Administracion FIA`
- Contrasena: `FIA20268`

## Scripts de base de datos

- SQLite historico/local: `scripts/sqlite_sgi_fia.sql`
- Firebase/Firestore usado por web y movil: `scripts/firebase_firestore_seed.mjs`

La base principal actual es Firebase/Firestore en el proyecto `pdm-ues`.
El script SQLite queda como respaldo documental de la estructura local equivalente.

**1. Móvil**

* Android nativo desarrollado en Java.
* IDE: Android Studio.
* Base de datos: Firebase Firestore.
* Interfaz: XML nativo.
* Autenticación local.
* Pruebas con JUnit, AndroidX Test y Espresso.
* Compatibilidad: `minSdk 24`, `targetSdk 36`.

**2. Web**
url: https://pdm-ues.web.app/
* Desarrollada con HTML, CSS y JavaScript.
* Arquitectura modular con ES Modules.
* Base de datos compartida: Firebase Firestore.
* Uso de Firebase Web SDK.
* Servidor local con Node.js.
* Autenticación local y sesión con `sessionStorage`.
* Consume colecciones como `documentos`, `equipos_informaticos`, `prestamos`, `devoluciones` y `levantamientos_fisicos`.
