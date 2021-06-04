package mqtt.detect;

public class Process {
	public static native boolean isRunning(String processName);
	//public static native boolean programIsInstalled(String programName);
	public static native String processList();
	public static native void kill(String processName);
	public static native String getRealProcessName(String processName);

	static {
		System.loadLibrary("Detector4");
	}
}
