package com.grupo1.sgi_fia.utils;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.grupo1.sgi_fia.model.Equipo;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class EquipoDao_Impl implements EquipoDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Equipo> __insertAdapterOfEquipo;

  private final EntityDeleteOrUpdateAdapter<Equipo> __deleteAdapterOfEquipo;

  private final EntityDeleteOrUpdateAdapter<Equipo> __updateAdapterOfEquipo;

  public EquipoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfEquipo = new EntityInsertAdapter<Equipo>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `equipos` (`id`,`nombre`,`clasificacion`,`estado`,`unidad_id`,`numero_serie`,`marca`,`modelo`,`ubicacion`,`costo_unidad`,`unidades`,`descripcion`,`fecha_ultimo_levantamiento`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Equipo entity) {
        statement.bindLong(1, entity.id);
        if (entity.nombre == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.nombre);
        }
        if (entity.clasificacion == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.clasificacion);
        }
        if (entity.estado == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.estado);
        }
        statement.bindLong(5, entity.unidad_id);
        if (entity.numero_serie == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.numero_serie);
        }
        if (entity.marca == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.marca);
        }
        if (entity.modelo == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.modelo);
        }
        if (entity.ubicacion == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.ubicacion);
        }
        statement.bindDouble(10, entity.costo_unidad);
        statement.bindLong(11, entity.unidades);
        if (entity.descripcion == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.descripcion);
        }
        if (entity.fecha_ultimo_levantamiento == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.fecha_ultimo_levantamiento);
        }
      }
    };
    this.__deleteAdapterOfEquipo = new EntityDeleteOrUpdateAdapter<Equipo>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `equipos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Equipo entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfEquipo = new EntityDeleteOrUpdateAdapter<Equipo>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `equipos` SET `id` = ?,`nombre` = ?,`clasificacion` = ?,`estado` = ?,`unidad_id` = ?,`numero_serie` = ?,`marca` = ?,`modelo` = ?,`ubicacion` = ?,`costo_unidad` = ?,`unidades` = ?,`descripcion` = ?,`fecha_ultimo_levantamiento` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Equipo entity) {
        statement.bindLong(1, entity.id);
        if (entity.nombre == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.nombre);
        }
        if (entity.clasificacion == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.clasificacion);
        }
        if (entity.estado == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.estado);
        }
        statement.bindLong(5, entity.unidad_id);
        if (entity.numero_serie == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.numero_serie);
        }
        if (entity.marca == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.marca);
        }
        if (entity.modelo == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.modelo);
        }
        if (entity.ubicacion == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.ubicacion);
        }
        statement.bindDouble(10, entity.costo_unidad);
        statement.bindLong(11, entity.unidades);
        if (entity.descripcion == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.descripcion);
        }
        if (entity.fecha_ultimo_levantamiento == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.fecha_ultimo_levantamiento);
        }
        statement.bindLong(14, entity.id);
      }
    };
  }

  @Override
  public void insert(final Equipo equipo) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfEquipo.insert(_connection, equipo);
      return null;
    });
  }

  @Override
  public void delete(final Equipo equipo) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfEquipo.handle(_connection, equipo);
      return null;
    });
  }

  @Override
  public void update(final Equipo equipo) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfEquipo.handle(_connection, equipo);
      return null;
    });
  }

  @Override
  public List<Equipo> getAll() {
    final String _sql = "SELECT * FROM equipos";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNombre = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nombre");
        final int _columnIndexOfClasificacion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "clasificacion");
        final int _columnIndexOfEstado = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "estado");
        final int _columnIndexOfUnidadId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "unidad_id");
        final int _columnIndexOfNumeroSerie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "numero_serie");
        final int _columnIndexOfMarca = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "marca");
        final int _columnIndexOfModelo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "modelo");
        final int _columnIndexOfUbicacion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ubicacion");
        final int _columnIndexOfCostoUnidad = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "costo_unidad");
        final int _columnIndexOfUnidades = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "unidades");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final int _columnIndexOfFechaUltimoLevantamiento = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fecha_ultimo_levantamiento");
        final List<Equipo> _result = new ArrayList<Equipo>();
        while (_stmt.step()) {
          final Equipo _item;
          _item = new Equipo();
          _item.id = (int) (_stmt.getLong(_columnIndexOfId));
          if (_stmt.isNull(_columnIndexOfNombre)) {
            _item.nombre = null;
          } else {
            _item.nombre = _stmt.getText(_columnIndexOfNombre);
          }
          if (_stmt.isNull(_columnIndexOfClasificacion)) {
            _item.clasificacion = null;
          } else {
            _item.clasificacion = _stmt.getText(_columnIndexOfClasificacion);
          }
          if (_stmt.isNull(_columnIndexOfEstado)) {
            _item.estado = null;
          } else {
            _item.estado = _stmt.getText(_columnIndexOfEstado);
          }
          _item.unidad_id = (int) (_stmt.getLong(_columnIndexOfUnidadId));
          if (_stmt.isNull(_columnIndexOfNumeroSerie)) {
            _item.numero_serie = null;
          } else {
            _item.numero_serie = _stmt.getText(_columnIndexOfNumeroSerie);
          }
          if (_stmt.isNull(_columnIndexOfMarca)) {
            _item.marca = null;
          } else {
            _item.marca = _stmt.getText(_columnIndexOfMarca);
          }
          if (_stmt.isNull(_columnIndexOfModelo)) {
            _item.modelo = null;
          } else {
            _item.modelo = _stmt.getText(_columnIndexOfModelo);
          }
          if (_stmt.isNull(_columnIndexOfUbicacion)) {
            _item.ubicacion = null;
          } else {
            _item.ubicacion = _stmt.getText(_columnIndexOfUbicacion);
          }
          _item.costo_unidad = _stmt.getDouble(_columnIndexOfCostoUnidad);
          _item.unidades = (int) (_stmt.getLong(_columnIndexOfUnidades));
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _item.descripcion = null;
          } else {
            _item.descripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          if (_stmt.isNull(_columnIndexOfFechaUltimoLevantamiento)) {
            _item.fecha_ultimo_levantamiento = null;
          } else {
            _item.fecha_ultimo_levantamiento = _stmt.getText(_columnIndexOfFechaUltimoLevantamiento);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Equipo getById(final int id) {
    final String _sql = "SELECT * FROM equipos WHERE id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNombre = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nombre");
        final int _columnIndexOfClasificacion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "clasificacion");
        final int _columnIndexOfEstado = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "estado");
        final int _columnIndexOfUnidadId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "unidad_id");
        final int _columnIndexOfNumeroSerie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "numero_serie");
        final int _columnIndexOfMarca = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "marca");
        final int _columnIndexOfModelo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "modelo");
        final int _columnIndexOfUbicacion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ubicacion");
        final int _columnIndexOfCostoUnidad = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "costo_unidad");
        final int _columnIndexOfUnidades = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "unidades");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final int _columnIndexOfFechaUltimoLevantamiento = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fecha_ultimo_levantamiento");
        final Equipo _result;
        if (_stmt.step()) {
          _result = new Equipo();
          _result.id = (int) (_stmt.getLong(_columnIndexOfId));
          if (_stmt.isNull(_columnIndexOfNombre)) {
            _result.nombre = null;
          } else {
            _result.nombre = _stmt.getText(_columnIndexOfNombre);
          }
          if (_stmt.isNull(_columnIndexOfClasificacion)) {
            _result.clasificacion = null;
          } else {
            _result.clasificacion = _stmt.getText(_columnIndexOfClasificacion);
          }
          if (_stmt.isNull(_columnIndexOfEstado)) {
            _result.estado = null;
          } else {
            _result.estado = _stmt.getText(_columnIndexOfEstado);
          }
          _result.unidad_id = (int) (_stmt.getLong(_columnIndexOfUnidadId));
          if (_stmt.isNull(_columnIndexOfNumeroSerie)) {
            _result.numero_serie = null;
          } else {
            _result.numero_serie = _stmt.getText(_columnIndexOfNumeroSerie);
          }
          if (_stmt.isNull(_columnIndexOfMarca)) {
            _result.marca = null;
          } else {
            _result.marca = _stmt.getText(_columnIndexOfMarca);
          }
          if (_stmt.isNull(_columnIndexOfModelo)) {
            _result.modelo = null;
          } else {
            _result.modelo = _stmt.getText(_columnIndexOfModelo);
          }
          if (_stmt.isNull(_columnIndexOfUbicacion)) {
            _result.ubicacion = null;
          } else {
            _result.ubicacion = _stmt.getText(_columnIndexOfUbicacion);
          }
          _result.costo_unidad = _stmt.getDouble(_columnIndexOfCostoUnidad);
          _result.unidades = (int) (_stmt.getLong(_columnIndexOfUnidades));
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _result.descripcion = null;
          } else {
            _result.descripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          if (_stmt.isNull(_columnIndexOfFechaUltimoLevantamiento)) {
            _result.fecha_ultimo_levantamiento = null;
          } else {
            _result.fecha_ultimo_levantamiento = _stmt.getText(_columnIndexOfFechaUltimoLevantamiento);
          }
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Equipo> searchBySerie(final String serie) {
    final String _sql = "SELECT * FROM equipos WHERE numero_serie LIKE ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (serie == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, serie);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfNombre = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nombre");
        final int _columnIndexOfClasificacion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "clasificacion");
        final int _columnIndexOfEstado = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "estado");
        final int _columnIndexOfUnidadId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "unidad_id");
        final int _columnIndexOfNumeroSerie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "numero_serie");
        final int _columnIndexOfMarca = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "marca");
        final int _columnIndexOfModelo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "modelo");
        final int _columnIndexOfUbicacion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ubicacion");
        final int _columnIndexOfCostoUnidad = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "costo_unidad");
        final int _columnIndexOfUnidades = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "unidades");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final int _columnIndexOfFechaUltimoLevantamiento = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fecha_ultimo_levantamiento");
        final List<Equipo> _result = new ArrayList<Equipo>();
        while (_stmt.step()) {
          final Equipo _item;
          _item = new Equipo();
          _item.id = (int) (_stmt.getLong(_columnIndexOfId));
          if (_stmt.isNull(_columnIndexOfNombre)) {
            _item.nombre = null;
          } else {
            _item.nombre = _stmt.getText(_columnIndexOfNombre);
          }
          if (_stmt.isNull(_columnIndexOfClasificacion)) {
            _item.clasificacion = null;
          } else {
            _item.clasificacion = _stmt.getText(_columnIndexOfClasificacion);
          }
          if (_stmt.isNull(_columnIndexOfEstado)) {
            _item.estado = null;
          } else {
            _item.estado = _stmt.getText(_columnIndexOfEstado);
          }
          _item.unidad_id = (int) (_stmt.getLong(_columnIndexOfUnidadId));
          if (_stmt.isNull(_columnIndexOfNumeroSerie)) {
            _item.numero_serie = null;
          } else {
            _item.numero_serie = _stmt.getText(_columnIndexOfNumeroSerie);
          }
          if (_stmt.isNull(_columnIndexOfMarca)) {
            _item.marca = null;
          } else {
            _item.marca = _stmt.getText(_columnIndexOfMarca);
          }
          if (_stmt.isNull(_columnIndexOfModelo)) {
            _item.modelo = null;
          } else {
            _item.modelo = _stmt.getText(_columnIndexOfModelo);
          }
          if (_stmt.isNull(_columnIndexOfUbicacion)) {
            _item.ubicacion = null;
          } else {
            _item.ubicacion = _stmt.getText(_columnIndexOfUbicacion);
          }
          _item.costo_unidad = _stmt.getDouble(_columnIndexOfCostoUnidad);
          _item.unidades = (int) (_stmt.getLong(_columnIndexOfUnidades));
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _item.descripcion = null;
          } else {
            _item.descripcion = _stmt.getText(_columnIndexOfDescripcion);
          }
          if (_stmt.isNull(_columnIndexOfFechaUltimoLevantamiento)) {
            _item.fecha_ultimo_levantamiento = null;
          } else {
            _item.fecha_ultimo_levantamiento = _stmt.getText(_columnIndexOfFechaUltimoLevantamiento);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
