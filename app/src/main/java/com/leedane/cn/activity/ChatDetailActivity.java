package com.leedane.cn.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.frament.MoodDetailFragment;
import com.leedane.cn.frament.SendToolbarFragment;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.EncodingHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 聊天首页activity
 * Created by LeeDane on 2016/5/4.
 */
public class ChatDetailActivity extends BaseActivity implements MoodDetailFragment.OnItemClickListener
        , SendToolbarFragment.OnAddCommentOrTransmitListener{

    public static final String TAG = "ChatDetailActivity";

    /**
     * 发送心情的imageview
     */
    public ImageView mRightImg;

    private int mid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkedIsLogin();
        setContentView(R.layout.activity_chat_detail);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.chat);
        backLayoutVisible();

        Intent it = getIntent();
        mid = it.getIntExtra("tableId", 0);
        boolean hasImg = it.getBooleanExtra("hasImg", false);

        if(mid < 1){
            Toast.makeText(ChatDetailActivity.this, "心情ID不存在", Toast.LENGTH_LONG).show();
            this.finish();
        }

        initRightImg();
        Bundle bundle = new Bundle();
        bundle.putInt("tableId", mid);
        bundle.putBoolean("hasImg", hasImg);
        MoodDetailFragment moodDetailFragment = MoodDetailFragment.newInstance(bundle);
        moodDetailFragment.setOnItemClickListener(this);
        SendToolbarFragment sendToolbarFragment = SendToolbarFragment.newInstance(bundle);
        sendToolbarFragment.setOnAddCommentOrTransmitListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, moodDetailFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.emoji_keyboard, sendToolbarFragment).commit();
    }

    private void initRightImg(){
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setImageResource(R.drawable.qr_code);
        mRightImg.setOnClickListener(this);
    }


    /**
     * 检查是否登录
     */
    private void checkedIsLogin() {
        //判断是否有缓存用户信息
        if (BaseApplication.getLoginUserId() < 1) {
            Intent it = new Intent(ChatDetailActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.ChatDetailActivity");
            startActivity(it);
            finish();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case  R.id.view_right_img://生成二维码
                showQrCodeDialog();
                break;
        }
    }

    private Bitmap qrCodeBitmap;
    private Dialog mQrCodeDialog;
    /**
     * 显示弹出二维码的Dialog
     */
    public void showQrCodeDialog(){
        dismissQrCodeDialog();
        recycleQrCodeBitmap();
        mQrCodeDialog = new Dialog(ChatDetailActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        ImageView imageView = new ImageView(ChatDetailActivity.this);
        String contentString = null;
        try{
            String str = "{'tableName':'t_mood','tableId':"+mid+"}";
            contentString = new JSONObject(str).toString();
        }catch (JSONException e){
            e.printStackTrace();
        }

        if (StringUtil.isNotNull(contentString)) {
            //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
            contentString =  CommonHandler.encodeQrCodeStr(contentString);
            ToastUtil.failure(ChatDetailActivity.this, "二维码创建成功，长按保存到本地", Toast.LENGTH_SHORT);
            try {
                qrCodeBitmap = EncodingHandler.createQRCode(contentString, 720);
                imageView.setImageBitmap(qrCodeBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String fPath = getQrCodeDir() + File.separator + System.currentTimeMillis() +".jpg";
                File f = new File(fPath);
                if(f.exists()){
                    f.delete();
                }
                if(BitmapUtil.bitmapToLocalPath(qrCodeBitmap, fPath)){
                    ToastUtil.failure(ChatDetailActivity.this, "二维码图片保存成功，路径是："+fPath);
                    dismissQrCodeDialog();
                }else
                    ToastUtil.failure(ChatDetailActivity.this, "二维码图片保存失败");
                return true;
            }
        });
        mQrCodeDialog.setTitle("我的二维码");
        mQrCodeDialog.setCancelable(true);
        mQrCodeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissQrCodeDialog();
            }
        });
        mQrCodeDialog.setContentView(imageView);
        mQrCodeDialog.show();
    }



    /**
     * 获取存放本地二维码的文件夹
     * @return
     */
    private File getQrCodeDir(){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = getCacheDir();
        }
        File cacheDir = new File(sdDir, getResources().getString(R.string.app_dirsname) + File.separator+ getResources().getString(R.string.qr_code_filepath));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 回收二维码生成的bitmap
     */
    private void recycleQrCodeBitmap(){
        if(qrCodeBitmap != null && !qrCodeBitmap.isRecycled()){
            qrCodeBitmap.recycle();
            System.gc();
        }
    }

    /**
     * 隐藏二维码
     */
    public void dismissQrCodeDialog(){
        if(mQrCodeDialog != null && mQrCodeDialog.isShowing())
            mQrCodeDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        recycleQrCodeBitmap();
        super.onDestroy();
    }


    @Override
    public void onItemClick(int position, CommentOrTransmitBean commentOrTransmitBean, int commentOrTransmit) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //找到SendToolBarFragment
        SendToolbarFragment sendToolbarFragment = (SendToolbarFragment) fragmentManager.findFragmentById(R.id.emoji_keyboard);
        sendToolbarFragment.onItemClick(position, commentOrTransmitBean, commentOrTransmit);
    }

    @Override
    public void clearPosition() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //找到SendToolBarFragment
        SendToolbarFragment sendToolbarFragment = (SendToolbarFragment) fragmentManager.findFragmentById(R.id.emoji_keyboard);
        sendToolbarFragment.clearPosition();
    }

    @Override
    public void afterSuccessAddCommentOrTransmit(int commentOrTransmit) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //找到MoodDetailFragment
        MoodDetailFragment moodDetailFragment = (MoodDetailFragment) fragmentManager.findFragmentById(R.id.container);
        moodDetailFragment.afterSuccessAddCommentOrTransmit(commentOrTransmit);
    }
}
