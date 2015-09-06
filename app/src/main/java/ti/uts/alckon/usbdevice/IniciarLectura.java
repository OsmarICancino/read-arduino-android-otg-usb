package ti.uts.alckon.usbdevice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by alckon on 3/09/15.
 */
public class IniciarLectura extends Activity {



    Button iniciarLectura;
    TextView txtVisual;

    public boolean ready = false;
    public BroadcastConnectionUSB mBroadcastConnectionUSB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lectura);

        iniciarLectura = (Button) findViewById(R.id.next_actividad);
        txtVisual = (TextView) findViewById(R.id.txt_visualizar);

        iniciarLectura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Iniciar lectura", Toast.LENGTH_LONG).show();
                if(!ready)
                {
                    txtVisual.setText("Not Connection");
                }
                else
                {
                    txtVisual.setText("Connection Sucessfull");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Inicialimamos Broacdast
        mBroadcastConnectionUSB = new BroadcastConnectionUSB();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
