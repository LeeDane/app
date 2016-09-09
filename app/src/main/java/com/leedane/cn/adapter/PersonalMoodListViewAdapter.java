package com.leedane.cn.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.leedane.cn.activity.MoodActivity;
import com.leedane.cn.activity.PersonalActivity;
import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.customview.MoodTextView;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.fragment.PersonalMoodFragment;
import com.leedane.cn.helper.PraiseUserHelper;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.List;

/**
 * 个人中心Tab为心情的数据展示的adapter对象
 * Created by LeeDane on 2015/12/8.
 */
public class PersonalMoodListViewAdapter extends BaseListAdapter<MoodBean>{
    public static final String TAG = "PersonalMoodListViewAdapter";
    private PopupMenu popupMenu;

    private PersonalMoodFragment fragment;
    public PersonalMoodListViewAdapter(Context context, List<MoodBean> data, PersonalMoodFragment fragment) {
        super(context, data);
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_personal_mood_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.content = (MoodTextView) view.findViewById(R.id.personal_mood_content);
            viewHolder.froms = (TextView) view.findViewById(R.id.personal_mood_froms);
            viewHolder.time = (TextView) view.findViewById(R.id.personal_mood_time);
            viewHolder.more = (ImageView) view.findViewById(R.id.personal_mood_more);
            viewHolder.location =(TextView) view.findViewById(R.id.personal_mood_location_show);
            viewHolder.imgContainer = (LinearLayout) view.findViewById(R.id.personal_mood_img_container);
            viewHolder.transmit = (RightBorderTextView) view.findViewById(R.id.personal_mood_operate_transmit);
            viewHolder.comment = (RightBorderTextView) view.findViewById(R.id.personal_mood_operate_comment);
            viewHolder.praiseList = (TextView)view.findViewById(R.id.personal_mood_praise_list);

            view.setTag(viewHolder);
        }

        final MoodBean moodBean = mDatas.get(position);
        viewHolder = (ViewHolder)view.getTag();

        String content = moodBean.getContent();
        viewHolder.content.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.content.setFocusable(false);
        viewHolder.content.setDispatchToParent(true);
        viewHolder.content.setLongClickable(false);
        //Spannable spannable = AssimilateUtils.assimilateOnlyLink(mContext, content);
        //spannable = AssimilateUtils.assimilateOnlyAtUser(mContext, spannable);
        //spannable = AssimilateUtils.assimilateOnlyTag(mContext, spannable);
        //spannable = InputHelper.displayEmoji(mContext.getResources(), spannable);

        Spannable spannable= AppUtil.textviewShowImg(mContext, content);
        spannable= AppUtil.textviewShowTopic(mContext, spannable, new AppUtil.ClickTextAction() {
            @Override
            public void call(String str) {
                ToastUtil.success(mContext, "哈哈+"+str);
            }
        });
        viewHolder.content.setText(spannable);

        //viewHolder.getmContent().setText(content, TextView.BufferType.SPANNABLE);

