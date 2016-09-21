package ar.edu.untref.adquisiciondedatos.utilidades;

import android.os.CountDownTimer;
import android.util.Log;

import ar.edu.untref.adquisiciondedatos.interfaces.NavegacionListener;
import ar.edu.untref.adquisiciondedatos.modelos.Indicacion;

/**
 * Created by garispe on 9/21/16.
 */

public class Temporizador extends CountDownTimer {

    private static final String TAG = Temporizador.class.getSimpleName();

    private NavegacionListener navegacionListener;
    private Indicacion indicacion;

    public Temporizador(Indicacion indicacion, NavegacionListener navegacionListener) {
        super(indicacion.getSegundos() * Constantes.SEGUNDO_EN_MILISEGUNDOS, Constantes.SEGUNDO_EN_MILISEGUNDOS);

        this.indicacion = indicacion;
        this.navegacionListener = navegacionListener;
    }

    public void comenzar() {
        start();
    }

    public void reanudar() {
        comenzar();
        // Desde tiempo guardado
    }

    public void pausar() {
        detener();
        // Guardar tiempo
    }

    public void detener() {
        cancel();
    }

    @Override
    public void onTick(long milisegundosRestantes) {

        int segundosIndicados = indicacion.getSegundos();
        int segundosRestantes = (int) (milisegundosRestantes / 1000);

        Log.i(TAG, "onTick: Segundos transcurridos: " + (segundosIndicados - segundosRestantes));
    }

    @Override
    public void onFinish() {
        detener();
        Log.i(TAG, "Indicacion completada");

        navegacionListener.setNuevaIndicacion();
    }
}
