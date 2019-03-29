package com.example.saving_electricity.botpager;

import android.app.DownloadManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.saving_electricity.R;
import com.example.saving_electricity.network.NetworkUtil;
import com.example.saving_electricity.util.Config;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller extends Fragment {
    SeekBar seekBar1;
    SeekBar seekBar2;
    SeekBar seekBar3;
    RadioButton rbtAuto;
    RadioButton rbtManual;
    Button btnSend;
    int seekBar1_data;
    int seekBar2_data;
    int seekBar3_data;
    String status;
    NetworkUtil networkUtil;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.controller, container, false);
        seekBar1 = (SeekBar)v.findViewById(R.id.seekbar_1);
        seekBar2 = (SeekBar)v.findViewById(R.id.seekbar_2);
        seekBar3 = (SeekBar)v.findViewById(R.id.seekbar_3);
        rbtAuto = (RadioButton) v.findViewById(R.id.auto);
        rbtManual = (RadioButton) v.findViewById(R.id.manual);
        btnSend = (Button)v.findViewById(R.id.send);
        networkUtil = new NetworkUtil(getContext());
        RadioButton.OnClickListener optionOnClickListener
                = new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rbtAuto.isChecked())
                    status = "auto";
                else
                    status = "manual";
            }
        };

        rbtAuto.setOnClickListener(optionOnClickListener);
        rbtManual.setOnClickListener(optionOnClickListener);

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBar1_data = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBar2_data = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBar3_data = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btnSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPostData();
            }
        });
        return v;
    }
    private void sendPostData(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room1",seekBar1_data);
            jsonObject.put("room2",seekBar2_data);
            jsonObject.put("room3",seekBar3_data);
            jsonObject.put("status",status);
            networkUtil.requestServer(Request.Method.POST,
                    Config.MAIN_URL+Config.POST_STATUS_DATA,
                    jsonObject, networkSuccessListener(),networkErrorListener());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private Response.Listener<JSONObject> networkSuccessListener() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.e("success",response.toString());
            }
        };
    }
    private Response.ErrorListener networkErrorListener() {
        return new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        };
    }

}
