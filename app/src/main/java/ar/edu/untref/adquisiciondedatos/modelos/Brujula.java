package ar.edu.untref.adquisiciondedatos.modelos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Brujula implements SensorEventListener {

    private static final String TAG = Brujula.class.getSimpleName();

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private Sensor magnetometro;
    private float[] valoresAcelerometro = new float[3];
    private float[] valoresMagnetometro = new float[3];
    private float azimuth = 0f;
    private float correccionAzimuth = 0;

    public ImageView flechas = null;
    public ImageView flechaOrientacion = null;

    public Brujula(Context context) {
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometro = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void iniciar() {
        sensorManager.registerListener(this, acelerometro,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometro,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void detener() {
        sensorManager.unregisterListener(this);
    }

    private void redibujarFlechas() {

        if (flechas != null && flechaOrientacion != null) {

            Animation animacionFlechas = new RotateAnimation(-correccionAzimuth, -azimuth,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);

            correccionAzimuth = azimuth;

            animacionFlechas.setDuration(500);
            animacionFlechas.setRepeatCount(0);
            animacionFlechas.setFillAfter(true);

            flechas.startAnimation(animacionFlechas);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        final float alpha = 0.97f;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            valoresAcelerometro[0] = alpha * valoresAcelerometro[0] + (1 - alpha)
                    * event.values[0];
            valoresAcelerometro[1] = alpha * valoresAcelerometro[1] + (1 - alpha)
                    * event.values[1];
            valoresAcelerometro[2] = alpha * valoresAcelerometro[2] + (1 - alpha)
                    * event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

            valoresMagnetometro[0] = alpha * valoresMagnetometro[0] + (1 - alpha)
                    * event.values[0];
            valoresMagnetometro[1] = alpha * valoresMagnetometro[1] + (1 - alpha)
                    * event.values[1];
            valoresMagnetometro[2] = alpha * valoresMagnetometro[2] + (1 - alpha)
                    * event.values[2];
        }

        float R[] = new float[9];
        float I[] = new float[9];

        boolean rotacionTomada = SensorManager.getRotationMatrix(R, I, valoresAcelerometro,
                valoresMagnetometro);

        if (rotacionTomada) {

            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);

            azimuth = (float) Math.toDegrees(orientation[0]);
            azimuth = (azimuth + 360) % 360;

            redibujarFlechas();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
