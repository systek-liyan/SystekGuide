package com.app.guide.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.guide.AppContext;
import com.app.guide.R;
import com.app.guide.bean.MapExhibitBean;

public class MapDialog extends PopupWindow {

	private TextView titleTextView;
	private ImageView mImageView;
	private MapExhibitBean mapExhibitBean;
	
	
	@SuppressLint("InflateParams")
	public MapDialog(final Context context, MapExhibitBean exhibitBean) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mapExhibitBean = exhibitBean;
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_map_exhibit, null);
		titleTextView = (TextView) view.findViewById(R.id.dia_map_title);
		mImageView = (ImageView) view.findViewById(R.id.dia_map_img);
		titleTextView.setText(mapExhibitBean.getName());
		mImageView.setImageBitmap(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.e1));
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((AppContext)context.getApplicationContext()).setGuideMode(false);
				((AppContext)context.getApplicationContext()).currentExhibitId = mapExhibitBean.getId();
				dismiss();
				((RadioButton) HomeActivity.mRadioGroup
						.findViewById(R.id.home_tab_follow)).setChecked(true);
				
			}
		});
		this.setWidth(200);
		this.setHeight(200);
		setContentView(view);
		this.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.bg_popupwindow));
		this.setOutsideTouchable(true);

	}

}
