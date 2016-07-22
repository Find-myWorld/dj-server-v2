package com.example.iimp_znxj_new2014.activity;

import com.example.iimp_znxj_new2014.util.Constant;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class AutoScrollView extends ScrollView {
	private final Handler handler = new Handler();
	private long duration = Constant.SCROLL_SPEED; //数�?越大，滚动越慢（40合�?�?
	private boolean isScrolled = false;
	private int currentIndex = 0;
	private long period = 12000; //滚动到结尾停顿的时间,单位：ms
	private int currentY = -1;
	private double x;
	private double y;
	private int type = -1;

	public AutoScrollView(Context context) {
		this(context, null);
	}

	public AutoScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AutoScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int Action = event.getAction();
		switch (Action) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			if (type == 0) {
				setScrolled(false);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			double moveY = event.getY() - y;
			double moveX = event.getX() - x;
			Log.d("test", "moveY = " + moveY + "  moveX = " + moveX);
			if ((moveY > 20 || moveY < -20) && (moveX < 50 || moveX > -50) && getParent() != null) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}

			break;
		case MotionEvent.ACTION_UP:
			if (type == 0) {
				currentIndex = getScrollY();
				setScrolled(true);
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent p_event) {
		Log.d("test", "onInterceptTouchEvent");
		return true;
	}

	public boolean isScrolled() {
		return isScrolled;
	}

	public void setScrolled(boolean isScrolled) {
		this.isScrolled = isScrolled;
		autoScroll();
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public long getSpeed() {
		return duration;
	}

	public void setSpeed(long speed) {
		this.duration = speed;
	}

	public void setType(int type) {
		this.type = type;
	}

	private void autoScroll() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				boolean flag = isScrolled;
				if (flag) {
					if (currentY == getScrollY()) {
						try {
							Thread.sleep(period);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						currentIndex = 0;
						scrollTo(0, 0);
						scrollEndListener.isEnd();
						handler.postDelayed(this, period);
					} else {
						currentY = getScrollY();
						handler.postDelayed(this, duration);
						currentIndex++;
						scrollTo(0, currentIndex * 1);
					}
				} else {
					//					currentIndex = 0;
					//					scrollTo(0, 0);
				}
			}
		}, duration);
	}
	
	interface ScrollEnd{
		
		void isEnd();
		
	}

	private ScrollEnd scrollEndListener;
	//滚动结束接口
	public void registerScrollListener(ScrollEnd scrollEndListener) {
		this.scrollEndListener=scrollEndListener;
	}
	
}
