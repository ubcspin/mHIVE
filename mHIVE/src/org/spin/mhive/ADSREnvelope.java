package org.spin.mhive;

public class ADSREnvelope
{
	private float attack; //in ms
	private float decay; //in ms
	private float sustain; //level, from 0-1
	private float release; //in ms
	
	public float getAttack() {return attack;}
	public float getDecay() {return decay;}
	public float getSustain() {return sustain;}
	public float getRelease() {return release;}
	
	public ADSREnvelope(float attack, float decay, float sustain, float release)
	{
		this.attack = attack;
		this.decay = decay;
		this.sustain = sustain;
		this.release = release;
	}
	

}
