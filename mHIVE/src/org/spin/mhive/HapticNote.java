package org.spin.mhive;

import java.util.LinkedList;
import java.util.List;

public class HapticNote
{
	String name;
	List<HapticNoteRecord> records = new LinkedList<HapticNoteRecord>();
	ADSREnvelope adsrEnvelope;
	int waveform;
	
	public HapticNote(String name, ADSREnvelope adsrEnvelope, int waveform)
	{
		this.name = name;
		this.adsrEnvelope = adsrEnvelope;
		this.waveform = waveform;
	}
	
	public void AddRecord(HapticNoteRecord record)
	{
		this.records.add(record);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public void SetADSREnvelope(ADSREnvelope adsrEnvelope)
	{
		this.adsrEnvelope = adsrEnvelope;
	}
	
	public ADSREnvelope GetADSREnvelope()
	{
		return this.adsrEnvelope;
	}
	
	public void SetWaveform(int waveform)
	{
		this.waveform = waveform;
	}
	
	public int GetWaveform()
	{
		return this.waveform;
	}
}
