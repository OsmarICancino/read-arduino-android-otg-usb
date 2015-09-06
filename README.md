# read-arduino-android-otg-usb
In this project we connect an Android device with an Arduino board through OTG Connection - USB , using an external library.
As a first step , we open connection to the device , then you write a byte to the plate, to ask you to send values ​​of 4 sensors at the time, to read them , and represent them on the display device .

For the project we use the following library :
https://github.com/mik3y/usb-serial-for-android

Openning connection and settings the Arduino Shield:

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
    
    And readding data:
    
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
            .


