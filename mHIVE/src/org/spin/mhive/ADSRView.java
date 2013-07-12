package org.spin.mhive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ADSRView extends View {
	
	Paint linePaint;
	Paint bgPaint;
	Paint circleStrokePaint;
	Paint circleBGPaint;

	RectF attackDecayCircle;
	RectF decaySustainCircle;
	RectF sustainReleaseCircle;
	
	float attack, decay, sustain, release;
	
	private final float MS_IN_WIDTH = 1000;
	private final int nDottedLinesForSustain = 10;
	
	
	private final int SELECTION_CIRCLE_RADIUS = 35;
	
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
		linePaint = new Paint();
		linePaint.setColor(Color.BLACK);
		linePaint.setStrokeWidth(5);
		
		bgPaint = new Paint();
		bgPaint.setColor(Color.WHITE);
		
		circleStrokePaint = new Paint();
		circleStrokePaint.setARGB(255, 0, 0, 255);
		circleStrokePaint.setStrokeWidth(5);
		circleStrokePaint.setStyle(Style.STROKE);
		
		circleBGPaint = new Paint();
		circleBGPaint.setARGB(127, 0, 0, 255);
		circleBGPaint.setStyle(Style.FILL);
		
		attack = 100;
		decay = 100;
		sustain = 0.5f;
		release = 100;
		
	}
	
	private float MS2Width(float ms)
	{
		return ms/MS_IN_WIDTH * getWidth();
	}
	
	private float SustainHeight()
	{
		return (1.0f-sustain)*getHeight(); 
	}
	
	private float SustainWidth()
	{
		//TODO: Stub
		return 100;
	}
	
	private float SustainLeft()
	{
		return MS2Width(attack+decay);
	}
	
	private float SustainRight()
	{
		return MS2Width(attack+decay)+SustainWidth();
	}
	
	@Override
	public void onDraw(Canvas c)
	{
		//bg
		c.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
		
		//attack
		c.drawLine(0, getHeight(), MS2Width(attack), 0, linePaint);
		
		//decay
		c.drawLine(MS2Width(attack), 0, SustainLeft(), SustainHeight(), linePaint);
		
		//sustain
		float[] sustain_pts = new float[nDottedLinesForSustain*4];
		float sustainLineWidth = SustainWidth()/((float)nDottedLinesForSustain*4);
		for (int i = 0; i < nDottedLinesForSustain*4; i+=4)
		{
			sustain_pts[i] = SustainLeft() + i*sustainLineWidth; //x1
			sustain_pts[i+1] = SustainHeight(); //y1
			sustain_pts[i+2] = SustainLeft() + (i+2)*sustainLineWidth; // x2
			sustain_pts[i+3] = SustainHeight(); // y2
		}
		c.drawLines(sustain_pts, linePaint);
		
		//release
		c.drawLine(SustainRight(), SustainHeight(), getWidth(), getHeight(), linePaint);
		
		
		//TODO: Move this to UPDATE
		attackDecayCircle = new RectF(	MS2Width(attack)-SELECTION_CIRCLE_RADIUS, -SELECTION_CIRCLE_RADIUS,
										MS2Width(attack)+SELECTION_CIRCLE_RADIUS, SELECTION_CIRCLE_RADIUS);
		decaySustainCircle = new RectF(	SustainLeft()-SELECTION_CIRCLE_RADIUS, SustainHeight()-SELECTION_CIRCLE_RADIUS,
										SustainLeft()+SELECTION_CIRCLE_RADIUS, SustainHeight()+SELECTION_CIRCLE_RADIUS);
		sustainReleaseCircle = new RectF(	SustainRight()-SELECTION_CIRCLE_RADIUS, SustainHeight()-SELECTION_CIRCLE_RADIUS,
											SustainRight()+SELECTION_CIRCLE_RADIUS, SustainHeight()+SELECTION_CIRCLE_RADIUS);
		
		//selection circles
		c.drawOval(attackDecayCircle, circleBGPaint);
		c.drawOval(attackDecayCircle, circleStrokePaint);
		c.drawOval(decaySustainCircle, circleBGPaint);
		c.drawOval(decaySustainCircle, circleStrokePaint);
		c.drawOval(sustainReleaseCircle, circleBGPaint);
		c.drawOval(sustainReleaseCircle, circleStrokePaint);
	}

}
