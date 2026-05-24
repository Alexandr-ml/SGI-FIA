# SGI-FIA Web

Primera version web del sistema SGI-FIA. La app vive en `web/`, usa modulos ES del navegador y se conecta a Cloud Firestore cuando se completa `firebase-config.js`.

## Configuracion Firebase

1. En Firebase Console, registre una app Web dentro del proyecto.
2. Active Cloud Firestore.
3. Copie el objeto `firebaseConfig` en `web/firebase-config.js`.
4. Sirva la carpeta con el servidor incluido:

```powershell
& 'C:\Users\Sopor\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe' .\web\dev-server.mjs 5173
```

Si `firebase-config.js` queda vacio, la interfaz usa `localStorage` para permitir pruebas sin tocar Firestore.

## Colecciones Firestore

- `documentos`
- `equipos_informaticos`
- `prestatarios`
- `prestamos`
- `prestamos_tesis`
- `prestamos_equipo_horas`
- `prestamos_equipo_recurrente`
- `devoluciones`
- `devoluciones_tesis`
- `levantamientos_fisicos`

Los nombres se mantienen cercanos a la base SQLite actual para facilitar la migracion.
