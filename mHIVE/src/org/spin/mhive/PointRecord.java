package org.spin.mhive;

public class PointRecord implements Comparable<PointRecord>
{
	public long t;
	public float x, y;
	public PointRecord(long t, float x, float y)
	{
		this.t = t;
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public int compareTo(PointRecord another) {
		return (int) (t - another.t);
	}
}