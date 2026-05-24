package com.grupo1.sgi_fia.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo1.sgi_fia.R;
import com.grupo1.sgi_fia.model.Equipo;

import java.util.List;

public class EquipoAdapter extends RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Equipo equipo);
    }

    private List<Equipo> equipos;
    private OnItemClickListener listener;

    public EquipoAdapter(List<Equipo> equipos, OnItemClickListener listener) {
        this.equipos = equipos;
        this.listener = listener;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipo, parent, false);
        return new EquipoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        Equipo equipo = equipos.get(position);
        holder.tvNombre.setText(equipo.nombre); // Usando campo SQL
        holder.tvUbicacion.setText(equipo.ubicacion); // Usando campo UI
        holder.itemView.setOnClickListener(v -> listener.onItemClick(equipo));
    }

    @Override
    public int getItemCount() {
        return equipos != null ? equipos.size() : 0;
    }

    static class EquipoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUbicacion;

        public EquipoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvEquipoNombre);
            tvUbicacion = itemView.findViewById(R.id.tvEquipoUbicacion);
        }
    }
}