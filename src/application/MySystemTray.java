package application;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MySystemTray {
	private static TrayIcon trayIcon;
	
	public static void createTrayIcon(final Stage stage) {
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();

			// load an image
			Image image = new ImageIcon("image/meerkat1.png").getImage();
			image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
			/*
			 * java.awt.Image image = null; try { URL url = new
			 * URL("http://www.digitalphotoartistry.com/rose1.jpg"); image =
			 * ImageIO.read(url); } catch (IOException ex) { System.out.println(ex); }
			 */

			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					hide(stage);
				}
			});

			// create a action listener to listen for default action executed on the tray
			// icon
			final ActionListener closeListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Main.mqttPublish.updateTopic("alive");
					Main.mqttPublish.publish("n");
					System.exit(0);
				}
			};

			ActionListener showListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.show();
						}
					});
				}
			};
			// create a popup menu
			PopupMenu popup = new PopupMenu();

			MenuItem showItem = new MenuItem("Show");
			showItem.addActionListener(showListener);
			popup.add(showItem);

			MenuItem closeItem = new MenuItem("Close");
			closeItem.addActionListener(closeListener);
			//popup.add(closeItem);
			/// ... add other items

			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "Meerkat", popup);
			// set the TrayIcon properties
			trayIcon.addActionListener(showListener);
			// ...
			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}
			// ...
		}
	}

	public static void showProgramIsMinimizedMsg() {
		if (Main.firstTime) {
			trayIcon.displayMessage("미어캣 최소화", "아직 감지 중", TrayIcon.MessageType.INFO);
			Main.firstTime = false;
		}
	}

	public static void showProgramIsStartedMsg() {
		trayIcon.displayMessage("미어캣", "미허용 소프트웨어 감지를 시작합니다.", TrayIcon.MessageType.INFO);
	}
	
	public static void showProcessKilledMsg(String processName) {
		trayIcon.displayMessage("미어캣", "미 허용 소프트웨어 " + processName + "이(가) 종료됩니다.", TrayIcon.MessageType.INFO);
	}
	
	// 사용을 막을 때 메시지 (실행 시 바로 종료)
	public static void showCannotUseMsg(String processName) {
		trayIcon.displayMessage("미어캣", "미 허용 소프트웨어 " + processName + "은(는) 사용할 수 없습니다.", TrayIcon.MessageType.INFO);
	}
	
	public static void showDetectedMsg(String processName) {
		trayIcon.displayMessage("미어캣", "미 허용 소프트웨어 " + processName + "이(가) 감지되었습니다.", TrayIcon.MessageType.INFO);
	}

	public static void hide(final Stage stage) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (SystemTray.isSupported()) {
					stage.hide();
					showProgramIsMinimizedMsg();
				} else {
					System.exit(0);
				}
			}
		});
	}
}
