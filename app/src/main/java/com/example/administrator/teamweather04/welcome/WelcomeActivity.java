package com.example.administrator.teamweather04.welcome;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.administrator.teamweather04.R;
import com.example.administrator.teamweather04.WeatherActivity;


public class WelcomeActivity extends AppCompatActivity implements Animation.AnimationListener {
    private ImageView mImgAnim = null;
    private Animation alphaAnimation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏标题栏
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);
        //动画
        mImgAnim = (ImageView)findViewById(R.id.welcome_bg);
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.splsh_anim);
        alphaAnimation.setFillEnabled(true);
        alphaAnimation.setFillAfter(true);
        mImgAnim.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(this);
    }
    //信使设置时间
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            goMain();
            super.handleMessage(msg);
        }
    };
    //跳转
    public void goMain(){
        Intent intent = new Intent(WelcomeActivity.this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        //handler跳转时间
        handler.sendEmptyMessageDelayed(0,100);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //屏蔽BACK键
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }
}
