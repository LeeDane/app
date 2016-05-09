package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leedane.cn.adapter.ImageDetailFragmentPagerAdapter;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.fragment.ImageDetailFragment;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.ToastUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 图像详情activity
 * Created by LeeDane on 2015/10/17.
 */
public class ImageDetailActivity extends BaseActivity {

    public static final String TAG = "ImageDetailActivity";
    /**
     * 当前的图像对象
     */
   // private ImageView mImageView;

    /**
     * 滑动viewpager对象
     */
    private ViewPager mViewPager;

    /**
     * 展示当前位置相对总数的文本
     */
    //private TextView mTextViewCurrent;

    /**
     * tab的总数
     */
    private int mTotalTabs;

    /**
     * 所有的请求地址
     */
    private List<ImageDetailBean> mImageDetailBeans;

    /**
     * 从0开始,Yab的索引
     */
    private int mCurrentTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        //mImageView = (ImageView)findViewById(R.id.image_detail_imageview);
        Intent it = getIntent();
        try{
            String json = it.getStringExtra("ImageDetailBeans");
            Type type = new TypeToken<ArrayList<ImageDetailBean>>(){}.getType();
            mImageDetailBeans = new Gson().fromJson(json,type);
            //当前位置
            mCurrentTab = it.getIntExtra("current", 0);
            if(mImageDetailBeans == null || mImageDetailBeans.size() == 0){
                ToastUtil.failure(getApplicationContext(), "查看图片参数有误", Toast.LENGTH_SHORT);
                finish();
            }
        }catch (Exception e){
            ToastUtil.failure(getApplicationContext(), "查看图片参数有误", Toast.LENGTH_SHORT);
            finish();
        }
        mTotalTabs = mImageDetailBeans.size();
        setTitleViewText(getStringResource(R.string.image_detail) + "(" +(mCurrentTab + 1) + "/" + mTotalTabs +")");
        backLayoutVisible();
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        initView();
    }

    /**
     * 初始化对象
     */
    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.image_detail_viewpager);
        //mTextViewCurrent = (TextView) findViewById(R.id.image_detail_current);
        //mTextViewCurrent.setText((mCurrentTab +1) + "/" + mTotalTabs);

        List< Fragment> framents = new ArrayList<>();
        for(int i = 0; i < mTotalTabs; i++){
            if(mCurrentTab == i)
                framents.add(new ImageDetailFragment(i, ImageDetailActivity.this, mImageDetailBeans.get(i)));
            else
                framents.add(null);
        }
        mViewPager.setAdapter(new ImageDetailFragmentPagerAdapter(getSupportFragmentManager(), ImageDetailActivity.this, framents, mImageDetailBeans));
        mViewPager.setCurrentItem(mCurrentTab);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mCurrentTab = position;

                FragmentManager fragmentManager = getSupportFragmentManager();

                //当前activity中活动着的frament个数，不包括没有活动的
                List<Fragment> list = fragmentManager.getFragments();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.show(list.get(position));
                //fragmentTransaction.commit();
                //ToastUtil.success(ImageDetailActivity.this, "位置：" + position + ", 总的：" + mTotalTabs + "，总：" + list.size(), Toast.LENGTH_SHORT);
                setTitleViewText(getStringResource(R.string.image_detail) + "(" +(mCurrentTab + 1) + "/" + mTotalTabs +")");
                //mTextViewCurrent.setText((mCurrentTab +1) + "/" + mTotalTabs);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(TAG, "onPageScrolled-->position: " + position + ", positionOffset:" + positionOffset + ", positionOffsetPixels:" + positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(TAG, "onPageScrollStateChanged:" + state);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*BitmapDrawable drawable = (BitmapDrawable)mImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ImageUtil.getInstance().destoryBimap(bitmap);*/
        System.gc();
    }
}
