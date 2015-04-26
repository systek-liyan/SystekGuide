package com.yetwish.tourismguide.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.yetwish.libs.BeaconSearcher;
import com.yetwish.tourismguide.R;
import com.yetwish.tourismguide.fragment.FollowGuideFragment;
import com.yetwish.tourismguide.fragment.MapFragment;
import com.yetwish.tourismguide.fragment.MuseumIntroduceFragment;
import com.yetwish.tourismguide.fragment.SubjectSelectFragment;

/**
 * 主界面,采用fragmentTabHost 实现 fragments切换 
 * fixme 有缺陷，管理不便，且与Fragment的通信不便，改为用fragmentManager管理
 * @author yetwish
 */
public class MainActivity extends FragmentActivity{

	private FragmentTabHost mTabHost;
	
	private LayoutInflater layoutInflater;
	
	/**
	 * fragments Class 对象
	 */
	private final Class fragmentArray[]  = {
		MuseumIntroduceFragment.class,FollowGuideFragment.class,SubjectSelectFragment.class,MapFragment.class
	};
	
	/**
	 * tabs icons
	 */
	private final int btnImageArray[] = {
		R.drawable.main_footer_geren_img,R.drawable.main_footer_luntan_img,
		R.drawable.main_footer_retie_img,R.drawable.main_footer_zuijin_img
	};
	
	/**
	 * tabs titles
	 */
	private final String tabTitlesArray[] = {
		"首页","随行导游","专题导游","地图"
	};
	
	/**
	 * store the textViews of tabs 
	 */
	private ArrayList<TextView> tvTitles = new ArrayList<TextView>();
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();
    }
    
    
    private void initViews(){
    	layoutInflater = LayoutInflater.from(this);
    	mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
    	mTabHost.setup(this, getSupportFragmentManager(), R.id.main_footer_tabs_content_layout);
    	int count = fragmentArray.length;
    	for(int i =0 ;i<count; i++){
    		//get tab spec 	
    		TabSpec tabSpec = mTabHost.newTabSpec(tabTitlesArray[i]).setIndicator(getTabItemView(i));
    		//add tab 
    		mTabHost.addTab(tabSpec,fragmentArray[i],null);
    		//set background
    		mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_bg);
    	}
    	mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				
				
			}
		});
    }

    
    private View getTabItemView(int i) {
    	View view = layoutInflater.inflate(R.layout.main_footer_tab_item, null);
    	ImageView ivIcon = (ImageView)view.findViewById(R.id.main_footer_tab_item_iv);
    	ivIcon.setImageResource(btnImageArray[i]);
    	TextView tvTitle = (TextView)view.findViewById(R.id.main_footer_tab_item_tv);
    	tvTitle.setText(tabTitlesArray[i]);
    	tvTitles.add(tvTitle);
		return view;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode,resultCode,intent);
    	BeaconSearcher.getInstance(this).onBluetoothResult(requestCode, resultCode);
    }
    
}
