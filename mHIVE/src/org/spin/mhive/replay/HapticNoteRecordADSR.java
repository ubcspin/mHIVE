package org.spin.mhive.replay;

import org.spin.mhive.ADSREnvelope;
import org.spin.mhive.HIVEAudioGenerator;

public class HapticNoteRecordADSR extends HapticNoteRecord
{
	private ADSREnvelope adsrEnvelope;

	public HapticNoteRecordADSR(long dt, ADSREnvelope adsrEnvelope)
	{
		super(dt);
		this.adsrEnvelope = adsrEnvelope;
	}

	@Override
	public void PerformAction(HIVEAudioGenerator audiogen)
	{
		audiogen.SetADSR(adsrEnvelope);
	}

}
