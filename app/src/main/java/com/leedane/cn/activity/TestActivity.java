package com.leedane.cn.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;


import com.leedane.cn.app.R;
import com.cn.leedane.netty.PushClient;

/**
 * 测试
 * Created by leedane on 2016/5/17.
 */
public class TestActivity extends FragmentActivity {
    public static final String TAG = "TestActivity";
   // private  PoiS;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //启动新谷app，传递参数
       /* String packageName = "com.xingu.policeservice";
        //要调用另一个APP的activity名字
        String activity = "com.xingu.policeservice.StartupActivity";
        ComponentName component = new ComponentName(packageName, activity);
        Intent intent = new Intent();
        intent.setComponent(component);
        intent.setFlags(101);
        intent.putExtra("data", "123");
        startActivity(intent);*/
        /*findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PushClient client = new PushClient();
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }


}
