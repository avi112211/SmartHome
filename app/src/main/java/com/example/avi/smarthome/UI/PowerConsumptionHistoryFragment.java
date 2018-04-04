package com.example.avi.smarthome.UI;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avi.smarthome.Firebase.FirebaseHandler;
import com.example.avi.smarthome.R;

import java.util.HashMap;
import java.util.Map;

public class PowerConsumptionHistoryFragment extends Fragment {
    FirebaseHandler firebaseHandler = null;

    public PowerConsumptionHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_power_consumption_history, container, false);
        firebaseHandler = FirebaseHandler.getInstance(getActivity().getApplicationContext());
        Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>>> powerConsumptionHistoryFb = firebaseHandler.getPowerConsumptionHistory();
        Map<String,Map<String,Map<String,Map<String,Map<String,Double>>>>> powerConsumptionHistoryMap = buildPowerConsumptionHistoryMap(powerConsumptionHistoryFb);
        

        return v;
    }

    private Map<String,Map<String,Map<String,Map<String,Map<String,Double>>>>>
        buildPowerConsumptionHistoryMap(Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Double>>>>>> powerConsumptionHistoryFb)
    {
        Map<String,Map<String,Map<String,Map<String,Map<String,Double>>>>> powerConsumptionHistoryMap = new HashMap<>();

        for(Map.Entry<String, Map<String,Map<String,Map<String,Map<String,Map<String,Double>>>>>> deviceMap : powerConsumptionHistoryFb.entrySet()) //<device, Map>
        {
            for(Map.Entry<String,Map<String,Map<String,Map<String,Map<String,Double>>>>> yearMap : deviceMap.getValue().entrySet())
            {
                for(Map.Entry<String,Map<String,Map<String,Map<String,Double>>>> mounthMap : yearMap.getValue().entrySet())
                {
                    for(Map.Entry<String,Map<String,Map<String,Double>>> dayMap : mounthMap.getValue().entrySet())
                    {

                        for(Map.Entry<String,Map<String,Double>> hourMap : dayMap.getValue().entrySet())
                        {
                            Map<String, Double> hourValueMap = new HashMap<>();

                            Double hourCounter = 0.0;
                            for(Map.Entry<String,Double> valueMap : hourMap.getValue().entrySet())
                                hourCounter += valueMap.getValue();

                            hourCounter = hourCounter / (hourMap.getValue().size());

                            hourValueMap.put(hourMap.getKey(), hourCounter);
                        }
                    }
                }
            }
        }

        return powerConsumptionHistoryMap;
    }
}
