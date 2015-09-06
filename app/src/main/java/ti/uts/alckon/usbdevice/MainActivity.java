package ti.uts.alckon.usbdevice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private static final String TAG = "USB-Example";

    Calendar lastRead;
    String lastString;

    // Variables GUI
    Button btn;
    Button next;
    Button lectura;

    TextView textViewTemperatura;
    TextView textViewHumedad;
    TextView textViewName;
    TextView textViewProtocol;
    TextView textViewID;
    TextView textViewVendor;
    TextView textEstado;
    TextView txtLectura;


    // TODO: Variables USB
    UsbManager mUsbManager;
    UsbDevice mUsbDevice;
    PendingIntent mPermissionIntent;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private UsbSerialDriver mSerialDevice;

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped.");
                }

                @Override
                public void onNewData(final byte[] data) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };



    // TODO: Al conectar a un dispositvo USB se solicita un permiso al usuario

    private static final String ACTION_USB_PERMISSION = "ti.uts.alckon.usbdevice.USB_PERMISSION";

    private void updateReceivedData(byte[] data) {
        String temp = new String(data);
        boolean continuar = false;
        if( temp.contains("=") && temp.contains(";") ) {
            lastString = temp;
            continuar = true;
        } else if( temp.contains("=") )
            lastString = temp;
        else if( temp.contains(";") ) {
            lastString = lastString!=null ? lastString + temp : temp;
            if( lastString.contains("=")&&lastString.contains(";"))
                continuar = true;
        } else {
            lastString = "";
        }
        if( continuar  ) {
            String message = lastString;

            if (message.contains("=") && message.contains(";"))
                txtLectura.setText(message);
            textEstado.setText(message);
            textViewHumedad.setText(message.contains("=") + " " + message.contains(";"));
            lastRead = null;
            lastString = "";
        }
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (mSerialDevice != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(mSerialDevice, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textViewTemperatura = (TextView) findViewById(R.id.textView1);
        textViewHumedad = (TextView) findViewById(R.id.textView2);

        textViewName = (TextView) findViewById(R.id.txt_name);
        textViewProtocol = (TextView) findViewById(R.id.txt_protocol);
        textViewID = (TextView) findViewById(R.id.txt_id);
        textViewVendor = (TextView) findViewById(R.id.txt_vendor);
        textEstado = (TextView) findViewById(R.id.txt_estado);

        txtLectura = (TextView) findViewById(R.id.txt_lectura);

        lectura = (Button) findViewById(R.id.log);
        next = (Button) findViewById(R.id.button2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, IniciarLectura.class);
                startActivity(i);
            }
        });


        // TODO: Boton Conectar.
        btn = (Button) findViewById(R.id.button1);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        btn.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void onClick(View v) {


            }
        });

        lectura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   Thread hilo = new Thread(new Runnable() {
                       @Override
                       public void run() {
                           while(true)
                           {
                               try
                               {
                                   mSerialDevice.write(new byte[]{5},0);
                               }
                               catch( final Exception e )
                               {
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(getApplicationContext(), "Error de envio...", Toast.LENGTH_LONG).show();
                                       }
                                   });

                               }
                               try
                               {
                                   Thread.sleep(1000);
                               }
                               catch (Exception e)
                               {
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(getApplicationContext(), "Error en el hilo", Toast.LENGTH_LONG).show();
                                       }
                                   });
                               }
                           }
                       }
                   });
                   //TODO: Iniciamos el Hilo de escritura al Arduino y comnezamos con la lectura

                    startListening();
                    hilo.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: Solicitamos permiso al usuario
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Toast.makeText(getApplicationContext(), "Solicitando permiso al Usuario", Toast.LENGTH_SHORT).show();

        //TODO: Registro del Broadcast
        //registerReceiver(mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        //registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
        startConnection();


    }

    @Override
    protected void onPause() {
        super.onPause();

        //if (this.mUsbReceiver != null){
        //    unregisterReceiver(mUsbReceiver);
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.menu_log) {
            Intent intent = new Intent(this,LogViewer.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startListening() {
        mSerialDevice = UsbSerialProber.acquire(mUsbManager);
        Toast.makeText(getApplicationContext(), "Resumed, mSerialDevice=" + mSerialDevice, Toast.LENGTH_SHORT).show();
        if (mSerialDevice == null)
        {
            textEstado.setText("Dispositivo Serial no Encontrado");
        } else
        {
            try
            {
                mSerialDevice.open();
                mSerialDevice.setBaudRate(9600);
            }
            catch (IOException e)
            {
                Toast.makeText(getApplicationContext(), "Error configurando el Dispositivo" + e.getMessage(), Toast.LENGTH_SHORT).show();
                textEstado.setText("Error abriendo el dispositivo: " + e.getMessage());
                try {
                    mSerialDevice.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mSerialDevice = null;
                return;
            }

            textEstado.setText("Dispositivo Serial: " + mSerialDevice);
        }

        onDeviceStateChange();
    }

    public void startConnection()
    {
        //TODO: Obtemos el Manager USB del sistema Android
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // TODO: Recuperamos todos los dispositvos USB detectados
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        //TODO: en nuestor ejemplo solo conectamos un disposito asi que sera el unico que encontraremos.

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if (deviceIterator != null) {
            while (deviceIterator.hasNext()) {

                mUsbDevice = deviceIterator.next();

                textViewName.setText("USB: " + mUsbDevice.getDeviceName());
                textViewProtocol.setText("Protocolo: " + mUsbDevice.getDeviceProtocol());
                textViewID.setText("ID: " + mUsbDevice.getProductId());
                textViewVendor.setText("Vendor: " + mUsbDevice.getVendorId());

                //TODO: Solicitamos el permiso al usuario.
                mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
                mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            }

        } else {
            Toast.makeText(getApplicationContext(), "Dispositivo No Conectado", Toast.LENGTH_SHORT).show();
        }
    }
}
