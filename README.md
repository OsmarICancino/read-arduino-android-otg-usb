# read-arduino-android-otg-usb
In this project we connect an Android device with an Arduino board through OTG Connection - USB , using an external library.
As a first step , we open connection to the device , then you write a byte to the plate, to ask you to send values ​​of 4 sensors at the time, to read them , and represent them on the display device .

For the project we use the following library :
https://github.com/mik3y/usb-serial-for-android

Openning connection and settings the Arduino Shield: <br>
```java
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
    
    //And Readding data 
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
    
    ```
    The permission in AndroidManifest.xml
```xml
<activity
    android:name="..."
    ...>
  <intent-filter>
    <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
  </intent-filter>
  <meta-data
      android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" 
      android:resource="@xml/device_filter" />
</activity>
```
   


