import { collections, createRepository, initialEquipos } from "./repository.js";

const root = document.querySelector("#app");
const LOGIN_USER = "Administracion FIA";
const LOGIN_PASSWORD = "FIA20268";
const SESSION_KEY = "sgi-fia-authenticated";

const navItems = [
  { id: "dashboard", label: "Panel" },
  { id: "documentos", label: "Documentos" },
  { id: "inventario", label: "Inventario" },
  { id: "prestamos", label: "Prestamos" },
  { id: "devoluciones", label: "Devoluciones" },
  { id: "levantamiento", label: "Levantamiento" },
  { id: "busqueda", label: "Busqueda" },
];

const state = {
  view: "dashboard",
  loanMode: "libro",
  returnMode: "libro",
  search: "",
  physicalFilter: "",
  busy: false,
  authenticated: sessionStorage.getItem(SESSION_KEY) === "true",
  status: null,
  data: emptyStateData(),
  lastSync: null,
};

let repository;
let shellEventsBound = false;

init();

async function init() {
  if (!state.authenticated) {
    renderLogin();
    return;
  }

  await startApplication();
}

async function startApplication() {
  try {
    const result = await createRepository();
    repository = result.adapter;
    state.status = result.status;
    renderShell();
    await refreshData();
  } catch (error) {
    root.innerHTML = `
      <main class="fatal-screen">
        <div>
          <p class="eyebrow">SGI-FIA Web</p>
          <h1>No se pudo iniciar la aplicacion</h1>
          <p>${escapeHtml(error.message)}</p>
        </div>
      </main>
    `;
  }
}

function renderLogin(errorMessage = "") {
  setBusy(false);
  root.innerHTML = `
    <main class="login-screen">
      <section class="login-panel" aria-labelledby="loginTitle">
        <div class="brand-mark" aria-hidden="true">SGI</div>
        <div>
          <p class="eyebrow">FIA</p>
          <h1 id="loginTitle">Acceso administrativo</h1>
          <p class="login-copy">Sistema de Gestion de Inventario</p>
        </div>
        <form class="login-form" data-login-form>
          <label>
            <span>Usuario</span>
            <input name="usuario" autocomplete="username" required autofocus />
          </label>
          <label>
            <span>Contrasena</span>
            <input name="contrasena" type="password" autocomplete="current-password" required />
          </label>
          <p class="login-error" data-login-error ${errorMessage ? "" : "hidden"}>
            ${escapeHtml(errorMessage)}
          </p>
          <button class="primary-button" type="submit">Ingresar</button>
        </form>
      </section>
    </main>
  `;

  root.querySelector("[data-login-form]").addEventListener("submit", handleLoginSubmit);
}

async function handleLoginSubmit(event) {
  event.preventDefault();
  const data = formData(event.currentTarget);
  const usuario = clean(data.usuario);
  const contrasena = clean(data.contrasena);

  if (usuario !== LOGIN_USER || contrasena !== LOGIN_PASSWORD) {
    showLoginError("Usuario o contrasena incorrectos.");
    return;
  }

  state.authenticated = true;
  sessionStorage.setItem(SESSION_KEY, "true");
  await startApplication();
}

function showLoginError(message) {
  const error = root.querySelector("[data-login-error]");
  if (!error) return;
  error.textContent = message;
  error.hidden = false;
}

function renderShell() {
  root.innerHTML = `
    <div class="app-shell">
      <aside class="sidebar">
        <div class="brand-block">
          <div class="brand-mark" aria-hidden="true">SGI</div>
          <div>
            <p class="eyebrow">FIA</p>
            <h1>SGI-FIA</h1>
          </div>
        </div>
        <nav class="nav-list" aria-label="Modulos SGI-FIA">
          ${navItems.map(renderNavButton).join("")}
        </nav>
        <div class="connection-card ${state.status.tone}">
          <span class="status-dot"></span>
          <div>
            <strong>${escapeHtml(state.status.label)}</strong>
            ${state.status.detail ? `<small>${escapeHtml(state.status.detail)}</small>` : ""}
          </div>
        </div>
      </aside>

      <main class="workspace">
        <header class="topbar">
          <div>
            <p class="eyebrow">Sistema de Gestion de Inventario</p>
            <h2 id="viewTitle"></h2>
          </div>
          <div class="topbar-actions">
            <span id="syncStatus" class="sync-status">Sin sincronizar</span>
            <button class="icon-button" data-refresh type="button" title="Actualizar datos" aria-label="Actualizar datos">
              <span aria-hidden="true">R</span>
            </button>
            <button class="secondary-button logout-button" data-logout type="button">Salir</button>
          </div>
        </header>

        <section class="status-strip" aria-label="Resumen rapido">
          <div>
            <span id="countDocuments">0</span>
            <small>Documentos</small>
          </div>
          <div>
            <span id="countEquipment">0</span>
            <small>Equipos</small>
          </div>
          <div>
            <span id="countPending">0</span>
            <small>Pendientes</small>
          </div>
          <div>
            <span id="countAudits">0</span>
            <small>Levantamientos</small>
          </div>
        </section>

        <section id="content" class="content-area" aria-live="polite"></section>
      </main>
    </div>
    <div id="toast" class="toast" role="status" aria-live="polite"></div>
  `;

  if (!shellEventsBound) {
    root.addEventListener("click", handleClick);
    root.addEventListener("submit", handleSubmit);
    root.addEventListener("input", handleInput);
    shellEventsBound = true;
  }
  renderContent();
}

function renderNavButton(item) {
  const activeClass = item.id === state.view ? "active" : "";
  return `
    <button class="nav-button ${activeClass}" data-view="${item.id}" type="button">
      <span class="nav-indicator" aria-hidden="true"></span>
      <span>${item.label}</span>
    </button>
  `;
}

async function refreshData() {
  setBusy(true);
  try {
    const entries = await Promise.all(
      Object.entries(collections).map(async ([key, collectionName]) => [
        key,
        await repository.list(collectionName),
      ]),
    );
    state.data = Object.fromEntries(entries);
    state.lastSync = new Date();
    updateCounters();
    renderContent();
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(false);
  }
}

function renderContent() {
  const viewTitle = document.querySelector("#viewTitle");
  const content = document.querySelector("#content");
  if (!content || !viewTitle) return;

  const activeItem = navItems.find((item) => item.id === state.view) || navItems[0];
  viewTitle.textContent = activeItem.label;

  document.querySelectorAll(".nav-button").forEach((button) => {
    button.classList.toggle("active", button.dataset.view === state.view);
  });

  const views = {
    dashboard: renderDashboard,
    documentos: renderDocumentos,
    inventario: renderInventario,
    prestamos: renderPrestamos,
    devoluciones: renderDevoluciones,
    levantamiento: renderLevantamiento,
    busqueda: renderBusqueda,
  };

  content.innerHTML = views[state.view]();
  updateCounters();
}

