package org.spin.mhive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ADSRView extends View {
	
	Paint testPaint;
	Paint bgPaint;

	public ADSRView(Context context) {
		super(context);
		Init();
	}

	public ADSRView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init();
	}

	public ADSRView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Init();
	}
	
	
	private void Init()
	{
		testPaint = new Paint();
		testPaint.setColor(Color.BLACK);
		testPaint.setStrokeWidth(10);
		
		bgPaint = new Paint();
		bgPaint.setColor(Color.WHITE);
	}
	
	
	@Override
	public void onDraw(Canvas c)
	{
		c.drawText("ADSR", 100, 100, testPaint);
		c.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
	}

}
