package com.leedane.cn.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.File.FileBean;
import com.leedane.cn.adapter.FileAdapter;
import com.leedane.cn.database.BaseSQLiteOpenHelper;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.SharedPreferenceUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 文件activity
 * Created by LeeDane on 2016/1/24.
 */
public class FileActivity extends BaseActivity {
    private static final String TAG = "FileActivity";
    /**
     * 是否有上传文件
     */
    public static final int IS_UPLOAD_FILE = 66;

    private ListView mListView;
    private int mClickPosition;//长按ListView的索引
    private FileAdapter mAdapter;
    private List<FileBean> mFiles;
    private BaseSQLiteDatabase sqLiteDatabase;

    private JSONObject mUserInfo;
    private Intent currentIntent;
    private int mLoginAccountId;

    /**
     * 上传文件的imageview
     */
    private ImageView mRightImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        currentIntent = getIntent();
        //检查是否登录
        checkedIsLogin();

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.nav_file);
        backLayoutVisible();
        sqLiteDatabase = new BaseSQLiteDatabase(FileActivity.this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mListView = (ListView)findViewById(R.id.file_listview);

        initData();
        mAdapter = new FileAdapter(mFiles, FileActivity.this, mListView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "触发Listview的长按事件");
                mClickPosition = position;
                //这里返回值必须是false,否则无法触发弹出上下文菜单
                return false;
            }
        });
        registerForContextMenu(mListView);

        //显示跳转到上传文件页面的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setImageResource(R.mipmap.download);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        mFiles = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        Random random = new Random();

        params.put("id", random.nextInt(10000));
        params.put("filename", "1_82378545124366_20160119184016_myJava.pdf");
        params.put("create_time", DateUtil.DateToString(new Date()));
        params.put("create_user_id", 1);
        sqLiteDatabase.insert("t_file", params);
        Cursor cursor = sqLiteDatabase.rowQuery("select * from "+ BaseSQLiteOpenHelper.TABLE_FILE+" where create_user_id =? order by id desc", new String[]{String.valueOf(mLoginAccountId)});
        if(cursor != null){
            String fileName,createTime;
            String id = "";
            FileBean fileBean;
            while(cursor.moveToNext()){
                int i = cursor.getColumnIndex("ID");
                id = cursor.getString(i);
                fileName = cursor.getString(cursor.getColumnIndex("filename"));
                createTime = cursor.getString(cursor.getColumnIndex("create_time"));
                fileBean = new FileBean();
                fileBean.setCreateTime(createTime);
                fileBean.setFileName(fileName);
                fileBean.setId(Integer.parseInt(id));
                mFiles.add(fileBean);
            }
        }

        if(mFiles.size() == 0){
            //请求网络获取服务器上的数据
            HttpRequestBean requestBean = new HttpRequestBean();
            requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
            requestBean.setServerMethod("leedane/");
            HashMap<String, Object> reqParams = new HashMap<>();
            //reqParams.put()
            requestBean.setParams(reqParams);

            TaskLoader.getInstance().startTaskForResult(TaskType.FILE_LOAD, this, requestBean);
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    /**
     * 检查是否登录
     */
    private void checkedIsLogin() {
        mUserInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
        //判断是否有缓存用户信息
        if(mUserInfo == null || !mUserInfo.has("account") ){
            Intent it = new Intent(FileActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.FileActivity");
            it.setData(currentIntent.getData());
            startActivity(it);
            FileActivity.this.finish();
            return;
        }

        try {
            //mLoginAccountName = mUserInfo.getString("account");
            mLoginAccountId = mUserInfo.getInt("id");
            Toast.makeText(getBaseContext(), "mLoginAccountId" +mLoginAccountId, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.i(TAG, "获取缓存的用户名称为空");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
            case R.id.view_right_img:
                //Toast.makeText(FileActivity.this, "上传文件", Toast.LENGTH_LONG).show();
                Intent it_upload_file = new Intent();
                it_upload_file.setClass(FileActivity.this, UploadFileActivity.class);
                startActivityForResult(it_upload_file, IS_UPLOAD_FILE);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IS_UPLOAD_FILE) {

                Toast.makeText(FileActivity.this, "是否有上传了文件?", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("操作");
        //添加菜单项
        menu.add(0, Menu.FIRST, 0, "删除");
        menu.add(0, Menu.FIRST+1, 0, "下载");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                Toast.makeText(FileActivity.this, mClickPosition + "-----"+ item.getTitle(), Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(FileActivity.this, mClickPosition + "-----"+ item.getTitle(), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
