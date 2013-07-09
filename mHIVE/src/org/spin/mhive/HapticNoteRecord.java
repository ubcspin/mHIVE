package org.spin.mhive;

/**
 * Simple storage class for haptic note components
 * Used to replay haptic notes
 * @author oli
 *
 */
public class HapticNoteRecord {
	public long dt;
	public double x, y;
	public float target_amplitude, frequency;
	
	public HapticNoteRecord(long dt, double x, double y, float target_amplitude, float frequency)
	{
		this.dt = dt;
		this.x = x;
		this.y = y;
		this.target_amplitude = target_amplitude;
		this.frequency = frequency;
	}

}
