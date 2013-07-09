package org.spin.mhive;

import org.spin.mhive.replay.HapticNoteRecord;

public class HapticNoteRecordWaveform extends HapticNoteRecord
{
	int waveform;

	public HapticNoteRecordWaveform(long dt, int waveform)
	{
		super(dt);
		this.waveform = waveform;
	}

	@Override
	public void PerformAction(HIVEAudioGenerator audiogen)
	{
		audiogen.setWaveform(waveform);
	}

}
