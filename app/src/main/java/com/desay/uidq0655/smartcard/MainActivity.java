package com.desay.uidq0655.smartcard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.simalliance.openmobileapi.Channel;
import org.simalliance.openmobileapi.Reader;
import org.simalliance.openmobileapi.SEService;
import org.simalliance.openmobileapi.Session;

public class MainActivity extends AppCompatActivity implements SEService.CallBack {
    private final String LOG_TAG = "HelloSmartcard";

    private SEService seService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT));

        Button button = new Button(this);
        button.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT));

        button.setText("Click Me");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.i(LOG_TAG, "Retrieve available readers...");
                    Reader[] readers = seService.getReaders();
                    if (readers.length < 1)
                        return;

                    Log.i(LOG_TAG, "Create Session from the first reader...");
                    Session session = readers[0].openSession();

                    Log.i(LOG_TAG, "Create logical channel within the session...");
                    Channel channel = session.openLogicalChannel(new byte[] {
                            (byte) 0xD2, 0x76, 0x00, 0x01, 0x18, 0x00, 0x02,
                            (byte) 0xFF, 0x49, 0x50, 0x25, (byte) 0x89,
                            (byte) 0xC0, 0x01, (byte) 0x9B, 0x01 });

                    Log.d(LOG_TAG, "Send HelloWorld APDU command");
                    byte[] respApdu = channel.transmit(new byte[] { (byte) 0x90, 0x10, 0x00, 0x00, 0x00 });

                    channel.close();

                    // Parse response APDU and show text but remove SW1 SW2 first
                    byte[] helloStr = new byte[respApdu.length - 2];
                    System.arraycopy(respApdu, 0, helloStr, 0, respApdu.length - 2);
                    Toast.makeText(MainActivity.this, new String(helloStr), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error occured:", e);
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        layout.addView(button);
        setContentView(layout);

        try {
            Log.i(LOG_TAG, "creating SEService object");
            seService = new SEService(this, this);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "Binding not allowed, uses-permission org.simalliance.openmobileapi.SMARTCARD?");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception: " + e.getMessage());
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (seService != null && seService.isConnected()) {
            seService.shutdown();
        }
        super.onDestroy();
    }

    public void serviceConnected(SEService service) {
        Log.i(LOG_TAG, "seviceConnected()");
    }
}
