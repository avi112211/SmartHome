package com.example.avi.smarthome.UI;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.avi.smarthome.Firebase.FirebaseHandler;
import com.example.avi.smarthome.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements Observer {
    FirebaseHandler firebaseHandler = null;
    private Map<String, String> personInRoomMap = new HashMap<>();
    private String path = "/personInRoom";
    private TextView textView = null;
    private TableLayout tableLayout = null;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_fraggment, container, false);
        firebaseHandler = FirebaseHandler.getInstance(getActivity().getApplicationContext());
        personInRoomMap = firebaseHandler.getPersonInRoomMap();
        textView = (TextView) v.findViewById(R.id.tv);
        tableLayout = (TableLayout) v.findViewById(R.id.pirtable);

        if(!personInRoomMap.isEmpty())
            buildPir();

        firebaseHandler.addObserver(this);
        firebaseHandler.startFBlistener(path);

        return v;
    }

    private void buildPir()
    {
        personInRoomMap = firebaseHandler.getPersonInRoomMap();
        tableLayout.removeAllViews();
        textView.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.VISIBLE);

        for(Map.Entry<String, String> entry : personInRoomMap.entrySet())
        {
            TableRow tr = new TableRow(getActivity());

            TextView roomName = new TextView(getActivity());
            roomName.setText(entry.getKey() + " - ");

            TextView pirStatus = new TextView(getActivity());
            pirStatus.setText(entry.getValue());

            tr.addView(roomName);
            tr.addView(pirStatus);

            tableLayout.addView(tr);
        }
    }

    @Override
    public void update(Observable o, Object arg)
    {
        buildPir();
    }

    @Override
    public void onDestroy() {
        //firstRun = true;
        firebaseHandler.stopFBlistener(path);
        firebaseHandler.deleteObserver(this);
        super.onDestroy();
    }
}
