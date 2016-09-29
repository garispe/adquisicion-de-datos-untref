package ar.edu.untref.adquisiciondedatos.actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ar.edu.untref.adquisiciondedatos.R;
import ar.edu.untref.adquisiciondedatos.controladores.ControladorBluetooth;
import ar.edu.untref.adquisiciondedatos.interfaces.NavegacionListener;
import ar.edu.untref.adquisiciondedatos.interfaces.OrientacionListener;
import ar.edu.untref.adquisiciondedatos.interfaces.TiempoListener;
import ar.edu.untref.adquisiciondedatos.utilidades.Brujula;
import ar.edu.untref.adquisiciondedatos.modelos.Indicacion;
import ar.edu.untref.adquisiciondedatos.utilidades.Constantes;
import ar.edu.untref.adquisiciondedatos.utilidades.Temporizador;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ActividadPrincipal extends AppCompatActivity implements OrientacionListener, NavegacionListener, TiempoListener {

    private static final int ROTACION_DER = 1;
    private static final int ROTACION_IZQ = -1;
    private static final int REQUEST_CODE_NUEVO_PLAN_NAVEGACION = 1;

    @Bind(R.id.layout_indicador_angulos)
    RelativeLayout layoutIndicadorAngulos;
    @Bind(R.id.angulos_respecto_norte)
    EditText angulosRespectoNorte;
    @Bind(R.id.texto_indicacion)
    TextView textoIndicacion;
    @Bind(R.id.texto_delta)
    TextView textoDelta;
    @Bind(R.id.imagen_indicacion)
    ImageView imagenIndicacion;
    @Bind(R.id.layout_indicador_angulos_tiempo)
    LinearLayout layoutIndicadorAngulosTiempo;
    @Bind(R.id.tAngulos_indicados)
    TextView tAngulosIndicacion;
    @Bind(R.id.tSegundos_indicados)
    TextView tSegundosIndicacion;

    private ArrayList<Indicacion> indicaciones = new ArrayList<>();
    private float angulosIndicados = 0;
    private float delta = Constantes.DELTA_POR_DEFECTO;
    private Brujula brujula;
    private ControladorBluetooth bluetooth;
    private Temporizador temporizador;
    private int indiceIndicacion = 0;

    private boolean vectorCompleto = false;
    private boolean enCurso = false;
    private Boolean vectorCurso[] = new Boolean[2];
    private int posicionVector = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        ButterKnife.bind(this);

        ocultarTeclado();

        textoDelta.setText(String.valueOf((int) delta));

        brujula = new Brujula(this, this);
        brujula.flechas = (ImageView) findViewById(R.id.imagen_flecha);
        brujula.imagenBrujula = (ImageView) findViewById(R.id.imagen_brujula);
        brujula.iniciar();

        bluetooth = ControladorBluetooth.getInstance();
//        bluetooth.conectar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        brujula.iniciar();

//        if(!bluetooth.estaConectado()){
//            bluetooth.conectar();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        brujula.detener();
        bluetooth.desconectar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        brujula.detener();
        bluetooth.desconectar();
    }

    @OnEditorAction(R.id.angulos_respecto_norte)
    public boolean indicarAngulosRespectoNorte(TextView v, int action, KeyEvent event) {

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

    @Override
    public void rotar(int angulos) {

        int diferencia = (int) (angulosIndicados - angulos);

        if (Math.abs(diferencia) < delta) {

            imagenIndicacion.setImageResource(R.drawable.orientacion_ok);
            enCurso = true;

        } else {

            imagenIndicacion.setImageResource(R.drawable.img_rotacion);
            enCurso = false;

            // Si la diferencia es menor a 0, la flecha roja está a la izquierda del 0.
            // ROTACION_IZQ: Indica que el móvil debe rotar a la izquierda
            // ROTACION_DER: Indica que el móvil debe rotar a la derecha

            if (diferencia + delta <= 0) {
                imagenIndicacion.setScaleX(ROTACION_IZQ);
            } else {
                imagenIndicacion.setScaleX(ROTACION_DER);
            }
        }

        detectarCambioCurso();

        diferencia = Math.abs(diferencia);
//        bluetooth.enviarDato(String.valueOf(diferencia));
        textoIndicacion.setText(String.format("%sº", String.valueOf(diferencia)));
    }

    private void detectarCambioCurso() {

        if (!vectorCompleto) {
            vectorCurso[posicionVector] = enCurso;
            posicionVector++;

            if (posicionVector == 2) {
                vectorCompleto = true;
                posicionVector = 0;
            }
        } else {

            if (vectorCurso[0] == false && vectorCurso[1] == true) {

                if (temporizador != null) {

                    int tiempoGuardado = temporizador.getTiempoGuardado();
                    Indicacion indicacionActual = temporizador.getIndicacion();
                    indicacionActual.setSegundos(tiempoGuardado);

                    temporizador.detener();

                    temporizador = new Temporizador(indicacionActual, this, this);
                    temporizador.comenzar();
                }


            } else if ((vectorCurso[0] == true && vectorCurso[1] == false)
                    || (vectorCurso[0] == false && vectorCurso[1] == false)) {

                if (temporizador != null) {
                    temporizador.detener();
                }
            }
            vectorCompleto = false;
        }
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
                    textoDelta.setText(deltaIndicado);
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

            if (!indicaciones.isEmpty()) {
                comenzarNavegacion();
            }
        }
    }

    private void comenzarNavegacion() {

        angulosRespectoNorte.setVisibility(GONE);
        layoutIndicadorAngulosTiempo.setVisibility(VISIBLE);

        Indicacion primeraIndicacion = indicaciones.get(indiceIndicacion);

        tAngulosIndicacion.setText("Angulos: " + primeraIndicacion.getAngulos() + "º");
        brujula.imagenBrujula.setRotation(-primeraIndicacion.getAngulos());
        angulosIndicados = Float.parseFloat(String.valueOf(primeraIndicacion.getAngulos()));

        temporizador = new Temporizador(primeraIndicacion, this, this);
        temporizador.comenzar();
    }

    @Override
    public void setNuevaIndicacion() {

        indiceIndicacion++;

        if (indiceIndicacion < indicaciones.size()) {

            Indicacion indicacion = indicaciones.get(indiceIndicacion);
            tAngulosIndicacion.setText("Angulos: " + indicacion.getAngulos() + "º");
            brujula.imagenBrujula.setRotation(-indicacion.getAngulos());
            angulosIndicados = Float.parseFloat(String.valueOf(indicacion.getAngulos()));

            temporizador = new Temporizador(indicacion, this, this);
            temporizador.comenzar();

        } else {

            indiceIndicacion = 0;
            angulosRespectoNorte.setVisibility(VISIBLE);
            layoutIndicadorAngulosTiempo.setVisibility(GONE);
        }
    }

    @Override
    public void contar(int segundos) {
        tSegundosIndicacion.setText("Segundos: " + segundos + "''");
    }
}
