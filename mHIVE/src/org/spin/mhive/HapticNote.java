package org.spin.mhive;

import java.util.LinkedList;
import java.util.List;

public class HapticNote
{
	String name;
	List<HapticNoteRecord> records = new LinkedList<HapticNoteRecord>();
	
	public HapticNote(String name)
	{
		this.name = name;
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
}
