package org.spin.mhive;

import org.fmod.FMODAudioDevice;
import org.spin.mhive.replay.HapticNote;
import org.spin.mhive.replay.HapticNoteRecord;
import org.spin.mhive.replay.HapticNoteRecordADSR;
import org.spin.mhive.replay.HapticNoteRecordEnableADSR;
import org.spin.mhive.replay.HapticNoteRecordPlay;
import org.spin.mhive.replay.HapticNoteRecordStop;
import org.spin.mhive.replay.HapticNoteRecordVisualPoint;
import org.spin.mhive.replay.HapticNoteRecordWaveform;

import android.R;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.UnsupportedOperationException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

//Handles audio generation for mHIVE
public class HIVEAudioGenerator extends Observable
{
	//log to file?
	private final boolean LOGGING = true;
	private long LOG_START_TIME = 0;
	private PrintWriter outWriter;
	
    private FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

    //These are integers for tighter integration with JNI
    //ENUM would be better, but unfortunately
    public static final int OSCILLATOR_SINE = 0;
    public static final int OSCILLATOR_SQUARE = 1;
    public static final int OSCILLATOR_SAWUP = 2;
    public static final int OSCILLATOR_TRIANGLE = 4;
    private int currentWaveform = OSCILLATOR_SINE;
    private final float[] waveformScaleFactor = {1.0f, 1.0f, 1.0f, 0.0f, 1.0f};//{0.8f, 0.1f, 0.1f, 0.0f, 1.0f}; //balancing overall output
	
	private boolean initiated = false;
	private boolean playing = false;
	public final String TAG = "HIVEAudioGenerator";
	ADSREnvelope currentADSREnvelope;
	
	ReplayThread replayThread;
    private boolean currentlyRecording = false;
    private HapticNote recordingNote;
    private long previousRecordTime = 0L;
    VisualTraceView visualTraceView;
    ADSRView adsrView;
	
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
		Log("SETWAVEFORM", new String[] {getCurrentWaveformName()});
		
		
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
		if(LOGGING)
		{
			SetupLogging();
		}
		initiated = true;
		mFMODAudioDevice.start();
		cBegin();
		cSetWaveform(currentWaveform);
		cSetChannelVolume(0);
		SetADSR(new ADSREnvelope(100, 100, 0.5f, 300));
		EnableADSR();
	}
	
	private void SetupLogging()
	{
		//set up output file (comma separated value text file)
		//date formats for directory and file name
		//file will have the day, hour, minute, and second as part of the file name 
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMMdd-hh-mm-ss");
		Date date = new Date();
		
		//set up the file name
		String fileString = "mHIVE_"+dateFormat.format(date)+".csv";
		
		//create directory
		File d = new File(Environment.getExternalStorageDirectory() + "/" + "mHIVE");
		if(!d.exists())
		{
			d.mkdirs();
		}
		File f = new File(Environment.getExternalStorageDirectory() + "/" + "mHIVE" + "/" + fileString);
		
		try {
			outWriter = new PrintWriter(f);
			LOG_START_TIME = System.currentTimeMillis();
			outWriter.println("START,"+LOG_START_TIME);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void Log(String cmd) {Log(cmd, null);}
	private void Log(String cmd, String[] args)
	{
		if (LOGGING)
		{
			long t = System.currentTimeMillis() - LOG_START_TIME;
			String logString = ""+t+","+cmd;
			if (args != null)
			{
				for (String arg : args)
				{
					logString = logString + "," + arg;
				}
			}
			outWriter.println(logString);
			outWriter.flush();
		}
	}
	
	public void SetVisualTraceView(VisualTraceView vtv)
	{
		visualTraceView = vtv;
	}
	
	public void SetADSRView(ADSRView adsrView)
	{
		this.adsrView = adsrView;
	}
	
	public VisualTraceView GetVisualTraceView()
	{
		return visualTraceView;
	}	
	
	public void ReplayVisualPoint(HapticNoteRecordVisualPoint vp)
	{
		if (visualTraceView != null)
		{
			visualTraceView.GetUIHandler().post(vp);
		}
		if (currentlyRecording)
		{
			long currentTime = System.currentTimeMillis();
    		recordingNote.AddRecord(new HapticNoteRecordVisualPoint(currentTime - previousRecordTime, vp.getX(), vp.getY()));
    		previousRecordTime = currentTime;
		}
	}
	
	
	public void Play(int freq, float atten)
	{
		if(!playing)
		{
			playing = true;
			cNoteOn();
	    	if(adsrView != null)
	    	{
	    		adsrView.NoteOn();
	    	}
		}
    	
		cSetChannelFrequency(freq);
		cSetChannelVolume(atten*waveformScaleFactor[currentWaveform]);
		
		if(currentlyRecording)
    	{
    		long currentTime = System.currentTimeMillis();
    		recordingNote.AddRecord(new HapticNoteRecordPlay(currentTime - previousRecordTime, atten, freq));
    		previousRecordTime = currentTime;
    	}
		Log("PLAY", new String[] {""+freq, ""+atten});

	}
	
	
	public void Play(int freq, float atten, float x, float y)
	{
		Play(freq, atten);
		if(currentlyRecording)
    	{
    		long currentTime = System.currentTimeMillis();
    		recordingNote.AddRecord(new HapticNoteRecordVisualPoint(currentTime - previousRecordTime, x, y));
    		previousRecordTime = currentTime;
    	}
		Log("PLAY_XY", new String[] {""+freq, ""+atten, ""+x, ""+y});
	}
	
	public void Stop()
	{
		if(playing)
		{
			//cSetChannelVolume(0);
	    	playing = false;
	    	cNoteOff();
	    	if(adsrView != null)
	    	{
	    		adsrView.NoteOff();
	    	}
		}
		
		if(currentlyRecording)
		{
    		long currentTime = System.currentTimeMillis();
    		recordingNote.AddRecord(new HapticNoteRecordStop(currentTime - previousRecordTime));
    		previousRecordTime = currentTime;
		}
		
		Log("STOP");
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
		Log("REPLAY");
	}
	
	public void StopReplay()
	{
		if(null != replayThread)
		{
			replayThread.stopPlaying();
		}
		Log("STOPREPLAY");
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
			
			Stop();
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
		Log("ENABLEADSR", new String[] {""+b});
		notifyObservers();
	}
	
	public boolean GetADSREnabled()
	{
		return cGetADSREnabled();
	}
	
	
	public void SetADSR(ADSREnvelope envelope)
	{
		if (envelope != currentADSREnvelope && envelope != null)
		{
			currentADSREnvelope = envelope;
			cSetADSR(currentADSREnvelope.getAttack(), currentADSREnvelope.getDecay(), currentADSREnvelope.getSustain(), currentADSREnvelope.getRelease());
			if(currentlyRecording)
			{
	    		long currentTime = System.currentTimeMillis();
	    		recordingNote.AddRecord(new HapticNoteRecordADSR(currentTime - previousRecordTime, currentADSREnvelope));
	    		previousRecordTime = currentTime;
			}
			setChanged();
			notifyObservers();
			Log("SETADSR", currentADSREnvelope.GetStringArray());
		}
	}
	
	public ADSREnvelope GetADSR()
	{
		return currentADSREnvelope;
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

	public void Synchronize()
	{
		Log("SYNCHRONIZE");
	}

}
