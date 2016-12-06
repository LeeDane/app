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

    //记账一级分类编辑返回的code
    public static final int ONE_LEVEL_CATEGORY_EDIT_CODE = 57;

    //记账二级分类编辑返回的code
    public static final int TWO_LEVEL_CATEGORY_EDIT_CODE = 59;

    //记账查询的动作
    public static final int FINANCIAL_SEARCH_ACTION = 61;

}
