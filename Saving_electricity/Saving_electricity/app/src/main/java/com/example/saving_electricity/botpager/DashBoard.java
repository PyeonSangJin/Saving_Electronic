package com.example.saving_electricity.botpager;

 import android.graphics.Color;
 import android.os.AsyncTask;
 import android.os.Bundle;
 import android.os.Handler;
 import android.support.v4.app.Fragment;
 import android.text.method.ScrollingMovementMethod;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.MotionEvent;
 import android.view.View;
import android.view.ViewGroup;
 import android.widget.ScrollView;
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
 import com.github.mikephil.charting.charts.LineChart;
 import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
 import com.github.mikephil.charting.components.XAxis;
 import com.github.mikephil.charting.components.YAxis;
 import com.github.mikephil.charting.data.Entry;
 import com.github.mikephil.charting.data.LineData;
 import com.github.mikephil.charting.data.LineDataSet;
 import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

 import org.json.JSONException;
 import org.json.JSONObject;
 import org.jsoup.Jsoup;
 import org.w3c.dom.Element;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.StringReader;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.List;

 import javax.xml.parsers.DocumentBuilder;
 import javax.xml.parsers.DocumentBuilderFactory;
 import javax.xml.parsers.ParserConfigurationException;

 import org.w3c.dom.Document;
 import org.w3c.dom.NodeList;
 import org.xml.sax.InputSource;
 import org.xml.sax.SAXException;

public class DashBoard extends Fragment {

    PieChart pieChart;
    LineChart realtimeChart;
   //  BarChart barChart;
    org.w3c.dom.Document doc = null;
    NetworkUtil networkUtil;
    TextView realTimeLog;
    String power;
    String percent;
    String date;
    RequestQueue mRequestQueue;
    ScrollView scrollView;
    String htmlUrl = "http://www.kpx.or.kr/www/contents.do?key=217";
    ArrayList elecData = new ArrayList<Float>();

    String year;
    String month;
    String datee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        getRealTimeData();



