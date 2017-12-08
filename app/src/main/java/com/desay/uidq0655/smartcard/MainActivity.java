package com.desay.uidq0655.smartcard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.desay.openmobile.Channel;
import com.desay.openmobile.Reader;
import com.desay.openmobile.SEService;
import com.desay.openmobile.Session;
import com.desay.openmobile.Tmc200;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SEService.CallBack {
    private final String LOG_TAG = "HelloSmartcard";
    private SEService seService;
    private Tmc200 tmc200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tmc200 = new Tmc200();

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT));

        Button button = new Button(this);
        button.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT));
        button.setText("Click Me");
        layout.addView(button);

        Button btTestReset = new Button(this);
        btTestReset.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT));
        btTestReset.setText("Reset");
        layout.addView(btTestReset);

        Button btTestTransmit = new Button(this);
        btTestTransmit.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT));
        btTestTransmit.setText("Transmit");
        layout.addView(btTestTransmit);

        Button btTestGetAtr = new Button(this);
        btTestGetAtr.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT));
        btTestGetAtr.setText("GetAtr");
        layout.addView(btTestGetAtr);

        Button btTestClose = new Button(this);
        btTestClose.setLayoutParams(new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT));
        btTestClose.setText("Close");
        layout.addView(btTestClose);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.i(LOG_TAG, "Retrieve available readers...");
                    Reader[] readers = seService.getReaders();
                    if (readers.length < 1)
                        return;

                    for (int i = 0; i < readers.length; i++) {
                        Log.d(LOG_TAG, "reader" + i + ":" + readers[i].getName());
                    }

                    Log.i(LOG_TAG, "Create Session from the first reader...");
                    Session session = readers[0].openSession();

                    Log.i(LOG_TAG, "Create logical channel within the session...");
                    Channel channel = session.openLogicalChannel(new byte[]{
                            0x01, (byte)0xa4, 0x04, 0x0, 0x10, (byte)0xa0, 0x0, 0x0,
                            0x06, 0x28, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x1, (byte)0xe2,
                            0x0, 0x01});

                    Log.d(LOG_TAG, "Send HelloWorld APDU command");
                    byte[] respApdu = channel.transmit(new byte[]{0x00, (byte) 0x84, 0x00, 0x00, 0x08});

                    channel.close();
                    readers[0].closeSessions();

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

        btTestReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tmc200.reset();
            }
        });

        btTestTransmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] cmd = {0x00, (byte) 0x84, 0x00, 0x00, 0x08};
                byte[] response;
                response = tmc200.transmit(cmd);
                if ((response != null) && (response.length != 0)) {
                    Log.d(LOG_TAG, Arrays.toString(response));
                }
            }
        });

        btTestGetAtr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] response;
                response = tmc200.getATR();
                if (response.length != 0) {
                    Log.d(LOG_TAG, response.toString());
                }
            }
        });

        btTestClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tmc200.close();
            }
        });

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
