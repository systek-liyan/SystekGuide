package com.app.guide.widget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.app.guide.R;
import com.app.guide.model.LyricModel;
import com.app.guide.utils.ScreenUtils;

public class LyricView extends View {

	public static final int INTERVAL = 10;// 歌词每行的间隔
	private static final int SIZE_LENGTH = 1600;// 显示歌词的高度
	public static int SIZE_TEXT_DISPLAY = 400;

	private TreeMap<Integer, LyricModel> lrc_map;
	private float mX; // 屏幕X轴的中点，此值固定，保持歌词在X中间显示
	private static boolean blLrc = false;
	private float touchY; // 当触摸歌词View时，保存为当前触点的Y轴坐标
	@SuppressWarnings("unused")
	private float touchX;
	private boolean blScrollView = false;
	private int lrcIndex = 0; // 保存歌词TreeMap的下标
	private int wordSize = 0;// 显示歌词文字的大小值
	private float offsetY; // 歌词在Y轴上的偏移量，此值会根据歌词的滚动变小

	Paint paint = new Paint();// 画笔，用于画不是高亮的歌词
	Paint paintHL = new Paint(); // 画笔，用于画高亮的歌词，即当前唱到这句歌词
	Paint paintTips = new Paint();// 画笔，用于画提示语
	private MediaPlayer mediaPlayer;

	private ShowLyricRunnable mShowLyricThread = null;

	public LyricView(Context context) {
		super(context);
		init();
	}

	public LyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 设置选中歌词的高度
	 * 
	 * @param height
	 */
	public void setTestDisplayHeight(int height) {
		SIZE_TEXT_DISPLAY = height;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (blLrc) {
			paintHL.setTextSize(wordSize);
			paint.setTextSize(wordSize);
			// TODO
			LyricModel temp = lrc_map.get(lrcIndex);
			if (temp == null)
				return;
			canvas.drawText(temp.lrc, mX, offsetY + (wordSize + INTERVAL)
					* lrcIndex, paintHL);
			// 画当前歌词之前的歌词
			for (int i = lrcIndex - 1; i >= 0; i--) {
				temp = lrc_map.get(i);
				if (offsetY + (wordSize + INTERVAL) * i < 0) {
					break;
				}
				canvas.drawText(temp.lrc, mX, offsetY + (wordSize + INTERVAL)
						* i, paint);
			}
			// 画当前歌词之后的歌词
			for (int i = lrcIndex + 1; i < lrc_map.size(); i++) {
				temp = lrc_map.get(i);
				if (offsetY + (wordSize + INTERVAL) * i > SIZE_LENGTH) {
					break;
				}
				canvas.drawText(temp.lrc, mX, offsetY + (wordSize + INTERVAL)
						* i, paint);
			}
		} else {
			// 没有找到歌词
			canvas.drawText(getResources().getString(R.string.lyric_not_found),
					mX, SIZE_TEXT_DISPLAY, paintTips);
		}
		super.onDraw(canvas);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		System.out.println("bllll===" + blScrollView);
		float tt = event.getY();
		if (!blLrc) {
			// return super.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			touchY = tt - touchY;
			offsetY = offsetY + touchY;
			break;
		case MotionEvent.ACTION_UP:
			blScrollView = false;
			break;
		}
		touchY = tt;
		return true;
	}

	private void init() {
		lrc_map = new TreeMap<Integer, LyricModel>();
		// Log.e("tag", "heightSpec:"+heightSpec);
		paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setAlpha(255);
		
		paintHL = new Paint();
		paintHL.setTextAlign(Paint.Align.CENTER);
		paintHL.setColor(Color.RED);
		paintHL.setAntiAlias(true);
		paintHL.setAlpha(255);

		
		paintTips = new Paint();
		paintTips.setTextAlign(Paint.Align.CENTER);
		paintTips.setColor(Color.BLACK);
		paintTips.setAntiAlias(true);
		paintTips.setTextSize(35);

	}

