package com.leedane.cn.richtext;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.activity.PushBlogActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 富文本编辑器的工具类
 * Created by LeeDane on 2016/10/11.
 */
public class ToolFragment extends Fragment implements View.OnClickListener{

    private View mRootView;
    private Context mContext;
    private LinearLayout mParentView; //父视图

    private ImageButton rich_text_undo;
    private ImageButton rich_text_redo;
    private ImageButton rich_text_bold;
    private ImageButton rich_text_italic;
    private ImageButton rich_text_strikethrough;
    private ImageButton rich_text_underline;
    private ImageButton rich_text_h1;
    private ImageButton rich_text_h2;
    private ImageButton rich_text_h3;
    private ImageButton rich_text_h4;
    private ImageButton rich_text_h5;
    private ImageButton rich_text_h6;
    private ImageButton rich_text_color;
    private ImageButton rich_text_indent;
    private ImageButton rich_text_left;
    private ImageButton rich_text_center;
    private ImageButton rich_text_right;
    private ImageButton rich_text_blockquote;
    private ImageButton rich_text_image;
    private ImageButton rich_text_link;

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


        rich_text_undo = (ImageButton)mRootView.findViewById(R.id.rich_text_undo);
        rich_text_redo = (ImageButton)mRootView.findViewById(R.id.rich_text_redo);
        rich_text_bold = (ImageButton)mRootView.findViewById(R.id.rich_text_bold);
        rich_text_italic = (ImageButton)mRootView.findViewById(R.id.rich_text_italic);
        rich_text_strikethrough = (ImageButton)mRootView.findViewById(R.id.rich_text_strikethrough);
        rich_text_underline = (ImageButton)mRootView.findViewById(R.id.rich_text_underline);
        rich_text_h1 = (ImageButton)mRootView.findViewById(R.id.rich_text_h1);
        rich_text_h2 = (ImageButton)mRootView.findViewById(R.id.rich_text_h2);
        rich_text_h3 = (ImageButton)mRootView.findViewById(R.id.rich_text_h3);
        rich_text_h4 = (ImageButton)mRootView.findViewById(R.id.rich_text_h4);
        rich_text_h5 = (ImageButton)mRootView.findViewById(R.id.rich_text_h5);
        rich_text_h6 = (ImageButton)mRootView.findViewById(R.id.rich_text_h6);
        rich_text_color = (ImageButton)mRootView.findViewById(R.id.rich_text_color);
        rich_text_indent = (ImageButton)mRootView.findViewById(R.id.rich_text_indent);
        rich_text_left = (ImageButton)mRootView.findViewById(R.id.rich_text_left);
        rich_text_center = (ImageButton)mRootView.findViewById(R.id.rich_text_center);
        rich_text_right = (ImageButton)mRootView.findViewById(R.id.rich_text_right);
        rich_text_blockquote = (ImageButton)mRootView.findViewById(R.id.rich_text_blockquote);
        rich_text_image = (ImageButton)mRootView.findViewById(R.id.rich_text_image);
        rich_text_link = (ImageButton)mRootView.findViewById(R.id.rich_text_link);

