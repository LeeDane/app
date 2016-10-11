package com.leedane.cn.richtext;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.activity.PushBlogActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.util.ToastUtil;

/**
 * 富文本编辑器的工具类
 * Created by LeeDane on 2016/10/11.
 */
public class ToolFragment extends Fragment implements View.OnClickListener{

    private View mRootView;
    private Context mContext;
    private LinearLayout mParentView; //父视图

    private TextView rich_text_bold;
    private TextView rich_text_italics;
    private TextView rich_text_p;
    private TextView rich_text_h1;
    private TextView rich_text_h2;
    private TextView rich_text_h3;
    private TextView rich_text_h4;
    private TextView rich_text_h5;
    private TextView rich_text_h6;
    private TextView rich_text_color;
    private TextView rich_text_image;
    private TextView rich_text_link;
    private TextView rich_text_quote;
    private TextView rich_text_hr;

    public ToolFragment(){
    }

    public static final ToolFragment newInstance(Bundle bundle){
        ToolFragment fragment = new ToolFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.rich_text_tools, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){

        }
        if(mContext == null)
            mContext = getActivity();

        mParentView = (LinearLayout)mRootView.findViewById(R.id.rich_text_parent);

        rich_text_bold = (TextView)mRootView.findViewById(R.id.rich_text_bold);
        rich_text_italics = (TextView)mRootView.findViewById(R.id.rich_text_italics);
        rich_text_p = (TextView)mRootView.findViewById(R.id.rich_text_p);
        rich_text_h1 = (TextView)mRootView.findViewById(R.id.rich_text_h1);
        rich_text_h2 = (TextView)mRootView.findViewById(R.id.rich_text_h2);
        rich_text_h3 = (TextView)mRootView.findViewById(R.id.rich_text_h3);
        rich_text_h4 = (TextView)mRootView.findViewById(R.id.rich_text_h4);
        rich_text_h5 = (TextView)mRootView.findViewById(R.id.rich_text_h5);
        rich_text_h6 = (TextView)mRootView.findViewById(R.id.rich_text_h6);
        rich_text_color = (TextView)mRootView.findViewById(R.id.rich_text_color);
        rich_text_image = (TextView)mRootView.findViewById(R.id.rich_text_image);
        rich_text_link = (TextView)mRootView.findViewById(R.id.rich_text_link);
        rich_text_quote = (TextView)mRootView.findViewById(R.id.rich_text_quote);
        rich_text_hr = (TextView)mRootView.findViewById(R.id.rich_text_hr);

        rich_text_bold.setOnClickListener(this);
        rich_text_italics.setOnClickListener(this);
        rich_text_p.setOnClickListener(this);
        rich_text_h1.setOnClickListener(this);
        rich_text_h2.setOnClickListener(this);
        rich_text_h3.setOnClickListener(this);
        rich_text_h4.setOnClickListener(this);
        rich_text_h5.setOnClickListener(this);
        rich_text_h6.setOnClickListener(this);
        rich_text_color.setOnClickListener(this);
        rich_text_image.setOnClickListener(this);
        rich_text_link.setOnClickListener(this);
        rich_text_quote.setOnClickListener(this);
        rich_text_hr.setOnClickListener(this);

    }



    /**
     * 工具栏的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        //清空所有的背景，给当前的view添加背景
        if(mParentView.getChildCount() > 0){
            for(int i = 0; i < mParentView.getChildCount();i++){
                mParentView.getChildAt(i).setBackgroundColor(mContext.getResources().getColor(R.color.white));
                if(v.getId() == mParentView.getChildAt(i).getId()){
                    v.setBackgroundResource(R.drawable.rich_text_tool_select_bg);
                }
            }
        }

        RichTextEditText richTextContent = ((PushBlogActivity)getActivity()).richTextContent;
        boolean isSelectText = richTextContent.getSelectionEnd() > richTextContent.getSelectionStart();//是否选中文字

        switch (v.getId()){
            case R.id.rich_text_bold: //加粗
                ToastUtil.success(mContext, richTextContent.getSelectionStart() + "=====" + richTextContent.getSelectionEnd());
                break;
            case R.id.rich_text_italics: //斜体

                break;
            case R.id.rich_text_p: //p标签换行

                break;
            case R.id.rich_text_h1: //H1标签

                break;
            case R.id.rich_text_h2: //H2标签

                break;
            case R.id.rich_text_h3: //H3标签

                break;
            case R.id.rich_text_h4: //H4标签

                break;
            case R.id.rich_text_h5: //H5标签

                break;
            case R.id.rich_text_h6: //H6标签

                break;
            case R.id.rich_text_color: //颜色

                break;
            case R.id.rich_text_image: //图片

                break;
            case R.id.rich_text_link: //链接

                break;
            case R.id.rich_text_quote: //引用

                break;
            case R.id.rich_text_hr: //分隔线

                break;
        }
    }
}
