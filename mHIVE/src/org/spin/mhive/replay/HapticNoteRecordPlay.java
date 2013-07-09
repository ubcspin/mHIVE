package org.spin.mhive.replay;

import org.spin.mhive.HIVEAudioGenerator;

public class HapticNoteRecordPlay extends HapticNoteRecord
{

	int frequency;
	public float target_amplitude;

	public HapticNoteRecordPlay(long dt, float target_amplitude, int frequency) {
		super(dt);
		this.target_amplitude = target_amplitude;
		this.frequency = frequency;
	}

	@Override
	public void PerformAction(HIVEAudioGenerator audiogen) {
		audiogen.Play(frequency, target_amplitude);
	}
	
}