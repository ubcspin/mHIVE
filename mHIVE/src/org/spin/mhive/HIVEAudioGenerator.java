package org.spin.mhive;

import org.fmod.FMODAudioDevice;

//Handles audio generation for mHIVE
public class HIVEAudioGenerator
{
	
    private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

    private final int OSCILLATOR_SINE = 0;
    private final int OSCILLATOR_SQUARE = 1;
    private final int OSCILLATOR_SAWUP = 2;
    private final int OSCILLATOR_TRIANGLE = 4;
	
	private boolean initiated = false;
	private boolean playing = false;
	public final String TAG = "HIVEAudioGenerator";
	
	static {
    	System.loadLibrary("fmodex");
    	System.loadLibrary("main");
	}
	
	
	public HIVEAudioGenerator()
	{	
		initiated = true;
		mFMODAudioDevice.start();
		cBegin();
		cSetWaveform(OSCILLATOR_SINE);
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
