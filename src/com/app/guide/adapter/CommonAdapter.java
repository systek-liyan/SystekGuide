package com.app.guide.adapter;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * 通用Adapter，用以简化程序代码。定义一个新的Adapter只需继承该类，并重写该类的convert()方法即可。<br>
 * 示例代码：
 * <pre>
 * class MyAdapter extends CommonAdapter<Bean>{
 *       public MyAdapter(Context context, List<ImageOption> data,
 *            int layoutId){
 *           super(context, data, layoutId);
 *       }	
 * 	 	
 *       public void convert(ViewHolder holder, int position) {
 *           holder.setText("HelloWorld").setImageResource(R.drawable.icon);
 *       }
 * } 
 * </pre>
 * Created by yetwish on 2015-05-11
 */

public abstract class CommonAdapter<T> extends BaseAdapter{

	/**
	 * 上下文对象
	 */
    protected Context mContext;
    
    /**
     * 存储具体bean的数组
     */
    protected List<T> mData;
    
    /**
     * layout文件的id
     */
    protected int mLayoutId;

    public CommonAdapter(Context context,List<T> data,int layoutId){
        mContext = context;
        mData = data;
        mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取holder
    	ViewHolder holder = ViewHolder.getHolder(mContext,convertView,mLayoutId,parent,position);
    	//给holder添加组件
        convert(holder,position);
        //返回holder绑定的convertView
        return holder.getConvertView();
    }
    
    /**
     * 抽象方法，用以给holder添加组件。
     */
    public abstract void convert(ViewHolder holder,int position);
}
