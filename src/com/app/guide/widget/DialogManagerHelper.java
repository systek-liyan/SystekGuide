package com.app.guide.widget;

import org.altbeacon.beacon.BleNotAvailableException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnCheckedChangedListener;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.app.guide.AppConfig;
import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;

import edu.xidian.NearestBeacon.BeaconSearcher;

/**
 * 对话框管理帮助类
 * 
 * @author yetwish
 * 
 */
public class DialogManagerHelper {

	private Context mContext;

	private Resources mResources;
	
	// private static final int REQUEST_CODE_GPS = 0x110;
	// private static final int REQUEST_CODE_WIFI = 0x111;

	public DialogManagerHelper(Context context) {
		this.mContext = context;
		this.mResources = mContext.getResources();
	}

	//mResources.getString(R.string.dialog_download_wifi)
	
	
	public boolean showDownloadDialog(final OnClickedListener listener){
		WifiManager wm = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (!wm.isWifiEnabled()) {
			//检测当前网络环境为非Wifi 是否继续下载
			final SweetAlertDialog sa = new SweetAlertDialog(mContext);
			sa.setTitleText(mResources
					.getString(R.string.dialog_download_wifi));
			sa.setCancelable(true);
			sa.setConfirmText(mResources.getString(R.string.dialog_setting))
					.setConfirmClickListener(new OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							if(listener!=null){
								listener.onClickedPositiveBtn();
							}
							sa.dismiss();
						}
					});
			sa.setCancelText(mResources.getString(R.string.dialog_cancel))
					.setCancelClickListener(new OnSweetClickListener() {
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					if(listener!=null){
						listener.onClickedNegativeBtn();
					}
					sa.dismiss();
				}
			});
			sa.show();
			return false;
		}
		return true;
	}
	
	/**
	 * 设置定位wifi dialog
	 */
	public void showWifiLocateDialog() {
		if(!AppConfig.getAppConfig(mContext).autoCheckGPS) return;
		WifiManager wm = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (!wm.isWifiEnabled()) {
			final SweetAlertDialog sa = new SweetAlertDialog(mContext,
					new OnCheckedChangedListener() {
				@Override
				public void oncheckedChanged(boolean isChecked) {
					AppConfig.getAppConfig(mContext).autoCheckGPS = !isChecked;
					
				}
			});
			sa.setTitleText(mResources.getString(R.string.dialog_wifi));
			sa.setContentText(mResources
					.getString(R.string.dialog_location_wifi));
			sa.setCancelable(true);
			sa.setConfirmText(mResources.getString(R.string.dialog_setting))
					.setConfirmClickListener(new OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							Intent intent = new Intent(
									Settings.ACTION_WIFI_SETTINGS);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(intent);
							sa.dismiss();
						}
					});
			sa.setCancelText(mResources.getString(R.string.dialog_cancel))
					.setCancelClickListener(new OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							sa.dismiss();
						}
					});
			sa.show();
		}
	}


	/**
	 * 根据当前GPS状态，若未开启则弹出GPS设置对话框
	 */
	public void showGPSSettingDialog() {
		if(!AppConfig.getAppConfig(mContext).autoCheckGPS) return;
		LocationManager lm = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// 请求开启GPS模块
			final SweetAlertDialog sa = new SweetAlertDialog(mContext,
					new OnCheckedChangedListener() {
						
						@Override
						public void oncheckedChanged(boolean isChecked) {
							AppConfig.getAppConfig(mContext).autoCheckGPS = !isChecked;
							
						}
					});
			sa.setTitleText(mResources.getString(R.string.dialog_gps));
			sa.setContentText(mResources.getString(R.string.dialog_open_gps));
			sa.setCancelable(true);
			sa.setConfirmText(mResources.getString(R.string.dialog_setting))
					.setConfirmClickListener(new OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(intent);
							sa.dismiss();

						}
					});
			sa.setCancelText(mResources.getString(R.string.dialog_cancel))
					.setCancelClickListener(new OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							sa.dismiss();
							// 若当前未开启Wifi，则设置wifi
							showWifiLocateDialog();
						}
					});
			sa.show();
		}
	}

	/**
	 * 根据设备当前蓝牙状态，若设备支持蓝牙4.0，则弹出蓝牙设置对话框；否则自动将系统支持蓝牙的全局变量置否，且弹出蓝牙不支持的对话框
	 * 
	 * @param beaconSearcher
	 */
	public void showBLESettingDialog(final BeaconSearcher beaconSearcher) {
		try {
			if (!beaconSearcher.checkBLEEnable()) {
				// 支持蓝牙4.0，且未开启
				final SweetAlertDialog sa = new SweetAlertDialog(mContext);
				sa.setTitleText(mResources.getString(R.string.dialog_bluetooth));
				sa.setContentText(mResources
						.getString(com.app.guide.R.string.dialog_open_bluetooth));
				sa.setCancelable(true);
				sa.setConfirmText(mResources.getString(R.string.dialog_open))
						.setConfirmClickListener(new OnSweetClickListener() {
							@Override
							public void onClick(
									SweetAlertDialog sweetAlertDialog) {
								// TODO Auto-generated method stub
								beaconSearcher.enableBluetooth();
								((AppContext)mContext.getApplicationContext()).setGuideMode(Constant.GUIDE_MODE_AUTO);
								sa.dismiss();

							}
						});
				sa.setCancelText(mResources.getString(R.string.dialog_cancel))
						.setCancelClickListener(new OnSweetClickListener() {
							@Override
							public void onClick(
									SweetAlertDialog sweetAlertDialog) {
								((AppContext)mContext.getApplicationContext()).setGuideMode(Constant.GUIDE_MODE_MANUALLY);
								((AppContext)mContext.getApplicationContext()).setBleEnable(false);
								sa.dismiss();
							}
						});
				sa.show();
			} else {
				// 蓝牙已开启
				((AppContext)mContext.getApplicationContext()).setGuideMode(Constant.GUIDE_MODE_AUTO);
			}
		} catch (BleNotAvailableException e) {
			// 不支持蓝牙4.0
			new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
					.setTitleText("Oops...")
					.setContentText(
							mResources
									.getString(com.app.guide.R.string.dialog_not_support_ble))
					.show();
			((AppContext)mContext.getApplicationContext()).setGuideMode(false);
			((AppContext)mContext.getApplicationContext()).setBleEnable(false);
		}

	}

	/**
	 * 加载中 对话框
	 * @return
	 */
	public SweetAlertDialog showLoadingProgressDialog() {
		final SweetAlertDialog pDialog = new SweetAlertDialog(mContext,
				SweetAlertDialog.PROGRESS_TYPE).setTitleText(mContext
						.getResources().getString(R.string.dialog_loading));
		pDialog.show();
		pDialog.setCancelable(false);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.getProgressHelper().setBarColor(
				mContext.getResources().getColor(R.color.blue_btn_bg_color));
		return pDialog;
	}
	
	
	

	/**
	 * 搜索对话框
	 * @return
	 */
	public SweetAlertDialog showSearchingProgressDialog() {
		final SweetAlertDialog pDialog = new SweetAlertDialog(mContext,
				SweetAlertDialog.PROGRESS_TYPE).setTitleText(mContext.getResources()
						.getString(R.string.dialog_searching));
		pDialog.show();
		pDialog.setCancelable(true);
		pDialog.setCancelText(mResources.getString(R.string.dialog_cancel));
		pDialog.setCancelClickListener(new OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				Toast.makeText(mContext, "您取消自动随行导游，请回到主页手动选择展品！", Toast.LENGTH_LONG).show();
				((AppContext) mContext.getApplicationContext()).setGuideMode(Constant.GUIDE_MODE_AUTO);
				pDialog.dismiss();
			}
		});
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.getProgressHelper().setBarColor(
				mContext.getResources().getColor(R.color.blue_btn_bg_color));
		return pDialog;
	}
	
	/**
	 * 成功dialog
	 */
	public void showSuccessDialog() {
		final SweetAlertDialog dialog = new SweetAlertDialog(mContext,SweetAlertDialog.SUCCESS_TYPE);
		dialog.setTitleText(mResources.getString(R.string.dialog_success))
				.setConfirmText(mResources.getString(R.string.dialog_confirm))
				.setConfirmClickListener(new OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						dialog.dismiss();
					}
				});
	}

	public interface OnClickedListener{
		
		void onClickedPositiveBtn();
		
		void onClickedNegativeBtn();
	}
	
	// public interface onWifiSettingListener{
	// void onWifiSettingComplete();
	// }
	//
	// public interface onGPSSettingListener{
	// void onGPSSettingComplete();
	// }
	//
	// private onWifiSettingListener mWifiListener;
	//
	// public void setWifiSettingListener(onWifiSettingListener listener){
	// mWifiListener = listener;
	// }
	//
	//
	// private onGPSSettingListener mGpsListener;
	//
	// public void setGPSSettingListener(onGPSSettingListener listener){
	// mGpsListener = listener;
	// }

}
