package mqtt.publish;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import application.Main;
import application.MainApplicationController;
import application.MySystemTray;
import application.SoundControl;
import application.Volume;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import mqtt.data.ClientOption;
import mqtt.data.UnallowedList;
import mqtt.detect.EventMessage;
import mqtt.detect.Process;
import mqtt.detect.RegistryHandler;
import mqtt.trace.Trace;

public class MqttPublish {
	public MqttClient objectClient; // 관리 대상이되는 클라이언트(PC)
	public MqttPublisher mqttPublisher; // mqttPublisher
	private MemoryPersistence persistence;
	public int numberOfProcessesCreated = 0;	// 지금까지 감지된 횟수 (creation, deletion)
	public int numberOfProcessesDeleted = 0;
	public Vector<String> creationProcessName = new Vector<String>();
	public Vector<String> creationProcessTime = new Vector<String>();
	public Vector<String> deletionProcessName = new Vector<String>();
	public Vector<String> deletionProcessTime = new Vector<String>();
	public int count = 0;	// message 앞에 같이 보내는 개수(현재 사용 중인 미허용 프로세스 개수)
	public String lastDetectedTime;
	
	private String content = "";
	private Vector<String> list;

	public MqttPublish() {
		// list = new UnallowedList().list; // .txt 파일 읽어와서 저장해놓은 벡터
		
		mqttPublisher = new MqttPublisher();
	}

	// 초기화 (ProcessRunnable에서 설정)
	public void init(String brokerIP, String floor, String classroom, String id) {
		mqttPublisher.setBrokerIP("tcp://" + brokerIP + ":1883");
		mqttPublisher.setClientId(classroom + "-" + id);
		mqttPublisher.setQos(2);
		mqttPublisher.setContent(null);
		mqttPublisher.setTopic("hansung/" + mqttPublisher.getPlusTopic() + "/" + floor + "/" + classroom + "/" + id);
		persistence = new MemoryPersistence();
	}

	public void updateTopic(String plusTopic) {
		mqttPublisher.setTopic("hansung" + "/" + plusTopic + "/" + ClientOption.properties.getProperty("floor") + "/"
				+ ClientOption.properties.getProperty("classroom") + "/" + ClientOption.properties.getProperty("id"));
		mqttPublisher.setPlusTopic(plusTopic);
	}

	// 시작 메시지 publish
	public void publishStartMessage() {
		Main.detecting = true;	// 감지 on
		
		updateTopic("alive");
		publish("y");
		
		updateTopic("ControlSettings");
		publish("detectionControl" + ";" + 1);
	}

	
	// 프로그램이 시작되면 실행 중인 프로세스 리스트 전체와 미 허용 리스트 비교 후 한번에 publish
	public void firstDetectionPublish() {
		list = new UnallowedList().list; // .txt 파일 읽어와서 저장해놓은 벡터
		count = 0;
		Vector<String> v = new Vector<String>();
		
		for (int i = 0; i < list.size(); i++) {
			if (Process.isRunning(list.get(i) + ".exe")) {
				count++;
				numberOfProcessesCreated++;
				
				content += list.get(i) + ";";
				v.add(list.get(i));
				
				EventMessage.temp.put(list.get(i), -1);	// 이미 실행 중이던 프로세스는 value에 -1
			}
		}
		
		mqttPublisher.setContent(count + ";" + content);
		//Main.controller.numberOfProcessesDetected.setText(numberOfProcessesDetected + "");

		// temp hashmap 내용 전체 출력
		Set<String> keys = EventMessage.temp.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			int value = EventMessage.temp.get(key);
			System.out.print("(" + key + ", " + value + ") ");
		}
		System.out.println();
		
		updateTopic("creationDetect");
		publish(mqttPublisher.getContent()); // 감지된 프로세스들을 message로 publish
		
		
		
		for(int i=0; i<v.size(); i++) {
			creationProcessName.add(v.get(i));
			creationProcessTime.add(lastDetectedTime);
		}
		
		//Main.controller.lastDetectedTime.setText(lastDetectedTime);
		//MainApplicationController.lastDetectedTime.setText("hello");
		
		
		/*
		String pName = "";
		if(Main.currentMode.equals("B")) {
			for(int i=0; i<v.size(); i++) {
				pName += v.get(i);
				if(i != v.size()-1)
					pName += ";";
				//Process.kill(v.get(i) + ".exe");
				//MySystemTray.showProcessKilledMsg(v.get(i));
				//RegistryHandler.addReg(v.get(i).replace(".exe", ""), v.get(i));
			}
			//MySystemTray.showProcessKilledMsg(pName);
			//new PopUpDialog(v);
		}*/
		
