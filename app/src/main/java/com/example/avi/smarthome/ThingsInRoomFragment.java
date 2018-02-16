package com.example.avi.smarthome;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.avi.smarthome.OpenHab.OpenHabHandler;
import com.example.avi.smarthome.OpenHab.Thing;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThingsInRoomFragment extends Fragment implements View.OnClickListener
{
    String roomName = "";
    private TableLayout table;
    OpenHabHandler openHabHandler = null;
    private ProgressBar spinner;

    public ThingsInRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_things_in_room, container, false);
        openHabHandler = OpenHabHandler.getInstance(getActivity().getApplicationContext());

        spinner=(ProgressBar) v.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);


        roomName = getArguments().getString("roomName");
        TextView headline = (TextView) v.findViewById(R.id.headlinetext);
        headline.setText(headline.getText() + roomName);

        table = (TableLayout) v.findViewById(R.id.tableDevices);
        table.removeAllViews();

        buildTable();

        return v;

    }

    private void buildTable()
    {
        ArrayList<Thing> things = openHabHandler.getThingsPerRoom().get(roomName);

        for(Thing thing : things)
        {
            String name = thing.getLabel();

            TableRow tr = new TableRow(getActivity());
            Button btn = new Button(getActivity().getApplicationContext());

            if(name.trim().equals("") || name.trim().equals("\"\""))
                btn.setText(thing.getThingTypeUID());
            else
                btn.setText(name);

            btn.setTextSize(18);
            btn.setTag(thing);
            btn.setOnClickListener(this);
            //tv1.setPadding(50/count, 50/count, 50/count, 50/count);
            tr.addView(btn);
            tr.setGravity(Gravity.CENTER_HORIZONTAL);

            table.addView(tr);
        }
    }

    @Override
    public void onClick(View view) {
        spinner.setVisibility(View.VISIBLE);

        final Thing thing = (Thing) view.getTag();
        openHabHandler.sendGet("PostGetReq?type=items&name=" + thing.getUID(), "items");

        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                while (true){
                    if(openHabHandler.isDataReady()) {
                        openHabHandler.setDataReady(false);
                        //spinner.setVisibility(View.GONE);

                        ItemFragment itemFragment = new ItemFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("label", thing.getLabel());
                        itemFragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, itemFragment, "bb");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    }
                }
            }
        });
        t.start();

            //t.join();

            Log.i("PostActivity", "aa");

    }
}
