package ar.edu.untref.adquisiciondedatos.actividades;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import ar.edu.untref.adquisiciondedatos.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ActividadPrincipal extends AppCompatActivity implements OnMapReadyCallback {

    @Bind(R.id.angulos_respecto_norte)
    EditText angulosRespectoNorte;

    private MapFragment fragmentoMapa;
    private double angulosRespectoNorteIndicados = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        ButterKnife.bind(this);

        fragmentoMapa = (MapFragment) getFragmentManager().findFragmentById(R.id.mapa);
        fragmentoMapa.getMapAsync(this);

        angulosRespectoNorte.setOnEditorActionListener(editorActionListener);
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int action, KeyEvent event) {

            if (action == EditorInfo.IME_ACTION_DONE) {

                String angulos = angulosRespectoNorte.getText().toString();

                if (!angulos.isEmpty()) {

                    angulosRespectoNorteIndicados = Double.parseDouble(angulos);
                    angulosRespectoNorte.setText("");
                }
            }

            return false;
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        googleMap.setPadding(20, 150, 0, 0);
    }
}
