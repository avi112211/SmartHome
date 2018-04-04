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
import java.util.Observable;

/**
 * Created by Avi on 16/02/2018.
 */

public class FirebaseHandler extends Observable {
    private static final String TAG = FirebaseHandler.class.getName();
    private static FirebaseHandler firebaseHandler;
    private Context context;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String settingsLabel = "settings";
    private String personInRoomLabel = "personInRoom";
    private String powerConsumptionHistoryLabel = "powerConsumptionHistory";
    private Map<String, String> ips = new HashMap<>();
    private Map<String, String> personInRoomMap = new HashMap<>();
    private Map<String, String> dataFromListener = new HashMap<>();
    private Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>>> powerConsumptionHistory = new HashMap<>();
    private boolean ipReady = false;
    private boolean pirReady = false;
    private boolean powerConsumptionHistoryReady = false;
    private ValueEventListener itemListener = null;

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
        personInRoomChecker();
        powerConsumptionHistory();
    }

    private void ipChecker()
    {
        DatabaseReference myRef = database.getReference(settingsLabel);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ips = (Map<String, String>) dataSnapshot.getValue();
                ipReady = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                int a = 1;
            }
        });
    }

    private void personInRoomChecker()
    {
        DatabaseReference myRef = database.getReference(personInRoomLabel);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                personInRoomMap = (Map<String, String>) dataSnapshot.getValue();
                pirReady = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                int a = 1;
            }
        });
    }

    private void powerConsumptionHistory()
    {
        DatabaseReference myRef = database.getReference(powerConsumptionHistoryLabel);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                powerConsumptionHistory = (Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>>>) dataSnapshot.getValue();
                powerConsumptionHistoryReady = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                int a = 1;
            }
        });
    }

    public void startFBlistener(String path)
    {
        itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataFromListener = (Map<String, String>) dataSnapshot.getValue();
                setChanged();
                notifyObservers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "startFBlistener:onCancelled", databaseError.toException());
                // ...
            }
        };
        DatabaseReference myRef = database.getReference(path);
        myRef.addValueEventListener(itemListener);
    }

    public void stopFBlistener(String path)
    {
        if(itemListener != null)
        {
            DatabaseReference myRef = database.getReference(path);
            myRef.removeEventListener(itemListener);
        }
    }

    public Map<String, String> getIps() {
        return ips;
    }

    public Map<String, String> getPersonInRoomMap() {
        return personInRoomMap;
    }

    public boolean isIpReady() {
        return ipReady;
    }

    public void setIpReady(boolean ipReady) {
        this.ipReady = ipReady;
    }

    public boolean isPirReady() {
        return pirReady;
    }

    public void setPirReady(boolean pirReady) {
        this.pirReady = pirReady;
    }

    public boolean isPowerConsumptionHistoryReady() {
        return powerConsumptionHistoryReady;
    }

    public void setPowerConsumptionHistoryReady(boolean powerConsumptionHistoryReady) {
        this.powerConsumptionHistoryReady = powerConsumptionHistoryReady;
    }

    public Map<String, String> getDataFromListener() {
        return dataFromListener;
    }

    public Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>>> getPowerConsumptionHistory() {
        return powerConsumptionHistory;
    }
}
