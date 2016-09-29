package ar.edu.untref.adquisiciondedatos.utilidades;

import android.os.CountDownTimer;
import android.util.Log;

import ar.edu.untref.adquisiciondedatos.interfaces.NavegacionListener;
import ar.edu.untref.adquisiciondedatos.interfaces.TiempoListener;
import ar.edu.untref.adquisiciondedatos.modelos.Indicacion;

/**
 * Created by garispe on 9/21/16.
 */

public class Temporizador extends CountDownTimer {

    private static final String TAG = Temporizador.class.getSimpleName();

    private NavegacionListener navegacionListener;
    private TiempoListener tiempoListener;
    private Indicacion indicacion;
    private int tiempoGuardado = 0;
    private int segundosIndicados = 0;
    private boolean estaDetenido = false;

    public Temporizador(Indicacion indicacion, NavegacionListener navegacionListener, TiempoListener tiempoListener) {
        super(indicacion.getSegundos() * Constantes.SEGUNDO_EN_MILISEGUNDOS, Constantes.SEGUNDO_EN_MILISEGUNDOS);

        this.indicacion = indicacion;
        this.tiempoListener = tiempoListener;
        this.navegacionListener = navegacionListener;
        this.segundosIndicados = indicacion.getSegundos();
    }

    public void comenzar() {
        estaDetenido = false;
        start();
    }

    public void detener() {
        estaDetenido = true;
        cancel();
    }

    @Override
    public void onTick(long milisegundosRestantes) {

        int segundosRestantes = (int) (milisegundosRestantes / 1000);

        tiempoGuardado = segundosRestantes;
        tiempoListener.contar(segundosRestantes);

        Log.w(TAG, "onTick: Segundos transcurridos: " + (segundosIndicados - segundosRestantes));
    }

    @Override
    public void onFinish() {
        detener();
        navegacionListener.setNuevaIndicacion();
    }

    public Indicacion getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(Indicacion indicacion) {
        this.indicacion = indicacion;
    }

    public int getTiempoGuardado() {
        return tiempoGuardado;
    }

    public void setTiempoGuardado(int tiempoGuardado) {
        this.tiempoGuardado = tiempoGuardado;
    }

    public boolean estaDetenido() {
        return estaDetenido;
    }
}
