package com.swpu.gantao.xinfu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.swpu.gantao.xinfu.service.SmsService;
import com.swpu.gantao.xinfu.utils.Anim_BetweenActivity;

/**
 * 欢迎界面
 */
public class WelcomeActivity extends AppCompatActivity {

    private TextView welcomeText;//欢迎的文字控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeText = (TextView) findViewById(R.id.aty_welcome_textView);
        init();
        getAlphaObjectAnimator().start();
    }

    /**
     * 获取TextView的动画
     *
     * @return 属性动画对象
     */
    private ObjectAnimator getAlphaObjectAnimator() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(welcomeText, View.ALPHA, 0, 1);
        objectAnimator.setDuration(2000);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                WelcomeActivity.this.finish();
                Anim_BetweenActivity.leftOut_rightIn(WelcomeActivity.this);
            }
        });
        return objectAnimator;
    }

    /**
     * 进行环境初始化
     */
    private void init() {
        //启动发送短信服务
        Intent intent = new Intent(this, SmsService.class);
        startService(intent);
    }

}
