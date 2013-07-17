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
	
	private String attackString;
	private String decayString;
	private String sustainString;
	private String releaseString;
	
	public String getAttackString() {return attackString;}
	public String getDecayString() {return decayString;}
	public String getSustainString() {return sustainString;}
	public String getReleaseString() {return releaseString;}
	
	private String[] stringArray = null;

	
	public ADSREnvelope(int attack, int decay, float sustain, int release)
	{
		this.attack = attack;
		this.decay = decay;
		this.sustain = sustain;
		this.release = release;
		
		attackString = ""+attack+"ms";
		decayString = ""+decay+"ms";
		sustainString = ""+Math.round(sustain*100)/100.0f;
		releaseString = ""+release+"ms";
		
		stringArray = new String[] {getAttackString(), getDecayString(), getSustainString(),getReleaseString()};
	}
	
	public String[] GetStringArray()
	{
		return stringArray;
	}
	

}
