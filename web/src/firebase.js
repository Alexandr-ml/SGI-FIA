import { firebaseConfig, firebaseOptions } from "../firebase-config.js";

const FIREBASE_VERSION = "12.13.0";
const FIREBASE_APP_URL = `https://www.gstatic.com/firebasejs/${FIREBASE_VERSION}/firebase-app.js`;
const FIREBASE_FIRESTORE_URL = `https://www.gstatic.com/firebasejs/${FIREBASE_VERSION}/firebase-firestore.js`;

let sdkPromise;

export const collections = {
  documentos: "documentos",
  equipos: "equipos_informaticos",
  prestatarios: "prestatarios",
  prestamos: "prestamos",
  prestamosTesis: "prestamos_tesis",
  prestamosEquipoHoras: "prestamos_equipo_horas",
  prestamosEquipoRecurrente: "prestamos_equipo_recurrente",
  devoluciones: "devoluciones",
  devolucionesTesis: "devoluciones_tesis",
  levantamientos: "levantamientos_fisicos",
};

export function hasFirebaseConfig() {
  return Boolean(
    firebaseConfig.apiKey &&
      firebaseConfig.authDomain &&
      firebaseConfig.projectId &&
      firebaseConfig.appId,
  );
}

export function canUseLocalFallback() {
  return firebaseOptions.enableLocalFallback !== false;
}

async function loadSdk() {
  if (!sdkPromise) {
    sdkPromise = Promise.all([import(FIREBASE_APP_URL), import(FIREBASE_FIRESTORE_URL)]);
  }
  const [appSdk, firestoreSdk] = await sdkPromise;
  return { ...appSdk, ...firestoreSdk };
}

export async function createFirestoreAdapter() {
  if (!hasFirebaseConfig()) {
    return null;
  }

  const sdk = await loadSdk();
  const app = sdk.getApps().length
    ? sdk.getApps()[0]
    : sdk.initializeApp(firebaseConfig);
  const db = sdk.getFirestore(app);

  return {
    mode: "firebase",
    projectId: firebaseConfig.projectId,

    async list(collectionName) {
      const snapshot = await sdk.getDocs(sdk.collection(db, collectionName));
      return snapshot.docs.map((documentSnapshot) => ({
        id: documentSnapshot.id,
        ...normalizeFirestoreData(documentSnapshot.data()),
      }));
    },

    async add(collectionName, value) {
      const now = sdk.serverTimestamp();
      const documentReference = await sdk.addDoc(sdk.collection(db, collectionName), {
        ...value,
        createdAt: now,
        updatedAt: now,
      });
      return documentReference.id;
    },

    async update(collectionName, id, value) {
      await sdk.updateDoc(sdk.doc(db, collectionName, id), {
        ...value,
        updatedAt: sdk.serverTimestamp(),
      });
      return id;
    },

    async upsert(collectionName, id, value) {
      await sdk.setDoc(
        sdk.doc(db, collectionName, id),
        {
          ...value,
          updatedAt: sdk.serverTimestamp(),
        },
        { merge: true },
      );
      return id;
    },
  };
}

function normalizeFirestoreData(value) {
  return Object.fromEntries(
    Object.entries(value).map(([key, entryValue]) => [
      key,
      typeof entryValue?.toDate === "function"
        ? entryValue.toDate().toISOString()
        : entryValue,
    ]),
  );
}
