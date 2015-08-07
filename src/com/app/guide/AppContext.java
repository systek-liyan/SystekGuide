package com.app.guide;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.app.guide.ui.HomeActivity.onBeaconSearcherListener;
import com.baidu.mapapi.SDKInitializer;

/**
 * Application类，用来保存全局变量 ，提供一个蓝牙状态变化监听接口<code>OnBluetoothStateChangedListener</code>
 * 和 一个自动/主动导游模式切换监听接口<code>OnGuideModeChangedListener<code/>
 * 
 * 导航模式切换监听者可以有多个，调用<code>addGuideModeChangedListener()、removeGuideModeChangedListener()</code>添加和移出导航模式切换监听者。
 * 蓝牙状态切换监听者目前只有一个，调用<code>setBleListener()</code>设置监听
 * 
 * 调用<code>setGuideMode(boolean guideMode)</code>设置当前app的导航模式，其中true表示自动导航，false表示手动导航，切换不同导航模式时，会自动通知导航模式监听者当前导航模式已改变。
 * 调用<code>setBleEnable(boolean enable)</code>设置当前手机BLE是否可用，切换不同状态时，会通知蓝牙状态监听者蓝牙状态已改变
 * 
 * 通过下列方式之一获得Application对象
 * ((AppContext) mContext.getApplicationContext())
 * ((AppContext) getActivity.getApplication()) 
 * ((AppContext) this.getApplication())
 */
public class AppContext extends Application {

	
	/**
	 * 当前选中的博物馆id
	 */
	public String currentMuseumId = "fb468fcd9a894dbf8108f9b8bbc88109";
	
	/**
	 * 当前选中的id 
	 */
	public String currentExhibitId;
	
	/**
	 * 传递的展品列表id  
	 * 规则：各个id间用“，”隔开
	 */
	public String exhibitsIds = "";
	
	/**
	 * 记录博物馆的楼层数
	 */
	public int floorCount = 1;
	
	/**
	 * 记录当前网络状态 ,默认值为Constant.NETWORK_NONE,即没网络状态
	 * @see Constant#NETWORK_NONE
	 */
	public int networkState = Constant.NETWORK_NONE;
	
	/**
	 * 判断加载的博物馆是否有离线数据,在进入博物馆时会进行初始化
	 */
	public boolean hasOffline = true;
	
	/**
	 * 判断是否有从searchActivity 跳转到homeActivity
	 */
	public boolean isSelectedInSearch = false;
	
	/**
	 * 是否自动导游, 默认为自动导航模式
	 * @see Constant#GUIDE_MODE_AUTO
	 */
	private boolean GuideMode = Constant.GUIDE_MODE_AUTO;
	
	/**
	 * 导航模式改变监听者
	 */
	private List<OnGuideModeChangedListener> guideModeListeners;
	
	
	/**
	 * 蓝牙状态改变监听者
	 */
	private OnBluetoothStateChangedListener bleListener;
	

	/**
	 * 是否支持ble
	 */
	private boolean isBleEnable = true;
	
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
	public void setBleListener(OnBluetoothStateChangedListener listener){
		bleListener = listener;
	}
	
	/**
	 * 设置导游模式
	 * @param guideMode boolean, 
	 * 		true：自动导航 ， false:手动导航
	 */
	public void setGuideMode(boolean guideMode){
		if(GuideMode == guideMode) return;
		GuideMode = guideMode;
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
	public boolean getGuideMode(){
		return GuideMode;
	}

	
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
		//初始化本地配置参数
		AppConfig.getAppConfig(this).loadConfigBySP();
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
	public interface OnBluetoothStateChangedListener{
		/**
		 * 当蓝牙状态改变时调用该回调
		 * @param isEnable
		 */
		void onBluetoothStateChanged(boolean isEnable);
	}

}
