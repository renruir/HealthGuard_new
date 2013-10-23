package com.healthguard.app.pressure.graph;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @ClassName : CoordinatesView
 * @Description : TODO
 * @author : ZGX zhangguoxiao_happy@163.com
 * @date : 2011-10-9 ����09:06:38
 * 
 */
public class Coordinates extends View {
	/*
	 * 颜料
	 */
	private Paint mPaint;
	/*
	 * 数据集合
	 */
	private List<PointF[]> mPointsList;
	private List<Paint> mPaintList;
	/*
	 * 标题
	 */
	private boolean mHasTitle;
	private String mTitle;
	private int mTitleHeight;
	private PointF mTitlePoint;
	/*
	 * 边距
	 */
	private int mLeftPad, mRightPad, mBottomPad, mTopPad;
	/*
	 * 横轴纵轴密度、长度和比例。
	 */
	private float mXValuePerPix, mYValuePerPix;
	private int mXLen, mYLen;
	private float mXScale, mYScale;
	/*
	 * 横轴纵轴标识和单位
	 */
	private String mXAxisPrickle, mYAxisPrickle;
	private String mXAxisName = "X", mYAxisName = "Y";
	/*
	 * 圆心（坐标值是相对与控件的左上角的）
	 */
	// private PointF mPointZero = new PointF();
	/*
	 * 参考坐标
	 */
	private PointF mPointBase = new PointF(0,0);
	private PointF mPointBaseValue = new PointF();
	/*
	 * 交叉点坐标中心点
	 */
	private PointF mPointOrigin = new PointF(0,0);

	/*
	 * 自定义控件一般写两个构造方法 CoordinatesView(Context context)用于java硬编码创建控件
	 * 如果想要让自己的控件能够通过xml来产生就必须有第2个构造方法 CoordinatesView(Context context,
	 * AttributeSet attrs) 因为框架会自动调用具有AttributeSet参数的这个构造方法来创建继承自View的控件
	 */
	public Coordinates(Context context) {
		super(context, null);
		init();
	}

	public Coordinates(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// 设置颜料
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		// 设置边距
		setCoordinatesPadding(0, 0, 0, 0);
		// 设置标题
		setTitleHeight(20);
		setTitleName("标题");

		mHasTitle = true;
		mTitlePoint = new PointF(mLeftPad, mTitleHeight);
		// 设置密度
		mXValuePerPix = 0.5f;
		mYValuePerPix = 0.5f;
		// 设置缩放比例
		mXScale = 1;
		mYScale = 1;
	}

	/**
	 * 设置标题高度
	 */
	public void setTitleHeight(int height) {
		mTitleHeight = height;
	}

	/**
	 * 设置图表标题
	 */
	public void setTitleName(String titleName) {
		mTitle = titleName;
	}

	/**
	 *设置放大缩小倍数
	 */
	public void setScaleXY(float xScale, float yScale) {
		mXScale = xScale;
		mYScale = yScale;
	}

	/**
	 * 设置边距
	 */
	public void setCoordinatesPadding(int leftPad, int rightPad, int topPad,
			int bottomPad) {
		mLeftPad = leftPad + 40;
		mRightPad = rightPad + 20;
		mTopPad = topPad + 10;
		mBottomPad = bottomPad + 40;
	}

	/**
	 * 添加一条曲线
	 */
	public void addPoints(PointF[] points, Paint paint) {
		if (points == null)
			return;
		if (mPointsList == null)
			mPointsList = new ArrayList<PointF[]>();
		mPointsList.add(points);
		if (mPaintList == null)
			mPaintList = new ArrayList<Paint>();
		if (paint != null)
			mPaintList.add(paint);
		else {
			mPaintList.add(mPaint);
		}
	}

	/**
	 *设置坐标名称和单位
	 */
	public void setAxisNamePrickleXY(String xName, String xPrickle,
			String yName, String yPrickle) {
		mXAxisName = xName;
		mXAxisPrickle = xPrickle;
		mYAxisName = yName;
		mYAxisPrickle = yPrickle;
	}

