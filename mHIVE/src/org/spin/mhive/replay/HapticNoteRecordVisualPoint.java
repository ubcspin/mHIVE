package org.spin.mhive.replay;

import org.spin.mhive.HIVEAudioGenerator;
import org.spin.mhive.VisualTraceView;

public class HapticNoteRecordVisualPoint extends HapticNoteRecord implements Runnable {

	float x, y;
	VisualTraceView vtv;
	public HapticNoteRecordVisualPoint(long dt, float x, float y)
	{
		super(dt);
		this.x = x;
		this.y = y;
	}

	@Override
	public void PerformAction(HIVEAudioGenerator audiogen)
	{
		audiogen.ReplayVisualPoint(this);
		vtv = audiogen.GetVisualTraceView();
		if(vtv != null)
		{
			vtv.GetUIHandler().post(this);
		}
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}

	@Override
	public void run()
	{
		vtv.addPoint(x, y);
	}

}
