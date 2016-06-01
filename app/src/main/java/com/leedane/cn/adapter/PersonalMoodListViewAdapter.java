package com.leedane.cn.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.activity.MoodActivity;
import com.leedane.cn.activity.PersonalActivity;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.bean.ZanUserBean;
import com.leedane.cn.customview.AutoLinkTextView;
import com.leedane.cn.customview.RightBorderTextView;
import com.leedane.cn.fragment.PersonalMoodFragment;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.helper.PraiseUserHelper;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心Tab为心情的数据展示的adapter对象
 * Created by LeeDane on 2015/12/8.
 */
public class PersonalMoodListViewAdapter extends BaseAdapter{
    public static final String TAG = "PersonalMoodListViewAdapter";
    private Context mContext;
    private List<MoodBean> mData;
    //private FragmentActivity mActivity;
    private PopupMenu popupMenu;

    private PersonalMoodFragment fragment;
    public PersonalMoodListViewAdapter(Context context, List<MoodBean> data, PersonalMoodFragment fragment) {
        this.mContext = context;
        this.mData = data;
        this.fragment = fragment;
        //this.mActivity = activity;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<MoodBean> moods){
        this.mData.clear();
        this.mData.addAll(moods);
        this.notifyDataSetChanged();
    }
    ViewHolder viewHolder = null;
    @Override
    public View getView(int position, View view, ViewGroup group) {
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_personal_mood_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmContent((AutoLinkTextView) view.findViewById(R.id.personal_mood_content));
            viewHolder.setmFroms((TextView) view.findViewById(R.id.personal_mood_froms));
            viewHolder.setmTime((TextView) view.findViewById(R.id.personal_mood_time));
            viewHolder.setmMore((ImageView) view.findViewById(R.id.personal_mood_more));

            viewHolder.setmImgMain((ImageView) view.findViewById(R.id.personal_mood_img_main));
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

        final MoodBean moodBean = mData.get(position);
        viewHolder = (ViewHolder)view.getTag();

        String content = moodBean.getContent();
        //SpannableString mSpanContent = new SpannableString(content);

        //设置字体大小（绝对值,单位：像素）
        //mSpanTitle.setSpan(new AbsoluteSizeSpan(70), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体
        //mSpanTitle.setSpan(new TypefaceSpan("monospace"), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.getmContent().setText("  "+ content, TextView.BufferType.SPANNABLE);
        viewHolder.getmFroms().setText("来自："+moodBean.getFroms());
        String createTime = moodBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            viewHolder.getmTime().setText("");
        }else{
            viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
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
                fragment.showMoodListItemMenuDialog(index, mData.get(index).getCreateUserId(), mData.get(index).isHasImg());
            }
        });

        //异步去获取该心情的图像路径列表
        if(!StringUtil.isNull(moodBean.getImgs())){
            viewHolder.getmImgMain().setVisibility(View.VISIBLE);
            String imgs = moodBean.getImgs();
            String[] showImages = imgs.split(",");
            for(String img: showImages){
                //拿到图像的路径后再去回去base64位的图像字符串填充到相应的ImageView
                ImageCacheManager.loadImage(img, viewHolder.getmImgMain(), 100 , 50);
            }

            viewHolder.getmImgMain().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startImageDetailActivity(mContext, moodBean.getImgs());
                }
            });
        }else{
            viewHolder.getmImgMain().setVisibility(View.GONE);
        }

        viewHolder.getmTransmit().setText(mContext.getResources().getString(R.string.personal_transmit) + "(" +String.valueOf(moodBean.getTransmitNumber()) + ")");
        viewHolder.getmComment().setText(mContext.getResources().getString(R.string.personal_comment) + "(" + String.valueOf(moodBean.getCommentNumber()) + ")");
        viewHolder.getmTransmit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "转发", Toast.LENGTH_SHORT).show();
                Intent it_transmit = new Intent();
                PersonalActivity activity =  (PersonalActivity)mContext;
                it_transmit.setClass(activity, MoodActivity.class);
                it_transmit.putExtra("operateType", EnumUtil.MoodOperateType.转发.value);
                it_transmit.putExtra("moodObj",mData.get(index));
                it_transmit.putExtra("width","30");//展示的图像的宽度
                it_transmit.putExtra("height","30"); //展示的图像的高度
                activity.startActivity(it_transmit);
            }
        });
        viewHolder.getmComment().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "评论", Toast.LENGTH_SHORT).show();
                Intent it_transmit = new Intent();
                PersonalActivity activity =  (PersonalActivity)mContext;
                it_transmit.setClass(activity, MoodActivity.class);
                it_transmit.putExtra("operateType", EnumUtil.MoodOperateType.评论.value);
                it_transmit.putExtra("moodObj",mData.get(index));
                it_transmit.putExtra("width","30");//展示的图像的宽度
                it_transmit.putExtra("height", "30"); //展示的图像的高度
                //activity.startActivity(it_transmit);
                activity.startActivityForResult(it_transmit, PersonalActivity.MOOD_COMMENT_REQUEST_CODE);
            }
        });
        /*viewHolder.getmPraise().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "赞:"+moodBean.getContent(), Toast.LENGTH_SHORT).show();
                HttpRequestBean requestBean = new HttpRequestBean();
                HashMap<String, Object> params = new HashMap<>();
                params.put("table_name", "t_mood");
                params.put("table_id", moodBean.getId());
                params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                requestBean.setParams(params);
                requestBean.setServerMethod("leedane/zan_add.action");
                TaskLoader.getInstance().startTaskForResult(TaskType.ADD_ZAN, PersonalMoodListViewAdapter.this, requestBean);
                //String serverUrl = SharedPreferenceUtil.getSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent() +"leedane/zan_add.action";

            }
        });*/
        return view;
    }

    private class ViewHolder{
        private TextView mFroms;
        private TextView mTime;
        private ImageView mMore;
        private AutoLinkTextView mContent;
        private ImageView mImgMain;
        private TextView mPraiseList;
        private RightBorderTextView mComment;
        private RightBorderTextView mTransmit;
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

        public ImageView getmImgMain() {
            return mImgMain;
        }

        public void setmImgMain(ImageView mImgMain) {
            this.mImgMain = mImgMain;
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
    }


}
