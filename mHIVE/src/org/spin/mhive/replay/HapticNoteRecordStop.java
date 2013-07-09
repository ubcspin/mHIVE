package org.spin.mhive.replay;

import org.spin.mhive.HIVEAudioGenerator;

public class HapticNoteRecordStop extends HapticNoteRecord
{
	public HapticNoteRecordStop(long dt) {
		super(dt);
	}

	@Override
	public void PerformAction(HIVEAudioGenerator audiogen) {
		audiogen.Stop();
	}
	
}