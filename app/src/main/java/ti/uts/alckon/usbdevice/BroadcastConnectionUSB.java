package ti.uts.alckon.usbdevice;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

/**
 * Created by alckon on 3/09/15.
 */
public class BroadcastConnectionUSB extends BroadcastReceiver {

    private static final String ACTION_USB_PERMISSION = "ti.uts.alckon.usbdevice.USB_PERMISSION";
    UsbManager mUsbManager;
    UsbDevice mUsbDevice;
    PendingIntent mPermissionIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            // TODO: Al aceptar el permiso del usuario.
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(context, "Permiso aceptado - Asignando Valor USBManager", Toast.LENGTH_LONG).show();

                        //TODO: Obtemos el Manager USB del sistema Android
                        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);


                    } else {
                        Toast.makeText(context, "Permiso Denegado", Toast.LENGTH_LONG).show();
                    }
                }
            }

            // TODO: Al desconectar el dispositivo USB cerramos las conexiones y liberamos la variables.
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call your method that cleans up and closes communication with the device

                }
                else
                {
                    Toast.makeText(context, "Dispositivo no encontrado --> Broadcast", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e ) {
            Toast.makeText(context, "ERROR en Broadcast", Toast.LENGTH_SHORT).show();
        }
    }


}
