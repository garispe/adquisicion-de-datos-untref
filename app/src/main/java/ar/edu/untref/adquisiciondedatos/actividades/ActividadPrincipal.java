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
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import ar.edu.untref.adquisiciondedatos.R;
import ar.edu.untref.adquisiciondedatos.enums.PuntoCardinal;
import ar.edu.untref.adquisiciondedatos.modelos.Brujula;
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
    private float angulosRespectoNorteIndicados = 0;
    private Brujula brujula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        ButterKnife.bind(this);

        brujula = new Brujula(this);
        brujula.flechas = (ImageView) findViewById(R.id.imagen_flecha);
        brujula.flechaOrientacion = (ImageView) findViewById(R.id.imagen_orientacion);
        brujula.iniciar();

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

        brujula.iniciar();

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
    protected void onStop() {
        super.onStop();
        brujula.detener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        brujula.detener();

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        String latitud = Preferencias.getString(this, Constantes.LATITUD);
        String longitud = Preferencias.getString(this, Constantes.LONGITUD);

        if (latitud != null && longitud != null) {
            CameraUpdate posicionActual = CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(latitud), Double.valueOf(longitud)));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.moveCamera(posicionActual);
            googleMap.animateCamera(zoom);
            setPosicionBrujula(googleMap);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitud = location.getLatitude();
        double longitud = location.getLongitude();
        Preferencias.guardarString(this, Constantes.LATITUD, String.valueOf(latitud));
        Preferencias.guardarString(this, Constantes.LONGITUD, String.valueOf(longitud));
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

    private void setPosicionBrujula(GoogleMap googleMap) {
        googleMap.setPadding(20, 150, 0, 0);
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int action, KeyEvent event) {

            if (action == EditorInfo.IME_ACTION_DONE) {

                String angulos = angulosRespectoNorte.getText().toString();

                if (!angulos.isEmpty()) {
                    angulosRespectoNorteIndicados = Float.parseFloat(angulos);
                    angulosRespectoNorte.setText("");
                }
            }

            return false;
        }
    };

//    private PuntoCardinal getDireccion(float azimuth) {
//
//
//        boolean condicionNorte = (azimuth >= 0 && azimuth < 22.5) || (azimuth >= 337.5);
//        boolean condicionNoreste = (azimuth >= 22.5 && azimuth < 67.5);
//        boolean condicionEste = (azimuth >= 67.5 && azimuth < 112.5);
//        boolean condicionSureste = (azimuth >= 112.5 && azimuth < 157.5);
//        boolean condicionSur = (azimuth >= 157.5 && azimuth < 202.5);
//        boolean condicionSuroeste = (azimuth >= 202.5 && azimuth < 247.5);
//        boolean condicionOeste = (azimuth >= 247.5 && azimuth < 292.5);
//        boolean condicionNoroeste = (azimuth >= 292.5 && azimuth < 337.5);
//
//        if (condicionNorte) {
//            return PuntoCardinal.NORTE;
//        } else if (condicionNoreste) {
//            return PuntoCardinal.NORESTE;
//        } else if (condicionEste) {
//            return PuntoCardinal.ESTE;
//        } else if (condicionSureste) {
//            return PuntoCardinal.SURESTE;
//        } else if (condicionSur) {
//            return PuntoCardinal.SUR;
//        } else if (condicionSuroeste) {
//            return PuntoCardinal.SUROESTE;
//        } else if (condicionOeste) {
//            return PuntoCardinal.OESTE;
//        } else if (condicionNoroeste) {
//            return PuntoCardinal.NOROESTE;
//        }
//
//        return PuntoCardinal.NONE;
//    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
