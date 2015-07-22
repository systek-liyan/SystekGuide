package com.app.guide.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.guide.R;
import com.app.guide.adapter.GridAdapter;
import com.app.guide.adapter.GridAdapter.GridItemClickListener;
import com.app.guide.model.LabelModel;

/**
 * 选择器 view，包含一个textView(组标签) 和一个gridView(子标签集)
 * 
 * @author yetwish
 * @date 2015-4-26
 */
public class LabelView extends LinearLayout {

	/**
	 * 上下文对象
	 */
	private Context mContext;

	/**
	 * 控件 tv，用以显示 标签组名
	 */
	private TextView tvTitle;

	/**
	 * 控件 gridView, 用以加载显示 一个标签组的所有标签项
	 */
	private GridView gvItems;

	/**
	 * bean,标签的数据源。<br>
	 * 一个LabelBean代表一组标签，由name:标签组名 和labels：标签项 组成
	 */
	private LabelModel mData;

	/**
	 * 标签项的Adapter
	 */
	private GridAdapter adapter;
	
	private GridItemClickListener mListener;

	public LabelView(Context context, LabelModel data) {
		super(context);
		this.mContext = context;
		this.mData = data;
		LayoutInflater.from(context).inflate(R.layout.label_group, this);
		// 初始化视图
		initViews();
	}

	/**
	 * 设置点击监听
	 * 
	 * @param listener
	 */
	public void setClickListener(GridItemClickListener listener) {
		
		if (adapter != null){
			mListener = listener;
			adapter.setItemClickListener(listener);
		}

	}

	private void initViews() {
		// findViews
		tvTitle = (TextView) findViewById(R.id.item_label_group_tv);
		gvItems = (GridView) findViewById(R.id.item_label_group_gridview);
		// 设置标签组名
		tvTitle.setText(mData.getName());
		// 设置标签项
		initItemsView();
	}

	/**
	 * 初始化 标签项 视图
	 */
	private void initItemsView() {
		// 获取子项的个数
		int size = mData.getLabels().size();
		// 从资源文件中获取子控件的宽度
		int itemWidth = (int) getResources().getDimension(
				R.dimen.gridview_item_width);
		int itemSpanWidth = (int) getResources().getDimension(
				R.dimen.gridview_item_span_width);
		// 获取view的总宽度
		int width = (int) (size * itemSpanWidth);
		// 设置view的layout parameters
		LayoutParams params = new LayoutParams(width, LayoutParams.MATCH_PARENT);
		gvItems.setLayoutParams(params);
		gvItems.setColumnWidth(itemWidth); // 根据itemWidth 设置gridView 的列宽
		gvItems.setHorizontalSpacing(10); // 设置水平间隔为10
		gvItems.setNumColumns(size); // 设置列的个数
		gvItems.setStretchMode(GridView.NO_STRETCH); // 设置缩放模式为不缩放
		// 给标签组设置adapter
		adapter = null;
		adapter = new GridAdapter(mContext, mData.getLabels(),
				R.layout.item_label_grid_view);
		gvItems.setAdapter(adapter);
	}

	/**
	 * 数据更新，更新adapter
	 * @param items
	 */
	public void notifyDataSetChanged(){
		initItemsView();
		if(mListener != null)
			adapter.setItemClickListener(mListener);
		adapter.notifyDataSetChanged();
	}

}
