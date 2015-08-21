package com.app.guide.widget;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyMap extends SurfaceView implements SurfaceHolder.Callback {

	//private static final String TAG = MyMap.class.getSimpleName();

	private static final long DOUBLE_CLICK_TIME_SPACE = 300;

	private float mCurrentScaleMax;//最大缩放比例
	private float mCurrentScale;//当前缩放比例
	private float mCurrentScaleMin;//最小缩放比例

	private float windowWidth, windowHeight;//地图的宽和高

	private Bitmap mBitmap;//地图显示的Bitmap
	private Paint mPaintMark;//绘制标记的Paint
	private Paint mPaintLocation;//绘制定位点的Pain个

	private PointF mStartPoint;
	private volatile PointF mapCenter;// mapCenter表示地图中心在屏幕上的坐标
	private DrawThread mDrawThread;//绘制图形的异步线程

	private float locationX, locationY;//定位坐标
	private float radius = 5;//标记点的半径大小
	private long lastClickTime;// 记录上一次点击屏幕的时间，以判断双击事件
	private Status mStatus = Status.NONE;//记录地图状态

	private float oldRate = 1;
	private float oldDist = 1;

	private boolean isShu = true;
	private boolean isShowMark = false;//是否显示地图标记

	/**
	 * 
	 * 表示地图状态的枚举类
	 * none表示无任何操作
	 * zoom表示正在进行缩放操作
	 * drag表示正在进行拖拽操作
	 * @author joe_c
	 *
	 */
	private enum Status {
		NONE, ZOOM, DRAG
	};

	private List<MarkObject> markList = new CopyOnWriteArrayList<MarkObject>();

	public MyMap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public MyMap(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public MyMap(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// 获取屏幕的宽和高
		windowWidth = getResources().getDisplayMetrics().widthPixels;
		windowHeight = getResources().getDisplayMetrics().heightPixels;
		mPaintMark = new Paint();
		mPaintMark.setColor(Color.RED);
		mPaintLocation = new Paint();
		mPaintLocation.setColor(Color.BLUE);
		mStartPoint = new PointF();
		mapCenter = new PointF();
		radius = windowWidth/120;
		mDrawThread = new DrawThread();
		mDrawThread.start();
	}

	/**
	 * 设置地图显示的图片
	 * @param bitmap
	 */
	public void setBitmap(Bitmap bitmap) {
		if (mBitmap != null) {
			mBitmap.recycle();
		}         
		mBitmap = bitmap;
		// 设置最小缩放为铺满屏幕，最大缩放为最小缩放的4倍
		mCurrentScaleMin = Math.min(windowHeight / mBitmap.getHeight(),
				windowWidth / mBitmap.getWidth());
		mCurrentScale = mCurrentScaleMin;
		mCurrentScaleMax = mCurrentScaleMin * 4;
		mapCenter.set(mBitmap.getWidth() * mCurrentScale / 2,
				mBitmap.getHeight() * mCurrentScale / 2);
		float bitmapRatio = mBitmap.getHeight() / mBitmap.getWidth();
		float winRatio = windowHeight / windowWidth;
		// 判断屏幕铺满的情况，isShu为true表示屏幕横向被铺满，为false表示屏幕纵向被铺满
		if (bitmapRatio <= winRatio) {
			isShu = true;
		} else {
			isShu = false;
		}
		markList.clear();
		draw();
	}

	/**
	 * 设置地图定位到的人的坐标并将地图定位到该点
	 * @param x
	 * @param y
	 */
	public void setLoactionPosition(float x, float y) {
		this.locationX = x;
		this.locationY = y;
		adjust(x, y);
	}
	
	/**
	 * 设置定位到的人的地图坐标，不移动地图
	 * @param x
	 * @param y
	 */
	public void setLocation(float x, float y) {
		this.locationX = x;
		this.locationY = y;
		draw();
	}

	/**
	 * 为当前地图添加标记
	 * 
	 * @param object
	 */
	public void addMark(MarkObject object) {
		markList.add(object);
	}

	/**
	 * 地图放大
	 */
	public void zoomIn() {
		mCurrentScale *= 1.5f;
		if (mCurrentScale > mCurrentScaleMax) {
			mCurrentScale = mCurrentScaleMax;
		}
		draw();
	}

	/**
	 * 地图缩小
	 */
	public void zoomOut() {
		mCurrentScale /= 1.5f;
		if (mCurrentScale < mCurrentScaleMin) {
			mCurrentScale = mCurrentScaleMin;
		}
		if (isShu) {
			if (mapCenter.x - mBitmap.getWidth() * mCurrentScale / 2 > 0) {
				mapCenter.x = mBitmap.getWidth() * mCurrentScale / 2;
			} else if (mapCenter.x + mBitmap.getWidth() * mCurrentScale / 2 < windowWidth) {
				mapCenter.x = windowWidth - mBitmap.getWidth() * mCurrentScale
						/ 2;
			}
			if (mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2 > 0) {
				mapCenter.y = mBitmap.getHeight() * mCurrentScale / 2;
			}
		} else {

			if (mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2 > 0) {
				mapCenter.y = mBitmap.getHeight() * mCurrentScale / 2;
			} else if (mapCenter.y + mBitmap.getHeight() * mCurrentScale / 2 < windowHeight) {
				mapCenter.y = windowHeight - mBitmap.getHeight()
						* mCurrentScale / 2;
			}

			if (mapCenter.x - mBitmap.getWidth() * mCurrentScale / 2 > 0) {
				mapCenter.x = mBitmap.getWidth() * mCurrentScale / 2;
			}
		}
		draw();
	}

	/**
	 * @param px
	 * @param py
	 * @param width
	 * @param height
	 *            mapCenter表示地图中心在屏幕上的坐标 px，py表示想要将地图坐标移动屏幕中心
	 *            判断px，py屏幕坐标与屏幕中心的差值，去和屏幕
	 * 
	 * 
	 */
	public void adjust(float px, float py) {
		float mx = convertToScreenX(px);
		float my = convertToScreenY(py);

		float offsetX = windowWidth / 2 - mx;
		float offsetY = windowHeight / 2 - my;
		for (int i = 0; i < 100; i++) {
			adjustByOffset(offsetX / 100, offsetY / 100);
		}
		draw();
	}

	/**
	 * 在Activity的onPause方法中调用此方法
	 */
	public void onPuase() {
		if (mBitmap != null) {
			mBitmap.recycle();
		}
	}

	/**
	 * 在Activity的onDestroy方法中调用此方法
	 */
	public void onDestory() {

		if (mDrawThread != null && mDrawThread.looper != null) {
			mDrawThread.looper.quit();
		}
	}

	// 处理拖拽事件
	private void drag(MotionEvent event) {
		PointF currentPoint = new PointF();
		currentPoint.set(event.getX(), event.getY());
		float offsetX = currentPoint.x - mStartPoint.x;
		float offsetY = currentPoint.y - mStartPoint.y;
		adjustByOffset(offsetX, offsetY);
		draw();
		mStartPoint = currentPoint;
	}

	/**
	 * 通过所给的偏移量进行地图的偏移
	 * @param offsetX
	 * @param offsetY
	 */
	private void adjustByOffset(float offsetX, float offsetY) {
		// 以下是进行判断，防止出现图片拖拽离开屏幕
		if (offsetX > 0
				&& mapCenter.x + offsetX - mBitmap.getWidth() * mCurrentScale
						/ 2 > 0) {
			offsetX = 0;
		}
		if (offsetX < 0
				&& mapCenter.x + offsetX + mBitmap.getWidth() * mCurrentScale
						/ 2 < windowWidth) {
			offsetX = 0;
		}
		if (offsetY > 0
				&& mapCenter.y + offsetY - mBitmap.getHeight() * mCurrentScale
						/ 2 > 0) {
			offsetY = 0;
		}
		if (offsetY < 0
				&& mapCenter.y + offsetY + mBitmap.getHeight() * mCurrentScale
						/ 2 < windowHeight) {
			offsetY = 0;
		}
		mapCenter.x += offsetX;
		mapCenter.y += offsetY;
	}

	// 处理多点触控缩放事件
	private void zoomAction(MotionEvent event) {
		float newDist = spacing(event);
		if (newDist > 10.0f) {
			mCurrentScale = oldRate * (newDist / oldDist);
			if (mCurrentScale < mCurrentScaleMin) {
				mCurrentScale = mCurrentScaleMin;
			} else if (mCurrentScale > mCurrentScaleMax) {
				mCurrentScale = mCurrentScaleMax;
			}

			if (isShu) {
				if (mapCenter.x - mBitmap.getWidth() * mCurrentScale / 2 > 0) {
					mapCenter.x = mBitmap.getWidth() * mCurrentScale / 2;
				} else if (mapCenter.x + mBitmap.getWidth() * mCurrentScale / 2 < windowWidth) {
					mapCenter.x = windowWidth - mBitmap.getWidth()
							* mCurrentScale / 2;
				}
				if (mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2 > 0) {
					mapCenter.y = mBitmap.getHeight() * mCurrentScale / 2;
				}
			} else {

				if (mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2 > 0) {
					mapCenter.y = mBitmap.getHeight() * mCurrentScale / 2;
				} else if (mapCenter.y + mBitmap.getHeight() * mCurrentScale
						/ 2 < windowHeight) {
					mapCenter.y = windowHeight - mBitmap.getHeight()
							* mCurrentScale / 2;
				}

				if (mapCenter.x - mBitmap.getWidth() * mCurrentScale / 2 > 0) {
					mapCenter.x = mBitmap.getWidth() * mCurrentScale / 2;
				}
			}
		}
		draw();
	}

	// 处理点击标记的事件
	private void clickAction(MotionEvent event) {

		int clickX = (int) event.getX();
		int clickY = (int) event.getY();

		for (MarkObject object : markList) {
			int objX = (int) convertToScreenX(object.getMapX());
			int objY = (int) convertToScreenY(object.getMapY());
			// 判断当前object是否包含触摸点，在这里为了得到更好的点击效果，我将标记的区域放大了
			if (objX - radius * 2 < clickX && objX + radius * 2 > clickX
					&& objY + radius * 2 > clickY && objY - radius * 2 < clickY) {
				if (object.getMarkListener() != null) {
					object.getMarkListener()
							.onMarkClick(object, clickX, clickY);
				}
				break;
			}

		}

	}

	// 计算两个触摸点的距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	private void draw() {
		if(mDrawThread.mHandler!=null){
			mDrawThread.mHandler.sendEmptyMessage(0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			if (event.getPointerCount() == 1) {
				// 如果两次点击时间间隔小于一定值，则默认为双击事件
				if (event.getEventTime() - lastClickTime < DOUBLE_CLICK_TIME_SPACE) {
					zoomIn();
				} else {
					mStartPoint.set(event.getX(), event.getY());
					mStatus = Status.DRAG;
				}
			}
			lastClickTime = event.getEventTime();
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			float distance = spacing(event);
			if (distance > 10f) {
				mStatus = Status.ZOOM;
				oldDist = distance;
			}
			break;

		case MotionEvent.ACTION_MOVE:

			if (mStatus == Status.DRAG) {
				drag(event);
			} else if (mStatus == Status.ZOOM) {
				zoomAction(event);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mStatus != Status.ZOOM) {
				clickAction(event);
			}

		case MotionEvent.ACTION_POINTER_UP:
			oldRate = mCurrentScale;
			mStatus = Status.NONE;
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		draw();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		windowHeight = height;
		windowWidth = width;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}

	public class DrawThread extends Thread {

		public Handler mHandler;
		private Looper looper;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			looper = Looper.myLooper();
			synchronized (this) {
				mHandler = new Handler() {// 当接收到绘图消息时，重新进行绘图

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						super.handleMessage(msg);
						Canvas canvas = getHolder().lockCanvas();
						if (canvas != null && mBitmap != null) {
							canvas.drawColor(Color.GRAY);
							Matrix matrix = new Matrix();
							matrix.setScale(mCurrentScale, mCurrentScale,
									mBitmap.getWidth() / 2,
									mBitmap.getHeight() / 2);
							matrix.postTranslate(
									mapCenter.x - mBitmap.getWidth() / 2,
									mapCenter.y - mBitmap.getHeight() / 2);
							canvas.drawBitmap(mBitmap, matrix, mPaintMark);
							if (isShowMark) {
								for (MarkObject object : markList) {
									float cx = convertToScreenX(
											object.getMapX());
									float cy = convertToScreenY(
											object.getMapY());
									canvas.drawCircle(cx, cy, radius,
											mPaintMark);
								}
							}
							float lx = convertToScreenX(locationX);
							float ly = convertToScreenY(locationY);
							canvas.drawCircle(lx, ly, 2 * radius,
									mPaintLocation);

						}
						if (canvas != null) {
							getHolder().unlockCanvasAndPost(canvas);
						}
					}

				};
			}
			Looper.loop();
		}
	}

	/**
	 * 是否绘制标记
	 * @return
	 */
	public boolean isShowMark() {
		return isShowMark;
	}

	/**
	 * 设置是否显示标记
	 * @param isShowMark
	 */
	public void setShowMark(boolean isShowMark) {
		this.isShowMark = isShowMark;
		draw();
	}

	/**
	 * 将地图x坐标转换为屏幕x坐标
	 * @param mapX
	 * @return
	 */
	public float convertToScreenX(float mapX) {
		return mapCenter.x  - mBitmap.getWidth() * mCurrentScale / 2
				+ mBitmap.getWidth() * mapX * mCurrentScale;
	}

	/**
	 * 将地图y坐标转换为屏幕y坐标
	 * @param mapY
	 * @return
	 */
	public float convertToScreenY(float mapY) {
		return mapCenter.y - mBitmap.getHeight() * mCurrentScale / 2
				+ mBitmap.getHeight() * mapY * mCurrentScale;
	}

}
