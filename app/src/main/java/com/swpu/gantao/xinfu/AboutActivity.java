package com.swpu.gantao.xinfu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.swpu.gantao.xinfu.utils.Anim_BetweenActivity;

/**
 * 关于界面
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initToolBar();
    }

    //初始化ActionBar
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
                Anim_BetweenActivity.leftIn_rightOut(AboutActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AboutActivity.this.finish();
        Anim_BetweenActivity.leftIn_rightOut(AboutActivity.this);
    }
}
