package com.app.guide;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.app.guide.ui.HomeActivity.onBeaconSearcherListener;
import com.baidu.mapapi.SDKInitializer;

public class AppContext extends Application {

	/**
	 * 当前选中的博物馆id
	 */
	public static int currentMuseumId = 1;
	
	/**
	 * 当前选中的id 
	 */
	public static int currentExhibitId = -1;
	
	/**
	 * 传递的展品列表id  
	 * 规则：各个id间用“，”隔开
	 */
	public static String exhibitsIdList = ""; 
	
	/**
	 * 判断是否有从searchActivity 跳转到homeActivity
	 */
	public static boolean isSelectedInSearch = false;
	
	/**
	 * 是否自动导游
	 */
	private static boolean isAutoGuide = true;
	
	/**
	 * 导航模式改变监听者
	 */
	private static List<OnGuideModeChangedListener> mListeners;
	
	/**
	 * 添加导航模式监听者
	 * @param listener the listener to set
	 */
	public static void addGuideModeChangedListener(OnGuideModeChangedListener listener){
		if(mListeners == null) mListeners = new ArrayList<AppContext.OnGuideModeChangedListener>();
		if(!mListeners.contains(listener))
			mListeners.add(listener);
	}
	
	/**
	 * 移除监听者
	 */
	public static void removeGuideModeListener(onBeaconSearcherListener listener){
		if(mListeners == null) return ;
		if(mListeners.contains(listener))
			mListeners.remove(listener);
	}
	
	
	/**
	 * 设置导游模式
	 * @param guideMode boolean, true：自动导航， false:手动导航
	 */
	public static void setGuideMode(boolean guideMode){
		isAutoGuide = guideMode;
		//通知监听者 导航模式已改变
		if(mListeners != null){
			for(OnGuideModeChangedListener listener: mListeners){
				listener.onGuideModeChanged(guideMode);
			}
		}
	}
	
	/**
	 * @return boolean 是否自动导航
	 */
	public static boolean isAutoGuide(){
		return isAutoGuide;
	}

	/**
	 * 是否支持ble
	 */
	public static boolean isBleEnable = true;
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
	}
	
	/**
	 * 导航模式改变时回调接口
	 * @author yetwish
	 */
	public interface OnGuideModeChangedListener{
		
		void onGuideModeChanged(boolean isAutoGuide);
	}

}