        viewHolder.froms.setTypeface(typeface);
        viewHolder.froms.setText("来自：" + moodBean.getFroms());
        String createTime = moodBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            viewHolder.time.setText("");
        }else{
            viewHolder.time.setTypeface(typeface);
            viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        if(StringUtil.isNotNull(moodBean.getLocation())){
            viewHolder.location.setVisibility(View.VISIBLE);
            viewHolder.location.setText("位置：" + moodBean.getLocation());
        }else{
            viewHolder.location.setVisibility(View.GONE);
        }

        String praiseList = moodBean.getPraiseUserList();

        try{
            PraiseUserHelper helper = new PraiseUserHelper("t_mood", moodBean.getId());
            helper.setLikeUsers(mContext, viewHolder.praiseList, praiseList, moodBean.getZanNumber());
        }catch (Exception e){
            e.printStackTrace();
        }

        final int index = position;
        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showMoodListItemMenuDialog(index, mDatas.get(index));
            }
        });

        //异步去获取该心情的图像路径列表
        /*if(!StringUtil.isNull(moodBean.getImgs())){

            String imgs = moodBean.getImgs();
            final String[] showImages = imgs.split(";");
            if(showImages.length == 1){
                viewHolder.getmImgMain1().setVisibility(View.VISIBLE);
                ImageCacheManager.loadImage(showImages[0], viewHolder.getmImgMain1(), 80, 100);
                viewHolder.getmImgMain1().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startImageDetailActivity(mContext, showImages[0]);
                    }
                });
            }else if(showImages.length == 2){
                viewHolder.getmImgMain1().setVisibility(View.VISIBLE);
                viewHolder.getmImgMain2().setVisibility(View.VISIBLE);
                ImageCacheManager.loadImage(showImages[0], viewHolder.getmImgMain1(), 80, 100);
                ImageCacheManager.loadImage(showImages[1], viewHolder.getmImgMain2(), 80, 100);
                viewHolder.getmImgMain1().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startImageDetailActivity(mContext, showImages[0]);
                    }
                });
                viewHolder.getmImgMain2().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startImageDetailActivity(mContext, showImages[1]);
                    }
                });
            }else if(showImages.length == 3){
                viewHolder.getmImgMain1().setVisibility(View.VISIBLE);
                viewHolder.getmImgMain2().setVisibility(View.VISIBLE);
                viewHolder.getmImgMain3().setVisibility(View.VISIBLE);
                ImageCacheManager.loadImage(showImages[0], viewHolder.getmImgMain1(), 80, 100);
                ImageCacheManager.loadImage(showImages[1], viewHolder.getmImgMain2(), 80, 100);
                ImageCacheManager.loadImage(showImages[2], viewHolder.getmImgMain3(), 80, 100);
                viewHolder.getmImgMain1().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startImageDetailActivity(mContext, showImages[0]);
                    }
                });
                viewHolder.getmImgMain2().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startImageDetailActivity(mContext, showImages[1]);
                    }
                });
                viewHolder.getmImgMain3().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonHandler.startImageDetailActivity(mContext, showImages[2]);
                    }
                });
            }
            *//*for(int i =0; i< showImages.length; i++){
                //拿到图像的路径后再去回去base64位的图像字符串填充到相应的ImageView

            }

           *//*
        }else{
            viewHolder.getmImgMain1().setVisibility(View.GONE);
            viewHolder.getmImgMain2().setVisibility(View.GONE);
            viewHolder.getmImgMain3().setVisibility(View.GONE);
        }*/
        ImageUtil.addImages(mContext, moodBean.getImgs(), viewHolder.imgContainer);

        viewHolder.transmit.setText(mContext.getResources().getString(R.string.personal_transmit) + "(" + StringUtil.changeNotNull(moodBean.getTransmitNumber()) + ")");
        viewHolder.comment.setText(mContext.getResources().getString(R.string.personal_comment) + "(" + StringUtil.changeNotNull(moodBean.getCommentNumber()) + ")");
        viewHolder.transmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.success(mContext, "转发", Toast.LENGTH_SHORT);
                Intent it_transmit = new Intent();
                PersonalActivity activity = (PersonalActivity) mContext;
                it_transmit.setClass(activity, MoodActivity.class);
                it_transmit.putExtra("operateType", EnumUtil.MoodOperateType.转发.value);
                it_transmit.putExtra("moodObj", mDatas.get(index));
                it_transmit.putExtra("width", "30");//展示的图像的宽度
                it_transmit.putExtra("height", "30"); //展示的图像的高度
                activity.startActivity(it_transmit);
            }
        });
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.success(mContext, "评论", Toast.LENGTH_SHORT);
                Intent it_transmit = new Intent();
                PersonalActivity activity = (PersonalActivity) mContext;
                it_transmit.setClass(activity, MoodActivity.class);
                it_transmit.putExtra("operateType", EnumUtil.MoodOperateType.评论.value);
                it_transmit.putExtra("moodObj", mDatas.get(index));
                it_transmit.putExtra("width", "30");//展示的图像的宽度
                it_transmit.putExtra("height", "30"); //展示的图像的高度
                activity.startActivityForResult(it_transmit, PersonalActivity.MOOD_UPDATE_REQUEST_CODE);
            }
        });
        return view;
    }

    static class ViewHolder{
        TextView froms;
        TextView time;
        ImageView more;
        MoodTextView content;
        LinearLayout imgContainer;
        TextView praiseList;
        RightBorderTextView comment;
        RightBorderTextView transmit;
        TextView location;
    }
}
