package com.example.administrator.teamweather04;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class WeatherActivity extends AppCompatActivity {
    private AutoCompleteTextView mCityname;
    private Button mSearch;
    private String db_name = "weather";
    private String db_path = "data/data/com.example.administrator.teamweather04/database/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        copydb();
        mCityname = (AutoCompleteTextView) findViewById(R.id.search_name);
        mSearch = (Button) findViewById(R.id.search_btn);

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
                System.out.println("2");
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
}