function renderDashboard() {
  const documentos = state.data.documentos;
  const equipos = state.data.equipos;
  const prestamosPendientes = pendingLoans();
  const recientes = recentActivity();
  const equiposSinLevantamiento = equipos
    .filter((equipo) => !equipo.fecha_ultimo_levantamiento)
    .slice(0, 5);

  return `
    <div class="dashboard-layout">
      <section class="metric-grid">
        ${renderMetric("Catalogo", documentos.length, "Libros y tesis registrados")}
        ${renderMetric("Inventario", equipos.length, "Activos informaticos")}
        ${renderMetric("Prestamos", prestamosPendientes.length, "Pendientes de cierre")}
        ${renderMetric("Auditoria", state.data.levantamientos.length, "Levantamientos fisicos")}
      </section>

      <section class="work-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">Actividad</p>
            <h3>Movimientos recientes</h3>
          </div>
          <button class="secondary-button" data-view="busqueda" type="button">Buscar</button>
        </div>
        <div class="timeline-list">
          ${
            recientes.length
              ? recientes.map(renderTimelineItem).join("")
              : `<p class="empty-state">Sin movimientos todavia.</p>`
          }
        </div>
      </section>

      <section class="work-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">Inventario</p>
            <h3>Activos sin levantamiento</h3>
          </div>
          <button class="secondary-button" data-view="levantamiento" type="button">Revisar</button>
        </div>
        <div class="compact-list">
          ${
            equiposSinLevantamiento.length
              ? equiposSinLevantamiento.map(renderEquipmentRow).join("")
              : `<p class="empty-state">Todos los activos tienen fecha de levantamiento.</p>`
          }
        </div>
      </section>

      <section class="work-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">Datos base</p>
            <h3>Equipos iniciales</h3>
          </div>
          <button class="primary-button" data-seed-equipment type="button">Cargar</button>
        </div>
        <div class="compact-list">
          ${initialEquipos.map(renderSeedRow).join("")}
        </div>
      </section>
    </div>
  `;
}

function renderDocumentos() {
  return `
    <div class="two-column-layout">
      <section class="work-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">Catalogo</p>
            <h3>Registro de documento</h3>
          </div>
        </div>
        <form class="form-grid" data-action="documento">
          <label>
            <span>Titulo</span>
            <input name="titulo" required autocomplete="off" />
          </label>
          <label>
            <span>Tipo</span>
            <select name="tipo" required>
              <option>Libro</option>
              <option>Tesis</option>
            </select>
          </label>
          <label>
            <span>ISBN</span>
            <input name="isbn" autocomplete="off" />
          </label>
          <label>
            <span>Idioma</span>
            <input name="idioma" autocomplete="off" />
          </label>
          <label>
            <span>Anio</span>
            <input name="anio" min="0" inputmode="numeric" type="number" />
          </label>
          <label>
            <span>Ejemplares</span>
            <input name="ejemplares" min="1" value="1" inputmode="numeric" type="number" />
          </label>
          <div class="form-actions">
            <button class="primary-button" type="submit">Guardar</button>
            <button class="secondary-button" type="reset">Limpiar</button>
          </div>
        </form>
      </section>

      <section class="work-panel wide-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">Documentos</p>
            <h3>Catalogo actual</h3>
          </div>
          <span class="count-pill">${state.data.documentos.length}</span>
        </div>
        ${renderDocumentTable(state.data.documentos)}
      </section>
    </div>
  `;
}

function renderInventario() {
  return `
    <div class="two-column-layout">
      <section class="work-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">Inventario</p>
            <h3>Nuevo equipo informatico</h3>
          </div>
        </div>
        <form class="form-grid" data-action="equipo">
          <label>
            <span>Numero de serie</span>
            <input name="numero_serie" required autocomplete="off" />
          </label>
          <label>
            <span>Marca</span>
            <input name="marca" required autocomplete="off" />
          </label>
          <label>
            <span>Modelo</span>
            <input name="modelo" required autocomplete="off" />
          </label>
          <label>
            <span>Ubicacion</span>
            <input name="ubicacion" autocomplete="off" />
          </label>
          <label>
            <span>Costo unidad</span>
            <input name="costo_unidad" min="0" step="0.01" inputmode="decimal" type="number" />
          </label>
          <label>
            <span>Unidades</span>
            <input name="unidades" min="1" value="1" inputmode="numeric" type="number" />
          </label>
          <label class="full-field">
            <span>Descripcion</span>
            <textarea name="descripcion" rows="3"></textarea>
          </label>
          <div class="form-actions">
            <button class="primary-button" type="submit">Anadir equipo</button>
            <button class="secondary-button" type="reset">Limpiar</button>
          </div>
        </form>
      </section>

      <section class="work-panel wide-panel">
        <div class="panel-heading">
          <div>
            <p class="eyebrow">Activos</p>
            <h3>Equipos registrados</h3>
          </div>
          <span class="count-pill">${state.data.equipos.length}</span>
        </div>
        ${renderEquipmentTable(state.data.equipos)}
      </section>
    </div>
  `;
}

function renderPrestamos() {
  return `
    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Prestamos</p>
          <h3>Registro de prestamos</h3>
        </div>
        ${renderSegmentedControl("loan", state.loanMode, [
          ["libro", "Libro"],
          ["tesis", "Tesis"],
          ["equipoHoras", "Equipo por horas"],
          ["recurrente", "Recurrente"],
        ])}
      </div>
      ${renderLoanForm()}
    </section>

    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Historial</p>
          <h3>Prestamos recientes</h3>
        </div>
      </div>
      ${renderLoanHistory()}
    </section>
  `;
}

function renderLoanForm() {
  if (state.loanMode === "tesis") {
    return `
      <form class="form-grid loan-form" data-action="prestamo-tesis">
        <label>
          <span>Nombre prestatario</span>
          <input name="nombre_prestatario" required autocomplete="off" />
        </label>
        <label>
          <span>Carnet</span>
          <input name="carnet_prestatario" autocomplete="off" placeholder="N/A" />
        </label>
        ${renderThesisSelector()}
        <label>
          <span>Fecha prestamo</span>
          <input name="fecha_prestamo" required type="date" value="${todayISO()}" />
        </label>
        <label>
          <span>Fecha devolucion</span>
          <input name="fecha_devolucion" required type="date" />
        </label>
        <label class="full-field">
          <span>Observaciones</span>
          <textarea name="observaciones" rows="2"></textarea>
        </label>
        <div class="form-actions">
          <button class="primary-button" type="submit">Registrar tesis</button>
        </div>
      </form>
    `;
  }

  if (state.loanMode === "equipoHoras") {
    return `
      <form class="form-grid loan-form" data-action="prestamo-equipo-horas">
        <label>
          <span>Nombre prestatario</span>
          <input name="nombre_prestatario" required autocomplete="off" />
        </label>
        <label>
          <span>Carnet</span>
          <input name="carnet_prestatario" autocomplete="off" placeholder="N/A" />
        </label>
        <label>
          <span>Fecha</span>
          <input name="fecha_prestamo" required type="date" value="${todayISO()}" />
        </label>
        <label>
          <span>Hora inicio</span>
          <input name="hora_inicio" required type="time" />
        </label>
        <label>
          <span>Hora fin</span>
          <input name="hora_fin" required type="time" />
        </label>
        <label>
          <span>Actividad</span>
          <input name="actividad" value="Prestamo por horas" required autocomplete="off" />
        </label>
        ${renderEquipmentPicker()}
        <label class="full-field">
          <span>Observaciones</span>
          <textarea name="observaciones" rows="2"></textarea>
        </label>
        <div class="form-actions">
          <button class="primary-button" type="submit">Registrar equipo</button>
        </div>
      </form>
    `;
  }

  if (state.loanMode === "recurrente") {
    return `
      <form class="form-grid loan-form" data-action="prestamo-recurrente">
        <label>
          <span>Nombre prestatario</span>
          <input name="nombre_prestatario" required autocomplete="off" />
        </label>
        <label>
          <span>Carnet</span>
          <input name="carnet_prestatario" autocomplete="off" placeholder="N/A" />
        </label>
        <label>
          <span>Fecha inicio</span>
          <input name="fecha_inicio" required type="date" value="${todayISO()}" />
        </label>
        <label>
          <span>Fecha fin</span>
          <input name="fecha_fin" required type="date" />
        </label>
        ${renderEquipmentPicker()}
        <label class="full-field">
          <span>Observaciones</span>
          <textarea name="observaciones" rows="2"></textarea>
        </label>
        <div class="form-actions">
          <button class="primary-button" type="submit">Registrar recurrente</button>
        </div>
      </form>
    `;
  }

  return `
    <form class="form-grid loan-form" data-action="prestamo-libro">
      <label>
        <span>Nombre prestatario</span>
        <input name="nombre_prestatario" required autocomplete="off" />
      </label>
      <label>
        <span>Carnet</span>
        <input name="carnet_prestatario" autocomplete="off" placeholder="N/A" />
      </label>
      <label class="full-field">
        <span>Titulo libro</span>
        <input name="titulo_libro" list="documentosLibro" required autocomplete="off" />
        <datalist id="documentosLibro">
          ${state.data.documentos
            .filter((documento) => documento.tipo === "Libro")
            .map((documento) => `<option value="${escapeHtml(documento.titulo)}"></option>`)
            .join("")}
        </datalist>
      </label>
      <label>
        <span>Fecha prestamo</span>
        <input name="fecha_prestamo" required type="date" value="${todayISO()}" />
      </label>
      <label>
        <span>Fecha limite</span>
        <input name="fecha_limite" required type="date" />
      </label>
      <div class="form-actions">
        <button class="primary-button" type="submit">Registrar libro</button>
      </div>
    </form>
    ${renderAvailableBooks()}
  `;
}