        rich_text_undo.setOnClickListener(this);
        rich_text_redo.setOnClickListener(this);
        rich_text_bold.setOnClickListener(this);
        rich_text_italic.setOnClickListener(this);
        rich_text_strikethrough.setOnClickListener(this);
        rich_text_underline.setOnClickListener(this);
        rich_text_h1.setOnClickListener(this);
        rich_text_h2.setOnClickListener(this);
        rich_text_h3.setOnClickListener(this);
        rich_text_h4.setOnClickListener(this);
        rich_text_h5.setOnClickListener(this);
        rich_text_h6.setOnClickListener(this);
        rich_text_color.setOnClickListener(this);
        rich_text_indent.setOnClickListener(this);
        rich_text_left.setOnClickListener(this);
        rich_text_center.setOnClickListener(this);
        rich_text_right.setOnClickListener(this);
        rich_text_blockquote.setOnClickListener(this);
        rich_text_image.setOnClickListener(this);
        rich_text_link.setOnClickListener(this);

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
                mParentView.getChildAt(i).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                if(v.getId() == mParentView.getChildAt(i).getId()){
                    v.setBackgroundResource(R.drawable.rich_text_tool_select_bg);
                }
            }
        }

        RichTextEditText richTextContent = ((PushBlogActivity)getActivity()).richTextContent;
        boolean isSelectText = richTextContent.getSelectionEnd() > richTextContent.getSelectionStart();//是否选中文字

        String currentText = richTextContent.getText().toString();
        StringBuffer buffer = new StringBuffer(currentText);
        switch (v.getId()){
            case R.id.rich_text_undo: //撤销
                ToastUtil.success(mContext, richTextContent.getSelectionStart() + "=====" + richTextContent.getSelectionEnd());
                break;
            case R.id.rich_text_redo: //反撤销

                break;
            case R.id.rich_text_bold: //粗体
                if(isSelectText){
                    String bStart = "<b>";
                    String bEnd = "</b>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_italic: //斜体
                if(isSelectText){
                    String bStart = "<i>";
                    String bEnd = "</i>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_strikethrough: //过期
                if(isSelectText){
                    String bStart = "<span style=\"text-decoration:line-through;\">";
                    String bEnd = "</span>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_underline: //下划线
                if(isSelectText){
                    String bStart = "<u>";
                    String bEnd = "</u>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;

            case R.id.rich_text_h1: //H1标签
                if(isSelectText){
                    String bStart = "<h1>";
                    String bEnd = "</h1>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_h2: //H2标签
                if(isSelectText){
                    String bStart = "<h2>";
                    String bEnd = "</h2>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_h3: //H3标签
                if(isSelectText){
                    String bStart = "<h3>";
                    String bEnd = "</h3>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_h4: //H4标签
                if(isSelectText){
                    String bStart = "<h4>";
                    String bEnd = "</h4>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_h5: //H5标签
                if(isSelectText){
                    String bStart = "<h5>";
                    String bEnd = "</h5>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_h6: //H6标签
                if(isSelectText){
                    String bStart = "<h6>";
                    String bEnd = "</h6>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.toString().length());
                }
                break;
            case R.id.rich_text_color: //文字颜色
                if(isSelectText){
                    showSelectColorDialog();
                }
                break;
            case R.id.rich_text_indent: //缩进

                break;
            case R.id.rich_text_left: //左对齐

                break;
            case R.id.rich_text_center: //居中对齐

                break;
            case R.id.rich_text_right: //右对齐

                break;
            case R.id.rich_text_blockquote: //引用

                break;
            case R.id.rich_text_image: //图片

                break;
            case R.id.rich_text_link: //链接

                break;
        }
    }


    private Dialog mDialog;

    /**
     * 显示选择颜色view
     */
    public void showSelectColorDialog(){

        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissSelectColorDialog();

        mDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add("#CA4443");
        menus.add("#FF7850");
        menus.add("#F8C461");
        menus.add("#A5F862");
        menus.add("#61AFF9");
        menus.add("#4F637B");
        menus.add("#B26EFF");

        SimpleListAdapter adapter = new SimpleListAdapter(getActivity().getApplicationContext(), menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                String bStart = "<font color=\""+textView.getText()+"\">";
                String bEnd = "</font>";
                RichTextEditText richTextContent = ((PushBlogActivity)getActivity()).richTextContent;
                StringBuffer buffer = new StringBuffer(richTextContent.getText().toString());
                buffer.insert(richTextContent.getSelectionStart(), bStart);
                buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                richTextContent.setText(buffer.toString());
                richTextContent.setSelection(buffer.toString().length());
                dismissSelectColorDialog();
            }
        });
        mDialog.setTitle("请选择颜色");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissSelectColorDialog();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(800,(menus.size() +1) * 90 +20);
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissSelectColorDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }
}
