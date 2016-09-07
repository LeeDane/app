package com.leedane.cn.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.customview.AutoLinkTextView;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.fragment.PersonalMoodFragment;
import com.leedane.cn.helper.PraiseUserHelper;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

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
            viewHolder.setmContent((AutoLinkTextView) view.findViewById(R.id.personal_mood_content));
            viewHolder.setmFroms((TextView) view.findViewById(R.id.personal_mood_froms));
            viewHolder.setmTime((TextView) view.findViewById(R.id.personal_mood_time));
            viewHolder.setmMore((ImageView) view.findViewById(R.id.personal_mood_more));

            viewHolder.setmLocation((TextView) view.findViewById(R.id.personal_mood_location_show));
            viewHolder.setmImgContainer((LinearLayout) view.findViewById(R.id.personal_mood_img_container));
            viewHolder.setmTransmit((RightBorderTextView) view.findViewById(R.id.personal_mood_operate_transmit));
            viewHolder.setmComment((RightBorderTextView) view.findViewById(R.id.personal_mood_operate_comment));
            //viewHolder.setmPraise((RightBorderTextView)view.findViewById(R.id.personal_mood_operate_praise));
            /*MulitLinkTextView tv = (MulitLinkTextView)view.findViewById(R.id.personal_mood_praise_list);
            tv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    boolean ret = false;
                    CharSequence text = ((TextView) v).getText();
                    Spannable stext = Spannable.Factory.getInstance().newSpannable(text);
                    TextView widget = (TextView) v;
                    int action = event.getAction();

                    if (action == MotionEvent.ACTION_UP ||
                            action == MotionEvent.ACTION_DOWN) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        x -= widget.getTotalPaddingLeft();
                        y -= widget.getTotalPaddingTop();

                        x += widget.getScrollX();
                        y += widget.getScrollY();

                        Layout layout = widget.getLayout();
                        int line = layout.getLineForVertical(y);
                        int off = layout.getOffsetForHorizontal(line, x);

                        ClickableSpan[] link = stext.getSpans(off, off, ClickableSpan.class);

                        if (link.length != 0) {
                            if (action == MotionEvent.ACTION_UP) {
                                link[0].onClick(widget);
                            }
                            ret = true;
                        }
                    }
                    return ret;
                }
            });*/
            viewHolder.setmPraiseList((TextView)view.findViewById(R.id.personal_mood_praise_list));

            view.setTag(viewHolder);
        }

        final MoodBean moodBean = mDatas.get(position);
        viewHolder = (ViewHolder)view.getTag();

        String content = moodBean.getContent();
        //SpannableString mSpanContent = new SpannableString(content);

        //设置字体大小（绝对值,单位：像素）
        //mSpanTitle.setSpan(new AbsoluteSizeSpan(70), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体
        //mSpanTitle.setSpan(new TypefaceSpan("monospace"), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.getmContent().setText(content, TextView.BufferType.SPANNABLE);

        viewHolder.getmFroms().setTypeface(typeface);
        viewHolder.getmFroms().setText("来自："+moodBean.getFroms());
        AppUtil.textviewShowImg(mContext, viewHolder.getmContent());
        String createTime = moodBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            viewHolder.getmTime().setText("");
        }else{
            viewHolder.getmTime().setTypeface(typeface);
            viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        if(StringUtil.isNotNull(moodBean.getLocation())){
            viewHolder.getmLocation().setVisibility(View.VISIBLE);
            viewHolder.getmLocation().setText("位置："+moodBean.getLocation());
        }else{
            viewHolder.getmLocation().setVisibility(View.GONE);
        }

        String praiseList = moodBean.getPraiseUserList();

        try{
            PraiseUserHelper helper = new PraiseUserHelper("t_mood", moodBean.getId());
            helper.setLikeUsers(mContext, viewHolder.getmPraiseList(), praiseList, moodBean.getZanNumber());
        }catch (Exception e){
            e.printStackTrace();
        }

        final int index = position;
        viewHolder.getmMore().setOnClickListener(new View.OnClickListener() {
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
        ImageUtil.addImages(mContext, moodBean.getImgs(), viewHolder.getmImgContainer());

        viewHolder.getmTransmit().setText(mContext.getResources().getString(R.string.personal_transmit) + "(" + StringUtil.changeNotNull(moodBean.getTransmitNumber()) + ")");
        viewHolder.getmComment().setText(mContext.getResources().getString(R.string.personal_comment) + "(" + StringUtil.changeNotNull(moodBean.getCommentNumber()) + ")");
        viewHolder.getmTransmit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.success(mContext, "转发", Toast.LENGTH_SHORT);
                Intent it_transmit = new Intent();
                PersonalActivity activity =  (PersonalActivity)mContext;
                it_transmit.setClass(activity, MoodActivity.class);
                it_transmit.putExtra("operateType", EnumUtil.MoodOperateType.转发.value);
                it_transmit.putExtra("moodObj",mDatas.get(index));
                it_transmit.putExtra("width","30");//展示的图像的宽度
                it_transmit.putExtra("height","30"); //展示的图像的高度
                activity.startActivity(it_transmit);
            }
        });
        viewHolder.getmComment().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.success(mContext, "评论", Toast.LENGTH_SHORT);
                Intent it_transmit = new Intent();
                PersonalActivity activity =  (PersonalActivity)mContext;
                it_transmit.setClass(activity, MoodActivity.class);
                it_transmit.putExtra("operateType", EnumUtil.MoodOperateType.评论.value);
                it_transmit.putExtra("moodObj",mDatas.get(index));
                it_transmit.putExtra("width","30");//展示的图像的宽度
                it_transmit.putExtra("height", "30"); //展示的图像的高度
                activity.startActivityForResult(it_transmit, PersonalActivity.MOOD_COMMENT_REQUEST_CODE);
            }
        });
        return view;
    }

    private class ViewHolder{
        private TextView mFroms;
        private TextView mTime;
        private ImageView mMore;
        private AutoLinkTextView mContent;
       private LinearLayout mImgContainer;
        private TextView mPraiseList;
        private RightBorderTextView mComment;
        private RightBorderTextView mTransmit;
        private TextView mLocation;
        //private RightBorderTextView mPraise;

        public ImageView getmMore() {
            return mMore;
        }

        public void setmMore(ImageView mMore) {
            this.mMore = mMore;
        }

        public RightBorderTextView getmComment() {
            return mComment;
        }

        public void setmComment(RightBorderTextView mComment) {
            this.mComment = mComment;
        }

        public AutoLinkTextView getmContent() {
            return mContent;
        }

        public void setmContent(AutoLinkTextView mContent) {
            this.mContent = mContent;
        }

        public TextView getmFroms() {
            return mFroms;
        }

        public void setmFroms(TextView mFroms) {
            this.mFroms = mFroms;
        }

        public LinearLayout getmImgContainer() {
            return mImgContainer;
        }

        public void setmImgContainer(LinearLayout mImgContainer) {
            this.mImgContainer = mImgContainer;
        }

        /*public RightBorderTextView getmPraise() {
            return mPraise;
        }

        public void setmPraise(RightBorderTextView mPraise) {
            this.mPraise = mPraise;
        }*/

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

       public RightBorderTextView getmTransmit() {
            return mTransmit;
        }

        public void setmTransmit(RightBorderTextView mTransmit) {
            this.mTransmit = mTransmit;
        }

        public TextView getmPraiseList() {
            return mPraiseList;
        }

        public void setmPraiseList(TextView mPraiseList) {
            this.mPraiseList = mPraiseList;
        }

        public TextView getmLocation() {
            return mLocation;
        }

        public void setmLocation(TextView mLocation) {
            this.mLocation = mLocation;
        }
    }


}
