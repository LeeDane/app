package com.leedane.cn.richtext;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.customview.NoScrollGridView;
import com.leedane.cn.util.DensityUtil;

/**
 * 颜色选择的Dialog
 * Created by LeeDane on 2016/10/13.
 */
public class ColorSelectDialog {
    private Dialog mDialog;
    private OnItemClickListener mOnItemClickListener;
    private String[] mVehicleArr;

    int mSelectIndex = 0;

    public void setItemSelectListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public ColorSelectDialog(Context context){
        initView(context);
    }

    @SuppressWarnings("deprecation")
    private void initView(final Context context){
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_select_color, null);
        mDialog = new Dialog(context, R.style.colorsdialog);
        mDialog.setContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = mDialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.FadeShowAnimation);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;//((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth() - DensityUtil.dipToPixels(context, 50);//ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        mDialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        mDialog.setCanceledOnTouchOutside(true);

        mVehicleArr = context.getResources().getStringArray(R.array.colors);
        NoScrollGridView gridview = (NoScrollGridView) layout.findViewById(R.id.gridview);
        gridview.setAdapter(new BaseAdapter(){

            @Override
            public int getCount() {
                if(mVehicleArr != null && mVehicleArr.length > 0){
                    return mVehicleArr.length;
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return mVehicleArr[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = ViewGroup.inflate(context, R.layout.item_dialog_select_color, null);//new ListViewCell(context, ListViewCell.Vehicle_Dialog).getView();
                    convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dipToPixels(context, 32)));
                }
                if(position == mSelectIndex){
                    //convertView.findViewById(R.id.textview).setBackgroundResource(R.drawable.btn_login_phone_n);
                    ((TextView)convertView.findViewById(R.id.textview)).setTextColor( Color.parseColor(mVehicleArr[position]));
                }
                else{
                    //convertView.findViewById(R.id.textview).setBackgroundResource(R.drawable.btn_login_phone_p);
                    /*((TextView)convertView.findViewById(R.id.textview)).setTextColor(Color.argb(255, 52, 52, 52));
                    ((TextView)convertView.findViewById(R.id.textview)).setTextColor(Color.argb(255, 52, 52, 52));*/
                    ((TextView)convertView.findViewById(R.id.textview)).setTextColor( Color.parseColor(mVehicleArr[position]));
                }
                ((TextView)convertView.findViewById(R.id.textview)).setText(mVehicleArr[position]);
                return convertView;
            }
        });

        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(parent, view, position, id);
                }
                mSelectIndex = position;
                cancel();
            }
        });
    }

    public void show(){
        cancel();
        if(mDialog != null && !mDialog.isShowing()){
            mDialog.show();
        }
    }

    public void cancel(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.cancel();
        }
    }
}
