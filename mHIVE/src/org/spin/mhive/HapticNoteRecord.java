package org.spin.mhive;

/**
 * Simple storage class for haptic note components
 * Used to replay haptic notes
 * @author oli
 *
 */
public class HapticNoteRecord {
	public long dt; //time BEFORE this record
	int frequency;
	public float x, y, target_amplitude;
	
	public HapticNoteRecord(long dt, float x,  float y, float target_amplitude, int frequency)
	{
		this.dt = dt;
		this.x = x;
		this.y = y;
		this.target_amplitude = target_amplitude;
		this.frequency = frequency;
	}

}
