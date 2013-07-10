package org.spin.mhive.replay;

import java.util.LinkedList;
import java.util.List;

import org.spin.mhive.ADSREnvelope;

public class HapticNote extends LinkedList<HapticNoteRecord>
{
	static int counter = 1;
	
	public static HapticNote NewIncrementedHapticNote(String name, ADSREnvelope adsrEnvelope, int waveform, boolean adsrEnabled)
	{
		name = name + counter;
		counter += 1;
		return new HapticNote(name, adsrEnvelope, waveform, adsrEnabled);
	}
	
	
	
	String name;
	
	public HapticNote(String name, ADSREnvelope adsrEnvelope, int waveform, boolean adsrEnabled)
	{
		this.name = name;

		add(new HapticNoteRecordADSR(0, adsrEnvelope));
		add(new HapticNoteRecordWaveform(0, waveform));
		add(new HapticNoteRecordEnableADSR(0, adsrEnabled));
	}
	
	
	public String GetName()
	{
		return name;
	}
	
	public void SetName(String newname)
	{
		name = newname;
	}
	
	public void AddRecord(HapticNoteRecord record)
	{
		add(record);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
}
