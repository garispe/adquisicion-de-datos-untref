package ar.edu.untref.adquisiciondedatos.actividades;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ar.edu.untref.adquisiciondedatos.R;
import ar.edu.untref.adquisiciondedatos.interfaces.OrientacionListener;
import ar.edu.untref.adquisiciondedatos.modelos.Brujula;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ActividadPrincipal extends AppCompatActivity implements OrientacionListener {

    private static final String TAG = ActividadPrincipal.class.getSimpleName();
    @Bind(R.id.angulos_respecto_norte)
    EditText angulosRespectoNorte;
    @Bind(R.id.texto_indicacion)
    TextView textoIndicacion;
    @Bind(R.id.imagen_indicacion)
    ImageView imagenIndicacion;

    private float angulosIndicados = 0;
    private Brujula brujula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        ButterKnife.bind(this);

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
            imagenIndicacion.setRotation(180);
        } else {
            imagenIndicacion.setRotation(0);
        }

        diferencia = Math.abs(diferencia);
        textoIndicacion.setText(String.valueOf(diferencia));
    }
}
