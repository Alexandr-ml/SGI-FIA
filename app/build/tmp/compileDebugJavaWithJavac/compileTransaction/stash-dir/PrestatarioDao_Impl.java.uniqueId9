package com.grupo1.sgi_fia.utils;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.grupo1.sgi_fia.model.Prestatario;
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
public final class PrestatarioDao_Impl implements PrestatarioDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Prestatario> __insertAdapterOfPrestatario;

  private final EntityDeleteOrUpdateAdapter<Prestatario> __deleteAdapterOfPrestatario;

  private final EntityDeleteOrUpdateAdapter<Prestatario> __updateAdapterOfPrestatario;

  public PrestatarioDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfPrestatario = new EntityInsertAdapter<Prestatario>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `prestatario` (`id_prestatario`,`carnet`,`nombre`,`apellido`,`correo`,`telefono`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Prestatario entity) {
        statement.bindLong(1, entity.id_prestatario);
        if (entity.carnet == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.carnet);
        }
        if (entity.nombre == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.nombre);
        }
        if (entity.apellido == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.apellido);
        }
        if (entity.correo == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.correo);
        }
        if (entity.telefono == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.telefono);
        }
      }
    };
    this.__deleteAdapterOfPrestatario = new EntityDeleteOrUpdateAdapter<Prestatario>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `prestatario` WHERE `id_prestatario` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Prestatario entity) {
        statement.bindLong(1, entity.id_prestatario);
      }
    };
    this.__updateAdapterOfPrestatario = new EntityDeleteOrUpdateAdapter<Prestatario>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `prestatario` SET `id_prestatario` = ?,`carnet` = ?,`nombre` = ?,`apellido` = ?,`correo` = ?,`telefono` = ? WHERE `id_prestatario` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Prestatario entity) {
        statement.bindLong(1, entity.id_prestatario);
        if (entity.carnet == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.carnet);
        }
        if (entity.nombre == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.nombre);
        }
        if (entity.apellido == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.apellido);
        }
        if (entity.correo == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.correo);
        }
        if (entity.telefono == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.telefono);
        }
        statement.bindLong(7, entity.id_prestatario);
      }
    };
  }

  @Override
  public void insert(final Prestatario prestatario) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfPrestatario.insert(_connection, prestatario);
      return null;
    });
  }

  @Override
  public void delete(final Prestatario prestatario) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfPrestatario.handle(_connection, prestatario);
      return null;
    });
  }

  @Override
  public void update(final Prestatario prestatario) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfPrestatario.handle(_connection, prestatario);
      return null;
    });
  }

  @Override
  public List<Prestatario> getAll() {
    final String _sql = "SELECT * FROM prestatario";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfIdPrestatario = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id_prestatario");
        final int _columnIndexOfCarnet = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "carnet");
        final int _columnIndexOfNombre = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nombre");
        final int _columnIndexOfApellido = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "apellido");
        final int _columnIndexOfCorreo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "correo");
        final int _columnIndexOfTelefono = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "telefono");
        final List<Prestatario> _result = new ArrayList<Prestatario>();
        while (_stmt.step()) {
          final Prestatario _item;
          _item = new Prestatario();
          _item.id_prestatario = (int) (_stmt.getLong(_columnIndexOfIdPrestatario));
          if (_stmt.isNull(_columnIndexOfCarnet)) {
            _item.carnet = null;
          } else {
            _item.carnet = _stmt.getText(_columnIndexOfCarnet);
          }
          if (_stmt.isNull(_columnIndexOfNombre)) {
            _item.nombre = null;
          } else {
            _item.nombre = _stmt.getText(_columnIndexOfNombre);
          }
          if (_stmt.isNull(_columnIndexOfApellido)) {
            _item.apellido = null;
          } else {
            _item.apellido = _stmt.getText(_columnIndexOfApellido);
          }
          if (_stmt.isNull(_columnIndexOfCorreo)) {
            _item.correo = null;
          } else {
            _item.correo = _stmt.getText(_columnIndexOfCorreo);
          }
          if (_stmt.isNull(_columnIndexOfTelefono)) {
            _item.telefono = null;
          } else {
            _item.telefono = _stmt.getText(_columnIndexOfTelefono);
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