function renderDevoluciones() {
  return `
    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Devoluciones</p>
          <h3>Registro de devolucion</h3>
        </div>
        ${renderSegmentedControl("return", state.returnMode, [
          ["libro", "Libro"],
          ["tesis", "Tesis"],
          ["equipoHoras", "Equipo por horas"],
          ["recurrente", "Recurrente"],
        ])}
      </div>
      ${renderReturnForm()}
    </section>

    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Pendientes</p>
          <h3>Prestamos abiertos</h3>
        </div>
        <span class="count-pill">${pendingLoans().length}</span>
      </div>
      ${renderPendingLoans()}
    </section>
  `;
}

function renderReturnForm() {
  if (state.returnMode === "tesis") {
    return renderThesisReturnForm();
  }
  if (state.returnMode === "equipoHoras") {
    return renderEquipmentHoursReturnForm();
  }
  if (state.returnMode === "recurrente") {
    return renderRecurrentReturnForm();
  }
  return renderBookReturnForm();
}

function renderBookReturnForm() {
  const pending = state.data.prestamos.filter((prestamo) => prestamo.estado !== "Devuelto");
  return `
    <form class="form-grid" data-action="devolucion-libro">
      <label class="full-field">
        <span>Prestamo</span>
        <select name="id_prestamo" required>
          <option value="">Seleccione prestamo</option>
          ${pending
            .map(
              (prestamo) =>
                `<option value="${prestamo.id}">${escapeHtml(loanLabel(prestamo))}</option>`,
            )
            .join("")}
        </select>
      </label>
      <label>
        <span>Fecha devolucion</span>
        <input name="fecha_devolucion" required type="date" value="${todayISO()}" />
      </label>
      <label class="checkbox-field">
        <input name="marcar_devuelto" type="checkbox" required />
        <span>Marcar como devuelto</span>
      </label>
      <div class="form-actions">
        <button class="primary-button" type="submit">Registrar devolucion</button>
      </div>
    </form>
  `;
}

function renderEquipmentHoursReturnForm() {
  const pending = state.data.prestamosEquipoHoras.filter(
    (prestamo) => prestamo.estado_prestamo !== "Devuelto",
  );
  return renderEquipmentReturnForm("devolucion-equipo-horas", "Prestamo equipo por horas", pending);
}

function renderRecurrentReturnForm() {
  const pending = state.data.prestamosEquipoRecurrente.filter(
    (prestamo) => prestamo.estado_prestamo !== "Devuelto",
  );
  return renderEquipmentReturnForm("devolucion-recurrente", "Prestamo recurrente", pending);
}

function renderEquipmentReturnForm(action, label, pending) {
  return `
    <form class="form-grid" data-action="${action}">
      <label class="full-field">
        <span>${label}</span>
        <select name="id_prestamo" required>
          <option value="">Seleccione prestamo</option>
          ${pending
            .map(
              (prestamo) =>
                `<option value="${prestamo.id}">${escapeHtml(equipmentLoanLabel(prestamo))}</option>`,
            )
            .join("")}
        </select>
      </label>
      <label>
        <span>Fecha devolucion</span>
        <input name="fecha_devolucion" required type="date" value="${todayISO()}" />
      </label>
      <label class="checkbox-field">
        <input name="marcar_devuelto" type="checkbox" required />
        <span>Marcar como devuelto</span>
      </label>
      <div class="form-actions">
        <button class="primary-button" type="submit">Registrar devolucion</button>
      </div>
    </form>
  `;
}

function renderThesisReturnForm() {
  const pending = state.data.prestamosTesis.filter(
    (prestamo) => prestamo.estado_prestamo !== "Devuelto",
  );
  return `
    <form class="form-grid" data-action="devolucion-tesis">
      <label class="full-field">
        <span>Prestamo tesis</span>
        <select name="id_prestamo" required>
          <option value="">Seleccione prestamo</option>
          ${pending
            .map(
              (prestamo) =>
                `<option value="${prestamo.id}">${escapeHtml(thesisLoanLabel(prestamo))}</option>`,
            )
            .join("")}
        </select>
      </label>
      <label>
        <span>Fecha devolucion</span>
        <input name="fecha_devolucion" required type="date" value="${todayISO()}" />
      </label>
      <label class="checkbox-field">
        <input name="marcar_devuelto" type="checkbox" required />
        <span>Marcar como devuelto</span>
      </label>
      <div class="form-actions">
        <button class="primary-button" type="submit">Registrar tesis</button>
      </div>
    </form>
  `;
}

function renderLevantamiento() {
  const filtro = normalizeText(state.physicalFilter);
  const equipos = state.data.equipos.filter((equipo) => {
    if (!filtro) return true;
    return normalizeText(equipo.numero_serie).includes(filtro);
  });

  return `
    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Auditoria fisica</p>
          <h3>Levantamiento de activos</h3>
        </div>
      </div>
      <form class="form-grid" data-action="levantamiento">
        <label>
          <span>Fecha</span>
          <input name="fecha_levantamiento" required type="date" value="${todayISO()}" />
        </label>
        <label>
          <span>Numero de serie</span>
          <input name="numero_serie" data-physical-filter value="${escapeHtml(state.physicalFilter)}" autocomplete="off" />
        </label>
        <label class="full-field">
          <span>Observaciones</span>
          <textarea name="observaciones" rows="2">Auditoria fisica de activos</textarea>
        </label>
        <div class="form-actions">
          <button class="primary-button" type="submit">Finalizar</button>
        </div>
      </form>
    </section>

    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Activos</p>
          <h3>Coincidencias</h3>
        </div>
        <span class="count-pill">${equipos.length}</span>
      </div>
      <div class="asset-list">
        ${
          equipos.length
            ? equipos.map(renderAssetRow).join("")
            : `<p class="empty-state">Sin activos para el filtro actual.</p>`
        }
      </div>
    </section>

    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Historial</p>
          <h3>Auditorias registradas</h3>
        </div>
        <span class="count-pill">${state.data.levantamientos.length}</span>
      </div>
      ${renderAuditHistory(state.data.levantamientos)}
    </section>
  `;
}

