package org.spin.mhive.replay;

import org.spin.mhive.HIVEAudioGenerator;

/**
 * Simple storage class for haptic note components
 * Used to replay haptic notes
 * @author oli
 *
 */
public abstract class HapticNoteRecord {
	public long dt; //time BEFORE this record
	
	public abstract void PerformAction(HIVEAudioGenerator audiogen);
	
	public HapticNoteRecord(long dt)
	{
		this.dt = dt;
	}

}