package ar.edu.untref.adquisiciondedatos.controladores;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ControladorBluetooth {

    private static final String TAG = ControladorBluetooth.class.getSimpleName();

    private static final String MAC_MODULO_BLUETOOTH = "98:D3:31:70:3D:01";
    private static final UUID UUID_CONEXION = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // RFCOMM

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    private boolean estaConectado = false;

    private static ControladorBluetooth instance = new ControladorBluetooth();

    private int intentos = 0;

    private ControladorBluetooth() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static ControladorBluetooth getInstance() {

        if (instance == null) {
            instance = new ControladorBluetooth();
        }
        return instance;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return this.bluetoothAdapter;
    }

    public void habilitarBluetooth() {

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
    }

    public void conectar() {

        if (bluetoothAdapter != null) {

            if (bluetoothAdapter.isEnabled()) {

                Log.i(TAG, "Bluetooth habilitado. Conectando...");
                intentos = 0;

                bluetoothAdapter.cancelDiscovery();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MAC_MODULO_BLUETOOTH);

                try {

                    socket = device.createRfcommSocketToServiceRecord(UUID_CONEXION);
                    socket.connect();

                    estaConectado = true;

                    outputStream = socket.getOutputStream();

                    Log.i(TAG, "Conexion establecida");

                } catch (IOException e) {

                    estaConectado = false;
                    Log.e(TAG, "Timeout de Conexion");
                    e.printStackTrace();
                }

            } else {

                intentos++;

                if (intentos <= 500) {

                    bluetoothAdapter.enable();
                    conectar();

                } else {

                    estaConectado = false;
                    Log.e(TAG, "No se pudo realizar la conexion. " + (intentos - 1) + " intentos realizados");
                }
            }
        }
    }

    public boolean estaConectado() {
        return estaConectado;
    }

    public void setEstaConectado(boolean conectado) {
        this.estaConectado = conectado;
    }

    public void desconectar() {

        try {
            if (this.socket != null) {
                this.socket.close();
            }
            this.bluetoothAdapter.disable();
            this.estaConectado = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarDato(String dato) {

        try {
            outputStream.write(dato.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}