function renderBusqueda() {
  const query = normalizeText(state.search);
  const results = [];

  state.data.documentos.forEach((documento) => {
    const searchable = normalizeText(`${documento.titulo} ${documento.tipo} ${documento.isbn}`);
    if (!query || searchable.includes(query)) {
      results.push({
        kind: "Libro o tesis",
        title: documento.titulo,
        meta: `${documento.tipo} - ${documento.ejemplares || 0} ej.`,
      });
    }
  });

  state.data.equipos.forEach((equipo) => {
    const searchable = normalizeText(
      `${equipo.nombre} ${equipo.modelo} ${equipo.marca} ${equipo.numero_serie}`,
    );
    if (!query || searchable.includes(query)) {
      results.push({
        kind: "Hardware",
        title: equipo.nombre || `${equipo.marca} ${equipo.modelo}`,
        meta: `${equipo.modelo || "Modelo pendiente"} - ${equipo.estado_prestamo || "Disponible"}`,
      });
    }
  });

  return `
    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Busqueda</p>
          <h3>Hardware, libros y tesis</h3>
        </div>
        <span class="count-pill">${results.length}</span>
      </div>
      <label class="search-box">
        <span>Buscar</span>
        <input data-search value="${escapeHtml(state.search)}" autocomplete="off" />
      </label>
    </section>

    <section class="result-grid">
      ${
        results.length
          ? results.slice(0, 24).map(renderResultCard).join("")
          : `<p class="empty-state">Sin resultados.</p>`
      }
    </section>
  `;
}

async function handleClick(event) {
  const viewButton = event.target.closest("[data-view]");
  const loanModeButton = event.target.closest("[data-loan-mode]");
  const returnModeButton = event.target.closest("[data-return-mode]");
  const refreshButton = event.target.closest("[data-refresh]");
  const seedButton = event.target.closest("[data-seed-equipment]");
  const logoutButton = event.target.closest("[data-logout]");

  if (logoutButton) {
    state.authenticated = false;
    state.status = null;
    state.data = emptyStateData();
    state.lastSync = null;
    repository = null;
    sessionStorage.removeItem(SESSION_KEY);
    renderLogin();
    return;
  }

  if (viewButton) {
    state.view = viewButton.dataset.view;
    renderContent();
    return;
  }

  if (loanModeButton) {
    state.loanMode = loanModeButton.dataset.loanMode;
    renderContent();
    return;
  }

  if (returnModeButton) {
    state.returnMode = returnModeButton.dataset.returnMode;
    renderContent();
    return;
  }

  if (refreshButton) {
    await refreshData();
    return;
  }

  if (seedButton) {
    await seedInitialEquipment();
  }
}

async function handleSubmit(event) {
  const form = event.target.closest("form[data-action]");
  if (!form) return;

  event.preventDefault();
  setBusy(true);

  const handlers = {
    documento: handleDocumento,
    equipo: handleEquipo,
    "prestamo-libro": handlePrestamoLibro,
    "prestamo-tesis": handlePrestamoTesis,
    "prestamo-equipo-horas": handlePrestamoEquipoHoras,
    "prestamo-recurrente": handlePrestamoRecurrente,
    "devolucion-libro": handleDevolucionLibro,
    "devolucion-tesis": handleDevolucionTesis,
    "devolucion-equipo-horas": handleDevolucionEquipoHoras,
    "devolucion-recurrente": handleDevolucionRecurrente,
    levantamiento: handleLevantamiento,
  };

  try {
    await handlers[form.dataset.action](form);
    form.reset();
    await refreshData();
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(false);
  }
}

function handleInput(event) {
  if (event.target.matches("[data-search]")) {
    state.search = event.target.value;
    rerenderPreservingFocus("[data-search]", event.target.selectionStart);
  }

  if (event.target.matches("[data-physical-filter]")) {
    state.physicalFilter = event.target.value;
    rerenderPreservingFocus("[data-physical-filter]", event.target.selectionStart);
  }
}

async function handleDocumento(form) {
  const data = formData(form);
  const titulo = clean(data.titulo);
  const tipo = clean(data.tipo);
  const isbn = clean(data.isbn);
  const idioma = clean(data.idioma);
  const anio = toInteger(data.anio, 0);
  const ejemplares = toInteger(data.ejemplares, 1);

  if (!titulo || !tipo) {
    throw new Error("Ingrese titulo y tipo.");
  }

  const existing = state.data.documentos.find(
    (documento) =>
      normalizeText(documento.titulo) === normalizeText(titulo) &&
      normalizeText(documento.tipo) === normalizeText(tipo) &&
      normalizeText(documento.isbn) === normalizeText(isbn) &&
      Number(documento.anio || 0) === anio,
  );

  if (existing) {
    const stock = Number(existing.ejemplares || 0) + ejemplares;
    await repository.update(collections.documentos, existing.id, { ejemplares: stock });
    showToast(`Stock actualizado a ${stock}.`, "ok");
    return;
  }

  await repository.add(collections.documentos, {
    titulo,
    tipo,
    isbn,
    idioma,
    anio,
    ejemplares,
  });
  showToast("Documento registrado.", "ok");
}

async function handleEquipo(form) {
  const data = formData(form);
  const numeroSerie = clean(data.numero_serie);
  const marca = clean(data.marca);
  const modelo = clean(data.modelo);

  if (!numeroSerie || !marca || !modelo) {
    throw new Error("Complete numero de serie, marca y modelo.");
  }

  await repository.add(collections.equipos, {
    nombre: `${marca} ${modelo}`,
    numero_serie: numeroSerie,
    marca,
    modelo,
    ubicacion: clean(data.ubicacion),
    costo_unidad: toNumber(data.costo_unidad, 0),
    unidades: toInteger(data.unidades, 1),
    descripcion: clean(data.descripcion),
    estado_funcional: "Activo",
    estado_prestamo: "Disponible",
  });
  showToast("Equipo informatico agregado.", "ok");
}

async function handlePrestamoLibro(form) {
  const data = formData(form);
  const nombre = clean(data.nombre_prestatario);
  const carnet = clean(data.carnet_prestatario) || "N/A";
  const titulo = clean(data.titulo_libro);
  const fechaPrestamo = clean(data.fecha_prestamo);
  const fechaLimite = clean(data.fecha_limite);

  if (!nombre || !titulo || !fechaPrestamo || !fechaLimite) {
    throw new Error("Complete los campos obligatorios.");
  }

  let documento = state.data.documentos.find(
    (item) => normalizeText(item.tipo) === "libro" && normalizeText(item.titulo) === normalizeText(titulo),
  );
  let documentId = documento?.id;

  if (!documentId) {
    documentId = await repository.add(collections.documentos, {
      titulo,
      tipo: "Libro",
      isbn: "",
      idioma: "",
      anio: 0,
      ejemplares: 1,
    });
    documento = { id: documentId, ejemplares: 1 };
  }

  const ejemplaresActual = Number(documento?.ejemplares || 0);
  if (ejemplaresActual <= 0) {
    throw new Error("No hay ejemplares disponibles para este libro.");
  }
  await upsertPrestatario(carnet, nombre);
  await repository.update(collections.documentos, documentId, { ejemplares: ejemplaresActual - 1 });

  await repository.add(collections.prestamos, {
    carnet_prestatario: carnet,
    nombre_prestatario: nombre,
    id_documento: documentId,
    titulo_documento: titulo,
    fecha_prestamo: fechaPrestamo,
    fecha_limite: fechaLimite,
    estado: "Pendiente",
  });
  showToast("Prestamo de libro registrado. Ejemplares actualizados.", "ok");
}

