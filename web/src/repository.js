import {
  canUseLocalFallback,
  collections,
  createFirestoreAdapter,
  hasFirebaseConfig,
} from "./firebase.js";

const STORAGE_KEY = "sgi-fia-web-db-v2";

const initialEquipos = [
  {
    id: "eq-monitor-dell",
    nombre: "Monitor Dell",
    numero_serie: "MON-001",
    marca: "Dell",
    modelo: "S2725HSM",
    ubicacion: "Centro de Computo",
    costo_unidad: 0,
    unidades: 1,
    descripcion: "Monitor para estaciones de trabajo",
    estado_funcional: "Activo",
    estado_prestamo: "Disponible",
  },
  {
    id: "eq-impresora-hp",
    nombre: "Impresora HP",
    numero_serie: "IMP-001",
    marca: "HP",
    modelo: "Smart Tank 580",
    ubicacion: "Secretaria de Facultad",
    costo_unidad: 0,
    unidades: 1,
    descripcion: "Impresora multifuncional",
    estado_funcional: "Activo",
    estado_prestamo: "Disponible",
  },
  {
    id: "eq-laptop-dell-x985",
    nombre: "Laptop Dell x985",
    numero_serie: "LAP-985",
    marca: "Dell",
    modelo: "x985",
    ubicacion: "Unidad de Ciencias Basicas",
    costo_unidad: 0,
    unidades: 1,
    descripcion: "Laptop asignada a unidad academica",
    estado_funcional: "Activo",
    estado_prestamo: "Disponible",
  },
  {
    id: "eq-proyector-spectra-q891",
    nombre: "Proyector Spectra Q891",
    numero_serie: "PRO-891",
    marca: "Spectra",
    modelo: "Q891",
    ubicacion: "Edificio B - Nivel 1 - FIA",
    costo_unidad: 0,
    unidades: 1,
    descripcion: "Proyector para aulas",
    estado_funcional: "Activo",
    estado_prestamo: "Disponible",
  },
];

const emptyDatabase = Object.fromEntries(
  Object.values(collections).map((collectionName) => [collectionName, []]),
);

export async function createRepository() {
  if (hasFirebaseConfig()) {
    try {
      const adapter = await createFirestoreAdapter();
      return {
        adapter,
        status: {
          mode: "firebase",
          label: `Firebase conectado: ${adapter.projectId}`,
          tone: "ok",
        },
      };
    } catch (error) {
      if (!canUseLocalFallback()) {
        throw error;
      }

      return {
        adapter: new LocalStorageAdapter(),
        status: {
          mode: "local",
          label: `Firebase no inicio. Usando datos locales.`,
          detail: error.message,
          tone: "warning",
        },
      };
    }
  }

  return {
    adapter: new LocalStorageAdapter(),
    status: {
      mode: "local",
      label: "Firebase pendiente. Usando datos locales.",
      detail: "Complete web/firebase-config.js para activar Cloud Firestore.",
      tone: "warning",
    },
  };
}

class LocalStorageAdapter {
  constructor() {
    this.mode = "local";
    this.database = this.load();
  }

  async list(collectionName) {
    return [...(this.database[collectionName] || [])].sort(compareByDateDesc);
  }

  async add(collectionName, value) {
    const id = crypto.randomUUID();
    const now = new Date().toISOString();
    this.ensureCollection(collectionName);
    this.database[collectionName].push({
      id,
      ...value,
      createdAt: now,
      updatedAt: now,
    });
    this.persist();
    return id;
  }

  async update(collectionName, id, value) {
    this.ensureCollection(collectionName);
    const index = this.database[collectionName].findIndex((item) => item.id === id);
    if (index === -1) {
      throw new Error(`No existe ${collectionName}/${id}`);
    }
    this.database[collectionName][index] = {
      ...this.database[collectionName][index],
      ...value,
      updatedAt: new Date().toISOString(),
    };
    this.persist();
    return id;
  }

  async upsert(collectionName, id, value) {
    this.ensureCollection(collectionName);
    const index = this.database[collectionName].findIndex((item) => item.id === id);
    const now = new Date().toISOString();
    if (index >= 0) {
      this.database[collectionName][index] = {
        ...this.database[collectionName][index],
        ...value,
        updatedAt: now,
      };
    } else {
      this.database[collectionName].push({
        id,
        ...value,
        createdAt: now,
        updatedAt: now,
      });
    }
    this.persist();
    return id;
  }

  ensureCollection(collectionName) {
    if (!this.database[collectionName]) {
      this.database[collectionName] = [];
    }
  }

  load() {
    const rawValue = localStorage.getItem(STORAGE_KEY);
    if (rawValue) {
      return { ...emptyDatabase, ...JSON.parse(rawValue) };
    }

    const now = new Date().toISOString();
    const database = {
      ...emptyDatabase,
      [collections.equipos]: initialEquipos.map((equipo) => ({
        ...equipo,
        createdAt: now,
        updatedAt: now,
      })),
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(database));
    return database;
  }

  persist() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(this.database));
  }
}

function compareByDateDesc(left, right) {
  const leftDate = new Date(left.updatedAt || left.createdAt || 0).getTime();
  const rightDate = new Date(right.updatedAt || right.createdAt || 0).getTime();
  return rightDate - leftDate;
}

export { collections, initialEquipos };
