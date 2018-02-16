package com.example.avi.smarthome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.avi.smarthome.Firebase.FirebaseHandler;
import com.example.avi.smarthome.OpenHab.OpenHabHandler;

public class BootActivity extends AppCompatActivity {

    OpenHabHandler openHabHandler = null;
    FirebaseHandler firebaseHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        final Intent intent = new Intent(this, MainActivity.class);

        firebaseHandler = FirebaseHandler.getInstance(getApplicationContext());

        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                while (true){
                    if(firebaseHandler.isIpReady()) {
                        openHabHandler = OpenHabHandler.getInstance(getApplicationContext());
                        openHabHandler.sendGet("PostGetReq?type=things", "things");
                        break;
                    }
                }

                while (true){
                    if(openHabHandler.isBootReady())
                        break;
                }

                // To do
                startActivity(intent);
                finish();
            }
        });
        t.start();
    }
}
