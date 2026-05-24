package com.grupo1.sgi_fia.utils;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.grupo1.sgi_fia.model.Inventario;
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
public final class InventarioDao_Impl implements InventarioDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Inventario> __insertAdapterOfInventario;

  public InventarioDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfInventario = new EntityInsertAdapter<Inventario>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `inventario` (`id`,`fecha`,`descripcion`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Inventario entity) {
        statement.bindLong(1, entity.id);
        if (entity.fecha == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.fecha);
        }
        if (entity.descripcion == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.descripcion);
        }
      }
    };
  }

  @Override
  public void insert(final Inventario inventario) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfInventario.insert(_connection, inventario);
      return null;
    });
  }

  @Override
  public List<Inventario> getAll() {
    final String _sql = "SELECT * FROM inventario";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfFecha = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fecha");
        final int _columnIndexOfDescripcion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "descripcion");
        final List<Inventario> _result = new ArrayList<Inventario>();
        while (_stmt.step()) {
          final Inventario _item;
          _item = new Inventario();
          _item.id = (int) (_stmt.getLong(_columnIndexOfId));
          if (_stmt.isNull(_columnIndexOfFecha)) {
            _item.fecha = null;
          } else {
            _item.fecha = _stmt.getText(_columnIndexOfFecha);
          }
          if (_stmt.isNull(_columnIndexOfDescripcion)) {
            _item.descripcion = null;
          } else {
            _item.descripcion = _stmt.getText(_columnIndexOfDescripcion);
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
