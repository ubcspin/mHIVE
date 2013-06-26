package org.spin.mhive;

public class ADSREnvelope
{
	private int attack; //in ms
	private int decay; //in ms
	private float sustain; //level, from 0-1
	private int release; //in ms
	
	public int getAttack() {return attack;}
	public int getDecay() {return decay;}
	public float getSustain() {return sustain;}
	public int getRelease() {return release;}
	
	public ADSREnvelope(int attack, int decay, float sustain, int release)
	{
		this.attack = attack;
		this.decay = decay;
		this.sustain = sustain;
		this.release = release;
	}
	

}
