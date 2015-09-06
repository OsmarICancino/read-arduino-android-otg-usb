package ti.uts.alckon.usbdevice;

import grindec.uts.utils.Logger;
import ti.uts.alckon.usbdevice.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogViewer extends Activity {
	
	TextView txt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_viewer);
		
		txt = (TextView) findViewById(R.id.textView1);
        txt.setText(Logger.read("error.log", getApplicationContext()));
	}
}
