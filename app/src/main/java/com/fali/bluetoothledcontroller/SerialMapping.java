package com.fali.bluetoothledcontroller;


public class SerialMapping {
	public static enum SerialKeywords {
		Mode,
		WaitTime,
		Color,
		Reverse,
		Brightness,
		RandomColor,
		FadeLength
	}
		
	public static String getSerialKey(SerialKeywords keyword)
	{
		switch(keyword)
		{
			case Mode:
				return "A";
			case WaitTime:
				return "B";
			case Color:
				return "C";
			case Reverse:
				return "D";
			case Brightness:
				return "F";
			case RandomColor:
				return "G";
			case FadeLength:
				return "H";
			default:
				return "";
		}
	}
	
	public enum ValidationState {
	    kVALIDATIONFAIL(0),
		kVALIDATED(1),
		kVALIDATING(2),
		kVALIDATIONWAITING(3);
	    
	    private final int value;
	    private ValidationState(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
}
