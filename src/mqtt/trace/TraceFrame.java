package mqtt.trace;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TraceFrame extends JFrame {
	protected static TraceFrame traceFrame = null;
	protected static JTextArea pubText = new JTextArea("", 10, 30);
	protected static JTextArea subText = new JTextArea("", 10, 30);
	protected static JScrollPane pubScroll = new JScrollPane(pubText);
	protected static JScrollPane subScroll = new JScrollPane(subText);

	public TraceFrame() {
		super("Trace");
		setSize(700, 800);
		setResizable(false);
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setContentPane(new TracePanel(this));
		setVisible(true);
	}

	class TracePanel extends JPanel {
		public TracePanel(TraceFrame frame) {
			this.setLayout(null);

			JLabel pubLabel = new JLabel("publish");
			pubLabel.setSize(60, 20);
			pubLabel.setLocation(25, 30);
			this.add(pubLabel);

			pubScroll.setBounds(25, 60, 640, 280);
			this.add(pubScroll);

			JLabel subLabel = new JLabel("subscribe");
			subLabel.setSize(60, 20);
			subLabel.setLocation(25, 400);
			this.add(subLabel);

			subScroll.setBounds(25, 430, 640, 280);
			this.add(subScroll);

			/*
			 * //int 형 변수에 jTextArea 객체의 텍스트의 총 길이를 저장 int pos =
			 * jTextArea.getText().length(); //caret 포지션을 가장 마지막으로 맞춤
			 * jTextArea.setCaretPosition(pos); //갱신 jTextArea.requestFocus();
			 * 
			 * 
			 * //JScrollPane의 바를 최 하단으로 맞춤
			 * jScrollPane.getVerticalScrollBar().setValue(jScrollPane.
			 * getVerticalScrollBar().getMaximum());
			 */
		}
	}

}
