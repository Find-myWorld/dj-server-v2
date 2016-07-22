package com.example.iimp_znxj_new2014.processor;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

/**
 * 数据处理基类，用于简化处理客户端和服务端接口异步交互 1)一个处理器的类仅有一个Listener与之对应 2)一个异步的任务对应Listener的一个接口
 * 3)所有异步的任务都需要继承BaseProcessor.ProcessorTask
 */
public abstract class BaseProcessor<Listener> {

	/**
	 * 与处理器关联的界面
	 */
	protected Context mContext;

	protected Listener mListener;

	protected Map<Class<? extends RestAsyncTask>, RestAsyncTask> mAsyncTasks;

	public BaseProcessor(Context context) {
		mContext = context;
		mAsyncTasks = new HashMap<Class<? extends RestAsyncTask>, RestAsyncTask>();
	}

	/**
	 * 注册监听类
	 * 
	 * @param listener
	 *            监听类
	 */
	public void registerListener(Listener listener) {
		mListener = listener;
	}

	/**
	 * 取消所有任务，并置空监听类
	 */
	public void destroy() {
		for (RestAsyncTask task : mAsyncTasks.values()) {
			if (!task.isCancelled()) {
				task.cancel(true);
			}
		}
		mListener = null;
	}

	/**
	 * 检查是否有未完成的同类任务，并使用新任务替换之
	 * 
	 * @param task
	 *            新的异步任务
	 */
	protected void checkRestAsyncTask(RestAsyncTask task) {
		RestAsyncTask oldTask = mAsyncTasks.get(task.getClass());
		if (oldTask != null && oldTask.isCancelled()) {
			oldTask.cancel(false);
		}
		mAsyncTasks.put(task.getClass(), task);
	}

	/**
	 * 数据处理任务的基类 子类需实现3个方法： 1)getRequestUrl() 返回接口地址 2)requestCallback()
	 * 接口返回值的处理 3)requestError() 接口返回错误处理
	 * 
	 * @param <InputInfo>
	 *            接口的输入Bean
	 * @param <Result>
	 *            接口返回的Response
	 */
	protected abstract class ProcessorTask<InputInfo, Result> extends
			RestAsyncTask<InputInfo, Result> {

		@Override
        protected void onPostExecute(Result response) {
            super.onPostExecute(response);
            if (mListener == null) {
                return;
            }
            if (response == null) {
            	
            }
                //requestError(getError().getRestErrorCode(), getError().getMessage());
             else {
                requestCallback(response);
            }
        }

		protected abstract void requestCallback(Result response);

		protected abstract void requestError(int errorCode, String errorMsg);
	}
}
