package com.app.guide.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.app.guide.R;

/**
 * 使用PopUpWindow作为地图的弹出菜单
 * 
 * @author joe_c
 *
 */
public class PopMapMenu extends PopupWindow {

	public Button locationButton;
	public Button friendButton;
	public ToggleButton toggleList;
	public RadioGroup mapRg;

	public PopMapMenu(Context context, int floor) {
		View view = LayoutInflater.from(context).inflate(R.layout.pop_map_menu,
				null);
		locationButton = (Button) view.findViewById(R.id.map_menu_location);
		friendButton = (Button) view.findViewById(R.id.map_menu_friend);
		toggleList = (ToggleButton) view.findViewById(R.id.map_menu_tog_list);
		Button dismissButton = (Button) view
				.findViewById(R.id.map_menu_dismiss);
		mapRg = (RadioGroup) view.findViewById(R.id.map_menu_group_floor);
		for (int i = 0; i < floor; i++) {
			RadioButton button = new RadioButton(context);
			button.setText("Floor" + (i + 1));
			mapRg.addView(button);
		}
		dismissButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setAnimationStyle(R.style.MapMenuAnimationStyle);
	}

}
