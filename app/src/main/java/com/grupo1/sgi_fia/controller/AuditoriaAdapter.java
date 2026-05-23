package com.grupo1.sgi_fia.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grupo1.sgi_fia.R;
import java.util.List;

public class AuditoriaAdapter extends RecyclerView.Adapter<AuditoriaAdapter.AuditoriaViewHolder> {

    private List<Auditoria> listaAuditorias;

    public AuditoriaAdapter(List<Auditoria> listaAuditorias) {
        this.listaAuditorias = listaAuditorias;
    }

    @NonNull
    @Override
    public AuditoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auditoria, parent, false);
        return new AuditoriaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AuditoriaViewHolder holder, int position) {
        Auditoria auditoria = listaAuditorias.get(position);

        holder.txtSerie.setText("Serie: " + auditoria.getNumeroSerie());
        holder.txtEstado.setText(auditoria.getEstadoConfirmado().toUpperCase());
        holder.txtDetalleEquipo.setText("Equipo: " + auditoria.getMarcaModelo() + " | Resp: " + auditoria.getResponsable());
        holder.txtUbicacionPeriodo.setText("Lugar: " + auditoria.getUbicacionReal() + " • " + auditoria.getPeriodo());

        if (auditoria.getObservaciones() == null || auditoria.getObservaciones().isEmpty()) {
            holder.txtObservaciones.setText("Obs: Sin observaciones.");
        } else {
            holder.txtObservaciones.setText("Obs: " + auditoria.getObservaciones());
        }

        if (auditoria.getEstadoConfirmado().equalsIgnoreCase("Encontrado")) {
            holder.txtEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.txtEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return listaAuditorias.size();
    }

    public static class AuditoriaViewHolder extends RecyclerView.ViewHolder {
        TextView txtSerie, txtEstado, txtDetalleEquipo, txtUbicacionPeriodo, txtObservaciones;

        public AuditoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSerie = itemView.findViewById(R.id.item_txt_serie);
            txtEstado = itemView.findViewById(R.id.item_txt_estado);
            txtDetalleEquipo = itemView.findViewById(R.id.item_txt_detalle_equipo);
            txtUbicacionPeriodo = itemView.findViewById(R.id.item_txt_ubicacion_periodo);
            txtObservaciones = itemView.findViewById(R.id.item_txt_observaciones);
        }
    }
}