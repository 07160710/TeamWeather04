package com.example.administrator.teamweather04;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout.LayoutParams;


public class WeatherPalaceActivity extends AppCompatActivity {
    HttpURLConnection httpURLConnection = null;
    InputStream din = null;
    private String db_name = "weather";
    private String db_path = "data/data/com.example.administrator.teamweather04/database/";
    private String cityname="广州";
    private AutoCompleteTextView mCitynameET;
    private Button msearchBtn;
    private RelativeLayout mShowTV;
    private TextView TV;
    private TextView Ganmao;
    private TextView TV1;
    private TextView TV2;
    private TextView TV3;
    private TextView TV4;
    private TextView TV5;
    private TextView TV6;
    private TextView TV7;
    //
    private TextView TV8;
    private TextView TV9;
    private TextView TV10;
    //
    private TextView TV11;
    private TextView TV12;
    private TextView TV13;
    private TextView Weilai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("地区未来天气查询");
        setContentView(R.layout.activity_weather_palace);
        mCitynameET = (AutoCompleteTextView) findViewById(R.id.cityname);
        msearchBtn = (Button) findViewById(R.id.search);
        mShowTV = (RelativeLayout) findViewById(R.id.show_weather);
        Ganmao = (TextView) findViewById(R.id.ganmao);
        TV = (TextView) findViewById(R.id.tv);
        TV1 = (TextView) findViewById(R.id.tv0);
        //
        TV2 = (TextView) findViewById(R.id.tv1);
        TV3 = (TextView) findViewById(R.id.tv2);
        TV4 = (TextView) findViewById(R.id.tv3);
        //
        TV5 = (TextView) findViewById(R.id.tv4);
        TV6 = (TextView) findViewById(R.id.tv5);
        TV7 = (TextView) findViewById(R.id.tv6);
        //
        TV8 = (TextView) findViewById(R.id.tv7);
        TV9 = (TextView) findViewById(R.id.tv8);
        TV10 = (TextView) findViewById(R.id.tv9);
        //
        TV11 = (TextView) findViewById(R.id.tv10);
        TV12 = (TextView) findViewById(R.id.tv11);
        TV13 = (TextView) findViewById(R.id.tv12);

