import {
  collections,
  createFirestoreAdapter,
  hasFirebaseConfig,
} from "./firebase.js";

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

export async function createRepository() {
  if (hasFirebaseConfig()) {
    const adapter = await createFirestoreAdapter();
    return {
      adapter,
      status: {
        mode: "firebase",
        label: `Firebase conectado: ${adapter.projectId}`,
        tone: "ok",
      },
    };
  }

  throw new Error("Firebase no esta configurado. Complete web/firebase-config.js.");
}

export { collections, initialEquipos };
