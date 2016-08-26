package ar.edu.untref.adquisiciondedatos.actividades;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import ar.edu.untref.adquisiciondedatos.R;
import ar.edu.untref.adquisiciondedatos.utilidades.Constantes;
import ar.edu.untref.adquisiciondedatos.utilidades.Preferencias;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ActividadPrincipal extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = ActividadPrincipal.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 1;
    @Bind(R.id.angulos_respecto_norte)
    EditText angulosRespectoNorte;

    private LocationManager locationManager;
    private boolean GPSactivado;
    private MapFragment fragmentoMapa;
    private double angulosRespectoNorteIndicados = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        ButterKnife.bind(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSactivado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GPSactivado) {
            mostrarDialogoParaHabilitarGPS();
        } else {
            getLocation();
        }

        fragmentoMapa = (MapFragment) getFragmentManager().findFragmentById(R.id.mapa);
        fragmentoMapa.getMapAsync(this);

        angulosRespectoNorte.setOnEditorActionListener(editorActionListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GPSactivado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean permisoFineLocationConcedido = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permisoCoarseLocationConcedido = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (GPSactivado) {

            if (permisoFineLocationConcedido && permisoCoarseLocationConcedido) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        boolean permisoFineLocationConcedido = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permisoCoarseLocationConcedido = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permisoFineLocationConcedido && permisoCoarseLocationConcedido) {
            locationManager.removeUpdates(this);
        }
    }

    private void getLocation() {

        boolean permisoFineLocationConcedido = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permisoCoarseLocationConcedido = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permisoFineLocationConcedido && permisoCoarseLocationConcedido) {

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        googleMap.setPadding(20, 150, 0, 0);
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.w(TAG, "Latitud:" + latitude);
        Log.w(TAG, "Longitud:" + longitude);

        Preferencias.guardarString(this, Constantes.LATITUD, String.valueOf(latitude));
        Preferencias.guardarString(this, Constantes.LONGITUD, String.valueOf(longitude));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private void mostrarDialogoParaHabilitarGPS() {

        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("GPS deshabilitado");
        dialogo.setMessage("La aplicación requiere que el GPS esté habilitado. ¿Desea habilitarlo ahora?");
        dialogo.setCancelable(false);
        dialogo.setPositiveButton("Habilitar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {

            GPSactivado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean permisoFineLocationConcedido = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            boolean permisoCoarseLocationConcedido = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (GPSactivado) {

                if (permisoFineLocationConcedido && permisoCoarseLocationConcedido) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                }
            }
        }
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
}
