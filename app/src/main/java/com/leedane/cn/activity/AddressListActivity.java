package com.leedane.cn.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.adapter.ContactAdapter;
import com.leedane.cn.adapter.expandRecyclerviewadapter.StickyRecyclerHeadersDecoration;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ContactBean;
import com.leedane.cn.bean.LocalContactBean;
import com.leedane.cn.pinyin.CharacterParser;
import com.leedane.cn.pinyin.PinyinComparator;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.widget.DividerDecoration;
import com.leedane.cn.widget.SideBar;
import com.leedane.cn.widget.TouchableRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通讯录activity
 * Created by LeeDane on 2016/4/21.
 */
public class AddressListActivity extends Activity implements ContactAdapter.OnRecyclerViewListener{
    public static final String TAG = "AddressListActivity";
    private SideBar mSideBar;
    private TextView mUserDialog;
    private TouchableRecyclerView mRecyclerView;

    ContactBean mModel;
    private List<ContactBean.MembersEntity> mMembers = new ArrayList<>();
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private ContactAdapter mAdapter;
    private List<ContactBean.MembersEntity> mAllLists = new ArrayList<>();
    private List<LocalContactBean> localContactBeans = new ArrayList<>();
    //private int mPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkedIsLogin();
        setContentView(R.layout.activity_address_list);
        //getPermission();
        initView();

    }

    /**
     * 检查是否登录
     */
    private void checkedIsLogin() {
        //判断是否有缓存用户信息
        if(BaseApplication.getLoginUserId() < 1){
            Intent it = new Intent(AddressListActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.AddressListActivity");
            startActivity(it);
            finish();
            return;
        }
    }

    /*private void getPermission() {
        mPermission = CommonString.PermissionCode.TEACHER;
    }*/


    private void initView() {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mSideBar = (SideBar) findViewById(R.id.contact_sidebar);
        mUserDialog = (TextView) findViewById(R.id.contact_dialog);
        mRecyclerView = (TouchableRecyclerView) findViewById(R.id.contact_member);
        mSideBar.setTextView(mUserDialog);


        //readAllLocalContacts();
//        fillData();
        getNetData(0);


    }

    /*
     * 读取联系人的信息
     */
    public void readAllLocalContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if(cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        LocalContactBean localContactBean;
        List<String> phoneNumbers;
        List<String> contactEmails;
        while(cursor.moveToNext()) {
            localContactBean = new LocalContactBean();
            phoneNumbers = new ArrayList<>();
            contactEmails = new ArrayList<>();
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            localContactBean.setId(contactId);
            localContactBean.setName(name);
            /*
             * 查找该联系人的phone信息
             */
            Cursor phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if(phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            while(phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                phoneNumbers.add(phoneNumber);
            }

            localContactBean.setPhoneNumbers(phoneNumbers);
            /*
             * 查找该联系人的email信息
             */
            Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId,
                    null, null);
            int emailIndex = 0;
            if(emails.getCount() > 0) {
                emailIndex = emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            }
            while(emails.moveToNext()) {
                String email = emails.getString(emailIndex);
                contactEmails.add(email);
            }
            localContactBean.setEmails(contactEmails);
            localContactBeans.add(localContactBean);

        }
    }


    public void getNetData(final int type) {

        //id 已经被处理过
        //String tempData = "{\"groupName\":\"中国\",\"admins\":[{\"id\":\"111221\",\"username\":\"程景瑞\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"钱黛\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"许勤颖\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"孙顺元\",\"profession\":\"teacher\"},{\"id\":\"fcd1feb5db2\",\"username\":\"朱佳\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"李茂\",\"profession\":\"teacher\"},{\"id\":\"d1feb5db2\",\"username\":\"周莺\",\"profession\":\"teacher\"},{\"id\":\"cd1feb5db2\",\"username\":\"任倩栋\",\"profession\":\"teacher\"},{\"id\":\"d1feb5db2\",\"username\":\"严庆佳\",\"profession\":\"teacher\"}],\"members\":[{\"id\":\"d1feb5db2\",\"username\":\"彭怡1\",\"profession\":\"student\"},{\"id\":\"d1feb5db2\",\"username\":\"方谦\",\"profession\":\"student\"},{\"id\":\"dd2feb5db2\",\"username\":\"谢鸣瑾\",\"profession\":\"student\"},{\"id\":\"dd2478fb5db2\",\"username\":\"孔秋\",\"profession\":\"student\"},{\"id\":\"dd24cd1feb5db2\",\"username\":\"曹莺安\",\"profession\":\"student\"},{\"id\":\"dd2478eb5db2\",\"username\":\"酆有松\",\"profession\":\"student\"},{\"id\":\"dd2478b5db2\",\"username\":\"姜莺岩\",\"profession\":\"student\"},{\"id\":\"dd2eb5db2\",\"username\":\"谢之轮\",\"profession\":\"student\"},{\"id\":\"dd2eb5db2\",\"username\":\"钱固茂\",\"profession\":\"student\"},{\"id\":\"dd2d1feb5db2\",\"username\":\"潘浩\",\"profession\":\"student\"},{\"id\":\"dd24ab5db2\",\"username\":\"花裕彪\",\"profession\":\"student\"},{\"id\":\"dd24ab5db2\",\"username\":\"史厚婉\",\"profession\":\"student\"},{\"id\":\"dd24a00d1feb5db2\",\"username\":\"陶信勤\",\"profession\":\"student\"},{\"id\":\"dd24a5db2\",\"username\":\"水天固\",\"profession\":\"student\"},{\"id\":\"dd24a5db2\",\"username\":\"柳莎婷\",\"profession\":\"student\"},{\"id\":\"dd2d1feb5db2\",\"username\":\"冯茜\",\"profession\":\"student\"},{\"id\":\"dd24a0eb5db2\",\"username\":\"吕言栋\",\"profession\":\"student\"}],\"creater\":{\"id\":\"1\",\"username\":\"褚奇清\",\"profession\":\"teacher\"}}";
        String tempData = "{\"groupName\":\"中国\",\"members\":[{\"id\":\"d1feb5db2\",\"username\":\"彭怡1\",\"profession\":\"student\"},{\"id\":\"d1feb5db2\",\"username\":\"方谦\",\"profession\":\"student\"},{\"id\":\"dd2feb5db2\",\"username\":\"谢鸣瑾\",\"profession\":\"student\"},{\"id\":\"dd2478fb5db2\",\"username\":\"孔秋\",\"profession\":\"student\"},{\"id\":\"dd24cd1feb5db2\",\"username\":\"曹莺安\",\"profession\":\"student\"},{\"id\":\"dd2478eb5db2\",\"username\":\"酆有松\",\"profession\":\"student\"},{\"id\":\"dd2478b5db2\",\"username\":\"姜莺岩\",\"profession\":\"student\"},{\"id\":\"dd2eb5db2\",\"username\":\"谢之轮\",\"profession\":\"student\"},{\"id\":\"dd2eb5db2\",\"username\":\"钱固茂\",\"profession\":\"student\"},{\"id\":\"dd2d1feb5db2\",\"username\":\"潘浩\",\"profession\":\"student\"},{\"id\":\"dd24ab5db2\",\"username\":\"花裕彪\",\"profession\":\"student\"},{\"id\":\"dd24ab5db2\",\"username\":\"史厚婉\",\"profession\":\"student\"},{\"id\":\"dd24a00d1feb5db2\",\"username\":\"陶信勤\",\"profession\":\"student\"},{\"id\":\"dd24a5db2\",\"username\":\"水天固\",\"profession\":\"student\"},{\"id\":\"dd24a5db2\",\"username\":\"柳莎婷\",\"profession\":\"student\"},{\"id\":\"dd2d1feb5db2\",\"username\":\"冯茜\",\"profession\":\"student\"},{\"id\":\"dd24a0eb5db2\",\"username\":\"吕言栋\",\"profession\":\"student\"}]}";

        try {
            Gson gson = new GsonBuilder().create();
            mModel = gson.fromJson(tempData, ContactBean.class);
            setUI();
        } catch (Exception e) {

        }


    }

    private void setUI() {

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (mAdapter != null) {
                    mAdapter.closeOpenedSwipeItemLayoutWithAnim();
                }
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRecyclerView.scrollToPosition(position);
                }

            }
        });
        seperateLists(mModel);

        if (mAdapter == null) {
            mAdapter = new ContactAdapter(this, mAllLists/*, mPermission, mModel.getCreater().getId()*/);
            int orientation = LinearLayoutManager.VERTICAL;
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
            mRecyclerView.setLayoutManager(layoutManager);

            mRecyclerView.setAdapter(mAdapter);
            final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
            mRecyclerView.addItemDecoration(headersDecor);
            mRecyclerView.addItemDecoration(new DividerDecoration(this));
            mAdapter.setOnRecyclerViewListener(this);
            //   setTouchHelper();
            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    headersDecor.invalidateHeaders();
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void seperateLists(ContactBean mModel) {
        //群主
        //ContactBean.CreaterEntity creatorEntity = mModel.getCreater();
       // ContactBean.MembersEntity tempMember = new ContactBean.MembersEntity();
       /* tempMember.setUsername(creatorEntity.getUsername());
        tempMember.setId(creatorEntity.getId());
        tempMember.setProfession(creatorEntity.getProfession());*/
        //tempMember.setSortLetters("$");

       // mAllLists.add(tempMember);


        //管理员

        /*if (mModel.getAdmins() != null && mModel.getAdmins().size() > 0) {
            for (ContactBean.AdminsEntity e : mModel.getAdmins()) {
                ContactBean.MembersEntity eMember = new ContactBean.MembersEntity();
                eMember.setSortLetters("%");
                eMember.setProfession(e.getProfession());
                eMember.setUsername(e.getUsername());
                eMember.setId(e.getId());
                mAllLists.add(eMember);
            }
        }*/
        //members;
        if (mModel.getMembers() != null && mModel.getMembers().size() > 0) {
            for (int i = 0; i < mModel.getMembers().size(); i++) {
                ContactBean.MembersEntity entity = new ContactBean.MembersEntity();
                entity.setId(mModel.getMembers().get(i).getId());
                entity.setUsername(mModel.getMembers().get(i).getUsername());
                entity.setProfession(mModel.getMembers().get(i).getProfession());
                String pinyin = characterParser.getSelling(mModel.getMembers().get(i).getUsername());
                String sortString = pinyin.substring(0, 1).toUpperCase();

                if (sortString.matches("[A-Z]")) {
                    entity.setSortLetters(sortString.toUpperCase());
                } else {
                    entity.setSortLetters("#");
                }
                mMembers.add(entity);
            }
            Collections.sort(mMembers, pinyinComparator);
            mAllLists.addAll(mMembers);
        }


    }


    public void deleteUser(final int position) {
        mAdapter.remove(mAdapter.getItem(position));
        showToast("删除成功");

    }

    public void showToast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(int position) {
        ToastUtil.success(AddressListActivity.this, "点击："+position);
       /* String title = findBeans.get(position).getTitle();
        if(title.equalsIgnoreCase(getStringResource(R.string.circle_of_friend))){ //朋友圈
            Intent it_circle = new Intent(FindActivity.this, CircleOfFriendActivity.class);
            startActivity(it_circle);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.sao_yi_sao))) {//扫一扫
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, MipcaActivityCapture.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.my_friends))){//我的朋友
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, FriendActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.address_list))){//通讯录
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, AddressListActivity.class);
            startActivity(intent);
        }*/
    }


    @Override
    public boolean onItemLongClick(int position) {
        ToastUtil.success(AddressListActivity.this, "长按：" + position);
        return false;
    }
}