async function handlePrestamoTesis(form) {
  const data = formData(form);
  const nombre = clean(data.nombre_prestatario);
  const carnet = clean(data.carnet_prestatario) || "N/A";
  const titulo = clean(data.titulo_tesis);
  const fechaPrestamo = clean(data.fecha_prestamo);
  const fechaDevolucion = clean(data.fecha_devolucion);

  if (!nombre || !titulo || !fechaPrestamo || !fechaDevolucion) {
    throw new Error("Complete los campos obligatorios.");
  }

  const documento = state.data.documentos.find(
    (d) => normalizeText(d.tipo) === "tesis" && normalizeText(d.titulo) === normalizeText(titulo),
  );

  if (!documento) {
    throw new Error("Tesis no encontrada en el catalogo. Registre la tesis antes de prestarla.");
  }

  const ejemplaresActual = Number(documento.ejemplares || 0);
  if (ejemplaresActual <= 0) {
    throw new Error("No hay ejemplares disponibles para esta tesis.");
  }

  await upsertPrestatario(carnet, nombre);
  const nuevosEjemplares = ejemplaresActual - 1;
  await repository.update(collections.documentos, documento.id, { ejemplares: nuevosEjemplares });

  await repository.add(collections.prestamosTesis, {
    nombre_prestatario: nombre,
    carnet_prestatario: carnet,
    codigo_tesis: "Automatico",
    titulo_tesis: titulo,
    fecha_prestamo: fechaPrestamo,
    fecha_devolucion: fechaDevolucion,
    estado_prestamo: "Pendiente",
    observaciones: clean(data.observaciones),
  });
  showToast("Prestamo de tesis registrado. Ejemplares actualizados.", "ok");
}

async function handlePrestamoEquipoHoras(form) {
  const data = formData(form);
  const equipos = selectedEquipment(form);
  const nombre = clean(data.nombre_prestatario);
  const carnet = clean(data.carnet_prestatario) || "N/A";

  if (!equipos.length) {
    throw new Error("Seleccione al menos un equipo.");
  }

  if (!nombre || !data.fecha_prestamo || !data.hora_inicio || !data.hora_fin) {
    throw new Error("Complete los campos obligatorios.");
  }

  const equiposObjs = equipos.map((e) => state.data.equipos.find((it) => it.id === e.id)).filter(Boolean);
  const unavailable = equiposObjs.filter((eq) => normalizeText(eq.estado_prestamo || "") !== "disponible");
  if (unavailable.length) {
    throw new Error(`Los siguientes equipos no estan disponibles: ${unavailable.map((e) => e.nombre).join(", ")}`);
  }

  await upsertPrestatario(carnet, nombre);
  await repository.add(collections.prestamosEquipoHoras, {
    nombre_prestatario: nombre,
    carnet_prestatario: carnet,
    equipo: equipos.map((equipo) => equipo.label).join("; "),
    equipos_ids: equipos.map((equipo) => equipo.id),
    fecha_prestamo: clean(data.fecha_prestamo),
    hora_inicio: clean(data.hora_inicio),
    hora_fin: clean(data.hora_fin),
    actividad: clean(data.actividad) || "Prestamo por horas",
    estado_prestamo: "Pendiente",
    observaciones: clean(data.observaciones),
  });
  await markEquipmentState(equipos.map((equipo) => equipo.id), "Prestado");
  showToast("Prestamo de equipo registrado. Equipos marcados como Prestado.", "ok");
}

async function handlePrestamoRecurrente(form) {
  const data = formData(form);
  const equipos = selectedEquipment(form);
  const nombre = clean(data.nombre_prestatario);
  const carnet = clean(data.carnet_prestatario) || "N/A";

  if (!equipos.length) {
    throw new Error("Seleccione al menos un equipo.");
  }

  if (!nombre || !data.fecha_inicio || !data.fecha_fin) {
    throw new Error("Complete los campos obligatorios.");
  }

  const equiposObjs = equipos.map((e) => state.data.equipos.find((it) => it.id === e.id)).filter(Boolean);
  const unavailable = equiposObjs.filter((eq) => normalizeText(eq.estado_prestamo || "") !== "disponible");
  if (unavailable.length) {
    throw new Error(`Los siguientes equipos no estan disponibles: ${unavailable.map((e) => e.nombre).join(", ")}`);
  }

  await upsertPrestatario(carnet, nombre);
  await repository.add(collections.prestamosEquipoRecurrente, {
    nombre_prestatario: nombre,
    carnet_prestatario: carnet,
    equipo: equipos.map((equipo) => equipo.label).join("; "),
    equipos_ids: equipos.map((equipo) => equipo.id),
    fecha_inicio: clean(data.fecha_inicio),
    fecha_fin: clean(data.fecha_fin),
    estado_prestamo: "Pendiente",
    observaciones: clean(data.observaciones),
  });
  await markEquipmentState(equipos.map((equipo) => equipo.id), "Prestado");
  showToast("Prestamo recurrente registrado. Equipos marcados como Prestado.", "ok");
}

async function handleDevolucionLibro(form) {
  const data = formData(form);
  const prestamo = state.data.prestamos.find((item) => item.id === data.id_prestamo);

  if (!prestamo) {
    throw new Error("Seleccione un prestamo valido.");
  }

  if (data.marcar_devuelto !== "on") {
    throw new Error("Marque la casilla de devolucion.");
  }

  await repository.add(collections.devoluciones, {
    id_prestamo: prestamo.id,
    marcar_devuelto: true,
    fecha_devolucion: clean(data.fecha_devolucion),
  });
  await repository.update(collections.prestamos, prestamo.id, { estado: "Devuelto" });

  // Incrementar ejemplares del documento asociado (si existe)
  try {
    if (prestamo.id_documento) {
      const doc = state.data.documentos.find((d) => d.id === prestamo.id_documento);
      if (doc) {
        const actuales = Number(doc.ejemplares || 0);
        await repository.update(collections.documentos, doc.id, { ejemplares: actuales + 1 });
      }
    }
  } catch (err) {
    // no bloquear la devolución por error en inventario
    console.warn("Error actualizando ejemplares al devolver libro:", err);
  }
  showToast("Devolucion registrada.", "ok");
}

async function handleDevolucionTesis(form) {
  const data = formData(form);
  const prestamo = state.data.prestamosTesis.find((item) => item.id === data.id_prestamo);

  if (!prestamo) {
    throw new Error("Seleccione un prestamo valido.");
  }

  if (data.marcar_devuelto !== "on") {
    throw new Error("Marque la casilla de devolucion.");
  }

  await repository.add(collections.devolucionesTesis, {
    id_prestamo: prestamo.id,
    titulo_tesis: prestamo.titulo_tesis,
    marcar_devuelto: true,
    fecha_devolucion: clean(data.fecha_devolucion),
  });
  await repository.update(collections.prestamosTesis, prestamo.id, {
    estado_prestamo: "Devuelto",
  });
  // Incrementar ejemplares de la tesis en el catalogo (si existe)
  try {
    const documento = state.data.documentos.find(
      (d) => normalizeText(d.tipo) === "tesis" && normalizeText(d.titulo) === normalizeText(prestamo.titulo_tesis),
    );
    if (documento) {
      const actuales = Number(documento.ejemplares || 0);
      await repository.update(collections.documentos, documento.id, { ejemplares: actuales + 1 });
    }
  } catch (err) {
    console.warn("Error actualizando ejemplares al devolver tesis:", err);
  }
  showToast("Devolucion de tesis registrada.", "ok");
}

