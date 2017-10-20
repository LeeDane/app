package com.leedane.cn.task;

/**
 * 任务的监听器类
 * Created by LeedDane on 2015/10/11.
 */
public interface TaskListener {

    /**
     * 任务启动
     * @param type  任务的类型
     */
    void taskStarted(TaskType type);


    /**
     * 任务的完成
     * @param type  任务类型
     * @param result  返回的结果集
     */
    void taskFinished(TaskType type, Object result);

    /**
     * 任务的取消
     * @param type  任务的类型
     */
    void taskCanceled(TaskType type);

}