	/**
	 * 根据歌词里面最长的那句来确定歌词字体的大小
	 */

	private void prepareTextSize() {
		if (!blLrc) {
			return;
		}
		int width = ScreenUtils.getScreenWidth(getContext());
		int max = lrc_map.get(0).lrc.length();
		for (int i = 1; i < lrc_map.size(); i++) {
			LyricModel lrcStrLength = lrc_map.get(i);
			if (max < lrcStrLength.lrc.length()) {
				max = lrcStrLength.lrc.length();
			}
		}
		wordSize = (int) (width / max / 1.5);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mX = w * 0.5f;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 歌词滚动的速度
	 * 
	 * @return 返回歌词滚动的速度
	 */
	private float speedLrc() {
		float speed = 0;
		speed = ((offsetY + (wordSize + INTERVAL) * lrcIndex - SIZE_TEXT_DISPLAY) / 20);
		return speed;
	}

	/**
	 * 获取播放时间 msec
	 */
	public int getDuration() {
		if (mediaPlayer != null) {
			return mediaPlayer.getDuration();
		} else
			return -1;
	}

	/**
	 * 设置当前播放进度
	 * 
	 * @param position
	 */
	public void setCurrentPosition(int position) {
		if (mediaPlayer != null) {
			mediaPlayer.seekTo(position);
			if (mProgressChangedListener != null) {
				mProgressChangedListener.onProgressChanged(position);
			}
		}
	}

	/**
	 * 按当前的歌曲的播放时间，从歌词里面获得那一句
	 * 
	 * @param time
	 *            当前歌曲的播放时间
	 * @return 返回当前歌词的索引值
	 */
	private int selectIndex(int time) {
		if (!blLrc) {
			return 0;
		}
		int index = 0;
		for (int i = 0; i < lrc_map.size(); i++) {
			LyricModel temp = lrc_map.get(i);
			if (temp.begintime < time) {
				++index;
			}
		}
		lrcIndex = index - 1;
		if (lrcIndex < 0) {
			lrcIndex = 0;
		}
		return lrcIndex;
	}

	/**
	 * 读取歌词文件
	 * 
	 * @param file
	 *            歌词的路径
	 * 
	 */
	private void read(String file) {
		TreeMap<Integer, LyricModel> lrc_read = new TreeMap<Integer, LyricModel>();
		String data = "";
		try {
			File saveFile = new File(file);
			// System.out.println("是否有歌词文件"+saveFile.isFile());
			if (!saveFile.isFile()) {
				blLrc = false;
				return;
			}
			blLrc = true;

			// System.out.println("bllrc==="+blLrc);
			FileInputStream stream = new FileInputStream(saveFile);// context.openFileInput(file);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stream));
			Pattern pattern = Pattern.compile("\\d{2}");
			while ((data = br.readLine()) != null) {
				data = data.replace("[", "");// 将前面的替换成后面的
				data = data.replace("]", "@");
				String splitdata[] = data.split("@");// 分隔
				if (data.endsWith("@")) {
					for (int k = 0; k < splitdata.length; k++) {
						String str = splitdata[k];

						str = str.replace(":", ".");
						str = str.replace(".", "@");
						String timedata[] = str.split("@");
						Matcher matcher = pattern.matcher(timedata[0]);
						if (timedata.length == 3 && matcher.matches()) {
							int m = Integer.parseInt(timedata[0]); // 分
							int s = Integer.parseInt(timedata[1]); // 秒
							int ms = Integer.parseInt(timedata[2]); // 毫秒
							int currTime = (m * 60 + s) * 1000 + ms * 10;
							LyricModel item1 = new LyricModel();
							item1.begintime = currTime;
							item1.lrc = "";
							lrc_read.put(currTime, item1);
						}
					}

				} else {
					String lrcContenet = splitdata[splitdata.length - 1];
					for (int j = 0; j < splitdata.length - 1; j++) {
						String tmpstr = splitdata[j];
						tmpstr = tmpstr.replace(":", ".");
						tmpstr = tmpstr.replace(".", "@");
						String timedata[] = tmpstr.split("@");
						Matcher matcher = pattern.matcher(timedata[0]);
						if (timedata.length == 3 && matcher.matches()) {
							int m = Integer.parseInt(timedata[0]); // 分
							int s = Integer.parseInt(timedata[1]); // 秒
							int ms = Integer.parseInt(timedata[2]); // 毫秒
							int currTime = (m * 60 + s) * 1000 + ms * 10;
							LyricModel item1 = new LyricModel();
							item1.begintime = currTime;
							item1.lrc = lrcContenet;
							lrc_read.put(currTime, item1);// 将currTime当标签
						}
					}
				}

			}
			stream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		/*
		 * 遍历hashmap 计算每句歌词所需要的时间
		 */
		lrc_map.clear();
		data = "";
		Iterator<Integer> iterator = lrc_read.keySet().iterator();
		LyricModel oldval = null;
		int i = 0;
		while (iterator.hasNext()) {
			Object ob = iterator.next();

			LyricModel val = (LyricModel) lrc_read.get(ob);

			if (oldval == null)
				oldval = val;
			else {
				LyricModel item1 = new LyricModel();
				item1 = oldval;
				item1.timeline = val.begintime - oldval.begintime;
				lrc_map.put(i, item1);
				i++;
				oldval = val;
			}
			if (!iterator.hasNext()) {
				lrc_map.put(i, val);
			}
		}
	}

	public void prepare(String mp3Path, String lyricPath) {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.reset();
		} else {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				//播放完毕
				@Override
				public void onCompletion(MediaPlayer mp) {
					if (mProgressChangedListener != null) {
						mProgressChangedListener.onMediaPlayCompleted();
					}
				}
			});
		}
		try {
			mediaPlayer.setDataSource(mp3Path);
			mediaPlayer.prepare();
			// 默认当前展品为循环播放模式
			mediaPlayer.setLooping(true);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		read(lyricPath);
		prepareTextSize();
		offsetY = SIZE_TEXT_DISPLAY;
	}

