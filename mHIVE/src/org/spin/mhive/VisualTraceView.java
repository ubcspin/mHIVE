package org.spin.mhive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class VisualTraceView extends TextView {

	Paint paint;
	
	public VisualTraceView(Context context) {
		super(context);
		initPaint();
	}

	public VisualTraceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public VisualTraceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
	}
	
	
	private void initPaint()
	{
		paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
	}
	
	
	@Override
	public void onDraw(Canvas c)
	{
		float pts[] = {0,0,1,5,6,78,23,67,600,234};
		c.drawLines(pts, paint);
	}

}
