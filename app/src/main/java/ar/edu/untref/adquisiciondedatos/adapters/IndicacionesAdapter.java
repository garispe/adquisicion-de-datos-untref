package ar.edu.untref.adquisiciondedatos.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.adquisiciondedatos.R;
import ar.edu.untref.adquisiciondedatos.modelos.Indicacion;

/**
 * Created by garispe on 9/13/16.
 */
public class IndicacionesAdapter extends RecyclerView.Adapter<IndicacionesAdapter.ViewHolder> {

    private List<Indicacion> indicaciones = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_indicacion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Indicacion indicacion = indicaciones.get(position);

        holder.angulos.setText(String.format("Angulos: %sº", String.valueOf(indicacion.getAngulos())));
        holder.segundos.setText(String.format("Segundos: %s''", String.valueOf(indicacion.getSegundos())));
    }

    @Override
    public int getItemCount() {
        return indicaciones.size();
    }

    public void setIndicaciones(List<Indicacion> indicaciones) {
        this.indicaciones = indicaciones;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView angulos;
        TextView segundos;

        public ViewHolder(View itemView) {
            super(itemView);

            angulos = (TextView) itemView.findViewById(R.id.angulos);
            segundos = (TextView) itemView.findViewById(R.id.segundos);
        }
    }
}
