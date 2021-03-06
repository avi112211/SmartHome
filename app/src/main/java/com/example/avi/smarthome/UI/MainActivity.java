package com.example.avi.smarthome.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.avi.smarthome.Firebase.FirebaseHandler;
import com.example.avi.smarthome.OpenHab.OpenHabHandler;
import com.example.avi.smarthome.OpenHab.Thing;
import com.example.avi.smarthome.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    OpenHabHandler openHabHandler = null;
    private Map<String, String> listElToClass = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        openHabHandler = OpenHabHandler.getInstance(getApplicationContext());

        addMenuItemInNavMenuDrawer();

        //set fragment initially
        MainFragment mainFragment = new MainFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String el = item.toString();

        //fragmentTransaction
        ThingsInRoomFragment thingsInRoomFragment;
        PowerConsumptionHistoryFragment powerConsumptionHistoryFragment;
        LiveCameraFragment liveCameraFragment;

        Bundle bundle = new Bundle();
        bundle.putString("roomName", el);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch (listElToClass.get(el)) {
            case "roomFragment":
                thingsInRoomFragment = new ThingsInRoomFragment();
                thingsInRoomFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, thingsInRoomFragment, "main");
                break;
            case "powerConsumptionHistoryFragment":
                powerConsumptionHistoryFragment = new PowerConsumptionHistoryFragment();
                powerConsumptionHistoryFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container, powerConsumptionHistoryFragment, "main");
                break;
            case "liveCameraFragment":
                liveCameraFragment = new LiveCameraFragment();
                fragmentTransaction.replace(R.id.fragment_container, liveCameraFragment, "main");
                break;
            default:
                break;
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addMenuItemInNavMenuDrawer() {
        Map<String, ArrayList<Thing>> thingsPerRoom = openHabHandler.getThingsPerRoom();
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navView.getMenu();
        Menu submenu = menu.addSubMenu("Room List");

        for(String roomName : thingsPerRoom.keySet()){
            submenu.add(roomName);
            listElToClass.put(roomName, "roomFragment");
    }

        FirebaseHandler firebaseHandler = FirebaseHandler.getInstance(getApplicationContext());
        if(!firebaseHandler.getPowerConsumptionHistory().isEmpty()) {
            submenu.add("Power Consumption History");
            listElToClass.put("Power Consumption History", "powerConsumptionHistoryFragment");
        }


        submenu.add("Live Camera");
        listElToClass.put("Live Camera", "liveCameraFragment");


        navView.invalidate();
    }
}
