package com.example.avi.smarthome.Firebase;

import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.example.avi.smarthome.OpenHab.OpenHabHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Avi on 16/02/2018.
 */

public class FirebaseHandler {
    private static final String LOGCAT = null;
    private static FirebaseHandler firebaseHandler;
    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String settingsLabel = "settings";
    private Map<String, String> ips = new HashMap<>();
    private boolean ipReady = false;

    public static synchronized FirebaseHandler getInstance(Context context) {

        if (firebaseHandler == null) {
            init(context);
        }
        return firebaseHandler;
    }

    private static void init(Context context){
        if (firebaseHandler == null) {
            context = context.getApplicationContext();
            firebaseHandler = new FirebaseHandler(context);

        }
    }
    private FirebaseHandler(Context context)
    {
        this.context = context;
        ipChecker();
    }

    private void ipChecker()
    {
        DatabaseReference myRef = database.getReference(settingsLabel);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ips = (Map<String, String>) dataSnapshot.getValue();
                ipReady = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                int a = 1;
            }
        });
    }

    public Map<String, String> getIps() {
        return ips;
    }

    public boolean isIpReady() {
        return ipReady;
    }

    public void setIpReady(boolean ipReady) {
        this.ipReady = ipReady;
    }
}
