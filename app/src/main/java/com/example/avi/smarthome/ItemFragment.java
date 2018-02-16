package com.example.avi.smarthome;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
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

import com.example.avi.smarthome.OpenHab.Item;
import com.example.avi.smarthome.OpenHab.OpenHabHandler;
import com.example.avi.smarthome.OpenHab.StateDescription;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {
    private TableLayout table;
    OpenHabHandler openHabHandler = null;
    ArrayList<Item> itemsInRoom = new ArrayList<>();
    //LinearLayout l;

    public ItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_item, container, false);
        openHabHandler = OpenHabHandler.getInstance(getActivity().getApplicationContext());

        //l = (LinearLayout) v.findViewById(R.id.linearLayout);

        itemsInRoom = openHabHandler.getItemsInRoom();
        TextView headline = (TextView) v.findViewById(R.id.headlinetext);
        headline.setText(getArguments().getString("label"));

        table = (TableLayout) v.findViewById(R.id.tableItems);
        table.removeAllViews();

        buildTable();

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
                    bar.setProgress(Integer.parseInt(item.getState().replace(",","")));
                    TextView state = new TextView(getActivity());

                    String statVal = "";
                    if(stateDescription != null)
                        statVal = String.format(stateDescription.getPattern(), Integer.parseInt(item.getState()));
                    else
                        statVal = item.getState();
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

                    if(numberStateDescription.isReadOnly())
                    {
                        TextView numberState = new TextView(getActivity());
                        String numberVal = String.format(numberStateDescription.getPattern(), Float.parseFloat(item.getState()));
                        numberState.setText(numberVal);

                        numberRow.addView(numberState);

                        TableRow numberLineRow = addLineRow();

                        table.addView(numberRow);
                        table.addView(numberLineRow);
                    }
                    else
                    {
                        final EditText numberText = new EditText(getActivity());
                        numberText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        if(!item.getState().toLowerCase().equals("null"))
                        {
                            String numberVal = "";
                            if(numberStateDescription.getPattern().toLowerCase().contains("d"))
                                numberVal = String.format(numberStateDescription.getPattern(), Integer.parseInt(item.getState()));
                            else
                                numberVal = String.format(numberStateDescription.getPattern(), Float.parseFloat(item.getState()));
                            numberText.setText(numberVal);
                            numberRow.addView(numberText);

                            TableRow numberLineRow = addLineRow();

                            table.addView(numberRow);
                            table.addView(numberLineRow);
                        }

                        numberText.setTag(item);

                        numberText.setOnFocusChangeListener(this);
                    }

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

                    break;
                default:
                    break;
            }
        }
    }

    private TableRow addLineRow()
    {
        TableRow lineRow = new TableRow(getActivity());
        TextView line1 = new TextView(getActivity());
        line1.setText("-------------------------------------------------------------------");
        line1.setTextColor(Color.BLUE);
        TextView line2 = new TextView(getActivity());
        line2.setText("------------");
        line2.setTextColor(Color.BLUE);
        lineRow.addView(line1);
        lineRow.addView(line2);

        return lineRow;
    }

    public void onBackPressed() {
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        Map<Object, Item> textToItem = (Map<Object, Item>) seekBar.getTag();

        for(Map.Entry<Object, Item> entry : textToItem.entrySet())
        {
            String type = entry.getValue().getType().toLowerCase();

            if (type.equals("dimmer"))
            {
                TextView tv = (TextView) entry.getKey();
                String statVal = "";
                if(entry.getValue().getStateDescription() != null)
                    statVal = String.format(entry.getValue().getStateDescription().getPattern(), i);
                else
                    statVal = i + "";
                tv.setText(statVal);
            }
        }

        Item item = null;

        for(Map.Entry<Object, Item> entry : textToItem.entrySet()){
            item = entry.getValue();
        }

        if(item.getType().toLowerCase().equals("color"))
        {
            String url = "PostGetReq?type=items&val=" + i + ",99,100" + "&name=" + item.getName();
            openHabHandler.sendPost(url, "items", i + ",99,100");
        }

        else
        {
            String url = "PostGetReq?type=items&val=" + i + "&name=" + item.getName();
            openHabHandler.sendPost(url, "items", i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int val = seekBar.getProgress();
        Item item = null;

        Map<Object, Item> textToItem = (Map<Object, Item>) seekBar.getTag();

        for(Map.Entry<Object, Item> entry : textToItem.entrySet()){
            item = entry.getValue();
        }

        String url = "PostGetReq?type=items&val=" + val + "&name=" + item.getName();
        openHabHandler.sendPost(url, "items", val);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Map<Switch, Item> switchTextToItem = (Map<Switch, Item>) compoundButton.getTag();
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

            String url = "PostGetReq?type=items&val=" + val + "&name=" + item.getName();
            openHabHandler.sendPost(url, "items", val);
        }
    }
}
