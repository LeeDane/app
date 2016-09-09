package com.leedane.cn.util;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.Service;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leedane.cn.emoji.EmojiUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/10/11.
 */
public class AppUtil {
    public static final String TAG = "AppUtil";

    /**
     * 获取当前android的sdk的版本
     * @return
     */
    public static int getAndroidSDKVersion(){
        int version = 0;
        try {
            version = Integer.valueOf(Build.VERSION.SDK_INT);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 接收外部分享的图片
     * @param activity
     * @return
     */
    public static List<String> getListPicPaths(Activity activity){
        List<String> picPaths = new ArrayList<>();
        Intent intent = activity.getIntent();//如果是从外部进入APP，则实现以下方法
        if(Intent.ACTION_SEND.equals(intent.getAction())){
            if(intent.getType().startsWith("image/")){
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if(imageUri != null){
                    //处理单张照片
                    Log.i(TAG, "外部分享到本app的地址："+imageUri.getPath());
                    if(!imageUri.getPath().contains("external/images/media"))
                        picPaths.add(imageUri.getPath());
                    else {
                        String img_path = MediaUtil.getImageAbsolutePath(activity, imageUri);
                        picPaths.add(img_path);
                    }
                }
            }
        }else if(Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())){
            if(intent.getType().startsWith("image/")){
                List<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if(imageUris != null){
                    //处理多张图片
                    for(int i =0; i < imageUris.size(); i++){
                        Log.i(TAG, "外部分享到本app的地址："+imageUris.get(i).getPath());
                        if(!imageUris.get(i).getPath().contains("external/images/media"))
                            picPaths.add(imageUris.get(i).getPath());
                        else {
                            String img_path = MediaUtil.getImageAbsolutePath(activity, imageUris.get(i));
                            picPaths.add(img_path);
                        }
                    }
                }
            }
        }

        return picPaths;
    }

    /**
     * 从uri中获取文件的真实路径
     * @param activity
     * @param uri
     * @return
     */
    /*public static String getPathByUri(Activity activity, Uri uri){
        String imagePath = uri.getPath();
        if(imagePath.contains("external/images/media")){
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            imagePath = actualimagecursor.getString(actual_image_column_index);
        }

        return imagePath;
    }*/

    /**
     * 手机设置震动
     * @param context
     * @param milliSeconds 振动时长，单位是毫秒
     */
    public static void vibrate(final Context context, long milliSeconds){
        Vibrator vib = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliSeconds);
    }

    /**
     * 手机设置震动
     * @param context
     * @param pattern 自定义振动模式，数组中的数字含义依次是【静止时长，振动时长，静止时长，振动时长。。。】时长的单位是毫秒
     * @param isRepeat 是否反复振动，如果是true，反复振动，如果是false,只振动一次
     */
    public static void vibrate(final Context context, long[] pattern, boolean isRepeat){
        Vibrator vib = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat? 1: -1);
    }

    public static void editTextShowImg(final Context context, EditText editText){
        /*EditText:
            通常用于显示文字，但有时候也需要在文字中夹杂一些图片，比如QQ中就可以使用表情图片，又比如需要的文字高亮显示等等，如何在android中也做到这样呢？
            记得android中有个android.text包，这里提供了对文本
        */

        //Drawable drawable = context.getResources().getDrawable(imageResId);

        //需要处理的文本，[ha]是需要被替代的文本
        //drawable.setBounds(0, 0, 60, 60);

        //要让图片替代指定的文字就要用ImageSpan
        //SpannableString spannable = new SpannableString(editText.getText().toString()+"[ha]");

        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
        //ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //spannable.setSpan(span, editText.getText().length(), editText.getText().length() + "[ha]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        //editText.setText(spannable);

        String text = editText.getText().toString();
        //Pattern pattern = Pattern.compile("\[(\S+?)\]");  //这里是过滤出[XX]这种形式的字符串，下面是把这种形式的字符串替换成对应的表情
        Pattern p=Pattern.compile("\\[([^\\[\\]]+)\\]");
        Matcher m=p.matcher(text);
        String group = null;
        //SpannableString spannableString = new SpannableString(text);
        int drawableSrc = 0;
        //要让图片替代指定的文字就要用ImageSpan
        SpannableString spannable = new SpannableString(text);
        while(m.find()){
            group = m.group().trim();
            if(StringUtil.isNotNull(group) && group.startsWith("[") && group.endsWith("]")){

                int start = m.start();
                int end = m.end();
                group = group.substring(1, group.length() -1);
                drawableSrc = EmojiUtil.getImgId(group);
                if(drawableSrc > 0){
                    Drawable drawable = context.getResources().getDrawable(drawableSrc);
                    //需要处理的文本，[ha]是需要被替代的文本
                    drawable.setBounds(0, 0, 60, 60);

                    //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
                    //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    spannable.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    /*spannableString.setSpan(span, start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                }
            }
        }
        editText.setText(spannable);
    }
    public interface ClickTextAction{
        void call(String str);
    }


    /**
     * 展示文本中的表情
     * @param context
     * @param text
     * @return
     */
    public static Spannable textviewShowImg(final Context context, CharSequence text){
        //Pattern pattern = Pattern.compile("\[(\S+?)\]");  //这里是过滤出[XX]这种形式的字符串，下面是把这种形式的字符串替换成对应的表情
        Pattern p=Pattern.compile("\\[([^\\[\\]]+)\\]");
        String group = null;
        //SpannableString spannableString = new SpannableString(text);
        int drawableSrc = 0;
        //要让图片替代指定的文字就要用ImageSpan
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher m=p.matcher(builder.toString());
        while(m.find()){
            group = m.group().trim();
            if(StringUtil.isNotNull(group) && group.startsWith("[") && group.endsWith("]")){

                int start = m.start();
                int end = m.end();
                final String g = group.substring(1, group.length() -1);
                drawableSrc = EmojiUtil.getImgId(g);
                if(drawableSrc > 0){
                    Drawable drawable = context.getResources().getDrawable(drawableSrc);
                    //需要处理的文本，[ha]是需要被替代的文本
                    drawable.setBounds(0, 0, 60, 60);

                    //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
                    //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    builder.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    /*spannableString.setSpan(new ImageSpan(context, drawableSrc), start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/

                }
            }
        }
        return builder;
    }

    /**
     * 展示文本中的话题
     * @param context
     * @param text
     * @param action
     * @return
     */
    public static Spannable textviewShowTopic(final Context context, CharSequence text, final ClickTextAction action){
        Pattern p = Pattern.compile("\\#([^\\[\\]]+)\\#");
        String group;
        //SpannableString spannableString = new SpannableString(text);
        //要让图片替代指定的文字就要用ImageSpan
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher m=p.matcher(builder.toString());
        while(m.find()){
            group = m.group().trim();
            if(StringUtil.isNotNull(group) && group.startsWith("#") && group.endsWith("#")){

                int start = m.start();
                int end = m.end();
                final String g = group.substring(1, group.length() -1);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        action.call(g);
                    }
                };
                builder.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }
}