	// private int centerX, centerY;
	/*
	 * 控件创建完成之后，在显示之前都会调用这个方法，此时可以获取控件的大小 并得到中心坐标和坐标轴圆心所在的点
	 */
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		// centerX = w / 2;
		// centerY = h / 2;
		mXLen = w - mLeftPad - mRightPad;
		mYLen = h - mBottomPad - mTopPad - (mHasTitle ? mTitleHeight : 0);
		mPointOrigin.set(mLeftPad, h - mBottomPad);
		mPointBase.set(mXLen / 2 + mPointOrigin.x, mPointOrigin.y - mYLen / 2);
		mPointBaseValue.set(mXLen / 2 * mXValuePerPix / mXScale, mYLen / 2
				* mYValuePerPix / mYScale);
		// mPointZero.set(mLeftPad, h - mBottomPad);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/*
	 * 自定义控件一般都会重载onDraw(Canvas canvas)方法，来绘制自己想要的图形
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (canvas == null) {
			return;
		}
		/*
		 * 画背景色
		 */
		canvas.drawColor(Color.WHITE);
		/*
		 * 画参考点
		 */
		Log.i("base point", "(" + Round(mPointBaseValue.x) + ","
				+ Round(mPointBaseValue.y) + ")");
		drawPoint(canvas, point2Physical(mPointBaseValue), mPaint);
		canvas.drawText("(" + Round(mPointBaseValue.x) + ","
				+ Round(mPointBaseValue.y) + ")", mPointBase.x - 15,
				mPointBase.y + 18 + 12, mPaint);
		/*
		 * 画标题
		 */
		canvas.drawText(mTitle, mTitlePoint.x, mTitlePoint.y, mPaint);
		/*
		 * 画坐标轴
		 */
		// 画直线
		canvas.drawLine(mPointOrigin.x, mPointOrigin.y, mPointOrigin.x + mXLen,
				mPointOrigin.y, mPaint);// ��X��
		canvas.drawLine(mPointOrigin.x, mPointOrigin.y, mPointOrigin.x,
				mPointOrigin.y - mYLen, mPaint);// ��Y��
		// 画X轴箭头
		float x = mPointOrigin.x + mXLen, y = mPointOrigin.y;
		drawTriangle(canvas, new PointF(x, y), new PointF(x - 10, y - 5), new PointF(x - 10, y + 5));
		canvas.drawText(mXAxisName, x - 15, y + 18, mPaint);
//		canvas.drawText("(" + mXAxisPrickle + ")", x - 15, y + 18 + 12, mPaint);
		// 画Y轴箭头
		x = mPointOrigin.x;
		y = mPointOrigin.y - mYLen;
		drawTriangle(canvas, new PointF(x, y), new PointF(x - 5, y + 10), new PointF(x + 5, y + 10));
//		canvas.drawText(mYAxisName + " (" + mYAxisPrickle + ")", x + 12, y + 15, mPaint);
		/*
		 * 画交叉点坐标
		 */
		// 先计算出来当前交叉点坐标的值
		PointF po = point2Logical(mPointOrigin);
		String centerString = "(" + Round(po.x) + "," + Round(po.y) + ")";
		// 然后显示坐标
		canvas.drawText(centerString, mPointOrigin.x - 25, mPointOrigin.y + 15,	mPaint);

