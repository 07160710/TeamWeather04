package com.example.administrator.teamweather04;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.teamweather04.utils.PinyinUtils;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.widget.LinearLayout.LayoutParams;

public class WeatherActivity extends AppCompatActivity implements Runnable{
    HttpURLConnection httpURLConnection = null;
    InputStream din = null;
    private AutoCompleteTextView mCityname;
    private Button mSearch;
    private String db_name = "weather";
    private String db_path = "data/data/com.example.administrator.teamweather04/database/";
    private LinearLayout mShowTV;
    Vector<String> cityname = new Vector<String>();
    Vector<String> low = new Vector<String>();
    Vector<String> high = new Vector<String>();
    Vector<String> icon = new Vector<String>();
    Vector<Bitmap> bitmap = new Vector<Bitmap>();
    Vector<String> summary = new Vector<String>();
    Vector<String> windState = new Vector<String>();
    Vector<String> windDir = new Vector<String>();
    String cname = "guangdong";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setTitle("天气查询");
        copydb();
        mCityname = (AutoCompleteTextView) findViewById(R.id.search_name);
        mSearch = (Button) findViewById(R.id.search_btn);
        mShowTV = (LinearLayout) findViewById(R.id.show_weather);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowTV.removeAllViews();
                cname = PinyinUtils.getPingYin(mCityname.getText().toString());
                Toast.makeText(WeatherActivity.this, "正在查询天气信息...", Toast.LENGTH_LONG).show();
                //String city = mCityname.getText().toString();
                //测试用的
                //String citypinyin = PinyinUtils.getPingYin(city);
                Thread th = new Thread(WeatherActivity.this);
                th.start();
            }
        });
        //省
        GetPro getPro = new GetPro();
        getPro.start();
        //
    }
    class GetPro extends Thread{
        @Override
        public void run() {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(db_path+db_name,null);
            Cursor cursor = null;
            Cursor cursor1 = null;
            try{
                cursor = db.rawQuery("select distinct province_name from weathers", null);
                cursor1 = db.rawQuery("select distinct city_name from weathers", null);
            }catch(Exception e){
                e.printStackTrace();
            }
            List<String> list = new ArrayList<String>();
            String pro="";
            String citys = "";
            while(cursor.moveToNext()){
                pro = cursor.getString(cursor.getColumnIndex("province_name"));
                list.add(pro);
            }
            while(cursor1.moveToNext()){
                citys = cursor1.getString(cursor1.getColumnIndex("city_name"));
                list.add(citys);
            }
            cursor.close();
            cursor1.close();
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(WeatherActivity.this,android.R.layout.simple_spinner_dropdown_item,list);
            Message msg = new Message();
            msg.obj = adapter;
            msg.what = 2;
            handler.sendMessage(msg);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_item2:
                //System.out.println("2");
                Intent intent1 = new Intent(WeatherActivity.this,WeatherPalaceActivity.class);
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void copydb(){
        File db_file = new File(db_path+db_name);
        Log.i("weather","数据库创建");
        if(!db_file.exists()){
            File db_dir= new File(db_path);
            if(!db_dir.exists()){
                db_dir.mkdir();
            }
            InputStream is = getResources().openRawResource(R.raw.weather);
            try {
                OutputStream os = new FileOutputStream(db_path+db_name);
                byte[]buff = new byte[1024];
                int length = 0;
                while((length=is.read(buff))>0){
                    os.write(buff,0,length);
                }
                os.flush();
                os.close();
                is.close();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        cityname.removeAllElements();
        low.removeAllElements();
        high.removeAllElements();
        icon.removeAllElements();
        bitmap.removeAllElements();
        summary.removeAllElements();
        windState.removeAllElements();
        windDir.removeAllElements();
        parseData();
        downImage();
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }
    public void parseData(){

        String weatherUrl = "http://flash.weather.com.cn/wmaps/xml/"+cname+".xml";
        String weatherIcon = "http://m.weather.com.cn/img/c";
        try{
            URL url = new URL(weatherUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            din = httpURLConnection.getInputStream();
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(din,"UTF-8");
            int evtType = xmlPullParser.getEventType();
            while(evtType!=XmlPullParser.END_DOCUMENT){
                switch (evtType){
                    case XmlPullParser.START_TAG:
                        String tag = xmlPullParser.getName();
                        if(tag.equalsIgnoreCase("city")){
                            cityname.addElement(xmlPullParser.getAttributeValue(null,"cityname"));
                            summary.addElement(xmlPullParser.getAttributeValue(null,"stateDetailed"));
                            low.addElement(xmlPullParser.getAttributeValue(null,"tem2")+"℃");
                            high.addElement(xmlPullParser.getAttributeValue(null,"tem1")+"℃");
                            icon.addElement(weatherIcon+xmlPullParser.getAttributeValue(null,"state1")+".gif");
                            windState.addElement(xmlPullParser.getAttributeValue(null,"windState"));
                            windDir.addElement(xmlPullParser.getAttributeValue(null,"windDir"));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                    default:
                        break;
                }
                evtType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void downImage(){
        int i = 0;
        for(i=0;i<icon.size();i++){
            try{
                URL url = new URL(icon.elementAt(i));
                System.out.println(icon.elementAt(i));
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                din = httpURLConnection.getInputStream();
                bitmap.addElement(BitmapFactory.decodeStream(httpURLConnection.getInputStream()));
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try{
                    din.close();
                    httpURLConnection.disconnect();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    private final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    show();
                    break;
                case 2:
                //case 3:
                    ArrayAdapter adapter = (ArrayAdapter)msg.obj;
                    mCityname.setAdapter(adapter);
                    mCityname.setThreshold(1);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    public void show(){
        mShowTV.removeAllViews();
        mShowTV.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        params.weight = 80;
        params.height = 50;
        LinearLayout head = new LinearLayout(this);
        head.setOrientation(LinearLayout.HORIZONTAL);
        TextView cityView = new TextView(this);
        cityView.setLayoutParams(params);
        cityView.setText("城市名");
        head.addView(cityView);

        //
        TextView summaryView1 = new TextView(this);
        summaryView1.setLayoutParams(params);
        summaryView1.setText("天气情况");
        head.addView(summaryView1);
        //
        TextView imageView = new TextView(this);
        imageView.setLayoutParams(params);
        imageView.setText("天气图");
        head.addView(imageView);
        //
        TextView lowViews = new TextView(this);
        lowViews.setLayoutParams(params);
        lowViews.setText("最低温度");
        head.addView(lowViews);
        //
        TextView highViews = new TextView(this);
        highViews.setLayoutParams(params);
        highViews.setText("最高温度");
        head.addView(highViews);
        //
        TextView windStateViews = new TextView(this);
        windStateViews.setLayoutParams(params);
        windStateViews.setText("风力");
        head.addView(windStateViews);
        //
        TextView windDirViews = new TextView(this);
        windDirViews.setLayoutParams(params);
        windDirViews.setText("风向");
        head.addView(windDirViews);

        mShowTV.addView(head);
        //......

        for(int i = 0;i<cityname.size();i++){
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            //1城市
            TextView dayView = new TextView(this);
            dayView.setLayoutParams(params);
            dayView.setText(cityname.elementAt(i));
            linearLayout.addView(dayView);
            //2描述
            TextView summaryView = new TextView(this);
            summaryView.setLayoutParams(params);
            summaryView.setText(summary.elementAt(i));
            linearLayout.addView(summaryView);
            //3天气图标显示
            ImageView icon = new ImageView(this);
            icon.setLayoutParams(params);
            icon.setImageBitmap(bitmap.elementAt(i));
            linearLayout.addView(icon);
            //4最低气温显示
            TextView lowView = new TextView(this);
            lowView.setLayoutParams(params);
            lowView.setText(low.elementAt(i));
            linearLayout.addView(lowView);
            //5最高视图显示
            TextView highView = new TextView(this);
            highView.setLayoutParams(params);
            highView.setText(high.elementAt(i));
            linearLayout.addView(highView);
            //风力
            TextView windView = new TextView(this);
            windView.setLayoutParams(params);
            windView.setText(windState.elementAt(i));
            linearLayout.addView(windView);
            //风向
            TextView dirView = new TextView(this);
            dirView.setLayoutParams(params);
            dirView.setText(windDir.elementAt(i));
            linearLayout.addView(dirView);
            //6添加所有的View到总视图中
            mShowTV.addView(linearLayout);
        }
    }

}
