package ar.edu.untref.adquisiciondedatos.actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;

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

    private EditText editAngulos;
    private EditText editSegundos;

    @OnClick(R.id.fab_nueva_indicacion)
    public void agregarIndicacion() {

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Nueva indicación");

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialogo_nueva_indicacion, null);
        dialogo.setView(view);

        editAngulos = (EditText) view.findViewById(R.id.angulos);
        editSegundos = (EditText) view.findViewById(R.id.segundos);

        dialogo.setPositiveButton(R.string.agregar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String angulosIndicados = editAngulos.getText().toString();
                String segundosIndicados = editSegundos.getText().toString();

                if (!angulosIndicados.isEmpty() && !segundosIndicados.isEmpty()) {

                    float angulos = Float.valueOf(angulosIndicados);
                    int segundos = Integer.valueOf(segundosIndicados);

                    Indicacion indicacion = new Indicacion();
                    indicacion.setAngulos(angulos);
                    indicacion.setSegundos(segundos);

                    indicaciones.add(indicacion);
                    indicacionesAdapter.agregarIndicacion(indicacion);

                    ocultarTeclado();
                    dialog.dismiss();
                }
            }

        })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ocultarTeclado();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @OnClick(R.id.comenzar)
    public void comenzarPlanNavegacion() {

        if (!indicaciones.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra(Constantes.INDICACIONES, indicaciones);
            setResult(Constantes.RESULT_CODE_PLAN_NAVEGACION, intent);
            finish();
        } else {
            Snackbar.make(recyclerView, "No hay indicaciones", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void ocultarTeclado() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