async function handleDevolucionEquipoHoras(form) {
  await handleEquipmentReturn(
    form,
    collections.prestamosEquipoHoras,
    state.data.prestamosEquipoHoras,
    "Equipo por horas",
  );
}

async function handleDevolucionRecurrente(form) {
  await handleEquipmentReturn(
    form,
    collections.prestamosEquipoRecurrente,
    state.data.prestamosEquipoRecurrente,
    "Equipo recurrente",
  );
}

async function handleEquipmentReturn(form, loanCollection, loans, loanType) {
  const data = formData(form);
  const prestamo = loans.find((item) => item.id === data.id_prestamo);

  if (!prestamo) {
    throw new Error("Seleccione un prestamo valido.");
  }

  if (data.marcar_devuelto !== "on") {
    throw new Error("Marque la casilla de devolucion.");
  }

  await repository.add(collections.devoluciones, {
    id_prestamo: prestamo.id,
    tipo_prestamo: loanType,
    marcar_devuelto: true,
    fecha_devolucion: clean(data.fecha_devolucion),
  });
  await repository.update(loanCollection, prestamo.id, { estado_prestamo: "Devuelto" });
  await markEquipmentState(prestamo.equipos_ids || [], "Disponible");
  showToast("Devolucion de equipo registrada.", "ok");
}

async function handleLevantamiento(form) {
  const data = formData(form);
  const fecha = clean(data.fecha_levantamiento);
  const numeroSerie = clean(data.numero_serie);

  if (!fecha) {
    throw new Error("Seleccione la fecha de levantamiento.");
  }

  await repository.add(collections.levantamientos, {
    fecha_levantamiento: fecha,
    numero_serie: numeroSerie,
    observaciones: clean(data.observaciones) || "Auditoria fisica de activos",
  });

  const equipos = numeroSerie
    ? state.data.equipos.filter((equipo) => equipo.numero_serie === numeroSerie)
    : state.data.equipos;

  await Promise.all(
    equipos.map((equipo) =>
      repository.update(collections.equipos, equipo.id, {
        fecha_ultimo_levantamiento: fecha,
      }),
    ),
  );
  state.physicalFilter = "";
  showToast("Levantamiento fisico finalizado.", "ok");
}

async function markEquipmentState(equipmentIds, estadoPrestamo) {
  await Promise.all(
    equipmentIds.map((id) =>
      repository.update(collections.equipos, id, {
        estado_prestamo: estadoPrestamo,
      }),
    ),
  );
}

async function seedInitialEquipment() {
  setBusy(true);
  try {
    const existingSeries = new Set(
      state.data.equipos.map((equipo) => normalizeText(equipo.numero_serie)),
    );
    const pending = initialEquipos.filter(
      (equipo) => !existingSeries.has(normalizeText(equipo.numero_serie)),
    );

    await Promise.all(
      pending.map((equipo) => {
        const { id, ...value } = equipo;
        return repository.add(collections.equipos, value);
      }),
    );

    await refreshData();
    showToast(
      pending.length ? `${pending.length} equipos base cargados.` : "Equipos base ya existen.",
      "ok",
    );
  } catch (error) {
    showToast(error.message, "error");
  } finally {
    setBusy(false);
  }
}

async function upsertPrestatario(carnet, nombre) {
  const id = normalizeText(carnet || nombre).replace(/[^a-z0-9]+/g, "-") || crypto.randomUUID();
  await repository.upsert(collections.prestatarios, id, {
    carnet,
    nombre,
  });
}

function renderMetric(label, value, caption) {
  return `
    <article class="metric-card">
      <span>${escapeHtml(label)}</span>
      <strong>${value}</strong>
      <small>${escapeHtml(caption)}</small>
    </article>
  `;
}

function renderTimelineItem(item) {
  return `
    <article class="timeline-item">
      <span class="timeline-kind">${escapeHtml(item.kind)}</span>
      <div>
        <strong>${escapeHtml(item.title)}</strong>
        <small>${escapeHtml(item.meta)}</small>
      </div>
    </article>
  `;
}

function renderEquipmentRow(equipo) {
  return `
    <article class="compact-row">
      <div>
        <strong>${escapeHtml(equipo.nombre || `${equipo.marca} ${equipo.modelo}`)}</strong>
        <small>${escapeHtml(equipo.ubicacion || "Ubicacion pendiente")}</small>
      </div>
      <span class="state-badge ${badgeClass(equipo.estado_prestamo)}">${escapeHtml(equipo.estado_prestamo || "Disponible")}</span>
    </article>
  `;
}

function renderSeedRow(equipo) {
  return `
    <article class="compact-row">
      <div>
        <strong>${escapeHtml(equipo.nombre)}</strong>
        <small>${escapeHtml(`${equipo.numero_serie} - ${equipo.ubicacion}`)}</small>
      </div>
    </article>
  `;
}

function renderDocumentTable(documentos) {
  if (!documentos.length) {
    return `<p class="empty-state">Sin documentos registrados.</p>`;
  }

  return `
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Titulo</th>
            <th>Tipo</th>
            <th>ISBN</th>
            <th>Anio</th>
            <th>Ej.</th>
          </tr>
        </thead>
        <tbody>
          ${documentos
            .map(
              (documento) => `
                <tr>
                  <td>${escapeHtml(documento.titulo)}</td>
                  <td>${escapeHtml(documento.tipo)}</td>
                  <td>${escapeHtml(documento.isbn || "N/A")}</td>
                  <td>${escapeHtml(documento.anio || "0")}</td>
                  <td>${escapeHtml(documento.ejemplares || "0")}</td>
                </tr>
              `,
            )
            .join("")}
        </tbody>
      </table>
    </div>
  `;
}

function renderEquipmentTable(equipos) {
  if (!equipos.length) {
    return `<p class="empty-state">Sin equipos registrados.</p>`;
  }

  return `
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Equipo</th>
            <th>Serie</th>
            <th>Ubicacion</th>
            <th>Unid.</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          ${equipos
            .map(
              (equipo) => `
                <tr>
                  <td>
                    <strong>${escapeHtml(equipo.nombre || `${equipo.marca} ${equipo.modelo}`)}</strong>
                    <small>${escapeHtml(equipo.modelo || "")}</small>
                  </td>
                  <td>${escapeHtml(equipo.numero_serie || "N/A")}</td>
                  <td>${escapeHtml(equipo.ubicacion || "Pendiente")}</td>
                  <td>${escapeHtml(equipo.unidades || 1)}</td>
                  <td><span class="state-badge ${badgeClass(equipo.estado_prestamo)}">${escapeHtml(equipo.estado_prestamo || "Disponible")}</span></td>
                </tr>
              `,
            )
            .join("")}
        </tbody>
      </table>
    </div>
  `;
}

