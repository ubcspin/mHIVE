package org.spin.mhive;

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
	float[] pts;
	final int MAX_SIZE = 100;
	int i = 0;
	
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
		paint.setARGB(255, 255, 255, 255);
		pts = new float[MAX_SIZE];
		
//		this.setOnDragListener(new OnDragListener()
//		{
//
//			@Override
//			public boolean onDrag(View v, DragEvent event) {
//				addPoint(event.getX(), event.getY());
//				return false;
//			}
//			
//		});
//		
//		this.setOnTouchListener(new OnTouchListener()
//						{
//				
//							@Override
//							public boolean onTouch(View view, MotionEvent event)
//							{
//								addPoint(event.getX(), event.getY());
//								return false;
//							}
//							
//						});
	}
	
	public void addPoint(float x, float y)
	{
		pts[(i++)%MAX_SIZE]=x;
		pts[(i++)%MAX_SIZE]=y;
		invalidate();
	}
	
	
	@Override
	public void onDraw(Canvas c)
	{
		if(pts.length >= 4)
		{
			c.drawLines(pts, paint);
		}
	}
	
	

}
