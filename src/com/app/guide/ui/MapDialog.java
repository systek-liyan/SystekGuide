package com.app.guide.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.guide.AppContext;
import com.app.guide.Constant;
import com.app.guide.R;
import com.app.guide.model.MapExhibitModel;
import com.app.guide.utils.BitmapUtils;

public class MapDialog extends PopupWindow {

	private TextView titleTextView;
	private NetworkImageView mImageView;
	private MapExhibitModel mapExhibitBean;

	@SuppressLint("InflateParams")
	public MapDialog(final Context context, MapExhibitModel exhibitBean) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mapExhibitBean = exhibitBean;
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_map_exhibit, null);
		titleTextView = (TextView) view.findViewById(R.id.dia_map_title);
		mImageView = (NetworkImageView) view.findViewById(R.id.dia_map_img);
		titleTextView.setText(mapExhibitBean.getName());
		mImageView.setDefaultImageResId(R.drawable.ic_launcher);
		mImageView.setErrorImageResId(R.drawable.ic_launcher);
		ImageLoader imageLoader = BitmapUtils.getImageLoader(context);
		mImageView.setImageUrl(exhibitBean.getIconUrl(), imageLoader);
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((AppContext) context.getApplicationContext())
						.setGuideMode(Constant.GUIDE_MODE_MANUALLY);
				((AppContext) context.getApplicationContext()).currentExhibitId = mapExhibitBean
						.getId();
				dismiss();
				RadioButton btn = (RadioButton) HomeActivity.mRadioGroup
						.findViewById(R.id.home_tab_follow);
				btn.setChecked(true);
				btn.setEnabled(true);
			}
		});
		this.setWidth(250);
		this.setHeight(280);
		setContentView(view);
		this.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.bg_popupwindow));
		this.setOutsideTouchable(true);
	}
}