function renderAuditHistory(levantamientos) {
  if (!levantamientos.length) {
    return `<p class="empty-state">Sin auditorias registradas.</p>`;
  }

  const rows = [...levantamientos].sort((left, right) => {
    const leftDate = sortableDate(left.fecha_levantamiento || left.updatedAt || left.createdAt);
    const rightDate = sortableDate(right.fecha_levantamiento || right.updatedAt || right.createdAt);
    return rightDate - leftDate;
  });

  return `
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Fecha</th>
            <th>Alcance</th>
            <th>Observaciones</th>
          </tr>
        </thead>
        <tbody>
          ${rows
            .map(
              (levantamiento) => `
                <tr>
                  <td>${escapeHtml(formatDate(levantamiento.fecha_levantamiento))}</td>
                  <td>${escapeHtml(auditScope(levantamiento))}</td>
                  <td>${escapeHtml(levantamiento.observaciones || "Sin observaciones")}</td>
                </tr>
              `,
            )
            .join("")}
        </tbody>
      </table>
    </div>
  `;
}

function auditScope(levantamiento) {
  return levantamiento.numero_serie
    ? `Serie ${levantamiento.numero_serie}`
    : "Inventario completo";
}

function sortableDate(value) {
  const date = new Date(value || 0);
  const time = date.getTime();
  return Number.isNaN(time) ? 0 : time;
}

function renderEquipmentPicker() {
  if (!state.data.equipos.length) {
    return `<p class="empty-state full-field">Sin equipos registrados.</p>`;
  }

  return `
    <fieldset class="equipment-picker full-field">
      <legend>Equipos</legend>
      <div class="picker-grid">
        ${state.data.equipos
          .map(
            (equipo) => {
              const disabled = equipo.estado_prestamo && normalizeText(equipo.estado_prestamo) !== "disponible";
              return `
              <label>
                <input type="checkbox" name="equipos" value="${equipo.id}" ${disabled ? 'disabled' : ''} />
                <span>
                  <strong>${escapeHtml(equipo.nombre || `${equipo.marca} ${equipo.modelo}`)}</strong>
                  <small>${escapeHtml(equipo.numero_serie || equipo.modelo || "Sin serie")}</small>
                </span>
                <small class="box-sub">${escapeHtml(equipo.estado_prestamo || 'Disponible')}</small>
              </label>
            `;
            },
          )
          .join("")}
      </div>
    </fieldset>
  `;
}

function renderAvailableBooks() {
  const libros = state.data.documentos.filter((d) => d.tipo === "Libro");
  if (!libros.length) return "";
  const available = libros.filter((d) => Number(d.ejemplares || 0) > 0);
  return `
    <section class="work-panel">
      <div class="panel-heading">
        <div>
          <p class="eyebrow">Disponibilidad</p>
          <h3>Libros disponibles</h3>
        </div>
        <span class="count-pill">${available.length}</span>
      </div>
      <div class="box-grid staggered">
        ${libros
          .map(
            (doc) => `
              <article class="box">
                <div class="box-title">${escapeHtml(doc.titulo)}</div>
                <div class="box-sub">Ejemplares: ${escapeHtml(String(doc.ejemplares || 0))}</div>
                <div style="margin-top:8px"><span class="state-badge ${badgeClass(Number(doc.ejemplares || 0) > 0 ? 'Disponible' : 'Agotado')}">${escapeHtml(Number(doc.ejemplares || 0) > 0 ? 'Disponible' : 'Agotado')}</span></div>
              </article>
            `,
          )
          .join("")}
      </div>
    </section>
  `;
}

function renderThesisSelector() {
  const tesis = state.data.documentos.filter((d) => normalizeText(d.tipo) === "tesis");
  if (!tesis.length) {
    return `
      <label class="full-field">
        <span>Titulo tesis</span>
        <input name="titulo_tesis" required autocomplete="off" placeholder="No hay tesis registradas, ingrese titulo" />
      </label>
    `;
  }

  const available = tesis.filter((t) => Number(t.ejemplares || 0) > 0);
  return `
    <label class="full-field">
      <span>Titulo tesis</span>
      <select name="titulo_tesis" required>
        <option value="">Seleccione tesis</option>
        ${tesis
          .map(
            (t) =>
              `<option value="${escapeHtml(t.titulo)}" ${Number(t.ejemplares || 0) === 0 ? 'disabled' : ''}>${escapeHtml(
                t.titulo,
              )} (${escapeHtml(String(t.ejemplares || 0))} ej.)</option>`,
          )
          .join("")}
      </select>
      ${available.length === 0 ? `<small class="box-sub">No hay ejemplares disponibles</small>` : ``}
    </label>
  `;
}

function renderSegmentedControl(kind, activeValue, options) {
  const attr = kind === "loan" ? "data-loan-mode" : "data-return-mode";
  return `
    <div class="segmented-control" role="group">
      ${options
        .map(
          ([value, label]) => `
            <button class="${value === activeValue ? "active" : ""}" ${attr}="${value}" type="button">
              ${escapeHtml(label)}
            </button>
          `,
        )
        .join("")}
    </div>
  `;
}

function renderLoanHistory() {
  const rows = [
    ...state.data.prestamos.map((prestamo) => ({
      tipo: "Libro",
      titulo: prestamo.titulo_documento,
      prestatario: prestamo.nombre_prestatario,
      fecha: prestamo.fecha_prestamo,
      estado: prestamo.estado,
    })),
    ...state.data.prestamosTesis.map((prestamo) => ({
      tipo: "Tesis",
      titulo: prestamo.titulo_tesis,
      prestatario: prestamo.nombre_prestatario,
      fecha: prestamo.fecha_prestamo,
      estado: prestamo.estado_prestamo,
    })),
    ...state.data.prestamosEquipoHoras.map((prestamo) => ({
      tipo: "Equipo horas",
      titulo: prestamo.equipo,
      prestatario: prestamo.nombre_prestatario,
      fecha: prestamo.fecha_prestamo,
      estado: prestamo.estado_prestamo || "Pendiente",
    })),
    ...state.data.prestamosEquipoRecurrente.map((prestamo) => ({
      tipo: "Recurrente",
      titulo: prestamo.equipo,
      prestatario: prestamo.nombre_prestatario,
      fecha: prestamo.fecha_inicio,
      estado: prestamo.estado_prestamo || "Pendiente",
    })),
  ].slice(0, 12);

  if (!rows.length) {
    return `<p class="empty-state">Sin prestamos registrados.</p>`;
  }

  return `
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Tipo</th>
            <th>Detalle</th>
            <th>Prestatario</th>
            <th>Fecha</th>
            <th>Estado</th>
          </tr>
        </thead>
        <tbody>
          ${rows
            .map(
              (row) => `
                <tr>
                  <td>${escapeHtml(row.tipo)}</td>
                  <td>${escapeHtml(row.titulo || "N/A")}</td>
                  <td>${escapeHtml(row.prestatario || "N/A")}</td>
                  <td>${escapeHtml(formatDate(row.fecha))}</td>
                  <td><span class="state-badge ${badgeClass(row.estado)}">${escapeHtml(row.estado || "Pendiente")}</span></td>
                </tr>
              `,
            )
            .join("")}
        </tbody>
      </table>
    </div>
  `;
}

function renderPendingLoans() {
  const rows = pendingLoans();

  if (!rows.length) {
    return `<p class="empty-state">Sin prestamos abiertos.</p>`;
  }

  return `
    <div class="compact-list">
      ${rows
        .map(
          (loan) => `
            <article class="compact-row">
              <div>
                <strong>${escapeHtml(loan.title)}</strong>
                <small>${escapeHtml(`${loan.type} - ${loan.person} - ${formatDate(loan.date)}`)}</small>
              </div>
              <span class="state-badge pending">Pendiente</span>
            </article>
          `,
        )
        .join("")}
    </div>
  `;
}

