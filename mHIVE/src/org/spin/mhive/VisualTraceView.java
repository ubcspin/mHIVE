package org.spin.mhive;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class VisualTraceView extends TextView {

	Paint paint;
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
		ptList = new ArrayList<PointRecord>();
		displayPts = new float[INITIAL_ARRAY_SIZE];
		
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
		
		if(displayPts.length <= ptList.size()*4 - 4)
		{
			displayPts = new float[ptList.size()*4 - 4];
		}
		
		updateDisplayPoints();
	}
	
	
	public synchronized void updateDisplayPoints()
	{
		//we need 2 floats for the first point record (x, y)
		
		if(ptList.size() >= 2)
		{
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
	
	@Override
	public void onDraw(Canvas c)
	{
		if(displayPts.length >= 4)
		{
			c.drawLines(displayPts, paint);
		}
	}
	
	
	//TODO: Should this be in a different (non-class) structure for efficiency?
	class PointRecord
	{
		public long t;
		public float x, y;
		public PointRecord(long t, float x, float y)
		{
			this.t = t;
			this.x = x;
			this.y = y;
		}
	}

}