        View v = inflater.inflate(R.layout.dashboard, container, false);
        pieChart = (PieChart) v.findViewById(R.id.piechart);
        realTimeLog = (TextView) v.findViewById(R.id.txtRealtime);
        realTimeLog.setMovementMethod(new ScrollingMovementMethod());
        scrollView = (ScrollView) v.findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
        realTimeLog.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                scrollView.requestDisallowInterceptTouchEvent(true);
                //스크롤뷰가 텍스트뷰의 터치이벤트를 가져가지 못하게 함
                return false;
            }
        });

        mRequestQueue = Volley.newRequestQueue(getContext());
        realtimeChart = (LineChart)v.findViewById(R.id.realtimelog);
       // barChart = (BarChart)v.findViewById(R.id.barchart);
        networkUtil = new NetworkUtil(getContext());
  //      new GetPieChartData().execute();
        new GetXMLTask().execute("http://dataopen.kospo.co.kr/openApi/Gene/GenePwrInfoList?strSdate=20181001&strEdate=20181130&numOfRows=10&pageNo=2&serviceKey=cGvRSwco4YoseCllIvc7HRPscricHNGOZMSSfpHu7aPVievxl4zIi%2BNcLOKbDob568ZAlW8GZlz6ZpOOjZLmXg%3D%3D");
        setPieChart(100,0);
       // setBarChart();
       // setRealtimeChart();
        return v;
    }

    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue == null)
            return null;
        return nValue.getNodeValue();
    }

    private class GetXMLTask extends AsyncTask<String, Void, org.w3c.dom.Document>{
        @Override protected void onPreExecute() {


            super.onPreExecute();

        }


        @Override
        protected org.w3c.dom.Document doInBackground(String... strings) {


            URL url;
            HttpURLConnection connection = null;
            OutputStream os = null;
            String returnString = "";
            try {
                url = new URL(strings[0]);
                Log.e("ERRRRRRR",strings[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );

                os = connection.getOutputStream();
                os.flush();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //결과값 수신
            int rc = 0;
            try {
                rc = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(rc==200){

                InputStreamReader in = null;
                try {
                    in = new InputStreamReader(connection.getInputStream(),"utf-8");
                    BufferedReader br = new BufferedReader(in);
                    String strLine;
                    while ((strLine = br.readLine()) != null){
                        returnString = returnString.concat(strLine);
                    }
                    //결과값출력
                    Log.e("returnString",returnString.toString());

                    String xml = returnString;
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    InputSource is;
                    builder = factory.newDocumentBuilder();
                    is = new InputSource(new StringReader(xml));
                    Document doc = builder.parse(is);
                    NodeList list = doc.getElementsByTagName("header");

                    for(int temp = 0; temp < list.getLength(); temp++) {
                        Node nNode = list.item(temp);
                        if(nNode.getNodeType() == Node.ELEMENT_NODE){
                          Element eElement = (Element) nNode;
                          elecData.add(Float.parseFloat(getTagValue("sparepowerpers", eElement)));
                          setRealtimeChart(elecData);
                        }	// for end
                     }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                return doc;
            }else{
                System.out.println("http response code error: "+rc+"\n");
                return doc;
            }
        }

    }


    private void getRealTimeData(){
        final Handler handler = new Handler();


        Thread thread = new Thread(new Runnable() {
            boolean once = true;
            @Override
            public void run() {
                while(true){
                    try {
                        requestRealTimeData();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                realTimeLog.append("시간:"+date + " " +"공급예비율" +percent+ " " + "공급예비력"+power +"\n"
                                );
                            }
                        });
                        if(once) {
                            new GetPieChartData().execute();
                            once = false;
                        }
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    public void requestRealTimeData(){
        org.jsoup.nodes.Document doc = null;
        try {

            doc = Jsoup.connect(htmlUrl).get();
            power = doc.select("#contents > div.content > div > div > div.conTable_type05.mb40 > table > tbody > tr:nth-child(4) > td").text();
            percent = doc.select("#contents > div.content > div > div > div.conTable_type05.mb40 > table > tbody > tr:nth-child(3) > td").text();
            date = doc.select("#contents > div.content > div > div > div:nth-child(3) > p").text();

            String aa = "",bb = "",cc = "";

            aa += date.charAt(0);
            aa += date.charAt(1);
            aa += date.charAt(2);
            aa += date.charAt(3);

            bb += date.charAt(6);
            bb += date.charAt(7);

            cc += date.charAt(10);
            cc += date.charAt(11);

            year = aa;
            month = bb;
            datee = cc;

        } catch (IOException e) {
            e.printStackTrace();
        }




      //  networkUtil.requestServer(Config.REALTIME_DATA,realTimeSuccessListener(),realTimeErrorListener());
    }

    /*
    private Response.Listener<JSONArray> realTimeSuccessListener() {
        return new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.e("realTimeSuccessListener",response.toString());
                 try {
                        for(int i=0;i<response.length();i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            power = jsonObject.getString("power");
                            percent = jsonObject.getString("percent");
                            date = jsonObject.getString("date");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
        }


    private Response.ErrorListener realTimeErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("realTimeErrorListener",error.toString());
            }
        };
    }
*/

    class GetPieChartData extends AsyncTask<Void, String, Void> {
        //백그라운드
        @Override
        protected Void doInBackground(Void... voids) {
            requestPieData();
            return null;
        }
    }



    public void requestPieData(){
      //  networkUtil.requestServer(Config.MAIN_URL+Config.GET_PIE_DATA,networkSuccessListener(),networkErrorListener());
        JsonObjectRequest myRequest = new JsonObjectRequest(
                Config.MAIN_URL + Config.GET_PIE_DATA, null,
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
                try {
                    double used = response.getDouble("used");
                    double notused = response.getDouble("notused");
                    setPieChart(used,notused);
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

  /*  private void setBarChart() {

        ArrayList<BarEntry> bargroup1 = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            bargroup1.add(new BarEntry(i, (float) (Math.random())));
        }
        BarDataSet barDataSet1 = new BarDataSet(bargroup1, "현재까지 아낀 전력량");
        barDataSet1.setColors(Color.rgb(104, 241, 175));
        BarData data = new BarData(barDataSet1);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = barChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = barChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDescription(description);
        barChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        barChart.invalidate();
    }*/
    private void setRealtimeChart(ArrayList arrayList){
        List<Entry> entries = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            entries.add(new Entry(i, (Float) arrayList.get(i)));
        }
//        entries.add(new Entry(1, 1));
//        entries.add(new Entry(2, 2));
//        entries.add(new Entry(3, 0));
//        entries.add(new Entry(4, 4));
//        entries.add(new Entry(5, 3));

        LineDataSet lineDataSet = new LineDataSet(entries, "전력량");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        realtimeChart.setData(lineData);

        XAxis xAxis = realtimeChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = realtimeChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = realtimeChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        realtimeChart.setDoubleTapToZoomEnabled(false);
        realtimeChart.setDrawGridBackground(false);
        realtimeChart.setDescription(description);
//        realtimeChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        realtimeChart.invalidate();
    }

    private void setPieChart(double used, double notused){
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        ArrayList<PieEntry> yValues =  new ArrayList<PieEntry>();
        yValues.add(new PieEntry((float)used,"현재 절약량"));
        yValues.add(new PieEntry((float) notused,"절약하지 않았을 경우"));
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        Description description = new Description();
        description.setText("최대전력 대비"); //라벨
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
        pieChart.setData(data);
    }

}