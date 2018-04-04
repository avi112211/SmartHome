package com.example.avi.smarthome.UI;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.avi.smarthome.Firebase.FirebaseHandler;
import com.example.avi.smarthome.OpenHab.Item;
import com.example.avi.smarthome.OpenHab.OpenHabHandler;
import com.example.avi.smarthome.OpenHab.StateDescription;
import com.example.avi.smarthome.R;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener,
        View.OnFocusChangeListener, Observer {
    private TableLayout table;
    OpenHabHandler openHabHandler = null;
    FirebaseHandler firebaseHandler = null;
    ArrayList<Item> itemsInRoom = new ArrayList<>();
    private String roomName = "";
    private String itemName = "";
    private String path = "";
    private Map<String, Map<String, Object>> typeToElementMapping = new HashMap<>();
    private Map<String, String> oldData = new HashMap<>();
    private boolean firstRun = true;
    private boolean changeFromApp = false;
    private boolean updateServer = true;
    private String status = "";

    public ItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_item, container, false);
        firstRun = true;
        openHabHandler = OpenHabHandler.getInstance(getActivity().getApplicationContext());
        firebaseHandler = FirebaseHandler.getInstance(getActivity().getApplicationContext());

        itemsInRoom = openHabHandler.getItemsInRoom();
        if(itemsInRoom.size() > 0)
        {
            String name = itemsInRoom.get(0).getName();
            itemName = name.substring(0,name.lastIndexOf("_"));
        }

        TextView headline = (TextView) v.findViewById(R.id.headlinetext);
        headline.setText(getArguments().getString("label"));
        status = getArguments().getString("status");
        roomName = getArguments().getString("room").replace(" ", "+");
        path = ("/rooms/" + roomName + "/" + itemName).replace("+", " ");

        table = (TableLayout) v.findViewById(R.id.tableItems);
        table.removeAllViews();

        buildTable();
        firebaseHandler.addObserver(this);
        firebaseHandler.startFBlistener(path);

        return v;
    }

    private void buildTable()
    {
        int count = itemsInRoom.size();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(600, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;

        Collections.reverse(itemsInRoom);

        for(Item item : itemsInRoom)
        {
            String label = item.getLabel();
            String type = item.getType().toLowerCase();
            String itemName = item.getName().substring(item.getName().lastIndexOf("_") + 1);

            switch (type)
            {
                case "dimmer":
                    StateDescription stateDescription = item.getStateDescription();

                    TableRow tr = new TableRow(getActivity());

                    TextView name = new TextView(getActivity());
                    name.setText(label);
                    name.setPadding(10, 10, 300, 10);

                    tr.addView(name);
                    table.addView(tr);

                    TableRow tr2 = new TableRow(getActivity());
                    SeekBar bar = new SeekBar(getActivity());

                    int stateVal = 0;
                    if(item.getState() != null && !item.getState().toLowerCase().equals("null"))
                        stateVal = Integer.parseInt(item.getState().replace(",",""));

                    bar.setProgress(stateVal);

                    TextView state = new TextView(getActivity());

                    String statVal = "";
                    if(stateDescription != null)
                        statVal = String.format(stateDescription.getPattern(), stateVal);
                    else
                        statVal = item.getState();

                    if(statVal.toLowerCase().equals("null"))
                        state.setText("0");
                    else
                        state.setText(statVal);

                    Map<TextView, Item> textToItem = new HashMap<>();
                    textToItem.put(state, item);
                    bar.setTag(textToItem);
                    bar.setOnSeekBarChangeListener(this);
                    tr2.addView(bar);
                    tr2.addView(state);

                    TableRow dimmerLineRow = addLineRow();

                    table.addView(tr2);
                    table.addView(dimmerLineRow);
                    Map<String, Object> typeToElement = new HashMap<>();
                    typeToElement.put(type, bar);
                    typeToElementMapping.put(itemName, typeToElement);

                    break;
                case "switch":
                    StateDescription switchStateDescription = item.getStateDescription();

                    TableRow switchRow = new TableRow(getActivity());

                    TextView switchName = new TextView(getActivity());
                    switchName.setText(item.getLabel());
                    switchName.setPadding(10, 10, 100, 10);

                    Switch switchWiget = new Switch(getActivity());
                    //state = ON OFF NULL
                    if(item.getState().toLowerCase().equals("on"))
                        switchWiget.setChecked(true);
                    else
                        switchWiget.setChecked(false);

                    if(switchStateDescription != null && switchStateDescription.isReadOnly())
                        switchWiget.setEnabled(false);

                    switchWiget.setOnCheckedChangeListener(this);

                    Map<Switch, Item> switchTextToItem = new HashMap<>();
                    switchTextToItem.put(switchWiget, item);

                    switchWiget.setTag(switchTextToItem);
                    switchRow.addView(switchName);
                    switchRow.addView(switchWiget);

                    TableRow switchLineRow = addLineRow();

                    table.addView(switchRow);
                    table.addView(switchLineRow);

                    Map<String, Object> switchTypeToElement = new HashMap<>();
                    switchTypeToElement.put(type, switchWiget);
                    typeToElementMapping.put(itemName, switchTypeToElement);

                    break;
                case "number":
                    StateDescription numberStateDescription = item.getStateDescription();
                    if(numberStateDescription == null)
                        break;

                    TableRow numberRow = new TableRow(getActivity());

                    TextView numberName = new TextView(getActivity());
                    numberName.setText(label);
                    numberName.setPadding(10, 10, 300, 10);

                    numberRow.addView(numberName);

                    Map<String, Object> numberTypeToElement = new HashMap<>();

                    if(numberStateDescription.isReadOnly())
                    {
                        TextView numberState = new TextView(getActivity());
                        String numberVal = "";
                        if(item.getState() == null || item.getState().toLowerCase().equals("null"))
                            numberVal = "N/A";
                        else
                            numberVal = String.format(numberStateDescription.getPattern(), Float.parseFloat(item.getState()));
                        numberState.setText(numberVal);
                        numberState.setTag(item);
                        //numberState.setPadding(10, 10, 10, 10);

                        numberRow.addView(numberState);

                        TableRow numberLineRow = addLineRow();

                        table.addView(numberRow);
                        table.addView(numberLineRow);
                        numberTypeToElement.put(type, numberState);
                    }
                    else
                    {
                        final EditText numberText = new EditText(getActivity());
                        numberText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        String val = item.getState();
                        String numberVal = "";

                        if((val.toLowerCase().equals("n/a") || val.toLowerCase().equals("null")) && numberStateDescription.getPattern().toLowerCase().contains("d"))
                            numberVal = String.format(numberStateDescription.getPattern(), 0);
                        else if((val.toLowerCase().equals("n/a") || val.toLowerCase().equals("null")) && numberStateDescription.getPattern().toLowerCase().contains("f"))
                            numberVal = String.format(numberStateDescription.getPattern(), 0.0);
                        else if(numberStateDescription.getPattern().toLowerCase().contains("d"))
                            numberVal = String.format(numberStateDescription.getPattern(), Integer.parseInt(val));
                        else
                            numberVal = String.format(numberStateDescription.getPattern(), Float.parseFloat(val));

                        numberText.setText(numberVal);
                        numberRow.addView(numberText);

                        TableRow numberLineRow = addLineRow();

                        table.addView(numberRow);
                        table.addView(numberLineRow);

                        numberText.setTag(item);

                        numberText.setOnFocusChangeListener(this);
                        numberTypeToElement.put(type, numberText);
                    }

                    typeToElementMapping.put(itemName, numberTypeToElement);
                    break;

                case "color":
                    if(item.getState() == null || item.getState().toLowerCase().equals("null"))
                        break;

                    TableRow colorRow = new TableRow(getActivity());

                    TextView colorName = new TextView(getActivity());
                    colorName.setText(label);
                    colorName.setPadding(10, 10, 30, 10);

                    colorRow.addView(colorName);

                    TableRow colorRow2 = new TableRow(getActivity());
                    SeekBar colorBar = new SeekBar(getActivity());
                    colorBar.setMax(360);
                    //colorBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

                    colorBar.setProgress(Integer.parseInt(item.getState().split(",")[0]));

                    Map<SeekBar, Item> numberTextToItem = new HashMap<>();
                    numberTextToItem.put(colorBar, item);
                    colorBar.setTag(numberTextToItem);
                    colorBar.setOnSeekBarChangeListener(this);
                    colorRow2.addView(colorBar);

                    TableRow colorLineRow = addLineRow();

                    table.addView(colorRow);
                    table.addView(colorRow2);
                    table.addView(colorLineRow);

                    Map<String, Object> colorTypeToElement = new HashMap<>();
                    colorTypeToElement.put(type, colorBar);
                    typeToElementMapping.put(itemName, colorTypeToElement);

                    break;

                case "datetime":
                    TableRow datetimeRow = new TableRow(getActivity());

                    TextView datetimeName = new TextView(getActivity());
                    datetimeName.setText(label);
                    datetimeName.setPadding(10, 10, 30, 10);

                    datetimeRow.addView(datetimeName);

                    TableRow datetimeRow2 = new TableRow(getActivity());
                    String []time = item.getState().toLowerCase().split("t");

                    TextView datetimeTime = new TextView(getActivity());
                    datetimeTime.setText(time[0] + " " + time[1].split("\\.")[0]);
                    datetimeTime.setPadding(10, 10, 30, 10);
                    datetimeRow2.addView(datetimeTime);

                    TableRow datetimeLineRow = addLineRow();

                    table.addView(datetimeRow);
                    table.addView(datetimeRow2);
                    table.addView(datetimeLineRow);

                    Map<String, Object> datetimeTypeToElement = new HashMap<>();
                    datetimeTypeToElement.put(type, datetimeTime);
                    typeToElementMapping.put(itemName, datetimeTypeToElement);

                    break;
                default:
                    break;
            }
        }

        if(status.toLowerCase().equals("offline"))
        {
            TableRow statusRow = new TableRow(getActivity());

            TextView status = new TextView(getActivity());
            status.setTextColor(Color.RED);
            status.setText("The Device Is Offline!!");
            status.setPadding(10, 10, 300, 10);
            statusRow.addView(status);
            table.addView(statusRow);
        }
    }

    private TableRow addLineRow()
    {
        TableRow lineRow = new TableRow(getActivity());
        TextView line1 = new TextView(getActivity());
        line1.setText("------------------------------------------------------------------");
        line1.setTextColor(Color.BLUE);
        TextView line2 = new TextView(getActivity());
        line2.setText("------------");
        line2.setTextColor(Color.BLUE);
        lineRow.addView(line1);
        lineRow.addView(line2);

        return lineRow;
    }

    @Override
    public void onDestroy() {
        //firstRun = true;
        firebaseHandler.stopFBlistener(path);
        firebaseHandler.deleteObserver(this);
        super.onDestroy();
    }

    public void onBackPressed()
    {
        firebaseHandler.stopFBlistener(path);
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        changeProgressBar(seekBar, i);
    }

    private void changeProgressBar(SeekBar seekBar, int val)
    {
        Map<Object, Item> textToItem = (Map<Object, Item>) seekBar.getTag();

        for(Map.Entry<Object, Item> entry : textToItem.entrySet())
        {
            String type = entry.getValue().getType().toLowerCase();

            if (type.equals("dimmer"))
            {
                TextView tv = (TextView) entry.getKey();
                String statVal = "";
                if(entry.getValue().getStateDescription() != null)
                    statVal = String.format(entry.getValue().getStateDescription().getPattern(), val);
                else
                    statVal = val + "";
                tv.setText(statVal);
            }
        }

        Item item = null;

        for(Map.Entry<Object, Item> entry : textToItem.entrySet()){
            item = entry.getValue();
        }

        oldData.put(item.getName().substring(item.getName().lastIndexOf("_") + 1), val + "");

        if(updateServer)
        {
            if(item.getType().toLowerCase().equals("color"))
            {
                String url = "PostGetReq?type=items&room=" + roomName + "&val=" + val + ",99,100" + "&name=" + item.getName();
                changeFromApp = true;
                openHabHandler.sendPost(url, "items", val + ",99,100");
            }

            else
            {
                String url = "PostGetReq?type=items&room=" + roomName + "&val=" + val + "&name=" + item.getName();
                changeFromApp = true;
                openHabHandler.sendPost(url, "items", val);
            }
        }
        else
            updateServer = true;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        changeFromApp = true;
        int val = seekBar.getProgress();
        Item item = null;

        Map<Object, Item> textToItem = (Map<Object, Item>) seekBar.getTag();

        for(Map.Entry<Object, Item> entry : textToItem.entrySet()){
            item = entry.getValue();
        }

        oldData.put(item.getName().substring(item.getName().lastIndexOf("_") + 1), val + "");
        String url = "PostGetReq?type=items&room=" + roomName + "&val=" + val + "&name=" + item.getName();
        openHabHandler.sendPost(url, "items", val);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeFromApp = false;
            }
        }, 4000);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Map<Switch, Item> switchTextToItem = (Map<Switch, Item>) compoundButton.getTag();
        String url = "PostGetReq?type=items&room=" + roomName;
        Item item = null;

        for(Map.Entry<Switch, Item> entry : switchTextToItem.entrySet()){
            item = entry.getValue();
        }

        if(b) {
            url = url + "&val=ON&name=" + item.getName();
            oldData.put(item.getName().substring(item.getName().lastIndexOf("_") + 1), "ON");
        }
        else{
            url = url + "&val=OFF&name=" + item.getName();
            oldData.put(item.getName().substring(item.getName().lastIndexOf("_") + 1), "OFF");
        }

        //changeFromApp = true;
        if(updateServer)
            openHabHandler.sendPost(url, "items", b);
        else
            updateServer = true;

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText e = (EditText) v;
        if(hasFocus){
            e.setText("");
        }
        else
        {
            String val = e.getText().toString();
            Item item = (Item) v.getTag();
            String numberVal = "";
            if(item.getStateDescription().getPattern().toLowerCase().contains("d"))
                numberVal = String.format(item.getStateDescription().getPattern(), Integer.parseInt(val));
            else
                numberVal = String.format(item.getStateDescription().getPattern(), Float.parseFloat(val));
            e.setText(numberVal);

            oldData.put(item.getName().substring(item.getName().lastIndexOf("_") + 1), val);

            String url = "PostGetReq?type=items&room=" + roomName + "&val=" + val + "&name=" + item.getName();
            //changeFromApp = true;
            if(updateServer)
                openHabHandler.sendPost(url, "items", val);
            else
                updateServer = true;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(firstRun)
        {
            firstRun = false;
            return;
        }

        if(changeFromApp)
            return;

        Map<String, String> data = firebaseHandler.getDataFromListener();

        for(Map.Entry entry : data.entrySet())
        {
            if(oldData.containsKey(entry.getKey()) && oldData.get(entry.getKey()).equals(entry.getValue()))
                continue;

            Map<String, Object> element = typeToElementMapping.get(entry.getKey().toString());

            if(element == null)
                continue;

            for(Map.Entry el : element.entrySet()) //only one time
            {
                updateServer = false;
                switch (el.getKey().toString())
                {
                    case "dimmer":
                        SeekBar dimmerBar = (SeekBar) el.getValue();
                        int stateVal = 0;
                        if(entry.getValue() != null && !((String)entry.getValue()).toLowerCase().equals("null")
                                && !((String)entry.getValue()).toLowerCase().equals("n/a"))
                            stateVal = Integer.parseInt((String) entry.getValue());
                        dimmerBar.setProgress(stateVal);
                        oldData.put(entry.getKey().toString(), stateVal + "");
                        break;

                    case "switch":
                        Switch switchButton = (Switch) el.getValue();
                        if(entry.getValue().equals("ON")) {
                            switchButton.setChecked(true);
                            oldData.put(entry.getKey().toString(), "ON");
                        }
                        else {
                            switchButton.setChecked(false);
                            oldData.put(entry.getKey().toString(), "OFF");
                        }

                        if(entry.getValue().toString().toLowerCase().equals("n/a"))
                            oldData.put(entry.getKey().toString(), entry.getValue() + "");
                        break;

                    case "number":
                        Item numberItem = null;
                        if(el.getValue() instanceof EditText) {
                            EditText editText = (EditText) el.getValue();
                            numberItem = (Item) editText.getTag();
                        }

                        else if(el.getValue() instanceof TextView) {
                            TextView textView = (TextView) el.getValue();
                            numberItem = (Item) textView.getTag();
                        }

                        String val = (String) entry.getValue();

                        String numberVal = "";
                        if(val.toLowerCase().equals("n/a") && numberItem.getStateDescription().getPattern().toLowerCase().contains("d"))
                            numberVal = String.format(numberItem.getStateDescription().getPattern(), 0);
                        else if(val.toLowerCase().equals("n/a") && numberItem.getStateDescription().getPattern().toLowerCase().contains("f"))
                            numberVal = String.format(numberItem.getStateDescription().getPattern(), 0.0);
                        else if(numberItem.getStateDescription().getPattern().toLowerCase().contains("d"))
                            numberVal = String.format(numberItem.getStateDescription().getPattern(), Integer.parseInt(val));
                        else
                            numberVal = String.format(numberItem.getStateDescription().getPattern(), Float.parseFloat(val));

                        if(el.getValue() instanceof EditText) {
                            EditText editText = (EditText) el.getValue();
                            editText.setText(numberVal);
                        }

                        else if(el.getValue() instanceof TextView) {
                            TextView textView = (TextView) el.getValue();
                            textView.setText(numberVal);
                        }

                        oldData.put(numberItem.getName().substring(numberItem.getName().lastIndexOf("_") + 1), (String) entry.getValue());
                        break;
                    case "color":
                        SeekBar colorBar = (SeekBar) el.getValue();
                        String colorVal = entry.getValue().toString().split(",")[0];
                        colorBar.setProgress(Integer.parseInt(colorVal));
                        //changeProgressBar(colorBar, Integer.parseInt(colorVal));
                        break;

                    case "datetime":
                        TextView textView = (TextView) el.getValue();
                        String []time = entry.getValue().toString().toLowerCase().split("t");
                        textView.setText(time[0] + " " + time[1].split("\\.")[0]);
                        oldData.put(entry.getKey().toString(), entry.getValue() + "");
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