function renderAssetRow(equipo) {
  return `
    <article class="asset-row">
      <div class="asset-icon" aria-hidden="true">${escapeHtml((equipo.marca || "EQ").slice(0, 2).toUpperCase())}</div>
      <div>
        <strong>${escapeHtml(equipo.nombre || `${equipo.marca} ${equipo.modelo}`)}</strong>
        <small>${escapeHtml(`${equipo.numero_serie || "Sin serie"} - ${equipo.ubicacion || "Ubicacion pendiente"}`)}</small>
      </div>
      <span>${escapeHtml(equipo.fecha_ultimo_levantamiento ? formatDate(equipo.fecha_ultimo_levantamiento) : "Sin fecha")}</span>
    </article>
  `;
}

function renderResultCard(result) {
  return `
    <article class="result-card">
      <span>${escapeHtml(result.kind)}</span>
      <strong>${escapeHtml(result.title)}</strong>
      <small>${escapeHtml(result.meta)}</small>
    </article>
  `;
}

function updateCounters() {
  const documents = state.data.documentos.length;
  const equipment = state.data.equipos.length;
  const pending = pendingLoans().length;
  const audits = state.data.levantamientos.length;
  setText("#countDocuments", documents);
  setText("#countEquipment", equipment);
  setText("#countPending", pending);
  setText("#countAudits", audits);
  setText(
    "#syncStatus",
    state.lastSync ? `Actualizado ${state.lastSync.toLocaleTimeString("es-SV")}` : "Sin sincronizar",
  );
}

function setBusy(isBusy) {
  state.busy = isBusy;
  document.body.classList.toggle("is-busy", isBusy);
}

function showToast(message, tone = "ok") {
  const toast = document.querySelector("#toast");
  if (!toast) return;
  toast.textContent = message;
  toast.dataset.tone = tone;
  toast.classList.add("visible");
  window.clearTimeout(showToast.timer);
  showToast.timer = window.setTimeout(() => toast.classList.remove("visible"), 2800);
}

function recentActivity() {
  return [
    ...state.data.documentos.map((item) => ({
      kind: item.tipo || "Documento",
      title: item.titulo,
      meta: `${item.ejemplares || 0} ejemplares`,
      date: item.updatedAt || item.createdAt,
    })),
    ...state.data.equipos.map((item) => ({
      kind: "Equipo",
      title: item.nombre || `${item.marca} ${item.modelo}`,
      meta: item.numero_serie || "Sin serie",
      date: item.updatedAt || item.createdAt,
    })),
    ...state.data.prestamos.map((item) => ({
      kind: "Prestamo libro",
      title: item.titulo_documento,
      meta: item.nombre_prestatario,
      date: item.updatedAt || item.createdAt,
    })),
    ...state.data.prestamosTesis.map((item) => ({
      kind: "Prestamo tesis",
      title: item.titulo_tesis,
      meta: item.nombre_prestatario,
      date: item.updatedAt || item.createdAt,
    })),
  ]
    .sort((left, right) => new Date(right.date || 0) - new Date(left.date || 0))
    .slice(0, 8);
}

function pendingLoans() {
  return [
    ...state.data.prestamos
      .filter((prestamo) => prestamo.estado !== "Devuelto")
      .map((prestamo) => ({
        id: prestamo.id,
        type: "Libro",
        title: prestamo.titulo_documento,
        person: prestamo.nombre_prestatario,
        date: prestamo.fecha_limite,
      })),
    ...state.data.prestamosTesis
      .filter((prestamo) => prestamo.estado_prestamo !== "Devuelto")
      .map((prestamo) => ({
        id: prestamo.id,
        type: "Tesis",
        title: prestamo.titulo_tesis,
        person: prestamo.nombre_prestatario,
        date: prestamo.fecha_devolucion,
      })),
    ...state.data.prestamosEquipoHoras
      .filter((prestamo) => prestamo.estado_prestamo !== "Devuelto")
      .map((prestamo) => ({
        id: prestamo.id,
        type: "Equipo horas",
        title: prestamo.equipo,
        person: prestamo.nombre_prestatario,
        date: prestamo.fecha_prestamo,
      })),
    ...state.data.prestamosEquipoRecurrente
      .filter((prestamo) => prestamo.estado_prestamo !== "Devuelto")
      .map((prestamo) => ({
        id: prestamo.id,
        type: "Recurrente",
        title: prestamo.equipo,
        person: prestamo.nombre_prestatario,
        date: prestamo.fecha_fin,
      })),
  ];
}

function selectedEquipment(form) {
  const selectedIds = new FormData(form).getAll("equipos");
  return selectedIds
    .map((id) => {
      const equipo = state.data.equipos.find((item) => item.id === id);
      if (!equipo) return null;
      return {
        id,
        label: `${equipo.nombre || `${equipo.marca} ${equipo.modelo}`} - Modelo ${equipo.modelo}`,
      };
    })
    .filter(Boolean);
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function loanLabel(prestamo) {
  return `${prestamo.titulo_documento || "Libro"} - ${prestamo.nombre_prestatario || "N/A"} - ${formatDate(prestamo.fecha_prestamo)}`;
}

function thesisLoanLabel(prestamo) {
  return `${prestamo.titulo_tesis || "Tesis"} - ${prestamo.nombre_prestatario || "N/A"} - ${formatDate(prestamo.fecha_prestamo)}`;
}

function equipmentLoanLabel(prestamo) {
  return `${prestamo.equipo || "Equipo"} - ${prestamo.nombre_prestatario || "N/A"} - ${formatDate(prestamo.fecha_prestamo || prestamo.fecha_inicio)}`;
}

function badgeClass(value = "") {
  const normalized = normalizeText(value);
  if (normalized.includes("devuelto") || normalized.includes("disponible")) return "ok";
  if (normalized.includes("pendiente") || normalized.includes("prestado")) return "pending";
  return "neutral";
}

function rerenderPreservingFocus(selector, selectionStart = 0) {
  renderContent();
  const nextInput = document.querySelector(selector);
  if (nextInput) {
    nextInput.focus();
    nextInput.setSelectionRange(selectionStart, selectionStart);
  }
}

function setText(selector, value) {
  const element = document.querySelector(selector);
  if (element) {
    element.textContent = value;
  }
}

function clean(value) {
  return String(value || "").trim();
}

function toInteger(value, fallback) {
  const parsed = Number.parseInt(value, 10);
  return Number.isFinite(parsed) ? parsed : fallback;
}

function toNumber(value, fallback) {
  const parsed = Number.parseFloat(String(value || "").replace(",", "."));
  return Number.isFinite(parsed) ? parsed : fallback;
}

function normalizeText(value = "") {
  return String(value)
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .trim()
    .toLowerCase();
}

function formatDate(value) {
  if (!value) return "N/A";
  if (/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    const [year, month, day] = value.split("-");
    return `${day}/${month}/${year}`;
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString("es-SV", { year: "numeric", month: "2-digit", day: "2-digit" });
}

function todayISO() {
  return new Date().toISOString().slice(0, 10);
}

function escapeHtml(value = "") {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function emptyStateData() {
  return Object.fromEntries(Object.keys(collections).map((key) => [key, []]));
}