        Weilai = (TextView) findViewById(R.id.weilai);
        msearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mShowTV.removeAllViews();
                cityname = mCitynameET.getText().toString();
                Toast.makeText(WeatherPalaceActivity.this,"正在查询...",Toast.LENGTH_LONG).show();
                GetJson getJson = new GetJson(cityname);
                getJson.start();
            }
        });
        GetArea getArea = new GetArea();
        getArea.start();
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showData((String) msg.obj);
                    break;
                case 2:
                    ArrayAdapter adapter = (ArrayAdapter)msg.obj;
                    mCitynameET.setAdapter(adapter);
                    mCitynameET.setThreshold(1);
            }
            super.handleMessage(msg);
        }
    };


    private  void showData(String jData){
        //mShowTV.removeAllViews();
        try {
            JSONObject jobj = new JSONObject(jData);
            JSONObject weather = jobj.getJSONObject("data");
            StringBuffer wbf = new StringBuffer();
//            wbf.append("当前温度：" + weather.getString("wendu") + "℃" + "\n");
//            wbf.append("天气提示：" + weather.getString("ganmao") + "\n");
            TV.setText("当前"+weather.getString("city")+"温度：" + weather.getString("wendu") + "℃");
            Ganmao.setText("天气提示：" + weather.getString("ganmao"));
            Weilai.setText("未来4天天气");
            JSONArray jary = weather.getJSONArray("forecast");
            for(int i=0;i<jary.length();i++){
                if(i == 0){
                    JSONObject pobj = (JSONObject)jary.opt(i);
                    String fengli = pobj.getString("fengli");
                    int eq = fengli.indexOf("]]>");
                    fengli = fengli.substring(9,eq);
                    TV1.setText("今日("+pobj.getString("date")+")天气实况："+pobj.getString("type")+"气温："+pobj.getString("low")+"/"+pobj.getString("high")+",风向/风力："+pobj.getString("fengxiang")+"/"+fengli);
                }else if(i == 1){
                    JSONObject pobj = (JSONObject)jary.opt(i);
                    TV2.setText(pobj.getString("date")+"气温："+pobj.getString("low")+"/"+pobj.getString("high"));
                    TV3.setText("天气："+pobj.getString("type"));
                    String fengli = pobj.getString("fengli");
                    int eq = fengli.indexOf("]]>");
                    fengli = fengli.substring(9,eq);
                    TV4.setText("风向："+pobj.getString("fengxiang")+",风力"+fengli);
                }else if(i == 2){
                    JSONObject pobj = (JSONObject)jary.opt(i);
                    TV5.setText(pobj.getString("date")+"气温："+pobj.getString("low")+"/"+pobj.getString("high"));
                    TV6.setText("天气："+pobj.getString("type"));
                    String fengli = pobj.getString("fengli");
                    int eq = fengli.indexOf("]]>");
                    fengli = fengli.substring(9,eq);
                    TV7.setText("风向："+pobj.getString("fengxiang")+",风力"+fengli);
                }else if(i == 3){
                    JSONObject pobj = (JSONObject)jary.opt(i);
                    TV8.setText(pobj.getString("date")+"气温："+pobj.getString("low")+"/"+pobj.getString("high"));
                    TV9.setText("天气："+pobj.getString("type"));
                    String fengli = pobj.getString("fengli");
                    int eq = fengli.indexOf("]]>");
                    fengli = fengli.substring(9,eq);
                    TV10.setText("风向："+pobj.getString("fengxiang")+",风力"+fengli);
                }else if(i == 4){
                    JSONObject pobj = (JSONObject)jary.opt(i);
                    TV11.setText(pobj.getString("date")+"气温："+pobj.getString("low")+"/"+pobj.getString("high"));
                    TV12.setText("天气："+pobj.getString("type"));
                    String fengli = pobj.getString("fengli");
                    int eq = fengli.indexOf("]]>");
                    fengli = fengli.substring(9,eq);
                    TV13.setText("风向："+pobj.getString("fengxiang")+",风力"+fengli);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    class GetArea extends Thread{
        @Override
        public void run() {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(db_path+db_name,null);
            Cursor cursor = null;
            try{
                cursor = db.rawQuery("select area_name from weathers", null);
            }catch(Exception e){
                e.printStackTrace();
            }
            List<String> list = new ArrayList<String>();
            String area="";
            while(cursor.moveToNext()){
                area = cursor.getString(cursor.getColumnIndex("area_name"));
                list.add(area);

            }
            cursor.close();
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(WeatherPalaceActivity.this,android.R.layout.simple_spinner_dropdown_item,list);
            Message msg = new Message();
            msg.obj = adapter;
            msg.what = 2;
            handler.sendMessage(msg);
        }
    }

    class GetJson extends Thread{
        private String urlstr =  "http://wthrcdn.etouch.cn/weather_mini?city=";
        public GetJson(String cityname){
            try{
                urlstr = urlstr+ URLEncoder.encode(cityname,"UTF-8");
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
        @Override
        public void run() {
            try {
                URL url = new URL(urlstr);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                din = httpURLConnection.getInputStream();
                InputStreamReader in = new InputStreamReader(din);
                BufferedReader buffer = new BufferedReader(in);
                StringBuffer sbf = new StringBuffer();
                String line = null;
                while( (line=buffer.readLine())!=null) {
                    sbf.append(line);
                }
                Message msg = new Message();
                msg.obj = sbf.toString();
                msg.what = 1;
                handler.sendMessage(msg);
                Looper.prepare();
                Toast.makeText(WeatherPalaceActivity.this,"获取数据成功",Toast.LENGTH_LONG).show();
                Looper.loop();
            }catch (Exception ee){
                Looper.prepare();
                Toast.makeText(WeatherPalaceActivity.this,"获取数据失败，网络连接失败或输入有误",Toast.LENGTH_LONG).show();
                Looper.loop();
                ee.printStackTrace();
            }finally {
                try{
                    httpURLConnection.disconnect();
                    din.close();
                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_item1:
                //System.out.println("1");
                Intent intent = new Intent(WeatherPalaceActivity.this,WeatherActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

}
