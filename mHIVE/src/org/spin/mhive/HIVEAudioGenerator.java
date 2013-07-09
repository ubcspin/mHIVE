package org.spin.mhive;

import org.fmod.FMODAudioDevice;
import org.spin.mhive.replay.HapticNote;
import org.spin.mhive.replay.HapticNoteRecord;

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
	
	ReplayThread replayThread;
	
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
		SetADSR(100, 100, 0.5f, 300);
		EnableADSR();
	}
	
	
	public void Play(int freq, float atten)
	{
		if(!playing)
		{
			playing = true;
			cNoteOn();
		}
    	
		cSetChannelFrequency(freq);
		cSetChannelVolume(atten);

	}
	
	public void Stop()
	{
		if(playing)
		{
			//cSetChannelVolume(0);
	    	playing = false;
	    	cNoteOff();
		}

	}
	
	public void Close()
	{
		Stop();
		cEnd();
    	mFMODAudioDevice.stop();
	}
	
	/*
	 *REPLAYING METHODS AND CLASSES 
	 */
	public void Replay(HapticNote hapticNote)
	{
		StopReplay();
		replayThread = new ReplayThread(this, hapticNote);
		replayThread.start();
	}
	
	public void StopReplay()
	{
		if(null != replayThread)
		{
			replayThread.stopPlaying();
		}
	}
	
	class ReplayThread extends Thread
	{
		
		HapticNote hapticNote;
		HIVEAudioGenerator parent;
		boolean running = true;
		
		public ReplayThread(HIVEAudioGenerator parent, HapticNote hapticNote)
		{
			this.parent = parent;
			this.hapticNote = hapticNote;
		}
		
		@Override
		public void run()
		{
			Stop();
			SetADSR(hapticNote.GetADSREnvelope());
			setWaveform(hapticNote.GetWaveform()); //TODO: Sloppy naming conventions...
			
			for (HapticNoteRecord hnr : hapticNote)
			{
				try {
					sleep(hnr.dt);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				hnr.PerformAction(parent);
				
				if(!running)
				{
					break; //TODO: break should probably be reworked
				}
			}
			
//			Stop();
		}
		
		public void stopPlaying()
		{
			running = false;
		}
	}
	
	
	/**
	 * ADSR STUB METHODS
	 */
	public void EnableADSR() {EnableADSR(true);}
	public void DisableADSR() {EnableADSR(false);}
	public void EnableADSR(boolean b)
	{
		cSetADSREnabled(b);
	}
	
	public void SetADSR(ADSREnvelope envelope) {SetADSR(envelope.getAttack(), envelope.getDecay(), envelope.getSustain(), envelope.getRelease());}
	public void SetADSR(int attack, int decay, float sustain, int release)
	{
		cSetADSR(attack, decay, sustain, release);
	}
	
	public ADSREnvelope GetADSR()
	{
		return new ADSREnvelope(cGetADSRAttack(),cGetADSRDecay(),cGetADSRSustain(),cGetADSRRelease());
	}
	
	/**
	 * Native Methods
	 */

	//Fundamentals
	public native void cBegin();
	public native void cUpdate();
	public native void cEnd();
	
	//Basic controls
	public native void cSetWaveform(int waveform);	
	public native boolean cGetIsChannelPlaying();
	public native float cGetChannelFrequency();
	public native float cGetChannelVolume();
	public native float cGetChannelPan();
	public native void cSetChannelFrequency(float frequency);
	public native void cSetChannelVolume(float volume);
	public native void cSetChannelPan(float pan);
	
	//ADSR and note handling
	public native void cNoteOn();
	public native void cNoteOff();
	public native void cSetADSR(int attack, int decay, float sustain, int release);
	public native void cSetADSREnabled(boolean b);
	public native int cGetADSRAttack();
	public native int cGetADSRDecay();
	public native float cGetADSRSustain();
	public native int cGetADSRRelease();

}
