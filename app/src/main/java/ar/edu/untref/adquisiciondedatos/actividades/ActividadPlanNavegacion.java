package ar.edu.untref.adquisiciondedatos.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.edu.untref.adquisiciondedatos.R;
import ar.edu.untref.adquisiciondedatos.adapters.IndicacionesAdapter;
import ar.edu.untref.adquisiciondedatos.modelos.Indicacion;
import ar.edu.untref.adquisiciondedatos.utilidades.Constantes;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActividadPlanNavegacion extends AppCompatActivity {

    @Bind(R.id.recycler_indicaciones)
    RecyclerView recyclerView;

    private ArrayList<Indicacion> indicaciones = new ArrayList<>();
    private IndicacionesAdapter indicacionesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_plan_navegacion);
        ButterKnife.bind(this);

        indicacionesAdapter = new IndicacionesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(indicacionesAdapter);
    }

    @OnClick(R.id.fab_nueva_indicacion)
    public void agregarIndicacion() {

        // TODO: Dialogo para agregar indicacion

//        indicaciones.add(indicacion);
//        indicacionesAdapter.setIndicaciones(indicaciones);
    }

    @OnClick(R.id.comenzar)
    public void comenzarPlanNavegacion(){

        Intent intent = new Intent();
        intent.putExtra(Constantes.INDICACIONES, indicaciones);
        setResult(Constantes.RESULT_CODE_PLAN_NAVEGACION);
        finish();
    }
}
