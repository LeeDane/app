package com.leedane.cn.financial.util;

/**
 * 统一对标记的管理
 * Created by LeeDane on 2016/12/2.
 */
public final class FlagUtil {

    //获取系统图库
    public static final int GET_SYSTEM_IMAGE_CODE = 112;

    //标记是否同步任务完成的监听
    public final static int SYNCHRONIZED_FLAG = 1001;

    //标记是否对记账对象进行编辑或者新增
    public static final int IS_EDIT_OR_SAVE_FINANCIAL_CODE = 117;

    //记账首页对同步云服务器的监听
    public static final int SYNCHRONIZED_CLOUD_CODE = 25;

    //记账模块首页对初始化数据的监听
    public static final int INIT_DATA_SUCCESS = 19;

    //记账模块首页获取未上传到云端数据的监听
    public static final int LOAD_NO_CLOUD_NUMBER = 20;

    //记账一级分类编辑返回的code
    public static final int ONE_LEVEL_CATEGORY_EDIT_CODE = 57;

    //记账二级分类编辑返回的code
    public static final int TWO_LEVEL_CATEGORY_EDIT_CODE = 59;

    //记账查询的动作
    public static final int FINANCIAL_SEARCH_ACTION = 61;

    //摇一摇的结果
    public static final int YAOYIYAO_RESULT_HANDLER = 62;

    //附近上传位置信息
    public static final int NEARBY_UPLOAD_LOCATION = 63;

    //附近人的结果集的查询
    public static final int DO_NEARBY_SEARCH = 65;

    //附近人的清除位置退出
    public static final int NEARBY_CLEAR_LOCATION = 66;

    //记账查询的动作
    public static final int FINANCIAL_FINANCIAL_LIST = 67;

    //图库加载网络图片
    public static final int GALLERY_LOAD_NETWORK_IMAGE = 68;

}
