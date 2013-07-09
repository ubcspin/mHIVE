package org.spin.mhive.replay;

import org.spin.mhive.HIVEAudioGenerator;

public class HapticNoteRecordEnableADSR extends HapticNoteRecord {
	
	boolean enabled;

	public HapticNoteRecordEnableADSR(long dt, boolean enabled)
	{
		super(dt);
		this.enabled = enabled;
	}

	@Override
	public void PerformAction(HIVEAudioGenerator audiogen)
	{
		audiogen.EnableADSR(enabled);
	}

}
