package mqtt.trace;

public class Trace extends TraceFrame {
	public Trace() {
	}

	static public TraceFrame getInstance() {
		if (traceFrame == null) {
			traceFrame = new Trace();
			return traceFrame;
		} else {
			return traceFrame;
		}
	}
	
	public static void pubPrint(String str) {
		if (pubText.getText().equals("")) {
			pubText.append(str);
		} else {
			pubText.append("\n" + str);
		}
		pubScroll.getVerticalScrollBar().setValue(pubScroll.getVerticalScrollBar().getMaximum());

		//스크롤바 위치 조정(하단으로)
		int pos = pubText.getText().length();
		pubText.setCaretPosition(pos);
		//pubText.requestFocus();

	}

	public static void subPrint(String str) {
		if (subText.getText().equals("")) {
			subText.append(str);
		} else {
			subText.append("\n" + str);
		}

		// 스크롤바 위치 조정(하단으로)
		int pos = subText.getText().length();
		subText.setCaretPosition(pos);
		//subText.requestFocus();
	}
}