		finish();	// content=""로 초기화
	}
	
	// B모드에서 필요한 기능
	public void firstDetect() {
		list = new UnallowedList().list; // .txt 파일 읽어와서 저장해놓은 벡터
		Vector<String> v = new Vector<String>();
		
		//boolean flag = false;
		for (int i = 0; i < list.size(); i++) {
			if (Process.isRunning(list.get(i) + ".exe")) {
				//flag = true;
				v.add(list.get(i));
			}
		}
		
		String content = "";
		if(Main.currentMode.equals("B")) {
			for(int i=0; i<v.size(); i++) {
				content += v.get(i);
				if(i != v.size()-1)
					content += ";";
				//MySystemTray.showCannotUseMsg(v.get(i));
				//Process.kill(v.get(i) + ".exe");
				//RegistryHandler.addReg(v.get(i).replace(".exe", ""), v.get(i));
			}
			//MySystemTray.showCannotUseMsg(content);
			
			//new PopUpDialog(temp);
				
		}
	}
	
	public void checkOperation(int delay) {
		new CheckOperationThread(delay).start();
	}
	
	
	class CheckOperationThread extends Thread {
		int delay;
		
		public CheckOperationThread(int delay) {
			this.delay = delay*1000;
			
		}
		
		public void run() {
			while(true) {
				try {
					sleep(delay);	// 첫 메시지를 publish한 후 
					
					updateTopic("alive");
					publish("y");
					
					// 혹시 subscribe가 disconnect상태이면 다시 subscribe해줌
					if(!Main.mqttSubscribe.isSubscribeConnect()) {
						Main.mqttSubscribe.subscribeRun();
					}
					if(!Main.mqttSubscribe.isBlacklistSubscribeConnect()) {
						Main.mqttSubscribe.blacklistSubscribeRun();
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void publishRun() {
		EventMessage.wmiInit();	// wmi 스레드로 프로세스 실행 시, 종료 시에 콜백 메소드 호출
		
		//
		// TrayIconHandler.displayMessage("미 허용 소프트웨어 실행 감지", count + "개의 소프트웨어가
		// 감지되었습니다.", MessageType.WARNING);
	}
	
	// msg를 message로 해서 publish하는 메소드
	public void publish(String msg) {
		String[] msgArray = msg.split(";");

		String message = "";
		for (int i = 0; i < msgArray.length; i++) {
			/*if (msgArray[i].contains(".exe"))
				message += msgArray[i].replace(".exe", ";");
			else*/
				message += msgArray[i];
				if(i != msgArray.length-1) {
					message += ";";
				}
		}
		
		
		mqttPublisher.setContent(message);
		// 여기까지 다른 함수로 빼기
		
		
		try {
			objectClient = new MqttClient(mqttPublisher.getBrokerIP(), mqttPublisher.getClientId(), persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			Trace.pubPrint("Connecting to broker : " + mqttPublisher.getBrokerIP());
			objectClient.connect(connOpts);
			Trace.pubPrint("Connected");
			Trace.pubPrint("Publishing message: " + mqttPublisher.getContent());
			MqttMessage pMessage = new MqttMessage(mqttPublisher.getContent().getBytes());

			pMessage.setQos(mqttPublisher.getQos());
			objectClient.publish(mqttPublisher.getTopic(), pMessage);
			Trace.pubPrint("Message published");
			Trace.pubPrint("Disconnected");
			objectClient.disconnect();

		} catch (MqttException me) {
			Trace.pubPrint("reason " + me.getReasonCode());
			Trace.pubPrint("msg " + me.getMessage());
			Trace.pubPrint("loc " + me.getLocalizedMessage());
			Trace.pubPrint("cause " + me.getCause());
			Trace.pubPrint("excep " + me);
			
			//new SettingDialog("broker" + me.getMessage() + " 오류");
			
			me.printStackTrace();
		}
		Trace.pubPrint("");
		
		if(mqttPublisher.getPlusTopic().equals("creationDetect")) {
			if(!msgArray[0].equals("0")) {
				Volume.volumeControl(Volume.volume);	// 소리 키우기 (0.0 ~ 1.0)
											// 나중에 웹페이지에서 음량 조절할 수 도 있게 바꾸기
				SoundControl.playSound();	// 소리 내기
				
				long curr = System.currentTimeMillis();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
				lastDetectedTime = sd.format(new Date(curr));
				
				String content = "";
				for(int i=0; i<msgArray.length; i++) {
					content += msgArray[i];
					if(i != msgArray.length-1)
						content += ", ";
				}
				//MySystemTray.showDetectedMsg(content);
				
				if(Main.currentMode.equals("B")) {
					for(int i=1; i<msgArray.length; i++) {
						//MySystemTray.showCannotUseMsg(msgArray[i]);
						//Process.kill(msgArray[i] + ".exe");
						//RegistryHandler.addReg(v.get(i).replace(".exe", ""), v.get(i));
					}
					
					//new PopUpDialog(temp);
						
				}
			}
		}
	}

	public void finish() {
		content = "";
		//count = 0;
	}
	
}
