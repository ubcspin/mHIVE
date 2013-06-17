package org.spin.mhive;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

//Singleton class for handling audio generation for mHIVE
public class HIVEAudioGenerator
{
	
	private static boolean initiated = false;
	private final static int minBufferSize = 4096;
	private static AudioThread audioThread;
	public final static double tau = 2*Math.PI;
	public final static String TAG = "HIVEAudioGenerator";
	
	public static void Init()
	{		

		initiated = true;
	}
	
	
	public static void Play(int freq, float atten)
	{
		if(!initiated)
		{
			Init();
		}
		if (audioThread != null)		
		{
			audioThread.stopPlaying();
		}
		audioThread = new AudioThread(freq, atten);
		audioThread.run();

	}
	
	public static void Stop()
	{
		if(!initiated)
		{
			Init();
		}
		
		if (audioThread != null)		
		{
			audioThread.stopPlaying();
		}
	}
	
	
	
	static class AudioThread extends Thread
	{
		private AudioTrack track;
		private int sample_rate = 44100; //Hz
		private int frequency = 440; //Hz
		private float attenuation = 1.0f;
		
		public AudioThread(int freq, float atten)
		{
			super();
			frequency = freq;
			attenuation = atten;
			this.track = new AudioTrack(
	        		AudioManager.STREAM_MUSIC,
	        		sample_rate,
	        		AudioFormat.CHANNEL_CONFIGURATION_MONO,
	        		AudioFormat.ENCODING_DEFAULT,
	        		minBufferSize,
	        		AudioTrack.MODE_STATIC);
			
			
			float samplesPerWave = (float)sample_rate/(float)frequency;
			int bufferSize = (int)samplesPerWave;
			bufferSize = bufferSize*(minBufferSize/bufferSize) + bufferSize;
			Log.v(TAG, "Buffer size: " + bufferSize);
	        	
	        float samples[] = new float[bufferSize];

	    	short buffer[] = new short[bufferSize];

	            for (int i = 0; i < samples.length; i++) {
	                samples[i] = (float) Math.sin( (float)i * (float)(tau)*frequency/sample_rate);    //the part that makes this a sine wave....
	                buffer[i] = (short) (samples[i] * Short.MAX_VALUE);
	            }
	            this.track.write( buffer, 0, samples.length );  //write to the audio buffer.... and start all over again!
		}
		
		@Override
		public void run()
		{
			this.track.setStereoVolume(attenuation, attenuation);
			this.track.setLoopPoints(0, minBufferSize/4, -1);
			this.track.play();
		}
		
		public void stopPlaying()
		{
			this.track.stop();
		}
	
	}
	
   
            

}
