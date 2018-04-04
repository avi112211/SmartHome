package com.example.avi.smarthome.OpenHab;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.avi.smarthome.Firebase.FirebaseHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Avi on 31/12/2017.
 */

public class OpenHabHandler {

    private static final String LOGCAT = null;
    private static OpenHabHandler openHabHandlerInstance;
    private FirebaseHandler firebaseHandler;
    private Map<String, ArrayList<Thing>> thingsPerRoom = new HashMap<>();
    private ArrayList<Item> itemsInRoom = new ArrayList<>();
    private String URL = "";
    private Map<String, String> ips = new HashMap<>();
    private RequestQueue requestQueue;
    private Context context;
    private Gson gson;
    private boolean isBootReady = false;
    private boolean isDataReady = false;
    private String type = "";

    public static synchronized OpenHabHandler getInstance(Context context) {

        if (openHabHandlerInstance == null) {
            init(context);
        }
        return openHabHandlerInstance;
    }

    private static void init(Context context){
        if (openHabHandlerInstance == null) {
            context = context.getApplicationContext();
            openHabHandlerInstance = new OpenHabHandler(context);
        }
    }
    private OpenHabHandler(Context context)
    {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        firebaseHandler = FirebaseHandler.getInstance(context);
        ips = firebaseHandler.getIps();
        URL = "http://" + ips.get("publicIp") + ":8181/SmartHome/";
    }

    public void sendGet(String url, String type) {
        this.type = type;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        StringRequest request = new StringRequest(Request.Method.GET, URL + url, onGetLoaded, onGetError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onGetLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response)
        {
            Type responseType = null;
            switch (type)
            {
                case "things":
                    responseType = new TypeToken<Map<String, ArrayList<Thing>>>(){}.getType();
                    thingsPerRoom = gson.fromJson(response, responseType);
                    isBootReady = true;
                    break;

                case "items":
                    responseType = new TypeToken<ArrayList<Item>>(){}.getType();
                    itemsInRoom = gson.fromJson(response, responseType);
                    isDataReady = true;
                    break;

                default:
                    break;

            }

            type = "";
            Log.i("PostActivity", response);
    }
    };

    private final Response.ErrorListener onGetError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

    public void sendPost(String url, String type, Object val) {
        StringRequest request = new StringRequest(Request.Method.POST, URL + url, onPostLoaded, onPostError);
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    private final Response.Listener<String> onPostLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response)
        {
            Log.i("PostActivity", response);
        }
    };

    private final Response.ErrorListener onPostError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

    public boolean isBootReady() {
        return isBootReady;
    }

    public void setBootReady(boolean bootReady) {
        isBootReady = bootReady;
    }

    public boolean isDataReady() {
        return isDataReady;
    }

    public void setDataReady(boolean dataReady) {
        isDataReady = dataReady;
    }

    public Map<String, ArrayList<Thing>> getThingsPerRoom() {
        return thingsPerRoom;
    }

    public void setThingsPerRoom(Map<String, ArrayList<Thing>> thingsPerRoom) {
        this.thingsPerRoom = thingsPerRoom;
    }

    public ArrayList<Item> getItemsInRoom() {
        return itemsInRoom;
    }

    public void setItemsInRoom(ArrayList<Item> itemsInRoom) {
        this.itemsInRoom = itemsInRoom;
    }
}
