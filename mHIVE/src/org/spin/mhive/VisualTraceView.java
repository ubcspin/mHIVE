package org.spin.mhive;

import java.util.LinkedList;
import java.util.Timer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class VisualTraceView extends TextView {

	Paint paint;
	LinkedList<Float> ptList;
	float[] displayPts;
	final int INITIAL_ARRAY_SIZE = 100;
	
	public VisualTraceView(Context context) {
		super(context);
		init();
	}

	public VisualTraceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VisualTraceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	
	private void init()
	{
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(10);
		paint.setARGB(127, 255, 255, 255);
		ptList = new LinkedList<Float>();
		displayPts = new float[INITIAL_ARRAY_SIZE];
		
		//Timer delayTimer = new Timer();

	}
	
	public void addPoint(float x, float y)
	{
		ptList.add(x);
		ptList.add(y);
		
		if(displayPts.length <= ptList.size())
		{
			displayPts = new float[ptList.size()*4];
		}
		
		displayPts[0] = ptList.get(0);
		displayPts[1] = ptList.get(1);
		for(int i = 2; i < ptList.size(); i+=4)
		{
			displayPts[i] = ptList.get(i);
			displayPts[i+1] = ptList.get(i+1);
			displayPts[i+2] = ptList.get(i);
			displayPts[i+3] = ptList.get(i+1);
		}
		invalidate();
	}
	
	
	@Override
	public void onDraw(Canvas c)
	{
		if(ptList.size() >= 4)
		{
			c.drawLines(displayPts, paint);
		}
	}
	
	

}
