package mqtt.detect;

public class RegistryHandler {
	public static native void addReg(String processkey, String processData); // 레지스트리 추가
	public static native void deleteReg(String process); // 레지스트리 삭제
	public static native void autoStartReg(String path); 

	static {
		System.loadLibrary("RegistryHandler");
	}
}
