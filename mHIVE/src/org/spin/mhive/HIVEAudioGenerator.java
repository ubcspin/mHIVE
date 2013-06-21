package org.spin.mhive;

import org.fmod.FMODAudioDevice;

import android.R;
import java.lang.UnsupportedOperationException;;

//Handles audio generation for mHIVE
public class HIVEAudioGenerator
{
	
    private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

    //These are integers for tighter integration with JNI
    //ENUM would be better, but unfortunately
    public static final int OSCILLATOR_SINE = 0;
    public static final int OSCILLATOR_SQUARE = 1;
    public static final int OSCILLATOR_SAWUP = 2;
    public static final int OSCILLATOR_TRIANGLE = 4;
    private int currentWaveform = OSCILLATOR_SINE;
	
	private boolean initiated = false;
	private boolean playing = false;
	public final String TAG = "HIVEAudioGenerator";
	
	static {
    	System.loadLibrary("fmodex");
    	System.loadLibrary("main");
	}
	
	
	public void setWaveform(int waveform)
	{
		if(		waveform == OSCILLATOR_SINE
				|| waveform == OSCILLATOR_SQUARE
				|| waveform == OSCILLATOR_SAWUP
				|| waveform == OSCILLATOR_TRIANGLE)
		{
			currentWaveform = waveform;
			cSetWaveform(currentWaveform);
			cSetChannelVolume(0);
		}
	}
	
	public int getCurrentWaveform()
	{
		return currentWaveform;
	}
	
	public String getCurrentWaveformName()
	{
		//TODO: Access resources for this
		switch(currentWaveform)
		{
			case OSCILLATOR_SINE:
				return "Sine";
			case OSCILLATOR_SQUARE:
				return "Square";
			case OSCILLATOR_SAWUP:
				return "SawUp";
			case OSCILLATOR_TRIANGLE:
				return "Triangle";
		}
		return "Error - unsupported waveform for waveform "+currentWaveform;
	}
	
	public HIVEAudioGenerator()
	{	
		initiated = true;
		mFMODAudioDevice.start();
		cBegin();
		cSetWaveform(currentWaveform);
		cSetChannelVolume(0);
	}
	
	
	public void Play(int freq, float atten)
	{
		if(!playing)
		{
			playing = true;
		}
    	
		cSetChannelFrequency(freq);
		cSetChannelVolume(atten);

	}
	
	public void Stop()
	{
		if(playing)
		{
			cSetChannelVolume(0);
	    	playing = false;
		}

	}
	
	public void Close()
	{
		Stop();
		cEnd();
    	mFMODAudioDevice.stop();
	}
	
	
	/**
	 * ADSR STUB METHODS
	 */
	public void EnableADSR() {EnableADSR(true);}
	public void DisableADSR() {EnableADSR(false);}
	public void EnableADSR(boolean b)
	{
		throw new UnsupportedOperationException("EnableADSR is a stub method");
	}
	
	public void SetADSR(ADSREnvelope envelope) {SetADSR(envelope.getAttack(), envelope.getDecay(), envelope.getSustain(), envelope.getRelease());}
	public void SetADSR(float attack, float decay, float sustain, float release)
	{
		throw new UnsupportedOperationException("SetADSR is a stub method");
	}
	
	public ADSREnvelope GetADSR(float attack, float decay, float sustain, float release)
	{
		throw new UnsupportedOperationException("GetADSR is a stub method");
	}
	
	/**
	 * Native Methods
	 */
	public native void cBegin();
	public native void cUpdate();
	public native void cEnd();
	
	public native void cSetWaveform(int waveform);	
	
	public native boolean cGetIsChannelPlaying();
	public native float cGetChannelFrequency();
	public native float cGetChannelVolume();
	public native float cGetChannelPan();
	
	public native void cSetChannelFrequency(float frequency);
	public native void cSetChannelVolume(float volume);
	public native void cSetChannelPan(float pan);

}
