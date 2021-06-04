package application;
	
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mqtt.data.ClientOption;
import mqtt.data.LoginInformation;
import mqtt.detect.RegistryHandler;
import mqtt.publish.MqttPublish;
import mqtt.subscribe.MqttSubscribe;
import mqtt.trace.Trace;

public class Main extends Application {
	public static MqttSubscribe mqttSubscribe = new MqttSubscribe();
	public static MqttPublish mqttPublish = new MqttPublish();
	public static boolean detecting; // 감지 on/off 구분하는 필드
	public static String currentMode = "A";
	public static String startTime;
	
	public static MainApplicationController controller;
	public static DetailsTableDialogController detailController;
	private static Stage primaryStage;
	private Scene scene;
	private AnchorPane root;
	private double yOffset;
	private double xOffset;
	public static boolean firstTime;
	
	
	public static void main(String[] args) {
		Font.loadFont(Main.class.getResource("NANUMBARUNPENB.TTF").toExternalForm(), 10);
		Font.loadFont(Main.class.getResource("NANUMBARUNPENR.TTF").toExternalForm(), 10);
		//Font.loadFont(Main.class.getResource("DXSJB-KSCpc-EUC-H.TTF").toExternalForm(), 10);
		
		String path = System.getProperty("user.dir");
		RegistryHandler.autoStartReg(path + "\\Meerkat.exe");
		
		Volume.volume = 0.2;
		
		new ClientOption();
		new LoginInformation();
		
		ClientOption.clientInit();
		
		Main.mqttSubscribe.subscribeRun();
		Main.mqttSubscribe.blacklistSubscribeRun();
		
		long curr = System.currentTimeMillis();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		startTime = sd.format(new Date(curr));
		
		Main.mqttPublish.publishStartMessage();
		Main.mqttPublish.checkOperation(180);// 살아있다는 메시지 publish (초 단위)
		
		Main.mqttPublish.firstDetectionPublish(); // 첫번째 감지 (실행 중인 프로세스 리스트와 미허용 리스트 비교)
		Main.mqttPublish.publishRun(); // publish
		
		//Trace.getInstance();
		launch(args);
		
		
		// 프로그램 종료 직전에 hansung/alive/// 로 "n;" publish
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Main.mqttPublish.updateTopic("alive");
				Main.mqttPublish.publish("n");
			}
		});
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		setPrimaryStage(primaryStage);
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("MainApplication.fxml"));
		//AnchorPane root = (AnchorPane)loader.load();//FXMLLoader.load(getClass().getResource("MainApplication.fxml"));
		//AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("SettingDialog.fxml"));
		
		root = (AnchorPane)loader.load();
		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); 
		
		// grab your root here 
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		
		// move around here 
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Main.getPrimaryStage().setX(event.getScreenX() - xOffset);
				Main.getPrimaryStage().setY(event.getScreenY() - yOffset);
			}
		});
		
		primaryStage.setTitle("Main");
		primaryStage.initStyle(StageStyle.UNDECORATED);
		
		controller = (MainApplicationController)loader.getController();
		controller.startTime.setText(startTime);
		controller.clientIdText.setText(ClientOption.getClientId());
		controller.numberOfProcessesCreated.setText("start " + mqttPublish.numberOfProcessesCreated);
		if(mqttPublish.numberOfProcessesCreated != 0)
			controller.lastDetectedTime.setText(mqttPublish.lastDetectedTime);
		
		MySystemTray.createTrayIcon(primaryStage);
		firstTime = true;
		Platform.setImplicitExit(false);
		
		if (ClientOption.isBrokerIPEmpty()) {
			MainApplicationController.showSettingDialog();
		} 
		else if (ClientOption.isClientOptionEmpty()) {
			MainApplicationController.showSettingDialog();
		}
		primaryStage.setScene(scene);
		//primaryStage.show();		//****나중에 주석 처리****
		MySystemTray.showProgramIsStartedMsg();
		
		FXMLLoader detailDialogloader = new FXMLLoader();
		detailDialogloader.setLocation(getClass().getResource("DetailsTableDialog.fxml"));
		detailController = (DetailsTableDialogController)detailDialogloader.getController();
		
	}
	
	public void setPrimaryStage(Stage primaryStage) {
	     Main.primaryStage = primaryStage;
	}
	public static Stage getPrimaryStage() {
	     return primaryStage;
	}
	
	
	

}

