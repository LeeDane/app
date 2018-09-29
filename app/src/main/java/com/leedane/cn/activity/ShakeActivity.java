package com.leedane.cn.activity;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.fragment.shake.ShakeBlogFragment;
import com.leedane.cn.fragment.shake.ShakeMoodFragment;
import com.leedane.cn.fragment.shake.ShakeUserFragment;
import com.leedane.cn.handler.ShakeHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

/**
 * 摇一摇功能实现activity
 * Created by LeeDane on 2016/12/20.
 */
public class ShakeActivity extends BaseActivity{
    public static final String TAG = "ShakeActivity";
    private SensorManager sensorManager;
    private Sensor sensor;
    private static final int UPTATE_INTERVAL_TIME = 50;
    private static final int SPEED_SHRESHOLD = 50;//这个值调节灵敏度
    private long lastUpdateTime;
    private float lastX;
    private float lastY;
    private float lastZ;

    private int countNumber = 1;

    private final String[] types = new String[]{"博客", "用户", "心情"};
    private Spinner yaoyiyaoType;//摇一摇的类型(0:博客， 1：用户， 2:心情)
    private String selectType = types[0]; //选中的类型

    private boolean canYao = false; //标记是否可以摇一摇
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.yao_yi_yao);
        backLayoutVisible();

        initView();
    }

    private Handler resultHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagUtil.YAOYIYAO_RESULT_HANDLER:
                    String type = msg.getData().getString("type");
                    Fragment fragment = null;
                    switch (selectType){
                        case "博客":
                            fragment = ShakeBlogFragment.newInstance(msg.getData());
                            break;
                        case "用户":
                            fragment = ShakeUserFragment.newInstance(msg.getData());
                            break;
                        case "心情":
                            fragment = ShakeMoodFragment.newInstance(msg.getData());
                            break;
                    }
                    ShakeActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.yaoyiyao_container, fragment).commit();
                    break;
            }
        }
    };

    /**
     * 初始化视图
     */
    private void initView(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        yaoyiyaoType = (Spinner)findViewById(R.id.yaoyiyao_type);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        yaoyiyaoType.setAdapter(arrayAdapter);
        yaoyiyaoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectType = types[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectType = types[0];
            }
        });
        canYao = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensor != null) {
            sensorManager.registerListener(sensorEventListener,  sensor, SensorManager.SENSOR_DELAY_GAME);//这里选择感应频率
        }
    }

    /**
     * 重力感应监听
    */
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public synchronized void onSensorChanged(SensorEvent event) {
            long currentUpdateTime = System.currentTimeMillis();
            long timeInterval = currentUpdateTime - lastUpdateTime;
            if (timeInterval < UPTATE_INTERVAL_TIME  && !canYao) {
                return;
             }
            lastUpdateTime = currentUpdateTime;
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;

            lastX = x;
            lastY = y;
            lastZ = z;
            double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY
                    + deltaZ * deltaZ) / timeInterval) * 100;
            if (speed >= SPEED_SHRESHOLD && speed <= 10000 && canYao && countNumber < 6) {
                canYao = false;
                AppUtil.vibrate(ShakeActivity.this, 50);
                showLoadingDialog("获取信息", "加载中。。。", true);
                switch (selectType){
                    case "博客":
                        ShakeHandler.getShakeBlogRequest(ShakeActivity.this);
                        break;
                    case "用户":
                        ShakeHandler.getShakeUserRequest(ShakeActivity.this);
                        break;
                    case "心情":
                        ShakeHandler.getShakeMoodRequest(ShakeActivity.this);
                        break;
                }

                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ToastUtil.success(ShakeActivity.this, "已经取消");
                        taskCanceled(TaskType.LOAD_SHAKE_BLOG);
                        taskCanceled(TaskType.LOAD_SHAKE_USER);
                        taskCanceled(TaskType.LOAD_SHAKE_MOOD);
                        canYao = true;
                        countNumber ++;
                        ((TextView)findViewById(R.id.countdown)).setText("今天您还可以免费摇"+ (6 - countNumber)+"次！");
                    }
                });
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void taskFinished(TaskType type, Object result) {
        canYao = true;
        if(result instanceof Error){
            ToastUtil.failure(getBaseContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            dismissLoadingDialog();
            return;
        }
        try {
            dismissLoadingDialog();
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            countNumber ++;
            ((TextView)findViewById(R.id.countdown)).setText("今天您还可以免费摇"+ (6 - countNumber)+"次！");
            if((TaskType.LOAD_SHAKE_BLOG == type || TaskType.LOAD_SHAKE_USER == type ||  TaskType.LOAD_SHAKE_MOOD == type )&& jsonObject != null){
                if(jsonObject.optBoolean("isSuccess")){
                    //ToastUtil.success(ShakeActivity.this, jsonObject);
                    Message message = new Message();
                    message.what = FlagUtil.YAOYIYAO_RESULT_HANDLER;
                    Bundle bundle = new Bundle();
                    bundle.putString("data", jsonObject.getString("message"));
                    bundle.putString("type", TaskType.LOAD_SHAKE_BLOG.toString());
                    message.setData(bundle);
                    resultHandler.sendMessage(message);
                }else{
                    ToastUtil.failure(ShakeActivity.this, JsonUtil.getErrorMessage(result), Toast.LENGTH_SHORT);
                }
                return;
            }
        } catch (Exception e) {
            Toast.makeText(ShakeActivity.this, "服务器处理异常", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        /*
         * 很关键的部分：注意，说明文档中提到，即使activity不可见的时候，感应器依然会继续的工作，测试的时候可以发现，没有正常的刷新频率
         * 也会非常高，所以一定要在onPause方法中关闭触发器，否则讲耗费用户大量电量，很不负责。
         * */
        sensorManager.unregisterListener(sensorEventListener);
        taskCanceled(TaskType.LOAD_SHAKE_BLOG);
        taskCanceled(TaskType.LOAD_SHAKE_USER);
        taskCanceled(TaskType.LOAD_SHAKE_MOOD);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(sensorManager != null){
            sensorManager.unregisterListener(sensorEventListener);
            sensorManager.flush(sensorEventListener);
            sensorManager = null;
        }

        if(sensor != null){
            sensor = null;
        }

        if(resultHandler != null){
            resultHandler.removeCallbacksAndMessages(null);
        }
        taskCanceled(TaskType.LOAD_SHAKE_BLOG);
        taskCanceled(TaskType.LOAD_SHAKE_USER);
        taskCanceled(TaskType.LOAD_SHAKE_MOOD);
        System.gc();
        super.onDestroy();
    }
}
