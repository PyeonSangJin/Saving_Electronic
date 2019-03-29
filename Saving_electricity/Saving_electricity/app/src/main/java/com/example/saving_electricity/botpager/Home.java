package com.example.saving_electricity.botpager;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.saving_electricity.R;
import com.example.saving_electricity.network.NetworkUtil;
import com.example.saving_electricity.util.Config;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home extends Fragment implements OnChartValueSelectedListener {

    PieChart pieChart;
    NetworkUtil networkUtil;
    RequestQueue mRequestQueue;
    TextView txtStatus;
    double room1=0;
    double room2=0;
    double room3=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.home, container, false);
        networkUtil = new NetworkUtil(getContext());
        pieChart = (PieChart) v.findViewById(R.id.piechart);
        txtStatus = (TextView) v.findViewById(R.id.txtStatus);
        mRequestQueue = Volley.newRequestQueue(getContext());
        requestLedStatus();
        setPieChart(100,0,0);

        new GetPieChartData().execute();
        return v;
    }

    class GetPieChartData extends AsyncTask<Void, String, Void> {
        //백그라운드
        @Override
        protected Void doInBackground(Void... voids) {
            requestLedStatus();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txtStatus.setText(Float.toString((float) (room1+room2+room3) * 10));
        }

    }

    private void requestLedStatus(){
        JsonObjectRequest myRequest = new JsonObjectRequest(
                Config.MAIN_URL + Config.GET_STATUS_DATA, null,
                (Response.Listener) networkSuccessListener(),
                (Response.ErrorListener) networkErrorListener());

        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                Config.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(myRequest);
    }

    private Response.Listener<JSONObject> networkSuccessListener() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.e("asdfasdf",response.toString());
                try {
                    room1 = response.getDouble("room1");
                    room2 = response.getDouble("room2");
                    room3 = response.getDouble("room3");
                    String status = response.getString("status");
                    txtStatus.setText(Float.toString((float) (room1+room2+room3)));
                    if(status.equals("auto")){
                        setPieChart(33,33,33);
                    }else{
                        setPieChart(room1,room2,room3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener networkErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("pieChart",error.toString());
            }
        };
    }


    private void setPieChart(double room1, double room2, double room3){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setHoleRadius(50f);
        pieChart.setDragDecelerationFrictionCoef(0.95f);;

        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry((float) room1,"1동"));
        yValues.add(new PieEntry((float) room2,"2동"));
        yValues.add(new PieEntry((float) room3,"3동"));

        Description description = new Description();
        description.setText("동별 전력 사용 추이"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"동");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);}


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}