	public void start() {
		mediaPlayer.start();
		offsetY = SIZE_TEXT_DISPLAY
				- selectIndex(mediaPlayer.getCurrentPosition())
				* (getSIZEWORD() + INTERVAL - 1);
		if (mShowLyricThread == null) {
			mShowLyricThread = new ShowLyricRunnable();
			mShowLyricThread.start();
		}
	}

	public void pause() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
	}

	/**
	 * 释放资源
	 */
	public void destroy() {
		if (paint != null) {
			paint = null;
		}
		if (paintHL != null) {
			paintHL = null;
		}
		if (mediaPlayer != null) {
			pause();
			mediaPlayer = null;
		}
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
		blLrc = false;
		lrc_map.clear();
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	/**
	 * @return the blLrc
	 */
	public static boolean isBlLrc() {
		return blLrc;
	}

	/**
	 * @return 返回歌词文字的大小
	 */
	public int getSIZEWORD() {
		return wordSize;
	}

	/**
	 * 设置歌词文字的大小
	 * 
	 * @param sIZEWORD
	 *            the sIZEWORD to set
	 */
	public void setSIZEWORD(int sIZEWORD) {
		wordSize = sIZEWORD;
	}

	private class ShowLyricRunnable extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (mediaPlayer == null)
					break;
				if (mediaPlayer.isPlaying()) {
					offsetY = offsetY - speedLrc();
					selectIndex(mediaPlayer.getCurrentPosition());
					postInvalidate();
					if (mProgressChangedListener != null) {
						mProgressChangedListener.onProgressChanged(mediaPlayer
								.getCurrentPosition());
					}
				} 
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void setProgressChangedListener(onProgressChangedListener listener) {
		this.mProgressChangedListener = listener;
	}

	private onProgressChangedListener mProgressChangedListener;

	public interface onProgressChangedListener {
		void onProgressChanged(int progress);

		void onMediaPlayCompleted();
	}
}