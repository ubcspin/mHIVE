package org.spin.mhive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ADSRView extends View {
	
	Paint linePaint;
	Paint bgPaint;
	Paint circleStrokePaint;
	Paint circleBGPaint;

	RectF attackDecayCircle;
	RectF decaySustainCircle;
	RectF sustainReleaseCircle;
	
	ADSREnvelope adsr;
	
	private final int MAX_MS = 1000;
	private final int MIN_SUSTAIN_WIDTH_IN_MS = 100;
	private final float MS_IN_WIDTH = 3*MAX_MS + MIN_SUSTAIN_WIDTH_IN_MS;
	private final int nDottedLinesForSustain = 10;
	
	
	private enum ADSRViewMode
	{
		DRAGGING_ATTACKDECAY_CIRCLE,
		DRAGGING_DECAYSUSTAIN_CIRCLE,
		DRAGGING_SUSTAINRELEASE_CIRCLE,
		NOT_DRAGGING
	}
	ADSRViewMode mode;
	
	
	private final int SELECTION_CIRCLE_RADIUS = 50;
	
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
		
		
		adsr = new ADSREnvelope(100, 100, 0.5f, 100);
		mode = ADSRViewMode.NOT_DRAGGING;
		Update();
	}
	
	
	@Override
    public boolean onTouchEvent(MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();
		
		if(event.getAction() == MotionEvent.ACTION_DOWN
    			|| event.getAction() == MotionEvent.ACTION_MOVE)
		{
		
		
				if (mode == ADSRViewMode.NOT_DRAGGING)
				{
					if (attackDecayCircle.contains(x, y))
					{
						mode = ADSRViewMode.DRAGGING_ATTACKDECAY_CIRCLE;
					} else if (decaySustainCircle.contains(x, y))
					{
						mode = ADSRViewMode.DRAGGING_DECAYSUSTAIN_CIRCLE;
		
					} else if (sustainReleaseCircle.contains(x, y))
					{
						mode = ADSRViewMode.DRAGGING_SUSTAINRELEASE_CIRCLE;
					}
				}
				
				if (mode == ADSRViewMode.DRAGGING_ATTACKDECAY_CIRCLE)
				{
					SetAttackXY(x, y);
				} else if (mode == ADSRViewMode.DRAGGING_DECAYSUSTAIN_CIRCLE)
				{
					
				} else if (mode == ADSRViewMode.DRAGGING_SUSTAINRELEASE_CIRCLE)
				{
					
				}
		} else if (event.getAction() == MotionEvent.ACTION_UP)
    	{
			mode = ADSRViewMode.NOT_DRAGGING;
    	}
		
		
		return true;
	}
	
	//ATTACK MOVEMENT
	private void SetAttackXY(float x, float y)
	{
		int ms = Width2MS(x); 
		if (ms >= MAX_MS)
		{
			adsr = new ADSREnvelope(MAX_MS, adsr.getDecay(), adsr.getSustain(), adsr.getRelease());
		} else if (ms <= 0)
		{
			adsr = new ADSREnvelope(0, adsr.getDecay(), adsr.getSustain(), adsr.getRelease());
		} else {
			adsr = new ADSREnvelope(ms, adsr.getDecay(), adsr.getSustain(), adsr.getRelease());
		}
		
		Update();
	}
	
	public void SetADSR(ADSREnvelope adsr)
	{
		this.adsr = adsr;
		Update();
	}
	
	private void Update()
	{
		attackDecayCircle = new RectF(	MS2Width(adsr.getAttack())-SELECTION_CIRCLE_RADIUS, -SELECTION_CIRCLE_RADIUS,
										MS2Width(adsr.getAttack())+SELECTION_CIRCLE_RADIUS, SELECTION_CIRCLE_RADIUS);
		decaySustainCircle = new RectF(	SustainLeft()-SELECTION_CIRCLE_RADIUS, SustainHeight()-SELECTION_CIRCLE_RADIUS,
										SustainLeft()+SELECTION_CIRCLE_RADIUS, SustainHeight()+SELECTION_CIRCLE_RADIUS);
		sustainReleaseCircle = new RectF(	SustainRight()-SELECTION_CIRCLE_RADIUS, SustainHeight()-SELECTION_CIRCLE_RADIUS,
											SustainRight()+SELECTION_CIRCLE_RADIUS, SustainHeight()+SELECTION_CIRCLE_RADIUS);
		this.invalidate();
	}
	
	private float MS2Width(float ms)
	{
		return ms/MS_IN_WIDTH * getWidth();
	}
	
	private int Width2MS(float x)
	{
		return (int) (x/getWidth()*((float)MS_IN_WIDTH));
	}
	
	private float SustainHeight()
	{
		return (1.0f-adsr.getSustain())*getHeight(); 
	}
	
	private float SustainWidth()
	{
		return MS2Width(MS_IN_WIDTH-(adsr.getAttack()+adsr.getDecay()+adsr.getRelease()));
	}
	
	private float SustainLeft()
	{
		return MS2Width(adsr.getAttack()+adsr.getDecay());
	}
	
	private float SustainRight()
	{
		return SustainLeft()+SustainWidth();
	}
	
	@Override
	public void onDraw(Canvas c)
	{
		//bg
		c.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
		
		//attack
		c.drawLine(0, getHeight(), MS2Width(adsr.getAttack()), 0, linePaint);
		
		//decay
		c.drawLine(MS2Width(adsr.getAttack()), 0, SustainLeft(), SustainHeight(), linePaint);
		
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
		
		//selection circles
		c.drawOval(attackDecayCircle, circleBGPaint);
		c.drawOval(attackDecayCircle, circleStrokePaint);
		c.drawOval(decaySustainCircle, circleBGPaint);
		c.drawOval(decaySustainCircle, circleStrokePaint);
		c.drawOval(sustainReleaseCircle, circleBGPaint);
		c.drawOval(sustainReleaseCircle, circleStrokePaint);
	}

}
