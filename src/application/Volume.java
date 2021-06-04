package application;

public class Volume {
	public static double volume;
	public static native void volumeControl(double volume);
	
	static {
		System.loadLibrary("Volume");
	}
}
