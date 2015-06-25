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
	public int currentMuseumId = 1;
	
	/**
	 * 当前选中的id 
	 */
	public int currentExhibitId = -1;
	
	/**
	 * 传递的展品列表id  
	 * 规则：各个id间用“，”隔开
	 */
	public String exhibitsIdList = ""; 
	
	/**
	 * 判断是否有从searchActivity 跳转到homeActivity
	 */
	public boolean isSelectedInSearch = false;
	
	/**
	 * 是否自动导游
	 */
	private boolean isAutoGuide = true;
	
	/**
	 * 导航模式改变监听者
	 */
	private List<OnGuideModeChangedListener> guideModeListeners;
	
	private onBluetoothStateChangedListener bleListener;
	
	/**
	 * 添加导航模式监听者
	 * @param listener the listener to set
	 */
	public void addGuideModeChangedListener(OnGuideModeChangedListener listener){
		if(guideModeListeners == null) guideModeListeners = new ArrayList<AppContext.OnGuideModeChangedListener>();
		if(!guideModeListeners.contains(listener))
			guideModeListeners.add(listener);
	}
	
	/**
	 * 移除监听者
	 */
	public void removeGuideModeListener(onBeaconSearcherListener listener){
		if(guideModeListeners == null) return ;
		if(guideModeListeners.contains(listener))
			guideModeListeners.remove(listener);
	}
	
	/**
	 * 设置ble listener 
	 * @param listener
	 */
	public void setBleListener(onBluetoothStateChangedListener listener){
		bleListener = listener;
	}
	
	/**
	 * 设置导游模式
	 * @param guideMode boolean, true：自动导航， false:手动导航
	 */
	public void setGuideMode(boolean guideMode){
		if(isAutoGuide == guideMode) return;
		isAutoGuide = guideMode;
		//通知监听者 导航模式已改变
		if(guideModeListeners != null){
			for(OnGuideModeChangedListener listener: guideModeListeners){
				listener.onGuideModeChanged(guideMode);
			}
		}
	}
	
	/**
	 * @return boolean 是否自动导航
	 */
	public boolean isAutoGuide(){
		return isAutoGuide;
	}

	/**
	 * 是否支持ble
	 */
	private boolean isBleEnable = true;
	
	/**
	 * @return 是否支持ble
	 */
	public boolean isBleEnable(){
		return isBleEnable;
	}
	
	/**
	 * 设置ble enable
	 * @param enable
	 */
	public void setBleEnable(boolean enable){
		if(isBleEnable == enable) return;
		isBleEnable = enable;
		if(bleListener!= null){
			bleListener.onBluetoothStateChanged(enable);
		}
	}
	

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
		
		/**
		 * 当导游模式改变时调用该回调
		 * @param isAutoGuide
		 */
		void onGuideModeChanged(boolean isAutoGuide);
		
	}
	
	/**
	 * 蓝牙改变时回调接口
	 * @author yetwish
	 */
	public interface onBluetoothStateChangedListener{
		/**
		 * 当蓝牙状态改变时调用该回调
		 * @param isEnable
		 */
		void onBluetoothStateChanged(boolean isEnable);
	}

}
