package org.spin.mhive;

import org.fmod.FMODAudioDevice;
import org.spin.mhive.replay.HapticNote;
import org.spin.mhive.replay.HapticNoteRecord;
import org.spin.mhive.replay.HapticNoteRecordADSR;
import org.spin.mhive.replay.HapticNoteRecordEnableADSR;
import org.spin.mhive.replay.HapticNoteRecordPlay;
import org.spin.mhive.replay.HapticNoteRecordStop;
import org.spin.mhive.replay.HapticNoteRecordWaveform;

import android.R;
import java.lang.UnsupportedOperationException;
import java.util.Observable;

//Handles audio generation for mHIVE
public class HIVEAudioGenerator extends Observable
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
    private boolean currentlyRecording = false;
    private HapticNote recordingNote;
    private long previousRecordTime = 0L;
	
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
			if(currentlyRecording)
			{
	    		long currentTime = System.currentTimeMillis();
	    		recordingNote.AddRecord(new HapticNoteRecordWaveform(currentTime - previousRecordTime, waveform));
	    		previousRecordTime = currentTime;
			}
			setChanged();
			notifyObservers();
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
		
		if(currentlyRecording)
    	{
    		long currentTime = System.currentTimeMillis();
    		recordingNote.AddRecord(new HapticNoteRecordPlay(currentTime - previousRecordTime, atten, freq));
    		previousRecordTime = currentTime;
    	}

	}
	
	public void Stop()
	{
		if(playing)
		{
			//cSetChannelVolume(0);
	    	playing = false;
	    	cNoteOff();
		}
		
		if(currentlyRecording)
		{
    		long currentTime = System.currentTimeMillis();
    		recordingNote.AddRecord(new HapticNoteRecordStop(currentTime - previousRecordTime));
    		previousRecordTime = currentTime;
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
	
	public void StartRecording()
	{
		recordingNote = HapticNote.NewIncrementedHapticNote("Recording ", GetADSR(), getCurrentWaveform(), cGetADSREnabled());
		previousRecordTime = System.currentTimeMillis();
		currentlyRecording = true;
	}
	
	public HapticNote StopRecording()
	{
		currentlyRecording = false;
		return recordingNote;
	}
	
	
	public void Replay(HapticNote hapticNote)
	{
		//if(!currentlyRecording)
		//{
			StopReplay();
			replayThread = new ReplayThread(this, hapticNote);
			replayThread.start();
		//}
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
		if (b != GetADSREnabled())
		{
			setChanged();
			cSetADSREnabled(b);
		}
		if(currentlyRecording)
		{
    		long currentTime = System.currentTimeMillis();
    		recordingNote.AddRecord(new HapticNoteRecordEnableADSR(currentTime - previousRecordTime, b));
    		previousRecordTime = currentTime;
		}
		notifyObservers();
	}
	
	public boolean GetADSREnabled()
	{
		return cGetADSREnabled();
	}
	
	
	public void SetADSR(ADSREnvelope envelope) {SetADSR(envelope.getAttack(), envelope.getDecay(), envelope.getSustain(), envelope.getRelease());}
	public void SetADSR(int attack, int decay, float sustain, int release)
	{
		cSetADSR(attack, decay, sustain, release);
		if(currentlyRecording)
		{
    		long currentTime = System.currentTimeMillis();
    		recordingNote.AddRecord(new HapticNoteRecordADSR(currentTime - previousRecordTime, new ADSREnvelope(attack, decay, sustain, release)));
    		previousRecordTime = currentTime;
		}
		setChanged();
		notifyObservers();
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
	public native boolean cGetADSREnabled();
	public native int cGetADSRAttack();
	public native int cGetADSRDecay();
	public native float cGetADSRSustain();
	public native int cGetADSRRelease();

}
