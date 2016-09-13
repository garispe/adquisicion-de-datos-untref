package ar.edu.untref.adquisiciondedatos.actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ar.edu.untref.adquisiciondedatos.R;
import ar.edu.untref.adquisiciondedatos.interfaces.OrientacionListener;
import ar.edu.untref.adquisiciondedatos.modelos.Brujula;
import ar.edu.untref.adquisiciondedatos.modelos.Indicacion;
import ar.edu.untref.adquisiciondedatos.utilidades.Constantes;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActividadPrincipal extends AppCompatActivity implements OrientacionListener {

    private static final int ROTACION_DER = 1;
    private static final int ROTACION_IZQ = -1;
    private static final int REQUEST_CODE_NUEVO_PLAN_NAVEGACION = 1;

    @Bind(R.id.layout_indicador_angulos)
    RelativeLayout layoutIndicadorAngulos;
    @Bind(R.id.angulos_respecto_norte)
    EditText angulosRespectoNorte;
    @Bind(R.id.texto_indicacion)
    TextView textoIndicacion;
    @Bind(R.id.imagen_indicacion)
    ImageView imagenIndicacion;

    private ArrayList<Indicacion> indicaciones = new ArrayList<>();
    private float angulosIndicados = 0;
    private float delta = 0;
    private Brujula brujula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        ButterKnife.bind(this);

        ocultarTeclado();

        brujula = new Brujula(this, this);
        brujula.flechas = (ImageView) findViewById(R.id.imagen_flecha);
        brujula.imagenBrujula = (ImageView) findViewById(R.id.imagen_brujula);
        brujula.iniciar();

        angulosRespectoNorte.setOnEditorActionListener(editorActionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        brujula.iniciar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        brujula.detener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        brujula.detener();
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int action, KeyEvent event) {

            if (action == EditorInfo.IME_ACTION_DONE) {

                String angulos = angulosRespectoNorte.getText().toString();

                if (!angulos.isEmpty()) {
                    angulosIndicados = Float.parseFloat(angulos);
                    angulosRespectoNorte.setText("");

                    brujula.imagenBrujula.setRotation(-angulosIndicados);
                }
            }

            return false;
        }
    };

    @Override
    public void rotar(int angulos) {

        int diferencia = (int) (angulosIndicados - angulos);

        if (diferencia < 0) {
            imagenIndicacion.setScaleX(ROTACION_IZQ);
        } else {
            imagenIndicacion.setScaleX(ROTACION_DER);
        }

        diferencia = Math.abs(diferencia);
        textoIndicacion.setText(String.format("%sº", String.valueOf(diferencia)));
    }

    @OnClick(R.id.delta)
    public void configurarDelta() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Configurar delta");

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialogo_configurar_delta, null);
        dialog.setView(view);

        final EditText editDelta = (EditText) view.findViewById(R.id.delta);

        dialog.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String deltaIndicado = editDelta.getText().toString();
                if (!deltaIndicado.isEmpty()) {
                    delta = Float.valueOf(deltaIndicado);
                }

                ocultarTeclado();
                dialog.dismiss();
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

    private void ocultarTeclado() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @OnClick(R.id.nuevo_plan_navegacion)
    public void crearNuevoPlanNavegacion() {

        Intent intent = new Intent(this, ActividadPlanNavegacion.class);
        startActivityForResult(intent, REQUEST_CODE_NUEVO_PLAN_NAVEGACION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constantes.RESULT_CODE_PLAN_NAVEGACION) {
            indicaciones = (ArrayList<Indicacion>) data.getExtras().get(Constantes.INDICACIONES);
        }
    }
}
