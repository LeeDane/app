package com.leedane.cn.richtext;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.activity.PushBlogActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DensityUtil;
import com.leedane.cn.util.StringUtil;
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

    private String[] colors;

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

        colors = mContext.getResources().getStringArray(R.array.colors);
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

        ((PushBlogActivity)getActivity()).preSelectToolId = v.getId();
        RichTextEditText richTextContent = ((PushBlogActivity)getActivity()).richTextContent;
        boolean isSelectText = richTextContent.getSelectionEnd() > richTextContent.getSelectionStart();//是否选中文字

        String currentText = richTextContent.getText().toString();
        StringBuffer buffer = new StringBuffer(currentText);
        switch (v.getId()){
            case R.id.rich_text_undo: //撤销
                if(((PushBlogActivity)getActivity()).isInsertImage){
                    //((PushBlogActivity)getActivity()).preSelectToolId = -1;
                    mRootView.findViewById(R.id.rich_text_undo).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                    ToastUtil.success(mContext, "有图片未上传完成，无法进行此操作");
                }else{
                    //((PushBlogActivity)getActivity()).preSelectToolId = -1;
                    mRootView.findViewById(R.id.rich_text_undo).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                    ((PushBlogActivity)getActivity()).removeOperateData();
                }
                break;
            case R.id.rich_text_redo: //反撤销
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_redo).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                break;
            case R.id.rich_text_bold: //粗体
                if(isSelectText){
                    String bStart = "<b>";
                    String bEnd = "</b>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_italic: //斜体
                if(isSelectText){
                    String bStart = "<i>";
                    String bEnd = "</i>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_strikethrough: //过期
                if(isSelectText){
                    String bStart = "<span style=\"text-decoration:line-through;\">";
                    String bEnd = "</span>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_underline: //下划线
                if(isSelectText){
                    String bStart = "<u>";
                    String bEnd = "</u>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;

            case R.id.rich_text_h1: //H1标签
                if(isSelectText){
                    String bStart = "<h1>";
                    String bEnd = "</h1>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_h2: //H2标签
                if(isSelectText){
                    String bStart = "<h2>";
                    String bEnd = "</h2>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_h3: //H3标签
                if(isSelectText){
                    String bStart = "<h3>";
                    String bEnd = "</h3>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_h4: //H4标签
                if(isSelectText){
                    String bStart = "<h4>";
                    String bEnd = "</h4>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_h5: //H5标签
                if(isSelectText){
                    String bStart = "<h5>";
                    String bEnd = "</h5>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_h6: //H6标签
                if(isSelectText){
                    String bStart = "<h6>";
                    String bEnd = "</h6>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }
                break;
            case R.id.rich_text_color: //文字颜色
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_color).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                if(isSelectText){
                    //showSelectColorDialog();
                    ColorSelectDialog dialog = new ColorSelectDialog(mContext);
                    dialog.setItemSelectListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String bStart = "<font color=\""+colors[position]+"\">";
                            String bEnd = "</font>";
                            RichTextEditText richTextContent = ((PushBlogActivity)getActivity()).richTextContent;
                            StringBuffer buffer = new StringBuffer(richTextContent.getText().toString());
                            buffer.insert(richTextContent.getSelectionStart(), bStart);
                            buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                            richTextContent.setText(buffer.toString());
                            richTextContent.setSelection(buffer.length());
                        }
                    });
                    dialog.show();
                }else{
                    ToastUtil.failure(mContext, "请先选中文字");
                }
                break;
            case R.id.rich_text_indent: //缩进
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_indent).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                break;
            case R.id.rich_text_left: //左对齐
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_left).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                break;
            case R.id.rich_text_center: //居中对齐
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_center).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                break;
            case R.id.rich_text_right: //右对齐
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_right).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                break;
            case R.id.rich_text_blockquote: //引用
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_blockquote).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                if(isSelectText){
                    String bStart = "<blockquote>";
                    String bEnd = "</blockquote>";
                    buffer.insert(richTextContent.getSelectionStart(), bStart);
                    buffer.insert(richTextContent.getSelectionEnd() + bStart.length(), bEnd);
                    richTextContent.setText(buffer.toString());
                    richTextContent.setSelection(buffer.length());
                }else{
                    ToastUtil.failure(mContext, "请先选中文字");
                }
                break;
            case R.id.rich_text_image: //图片
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_image).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                //弹出选择
                showSelectItemMenuDialog();
                break;
            case R.id.rich_text_link: //链接
                ((PushBlogActivity)getActivity()).preSelectToolId = -1;
                mRootView.findViewById(R.id.rich_text_link).setBackgroundColor(mContext.getResources().getColor(R.color.black));
                showAddLinkDialog();
                break;
        }
    }

    /**
     * 弹出添加链接的Dialog
     */
    private void showAddLinkDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_link_dialog, null);
        final EditText inputServer = (EditText)view.findViewById(R.id.add_link_dialog_text);
        final TextView inputDesc = (TextView)view.findViewById(R.id.add_link_dialog_desc);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请输入链接").setIcon(R.drawable.ic_http_red_200_18dp).setView(view)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String text = inputServer.getText().toString();
                if (StringUtil.isNotNull(text)) {
                    if(StringUtil.isLink(text)){
                        RichTextEditText richTextContent = ((PushBlogActivity)getActivity()).richTextContent;
                        String currentText = richTextContent.getText().toString();
                        StringBuffer buffer = new StringBuffer(currentText);
                        text = "<a href=\""+text+"\">" + inputDesc.getText().toString() + "</a>";
                        buffer.insert(richTextContent.getSelectionStart(), text);
                        ((PushBlogActivity)getActivity()).preSelectToolId = 0;
                        richTextContent.setText(buffer.toString());
                        richTextContent.setSelection(buffer.length());
                    }else{
                        ToastUtil.failure(mContext, mContext.getString(R.string.is_not_a_link));
                    }
                }else{
                    ToastUtil.failure(mContext, "请输入链接!");
                }
            }
        });
        builder.show();
    }

    private Dialog mDialog;
    /**
     * 显示弹出自定义view
     */
    public void showSelectItemMenuDialog(){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissSelectItemMenuDialog();

        mDialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(mContext).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add(BaseApplication.newInstance().getString(R.string.select_gallery));
        menus.add(BaseApplication.newInstance().getString(R.string.img_link));
        SimpleListAdapter adapter = new SimpleListAdapter(mContext, menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //选择图库
                if(textView.getText().toString().equalsIgnoreCase(BaseApplication.newInstance().getString(R.string.select_gallery))){
                    ((PushBlogActivity)getActivity()).getSystemImage();
                    //选择链接
                }else if(textView.getText().toString().equalsIgnoreCase(BaseApplication.newInstance().getString(R.string.img_link))){
                    final EditText inputServer = new EditText(mContext);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("请输入网络图片(大小最好不要超过500k)").setIcon(R.drawable.ic_http_red_200_18dp).setView(inputServer)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String text = inputServer.getText().toString();
                            if (StringUtil.isNotNull(text)) {
                                if(StringUtil.isLink(text)){
                                    RichTextEditText richTextContent = ((PushBlogActivity)getActivity()).richTextContent;
                                    String currentText = richTextContent.getText().toString();
                                    StringBuffer buffer = new StringBuffer(currentText);
                                    float device_width_dp = DensityUtil.px2dip(mContext, BaseApplication.newInstance().getScreenWidthAndHeight()[0]) -10;
                                    text = "<img width=\""+device_width_dp+"\" src=\"" + text+"\">";
                                    buffer.insert(richTextContent.getSelectionStart(), text);
                                    ((PushBlogActivity)getActivity()).preSelectToolId = 0;
                                    richTextContent.setText(buffer.toString());
                                    richTextContent.setSelection(buffer.length());
                                }else{
                                    ToastUtil.failure(mContext, mContext.getString(R.string.is_not_a_link));
                                }
                            }else{
                                ToastUtil.failure(mContext, "请输入网络图片链接!");
                            }
                        }
                    });
                    builder.show();
                }
                dismissSelectItemMenuDialog();
            }
        });
        mDialog.setTitle("选择");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissSelectItemMenuDialog();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(800,(menus.size() +1) * 90 +20);
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissSelectItemMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }
}