		/*
		 * 画数据 所有外部需要在坐标轴上画的数据，都在这里进行绘制
		 */
		drawMultiLines(canvas, mPointsList, mPaintList);
	}

	/**
	 * 保留小数
	 */
	private float Round(float f) {
		return (float) (Math.round(f * 10)) / 10;
	}

	/**
	 * 画点
	 */
	private void drawPoint(Canvas canvas, PointF p, Paint paint) {
		canvas.drawCircle(p.x, p.y, 2, paint);
	}

	/**
	 * 画线
	 */
	private void drawLine(Canvas canvas, PointF pa, PointF pb, Paint paint) {
		canvas.drawLine(pa.x, pa.y, pb.x, pb.y, paint);
	}

	/**
	 * 逻辑坐标转化为屏幕坐标
	 * 将逻辑坐标logPointF点转换为物理坐标
	 */
	private PointF point2Physical(PointF logPointF) {
		PointF physicalPointF = new PointF();
		physicalPointF.set((logPointF.x - mPointBaseValue.x) * mXScale
				/ mXValuePerPix + mPointBase.x,
				-(logPointF.y - mPointBaseValue.y) * mYScale / mYValuePerPix
						+ mPointBase.y);
		return physicalPointF;
	}

	/**
	 * 物理坐标转化为逻辑坐标
	 * 将物理坐标phyPointF点转换为逻辑坐标
	 */
	private PointF point2Logical(PointF phyPointF) {
		float x = (phyPointF.x - mPointBase.x) * mXValuePerPix / mXScale
				+ mPointBaseValue.x;
		float y = (mPointBase.y - phyPointF.y) * mYValuePerPix / mYScale
				+ mPointBaseValue.y;
		PointF logicalPointF = new PointF(x, y);
		return logicalPointF;
	}

	/**
	 * 画三角形 用于画坐标轴的箭头
	 */
	private void drawTriangle(Canvas canvas, PointF p1, PointF p2, PointF p3) {
		Path path = new Path();
		path.moveTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p3.x, p3.y);
		path.close();

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.FILL);
		//绘制这个多边形
		canvas.drawPath(path, paint);
	}

	/**
	 * 数据连线
	 */
	private void drawLines(Canvas canvas, PointF[] point, Paint paint) {
		int len = (point == null) ? 0 : point.length;
		if (len > 0) {
			PointF pa = point[0];
			PointF pb;
			drawPoint(canvas, point2Physical(pa), paint);
			for (int i = 1; i < len; i++) {
				pb = point[i];
				drawLine(canvas, point2Physical(pa), point2Physical(pb), paint);
				drawPoint(canvas, point2Physical(pb), paint);
				pa = pb;
			}
			pb = point[len - 1];
			drawLine(canvas, point2Physical(pa), point2Physical(pb), paint);
			drawPoint(canvas, point2Physical(pb), paint);
		}
	}

	/**
	 * 画多条曲线
	 */
	private void drawMultiLines(Canvas canvas, List<PointF[]> pointList,
			List<Paint> paintList) {
		int len = (pointList == null) ? 0 : pointList.size();
		for (int i = 0; i < len; i++) {
			drawLines(canvas, pointList.get(i), paintList.get(i));
		}
	}

	/*
	 * 用于保存拖动时的上一个点的位置
	 */
	int x0, y0;

	/*
	 * 拖动事件监听
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		/*
		 * (x,y)点为发生事件时的点，它的坐标值为相对于该控件左上角的距离
		 */
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {

		case MotionEvent.ACTION_DOWN: // ����
			x0 = x;
			y0 = y;
			Log.i("down", "(" + x0 + "," + y0 + ")");
			break;
		case MotionEvent.ACTION_MOVE: // �϶�
			/*
			 * (x-x0, y-y0)为物理坐标相对运动矢量
			 * mXValuePerPix / mXScale 为X轴的密度
			 * mYValuePerPix / mYScale 为Y轴的密度
			 * 即每个像素长度代表的长度
			 * 
			 * 向右和向上移动的时候，坐标值都是变大的，而参考点是不动的所以相对来说是变小的
			 * 但是物理Y轴的矢量应该先取反，因为物理坐标点的坐标轴是向下Y增大的。
			 */
//			mPointBaseValue.x -= (x - x0) * mXValuePerPix / mXScale;
//			mPointBaseValue.y -= -(y - y0) * mYValuePerPix / mYScale;
//			x0 = x;
//			y0 = y;
//			Log.i("move", "(" + x0 + "," + y0 + ")");
//			invalidate();
			break;
		case MotionEvent.ACTION_UP: // ����
			break;
		}
		/*
		 * 注意：这里一定要返回true 返回false和super.onTouchEvent(event)都会本监听只能检测到按下消息
		 * 这是因为false和super.onTouchEvent(event)的处理都是告诉系统该控件不能处理这样的消息，
		 * 最终系统会将这些事件交给它的父容器处理。
		 */
		return true;
	}
}
