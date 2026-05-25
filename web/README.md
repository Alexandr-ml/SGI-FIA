# SGI-FIA Web

Primera version web del sistema SGI-FIA. La app vive en `web/`, usa modulos ES del navegador y se conecta a Cloud Firestore cuando se completa `firebase-config.js`.

## Configuracion Firebase

1. En Firebase Console, registre una app Web dentro del proyecto.
2. Active Cloud Firestore.
3. En Google Cloud Console, habilite la API de Cloud Firestore para el proyecto.
4. Copie el objeto `firebaseConfig` en `web/firebase-config.js`.
5. Sirva la carpeta con el servidor incluido:

```powershell
& 'C:\Users\Sopor\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe' .\web\dev-server.mjs 5173
```

La version web usa Firebase como unica base de datos. Si `firebase-config.js` queda incompleto o Firestore no responde, la aplicacion muestra error de inicio en lugar de guardar datos locales.

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

Los nombres se comparten con la app Android para que ambos clientes lean y escriban en el mismo Firestore.
