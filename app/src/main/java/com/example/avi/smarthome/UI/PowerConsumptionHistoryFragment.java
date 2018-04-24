package com.example.avi.smarthome.UI;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.avi.smarthome.Firebase.FirebaseHandler;
import com.example.avi.smarthome.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class PowerConsumptionHistoryFragment extends Fragment implements View.OnClickListener {
    private FirebaseHandler firebaseHandler = null;
    private Map<String,Map<String,Map<String,Map<String,Map<String,Float>>>>> powerConsumptionHistoryMap = new HashMap<>();
    private BarChart barChart;
    private ListView lv;
    private ArrayList<Button> btnList = new ArrayList<>();
    private HashMap<String, String> monthNumToNameMapping = new HashMap<>();

    public PowerConsumptionHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View v = inflater.inflate(R.layout.fragment_power_consumption_history, container, false);
        firebaseHandler = FirebaseHandler.getInstance(getActivity().getApplicationContext());
        Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Number>>>>>> powerConsumptionHistoryFb = firebaseHandler.getPowerConsumptionHistory();
        powerConsumptionHistoryMap = buildPowerConsumptionHistoryMap(powerConsumptionHistoryFb);

        lv = (ListView) v.findViewById(R.id.listView);
        Button yearlyBtn = (Button) v.findViewById(R.id.btnYearly);
        Button monthlybtn = (Button) v.findViewById(R.id.btnMonthly);
        Button dailyBtn = (Button) v.findViewById(R.id.btnDaily);

        yearlyBtn.setOnClickListener(this);
        monthlybtn.setOnClickListener(this);
        dailyBtn.setOnClickListener(this);

        btnList.add(yearlyBtn);
        btnList.add(monthlybtn);
        btnList.add(dailyBtn);

        buildMonthNumToNameMapping();
        buildGraphData("day");

        return v;
    }

    private Map<String, Map<String, Map<String, Map<String, Map<String, Float>>>>>
        buildPowerConsumptionHistoryMap(Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Number>>>>>> powerConsumptionHistoryFb)
    {
        Map<String,Map<String,Map<String,Map<String,Map<String,Float>>>>> powerConsumptionHistoryMap = new LinkedHashMap<>();

        for(Map.Entry<String, Map<String,Map<String,Map<String,Map<String,Map<String,Number>>>>>> deviceMap : powerConsumptionHistoryFb.entrySet()) //<device, Map>
        {
            Map<String, Map<String, Map<String, Map<String, Float>>>> yearMapValue = new LinkedHashMap<>();
            for(Map.Entry<String,Map<String,Map<String,Map<String,Map<String,Number>>>>> yearMap : deviceMap.getValue().entrySet())
            {
                Map<String, Map<String, Map<String, Float>>> mounthValueMap = new LinkedHashMap<>();
                for(Map.Entry<String,Map<String,Map<String,Map<String,Number>>>> mounthMap : yearMap.getValue().entrySet())
                {
                    Map<String, Map<String, Float>> dayValueMap = new LinkedHashMap<>();
                    for(Map.Entry<String,Map<String,Map<String,Number>>> dayMap : mounthMap.getValue().entrySet())
                    {
                        Map<String, Float> hourValueMap = new LinkedHashMap<>();
                        for(Map.Entry<String,Map<String,Number>> hourMap : dayMap.getValue().entrySet())
                        {
                            float hourCounter = 0;
                            for(Map.Entry<String,Number> valueMap : hourMap.getValue().entrySet())
                                hourCounter += valueMap.getValue().floatValue();

                            hourCounter = hourCounter / (hourMap.getValue().size());
                            DecimalFormat decimalFormat = new DecimalFormat("#.##");
                            hourCounter = Float.valueOf(decimalFormat.format(hourCounter));
                            hourValueMap.put(hourMap.getKey() + ":00", hourCounter);
                        }
                        dayValueMap.put(dayMap.getKey(), hourValueMap);
                    }
                    mounthValueMap.put(mounthMap.getKey(), dayValueMap);
                }
                yearMapValue.put(yearMap.getKey(), mounthValueMap);
            }
            powerConsumptionHistoryMap.put(deviceMap.getKey(), yearMapValue);
        }

        return powerConsumptionHistoryMap;
    }

    private void buildGraphData(String graphType)
    {
        ArrayList<Pair<BarData, Pair<String, ArrayList<String>>>> list = new ArrayList<>();
        Map<String, Map<String, Float>> dailyMap = buildDailyMap( powerConsumptionHistoryMap.get("miio_generic_ab729bcf"), graphType);

        for(Map.Entry<String, Map<String, Float>> dailyEntry : dailyMap.entrySet())
            list.add(generateBarData(dailyEntry.getKey(), dailyEntry.getValue()));

        ChartDataAdapter chartDataAdapter = new ChartDataAdapter(getContext(), list);
        lv.setAdapter(chartDataAdapter);
    }

    private Pair<BarData, Pair<String, ArrayList<String>>> generateBarData(String date, Map<String, Float> map)
    {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> hours = new ArrayList<>();

        int i = 0;
        for(Map.Entry<String, Float> entry : map.entrySet()){
            entries.add(new BarEntry(i, entry.getValue()));
            hours.add(entry.getKey());
            i++;
        }

        /*
        int n = hours.size();
        String tempHour = "0";
        BarEntry tempEntry;
        for(i=0; i < n; i++){
            for(int j=1; j < (n-i); j++){
                if(Integer.parseInt(hours.get(j - 1)) > Integer.parseInt(hours.get(j))){
                    //swap elements
                    tempHour = hours.get(j-1);
                    tempEntry = entries.get(j-1);
                    hours.set(j - 1, hours.get(j));
                    entries.set(j - 1, entries.get(j));
                    hours.set(j, tempHour);
                    entries.set(j, tempEntry);
                }
            }
        }
        */

        BarDataSet dataSet = new BarDataSet(entries, "avg");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setBarShadowColor(Color.rgb(203, 203, 203));

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        Pair<BarData, Pair<String, ArrayList<String>>> returnPair = new Pair<>(data, new Pair<String, ArrayList<String>>(date, hours));

        return returnPair;
    }

    private Map<String, Map<String, Float>> buildDailyMap(Map<String, Map<String, Map<String, Map<String, Float>>>> itemMap, String graphType)
    {
        Map<String, Map<String, Float>> returnMap = new TreeMap<>(Collections.reverseOrder());

        for(Map.Entry<String, Map<String, Map<String, Map<String, Float>>>> yearEntry : itemMap.entrySet())
        {
            String year = yearEntry.getKey();
            Map<String, Float> monthAvgMap = new TreeMap<>();
            int numOfDaysInMonth = 0;

            for(Map.Entry<String, Map<String, Map<String, Float>>> monthEntry : yearEntry.getValue().entrySet())
            {
                String month = monthEntry.getKey();
                Map<String, Float> dayAvgMap = new TreeMap<>();
                Float monthAvg = new Float(0);

                for(Map.Entry<String, Map<String, Float>> dayEntry : monthEntry.getValue().entrySet())
                {
                    String day = dayEntry.getKey();
                    switch (graphType)
                    {
                        case "day":
                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String date = day + "/" + month + "/" + year;
                            try {
                                returnMap.put(new Date(dateFormat.parse(date).getTime()).toString(), dayEntry.getValue());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "year":
                        case "month":
                            Float dayAvg = new Float(0);
                            for(Map.Entry<String, Float> dayDetails : dayEntry.getValue().entrySet())
                                dayAvg += dayDetails.getValue();
                            dayAvg = dayAvg / dayEntry.getValue().size();
                            dayAvgMap.put(day + "th " + monthNumToNameMapping.get(month), dayAvg);
                            break;
                        case "year1":
                            for(Map.Entry<String, Float> dayDetails : dayEntry.getValue().entrySet()) {
                                monthAvg += dayDetails.getValue();
                                numOfDaysInMonth++;
                            }
                            break;
                    }
                }

                if(graphType.equals("month"))
                    returnMap.put(monthNumToNameMapping.get(month) + " " + year, dayAvgMap);

                else if(graphType.equals("year"))
                    monthAvgMap.put(monthNumToNameMapping.get(month), monthAvg / numOfDaysInMonth);
            }

            if(graphType.equals("year"))
                returnMap.put(year, monthAvgMap);
        }
        return returnMap;
    }

    private class ChartDataAdapter extends ArrayAdapter<Pair<BarData, Pair<String, ArrayList<String>>>>
    {
        public ChartDataAdapter(Context context, ArrayList<Pair<BarData, Pair<String, ArrayList<String>>>> objects){
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Pair<BarData, Pair<String, ArrayList<String>>> pairData = getItem(position);
            BarData data = pairData.first;

            ViewHolder holder = null;

            if(convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_barchart, null);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);
                TextView headline = (TextView) convertView.findViewById(R.id.headline);
                headline.setText(pairData.second.first);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
                TextView headline = (TextView) convertView.findViewById(R.id.headline);
                headline.setText(pairData.second.first.toString());
            }

            data.setValueTextColor(Color.BLACK);
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setDrawGridBackground(false);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(pairData.second.second));

            /*
            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    try {
                        return pairData.second.get((int) value);
                    }catch (Exception e)
                    {
                        return null;
                    }
                }
            };

            xAxis.setValueFormatter(formatter);
            */

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setLabelCount(10, false);
            leftAxis.setSpaceTop(15f);

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setLabelCount(10, false);
            rightAxis.setSpaceTop(15f);

            holder.chart.setData(data);
            holder.chart.setFitBars(true);

            holder.chart.animateY(500);

            return convertView;
        }

        private class ViewHolder
        {
            BarChart chart;
        }
    }

    private void buildMonthNumToNameMapping()
    {
        monthNumToNameMapping.put("1", "Jan");
        monthNumToNameMapping.put("01", "Jan");
        monthNumToNameMapping.put("2", "Feb");
        monthNumToNameMapping.put("02", "Feb");
        monthNumToNameMapping.put("3", "Mar");
        monthNumToNameMapping.put("03", "Mar");
        monthNumToNameMapping.put("4", "Apr");
        monthNumToNameMapping.put("04", "Apr");
        monthNumToNameMapping.put("5", "May");
        monthNumToNameMapping.put("05", "May");
        monthNumToNameMapping.put("6", "Jun");
        monthNumToNameMapping.put("06", "Jun");
        monthNumToNameMapping.put("7", "Jul");
        monthNumToNameMapping.put("07", "Jul");
        monthNumToNameMapping.put("8", "Aug");
        monthNumToNameMapping.put("08", "Aug");
        monthNumToNameMapping.put("9", "Sep");
        monthNumToNameMapping.put("09", "Sep");
        monthNumToNameMapping.put("10", "Oct");
        monthNumToNameMapping.put("11", "Nov");
        monthNumToNameMapping.put("12", "Dec");
    }

    @Override
    public void onClick(View v)
    {
        Button b = (Button) v;

        for(Button btn : btnList) {
            btn.setEnabled(true);
            btn.setTextColor(Color.BLACK);
        }

        b.setEnabled(false);
        b.setTextColor(Color.BLUE);

        switch (v.getId())
        {
            case R.id.btnDaily:
                buildGraphData("day");
                break;
            case R.id.btnMonthly:
                buildGraphData("month");
                break;
            case R.id.btnYearly:
                buildGraphData("year");
                break;
        }
    }
}
