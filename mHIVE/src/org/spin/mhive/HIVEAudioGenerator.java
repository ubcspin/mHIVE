package org.spin.mhive;

import org.fmod.FMODAudioDevice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

//Singleton class for handling audio generation for mHIVE
public class HIVEAudioGenerator
{
	
    private static FMODAudioDevice mFMODAudioDevice = new FMODAudioDevice();

    private static final int OSCILLATOR_SINE = 0;
    private static final int OSCILLATOR_SQUARE = 1;
    private static final int OSCILLATOR_SAWUP = 2;
    private static final int OSCILLATOR_TRIANGLE = 4;
	
	private static boolean initiated = false;
	private static boolean playing = false;
	private final static int minBufferSize = 4096;
	public final static double tau = 2*Math.PI;
	public final static String TAG = "HIVEAudioGenerator";
	
	public static void Init()
	{	
    	System.loadLibrary("fmodex");
    	System.loadLibrary("main");
		initiated = true;
		mFMODAudioDevice.start();
		cBegin();
		cSetWaveform(0);
		//cSetChannelVolume(0);
	}
	
	
	public static void Play(int freq, float atten)
	{
		if(!initiated)
		{
			Init();
		}
		if(!playing)
		{
			playing = true;
		}
    	
		cSetChannelFrequency(freq);
		cSetChannelVolume(atten);

	}
	
	public static void Stop()
	{
		if(playing)
		{
	    	//cEnd();
	    	//mFMODAudioDevice.stop();
			cSetChannelVolume(0);
	    	playing = false;
		}

	}
	
//	static 
//    {
//    	System.loadLibrary("fmodex");
//        System.loadLibrary("main");
//    }
    
	public native static void cBegin();
	public native static void cUpdate();
	public native static void cEnd();
	
	public native static void cSetWaveform(int waveform);	
	
	public native static boolean cGetIsChannelPlaying();
	public native static float cGetChannelFrequency();
	public native static float cGetChannelVolume();
	public native static float cGetChannelPan();
	
	public native static void cSetChannelFrequency(float frequency);
	public native static void cSetChannelVolume(float volume);
	public native static void cSetChannelPan(float pan);

}
