package org.spin.mhive;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ADSRView extends View {
	
	Paint linePaint;
	Paint bgPaint;
	Paint circleStrokePaint;
	Paint circleBGPaint;
	Paint playHeadPaint;
	Paint numericDisplayPaint;
	Paint numericDisplayTextPaint;

	RectF attackDecayCircle;
	RectF decaySustainCircle;
	RectF sustainReleaseCircle;
	
	ADSREnvelope adsr;
	
	
	private final int SELECTION_CIRCLE_RADIUS = 50;
	private final int SELECTION_CIRCLE_STROKE_WIDTH = 4;
	private final int NUMERIC_LINE_STROKE_WIDTH = 4;
	private final float numericDisplayHeight = SELECTION_CIRCLE_RADIUS+SELECTION_CIRCLE_STROKE_WIDTH/2;
	private final int MAX_MS = 1000;
	private final int MIN_SUSTAIN_WIDTH_IN_MS = 300;
	private final float MS_IN_WIDTH = 3*MAX_MS + MIN_SUSTAIN_WIDTH_IN_MS;
	private final float nDottedLinesForSustainPerPx = 0.15f;
	
	//TODO: THIS SHOULD BE INTERFACE 
	MainActivity mainActivity;
	
	
	private enum ADSRViewMode
	{
		DRAGGING_ATTACKDECAY_CIRCLE,
		DRAGGING_DECAYSUSTAIN_CIRCLE,
		DRAGGING_SUSTAINRELEASE_CIRCLE,
		NOT_DRAGGING
	}
	ADSRViewMode mode;
	
	Timer tmrPlayBar;
	final int PLAYBAR_UPDATE_INTERVAL = 33;//ms
	long playPosition = 0; //in MS
	long startTime = 0; //in MS;
	float playBarX = 0;
	boolean isPlaying = false;
	Handler uiHandler;
	


	
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
	
	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	  super.onWindowFocusChanged(hasFocus);
	  Update();
	  }
	
	
	public void SetMainActivity(MainActivity ma)
	{
		mainActivity = ma;
	}
	
	
	private void Init()
	{
		uiHandler = new Handler();
		
		linePaint = new Paint();
		linePaint.setColor(Color.BLACK);
		linePaint.setStrokeWidth(5);
		
		bgPaint = new Paint();
		bgPaint.setColor(Color.WHITE);
		
		circleStrokePaint = new Paint();
		circleStrokePaint.setARGB(255, 0, 0, 255);
		circleStrokePaint.setStrokeWidth(SELECTION_CIRCLE_STROKE_WIDTH);
		circleStrokePaint.setStyle(Style.STROKE);
		
		circleBGPaint = new Paint();
		circleBGPaint.setARGB(127, 0, 0, 255);
		circleBGPaint.setStyle(Style.FILL);
		
		
		playHeadPaint = new Paint();
		playHeadPaint.setARGB(127,  255, 0, 0);
		playHeadPaint.setStrokeWidth(5);
		
		numericDisplayPaint = new Paint();
		numericDisplayPaint.setARGB(255, 0, 127, 255);
		numericDisplayPaint.setStrokeWidth(NUMERIC_LINE_STROKE_WIDTH);
		
		numericDisplayTextPaint = new Paint();
		numericDisplayTextPaint.setARGB(255, 0, 127, 255);
		numericDisplayTextPaint.setTextAlign(Align.CENTER);
		numericDisplayTextPaint.setTextSize(24);

		
		tmrPlayBar = new Timer();
		tmrPlayBar.scheduleAtFixedRate(new PlayBarTask(), PLAYBAR_UPDATE_INTERVAL, PLAYBAR_UPDATE_INTERVAL);
		
		adsr = new ADSREnvelope(100, 100, 0.5f, 100);
		mode = ADSRViewMode.NOT_DRAGGING;
		Update();
	}
	
	
	private float GetVisualizationHeight()
	{
		return getHeight() - numericDisplayHeight;
	}
	
	private float GetVisualizationTop()
	{
		return numericDisplayHeight;
	}
	
	private float GetVisualizationBottom()
	{
		return getHeight();
	}
	
	private float GetNumericTop()
	{
		return 0;
	}
	
	private float GetNumericBottom()
	{
		return GetVisualizationTop();
	}
	
	private float GetNumericHorizontalLineHeight()
	{
		return (GetNumericBottom() + GetNumericTop())*2/3;
	}
	
	private float GetNumericTextHeight()
	{
		return GetNumericHorizontalLineHeight() - 5;
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
					SetDecaySustainXY(x, y);
				} else if (mode == ADSRViewMode.DRAGGING_SUSTAINRELEASE_CIRCLE)
				{
					SetSustainReleaseXY(x,y);
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
	
	
	//DECAY_SUSTAIN MOVEMENT
	private void SetDecaySustainXY(float x, float y)
	{
		int ms = Width2MS(x)-adsr.getAttack();
		float atten = Height2Sustain(y);
		
		int newDecay = 0;
		float newSustain = 1.0f;
		
		
		if (ms >= MAX_MS)
		{
			newDecay = MAX_MS;
		} else if (ms <= 0)
		{
			newDecay = 0;
		} else {
			newDecay = ms;
		}
		
		if (atten >= 1.0f)
		{
			newSustain = 1.0f;
		} else if (atten <= 0)
		{
			newSustain = 0;
		} else {
			newSustain = atten;
		}
		
		adsr = new ADSREnvelope(adsr.getAttack(), newDecay, newSustain, adsr.getRelease());
		Update();
	}
	
	//SUSTAIN_RELEASE MOVEMENT
	//TODO: extract SUSTAIN stuff to separate function?
	private void SetSustainReleaseXY(float x, float y)
	{
		int ms = Width2MS(getWidth()-x);
		float atten = Height2Sustain(y);
		
		int newRelease = 0;
		float newSustain = 1.0f;
		
		
		if (ms >= MAX_MS)
		{
			newRelease = MAX_MS;
		} else if (ms <= 0)
		{
			newRelease = 0;
		} else {
			newRelease = ms;
		}
		
		if (atten >= 1.0f)
		{
			newSustain = 1.0f;
		} else if (atten <= 0)
		{
			newSustain = 0;
		} else {
			newSustain = atten;
		}
		
		adsr = new ADSREnvelope(adsr.getAttack(), adsr.getDecay(), newSustain, newRelease);
		Update();
	}
	
	public void SetADSR(ADSREnvelope adsr)
	{
		if(adsr != this.adsr)
		{
			this.adsr = adsr;
			Update();
		}
	}
	
	public ADSREnvelope GetADSR()
	{
		return adsr;
	}
	
	public void NoteOn()
	{
		startTime = System.currentTimeMillis();
		isPlaying = true;
	}
	
	public void NoteOff()
	{
		startTime = System.currentTimeMillis();
		isPlaying = false;
	}
	
	private void Update()
	{
		attackDecayCircle = new RectF(	MS2Width(adsr.getAttack())-SELECTION_CIRCLE_RADIUS, GetVisualizationTop()-SELECTION_CIRCLE_RADIUS,
										MS2Width(adsr.getAttack())+SELECTION_CIRCLE_RADIUS, GetVisualizationTop()+SELECTION_CIRCLE_RADIUS);
		decaySustainCircle = new RectF(	SustainLeft()-SELECTION_CIRCLE_RADIUS, SustainHeight()-SELECTION_CIRCLE_RADIUS,
										SustainLeft()+SELECTION_CIRCLE_RADIUS, SustainHeight()+SELECTION_CIRCLE_RADIUS);
		sustainReleaseCircle = new RectF(	SustainRight()-SELECTION_CIRCLE_RADIUS, SustainHeight()-SELECTION_CIRCLE_RADIUS,
											SustainRight()+SELECTION_CIRCLE_RADIUS, SustainHeight()+SELECTION_CIRCLE_RADIUS);
		if(mainActivity != null)
		{
			mainActivity.SetADSR(adsr);
		}
		
		this.invalidate();
	}
	
	private void UpdatePlayBar()
	{
		playPosition = System.currentTimeMillis() - startTime;
		if(isPlaying)
		{
			playBarX = MS2Width(playPosition);
			if (playBarX > SustainRight())
			{
				playBarX = SustainLeft() + ((int)(MS2Width(playPosition)-SustainLeft()))%((int)SustainWidth());
			}
		} else {
			playBarX = MS2Width(playPosition)+SustainRight();
		}
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
	
	private float SustainHeight() { return SustainHeight(adsr.getSustain()); }
	private float SustainHeight(float sus)
	{
		return (1.0f-sus)*GetVisualizationHeight()+GetVisualizationTop(); 
	}
	
	private float Height2Sustain(float y)
	{
		return 1.0f - (y/GetVisualizationHeight());
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
		//DRAW MAIN VISUALIZATION
		
		//bg
		c.drawRect(0, GetVisualizationTop(), getWidth(), GetVisualizationBottom(), bgPaint);
		
		//attack
		c.drawLine(0, GetVisualizationBottom(), MS2Width(adsr.getAttack()), GetVisualizationTop(), linePaint);
		
		//decay
		c.drawLine(MS2Width(adsr.getAttack()), GetVisualizationTop(), SustainLeft(), SustainHeight(), linePaint);
		
		//sustain
		int nDottedLinesForSustain = (int) (nDottedLinesForSustainPerPx*SustainWidth());
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
		c.drawLine(SustainRight(), SustainHeight(), getWidth(), GetVisualizationBottom(), linePaint);		
		
		//selection circles
		c.drawOval(attackDecayCircle, circleBGPaint);
		c.drawOval(attackDecayCircle, circleStrokePaint);
		c.drawOval(decaySustainCircle, circleBGPaint);
		c.drawOval(decaySustainCircle, circleStrokePaint);
		c.drawOval(sustainReleaseCircle, circleBGPaint);
		c.drawOval(sustainReleaseCircle, circleStrokePaint);
		
		//draw playhead
		c.drawLine(playBarX, GetVisualizationTop(), playBarX, GetVisualizationBottom(), playHeadPaint);
		
		
		//DRAW NUMERIC TOP
		
		//lines
		c.drawLine(NUMERIC_LINE_STROKE_WIDTH/2, GetNumericTop(), 2/NUMERIC_LINE_STROKE_WIDTH, GetNumericBottom(), numericDisplayPaint);
		c.drawLine(NUMERIC_LINE_STROKE_WIDTH/2, GetNumericHorizontalLineHeight(), MS2Width(adsr.getAttack()), GetNumericHorizontalLineHeight(), numericDisplayPaint);
		c.drawLine(MS2Width(adsr.getAttack()), GetNumericTop(), MS2Width(adsr.getAttack()), GetNumericBottom(), numericDisplayPaint);
		c.drawLine(MS2Width(adsr.getAttack()), GetNumericHorizontalLineHeight(), SustainLeft(), GetNumericHorizontalLineHeight(), numericDisplayPaint);
		c.drawLine(SustainLeft(), GetNumericTop(), SustainLeft(), GetNumericBottom(), numericDisplayPaint);
		c.drawLine(SustainRight(), GetNumericTop(), SustainRight(), GetNumericBottom(), numericDisplayPaint);
		c.drawLine(SustainRight(), GetNumericHorizontalLineHeight(), getWidth(), GetNumericHorizontalLineHeight(), numericDisplayPaint);
		c.drawLine(getWidth()-NUMERIC_LINE_STROKE_WIDTH/2, GetNumericTop(), getWidth()-NUMERIC_LINE_STROKE_WIDTH/2, GetNumericBottom(), numericDisplayPaint);
		
		//text
		c.drawText(adsr.getAttackString(), MS2Width(adsr.getAttack())/2, GetNumericTextHeight(), numericDisplayTextPaint);
		c.drawText(adsr.getDecayString(), MS2Width(adsr.getAttack())+MS2Width(adsr.getDecay())/2, GetNumericTextHeight(), numericDisplayTextPaint);
		c.drawText(adsr.getSustainString(), SustainLeft() + SustainWidth()/2, GetNumericTextHeight(), numericDisplayTextPaint);
		c.drawText(adsr.getReleaseString(), SustainRight() + MS2Width(adsr.getRelease())/2, GetNumericTextHeight(), numericDisplayTextPaint);
		
	}
	
	
	class PlayBarTask extends TimerTask
	{

		@Override
		public void run()
		{
			uiHandler.post(new Runnable() {
									@Override
									public void run() {
										UpdatePlayBar();
									}
							});
		}
		
	}
}
