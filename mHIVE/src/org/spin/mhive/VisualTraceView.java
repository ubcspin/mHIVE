package org.spin.mhive;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class VisualTraceView extends TextView {

	Paint linePaint;
	Paint circlePaint;
	Paint circleLinePaint;
	Paint bgTextPaint;
	float CIRCLERADIUS = 20;
	float maxFreq = 0;
	float minFreq = 0;
	float freqRange = 0;
	List<PointRecord> ptList;
	float[] displayPts;
	final int INITIAL_ARRAY_SIZE = 4;
	final int DECAY_TIME_IN_MS = 500;
	final int DECAY_UPDATE_RATE_IN_MS = 10;
	Handler uiHandler;
	Runnable uiUpdateDecay;
	
	public VisualTraceView(Context context) {
		super(context);
		init();
	}

	public Handler GetUIHandler()
	{
		return uiHandler;
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
		linePaint = new Paint();
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(10);
		linePaint.setARGB(127, 255, 255, 255);
		ptList = new ArrayList<PointRecord>();
		displayPts = new float[INITIAL_ARRAY_SIZE];
		
		circlePaint = new Paint();
		circlePaint.setARGB(170, 255, 255, 255);
		circlePaint.setStyle(Style.FILL);

		
		circleLinePaint = new Paint();
		circleLinePaint.setARGB(255, 255, 255, 255);
		circleLinePaint.setStrokeWidth(2);
		circleLinePaint.setStyle(Style.STROKE);
		
		bgTextPaint = new Paint();
		bgTextPaint.setARGB(65, 255, 255, 255);
		bgTextPaint.setTextSize(36);
		bgTextPaint.setTextAlign(Align.CENTER);
		
		uiHandler = new Handler();
		uiUpdateDecay = new Thread()
		{
			@Override
			public void run()
			{
				updateDecay();
			}
		};
		
		
		Timer delayTimer = new Timer();
		delayTimer.scheduleAtFixedRate(new DecayUpdateTask(), DECAY_UPDATE_RATE_IN_MS, DECAY_UPDATE_RATE_IN_MS);
	}
	
	public void SetFrequencyRange(int min, int max)
	{
		minFreq = min;
		maxFreq = max;
		freqRange = max-min;
	}
	
	class DecayUpdateTask extends TimerTask {

		@Override
		public void run()
		{
			uiHandler.post(uiUpdateDecay);
		}
	}
	
	
	public synchronized void updateDecay()
	{
		long t = System.currentTimeMillis();
		while(ptList.size() > 0 && ptList.get(0).t < t-DECAY_TIME_IN_MS)
		{
			ptList.remove(0);
		}
		
		updateDisplayPoints();
	}
	
	public synchronized void addPoint(float x, float y)
	{
		ptList.add(new PointRecord(System.currentTimeMillis(), x, y));
		
		updateDisplayPoints();
	}
	
	
	public synchronized void updateDisplayPoints()
	{
		
		if(ptList.size() >= 2)
		{
			//inefficient but keeping it around causes bugs
			displayPts = new float[ptList.size()*4 - 4];
			
			//we need 2 floats for the first point record (x, y)
			displayPts[0] = ptList.get(0).x;
			displayPts[1] = ptList.get(0).y;
			
			for(int i = 1; i < ptList.size()-1; i++)
			{
				//4 floats for every point record in the middle (x, y, x, y),
				//    since we need to give a finish and a start
				int displayPtsInt = 2 + 4*(i-1);
				displayPts[displayPtsInt] = ptList.get(i).x;
				displayPts[displayPtsInt+1] = ptList.get(i).y;
				displayPts[displayPtsInt+2] = ptList.get(i).x;
				displayPts[displayPtsInt+3] = ptList.get(i).y;
			}
			
			//then, get final point. Only need x, y for this one.
			displayPts[displayPts.length-2] = ptList.get(ptList.size()-1).x;
			displayPts[displayPts.length-1] = ptList.get(ptList.size()-1).y;
			
		} else {
			displayPts = new float[0];
		}
		
		invalidate();
	}
	
	private void drawVolumeBackground(Canvas c, float f)
	{
		c.drawText(""+(int)(f*10)+" dB", getWidth()/2, (1-f)*getHeight(), bgTextPaint);
	}
	
	private void drawFrequencyBackground(Canvas c, float f)
	{
		if (minFreq != maxFreq)
		{
			c.drawText(""+f+"Hz", (f-minFreq)/freqRange*getWidth(), getHeight()/2.0f, bgTextPaint);
		}
	}
	
	@Override
	public void onDraw(Canvas c)
	{
		if(displayPts.length >= 4)
		{
			c.drawLines(displayPts, linePaint);
			c.drawCircle(ptList.get(ptList.size()-1).x, ptList.get(ptList.size()-1).y, CIRCLERADIUS, circlePaint);
			c.drawCircle(ptList.get(ptList.size()-1).x, ptList.get(ptList.size()-1).y, CIRCLERADIUS, circleLinePaint);
		}
		drawVolumeBackground(c, 0.1f);
		drawVolumeBackground(c, 0.3f);
		drawVolumeBackground(c, 0.5f);
		drawVolumeBackground(c, 0.7f);
		drawVolumeBackground(c, 0.9f);
		
		drawFrequencyBackground(c, 30);
		drawFrequencyBackground(c, 60);
		drawFrequencyBackground(c, 100);
		drawFrequencyBackground(c, 130);
		
	}
	
}

