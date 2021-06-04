package mqtt.subscribe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import mqtt.data.UnallowedList;
import mqtt.detect.Process;
import application.Main;
import application.MySystemTray;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mqtt.trace.Trace;

public class MqttSubscribe {
	MqttClient client; 
	MqttClient blacklistClient;
	public MqttSubscriber mqttSubscriber;
	public BlacklistSubscriber blacklistSubscriber;
	private MemoryPersistence persistence;
	private Vector<String> list;// = new UnallowedList().list;
	private BufferedReader r = null;
	private BufferedWriter w = null;
	
	private String token = "/";
	private String topicListAdd = "hansung/blacklistAdd";			
	private String topicListDelete = "hansung/blacklistDelete";
	private String topicListShow = "hansung/blacklistShow";
	private String topicProcessKill = "hansung/processKill";
	private String topicProcesslistShow = "hansung/processlistShow";
	private String topicControlSettings = "hansung/controlSettings";
	String targetProcess;
	//String msg;
	//String [] msgArray;
	
	
	public MqttSubscribe() { 
		mqttSubscriber = new MqttSubscriber();
		blacklistSubscriber = new BlacklistSubscriber();
	}
	
	public void init(String brokerIP, String floor, String classroom, String id) {
		mqttSubscriber.setBrokerIP("tcp://" + brokerIP + ":1883");
		mqttSubscriber.setClientId(classroom + "_" + id);
		mqttSubscriber.setQos(2);
		mqttSubscriber.setContent(null);
		mqttSubscriber.setTopic("hansung/" + "+/"+ floor + "/" + classroom + "/" + id);

		blacklistSubscriber.setBrokerIP("tcp://" + brokerIP + ":1883");
		blacklistSubscriber.setClientId(classroom + "_" + "#");
		blacklistSubscriber.setQos(2);
		blacklistSubscriber.setContent(null);
		blacklistSubscriber.setTopic("hansung/" + "+/"+ floor + "/" + classroom + "/" + "#");
		
		persistence = new MemoryPersistence();
	}
	
	// 'topic'이 'topic2/#'과 같은지 확인해주는 함수
	// tokenizer로 해도 조건식을 처리해줘야해서 아예 만들었는데 나중에 바뀔 수 도있음
	public boolean compareTopics(String topic, String topic2) {
		boolean flag = false;
		if(topic.length() >= topic2.length()) {
			for(int i=0; i<topic2.length(); i++) {
				flag = false;
				if(topic.charAt(i)==topic2.charAt(i)) {
					flag = true;
				}
				if(!flag) break;
			}
		}
		if(flag && (topic.length() == topic2.length() || topic.charAt(topic2.length()) == token.charAt(0))) {
			return true;
		}
		return false;
	}
	
	public void publishBlacklist() {
		list = new UnallowedList().list;
		String content = "";
		
		for(int i=0; i<list.size(); i++) {
			content += list.get(i);
			if(i != list.size()-1) {
				content += ";";
			}
		}
		
		Main.mqttPublish.updateTopic("BlacklistShow");
		Main.mqttPublish.publish(list.size() + ";" + content);
	}
	
	public void listAdd(String[] msgArray) {
		boolean flag = false;
		for(int i=0; i<msgArray.length; i++) {
			list = new UnallowedList().list;
			for(int j=0; j<list.size(); j++) {
				flag = false;
				if(msgArray[i].equalsIgnoreCase(list.get(j))) {
					flag = true;
					break;
				}
			}
			if(!flag) {
				// list 벡터에 msg로 받은 process가 없을 경우 .txt파일에 저장 & list 벡터에 저장
				try {
					w = new BufferedWriter(new FileWriter(UnallowedList.fileName, true));
					
					w.write(msgArray[i] + "\r\n");
					w.close();
					list.add(msgArray[i]);
					
					/*ObservableList<String> items = FXCollections.observableArrayList();
					Vector<String> list = new UnallowedList().list;
					for (int j = 0; j < list.size(); j++) {
						items.add(list.get(j));
					}*/
					Main.controller.blackListView.getItems().add(msgArray[i]);
					//Main.controller.blackListView.setItems(items);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		}
		
		for(int i=0; i<msgArray.length; i++) {
			if(Process.isRunning(msgArray[i] + ".exe")) {
				Main.mqttPublish.updateTopic("creationDetect");
				Main.mqttPublish.publish(++Main.mqttPublish.count + ";" + msgArray[i]);
				
				Main.mqttPublish.numberOfProcessesCreated++;
				Main.controller.numberOfProcessesCreated.setText("생성 " + Main.mqttPublish.numberOfProcessesCreated);	// MainPanel 감지된 프로세스 개수 찍어주기
				Main.mqttPublish.creationProcessName.add(msgArray[i]);
				// 마지막 감지 시간 알아오기
				long curr = System.currentTimeMillis();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
				Main.mqttPublish.lastDetectedTime = sd.format(new Date(curr));
				Main.controller.lastDetectedTime.setText(Main.mqttPublish.lastDetectedTime);	// MainPanel에 마지막 감지 시간 찍어주기
				Main.mqttPublish.creationProcessTime.add(Main.mqttPublish.lastDetectedTime);
			}
		}
	}
	
	public void listDelete(String[] msgArray) {
		for(int i=0; i<msgArray.length; i++) {
			list = new UnallowedList().list;
			for(int j=0; j<list.size(); j++) {
				if(list.get(j).equalsIgnoreCase(msgArray[i])) {
					list.remove(j);	// list 벡터에서 msg삭제		
				}
			}
			UnallowedList.removeLineFromFile(UnallowedList.fileName, msgArray[i]);	// fileName에서 msg 한 줄 삭제
		
			/*ObservableList<String> items = FXCollections.observableArrayList();
			Vector<String> list = new UnallowedList().list;
			for (int j = 0; j < list.size(); j++) {
				items.add(list.get(j));
			}*/
			Main.controller.blackListView.getItems().remove(msgArray[i]);
			//Main.controller.blackListView.setItems(items);
		}
		
		for(int i=0; i<msgArray.length; i++) {
			if(Process.isRunning(msgArray[i] + ".exe")) {
				Main.mqttPublish.count--;
			}
		}
	}
	
	// 여러개의 프로세스 한 번에 다 죽임 
	public void processKill(String[] msgArray) {
		String str = "";
		boolean flag = false;
		Vector<String> temp = new Vector<String>();
		
		for(int i=0; i<msgArray.length; i++) {
			if(Process.isRunning(msgArray[i] + ".exe")) {
				flag = true;
				temp.add(msgArray[i]);
			}
		}
		if(flag) {
			for(int i=0; i<temp.size(); i++) {
				Process.kill(temp.get(i) + ".exe");
				MySystemTray.showProcessKilledMsg(temp.get(i));
			}
			
			//new PopUpDialog(temp);
			
		}
	}
	
	public void publishProcesslist() {
		String str = Process.processList();	// 중복 있는 processlist
		String [] s = str.split(" ");
		Vector<String> v = new Vector<String>();
		String processlist = "";
		
		// str에서 중복을 제거하고 v벡터에 넣
		boolean flag2 = true;
		for(int i=0; i<s.length; i++) {
			flag2 = true;
			for (int j = 0; j < v.size(); j++) {
				if(v.get(j).equals(s[i])) {
					flag2 = false;
				}
			}
			if(flag2)
				v.add(s[i]);
		}
		
		for(int i=0; i<v.size(); i++) {
			processlist += v.get(i);
			if(i != v.size()-1)
				processlist += ";";
		}
		
		
		Main.mqttPublish.updateTopic("ProcesslistShow");
		Main.mqttPublish.publish(v.size() + ";" + processlist);
	}
	
	public void detectionControl(String state) {
		if (state.equals("0")) {	// 감지 멈추기
			Main.detecting = false;
			
			Main.mqttPublish.updateTopic("ControlSettings");
			Main.mqttPublish.publish("detectionControl" + ";" + 0);
		}

		else if (state.equals("1")){	// 감지 다시 시작
			Main.detecting = true;
			
			Main.mqttPublish.updateTopic("ControlSettings");
			Main.mqttPublish.publish("detectionControl" + ";" + 1);
			
			Main.mqttPublish.firstDetectionPublish();// 감지 다시 시작하면 그동안 쌓인 실행 프로세스 이름 publish
		}
	}
	
	class SubscribeCallback implements MqttCallback {

		public void connectionLost(Throwable cause) {
		}
		
		public void deliveryComplete(IMqttDeliveryToken token) {
		}
		
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String time = new Timestamp(System.currentTimeMillis()).toString();
			
			String msg = new String(message.getPayload());
			
			Trace.subPrint(topic + " : " + msg + "\tTime : " +time);
			Trace.subPrint("");
			
			String [] msgArray = msg.split(";");	//";"로 msg 분리
			/*for(int i=0; i<msgArray.length; i++) {
				msgArray[i] += ".exe";
			}*/
			
			// 현재 msg로 받은 프로세스가 실행 중이면 종료 후 종료 메시지 publish
			//***********************************************************************************************
			if(compareTopics(topic, topicProcessKill)) {
				
				processKill(msgArray);
				
				//mqttPublish.updateTopic("ProcessKill");
				//mqttPublish.publish(processKill(msgArray));
			}
			
			/*
			// 관리 리스트에 msg 추가, .txt 파일에도 추가
			if(compareTopics(topic, topicListAdd)) {
				
				listAdd(msgArray);
					
				publishBlacklist();
			}
			
			// 관리 리스트에서 msg 삭제
			// list 벡터에서도 해당 msg지우고, .txt파일에서도 지움
			if(compareTopics(topic, topicListDelete)) {
				
				listDelete(msgArray);
				
				publishBlacklist();
			}
			
			// 관리 리스트 전체 보기
			if(compareTopics(topic, topicListShow)) {
				publishBlacklist();
			}
			*/
			
			// 실행 중인 프로세스 목록 전체 보기
			if(compareTopics(topic, topicProcesslistShow)) {
				publishProcesslist();
			}
			
			if(compareTopics(topic, topicControlSettings)) {
				if(msgArray[0].equals("detectionControl")) {
					detectionControl(msgArray[1]);
				}
			}
		}
	}
	
	class BlacklistSubscribeCallback implements MqttCallback {

		public void connectionLost(Throwable cause) {
		}
		
		public void deliveryComplete(IMqttDeliveryToken token) {
		}
		
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			String time = new Timestamp(System.currentTimeMillis()).toString();
			
			String msg = new String(message.getPayload());
			
			String [] msgArray = msg.split(";");	//";"로 msg 분리
			/*for(int i=0; i<msgArray.length; i++) {
				msgArray[i] += ".exe";
			}*/
			
			
			// 관리 리스트에 msg 추가, .txt 파일에도 추가
			if(compareTopics(topic, topicListAdd)) {
				Trace.subPrint(topic + " : " + msg + "\tTime : " +time);
				Trace.subPrint("");
				
				listAdd(msgArray);
					
				publishBlacklist();
			}
			
			// 관리 리스트에서 msg 삭제
			// list 벡터에서도 해당 msg지우고, .txt파일에서도 지움
			if(compareTopics(topic, topicListDelete)) {
				Trace.subPrint(topic + " : " + msg + "\tTime : " +time);
				Trace.subPrint("");
				
				listDelete(msgArray);
				
				publishBlacklist();
			}
			
			// 관리 리스트 전체 보기
			if(compareTopics(topic, topicListShow)) {
				Trace.subPrint(topic + " : " + msg + "\tTime : " +time);
				Trace.subPrint("");
				
				publishBlacklist();
			}
			
		}
	}
	
	public void subscribeRun() {
				
		try {
			client = new MqttClient(mqttSubscriber.getBrokerIP(), mqttSubscriber.getClientId());
			client.setCallback(new SubscribeCallback());
			MqttConnectOptions conOptions = new MqttConnectOptions();
			conOptions.setCleanSession(true);
			client.connect(conOptions);
			
			Trace.subPrint("Subscribing to topic \"" + mqttSubscriber.getTopic()
			+ "\" for client instance \"" + client.getClientId()
			+ "\" using QoS " + mqttSubscriber.getQos() + ". Clean session is "
			+ conOptions.isCleanSession());
			client.subscribe(mqttSubscriber.getTopic(), mqttSubscriber.getQos());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void blacklistSubscribeRun() {
		
		try {
			blacklistClient = new MqttClient(blacklistSubscriber.getBrokerIP(), blacklistSubscriber.getClientId());
			blacklistClient.setCallback(new BlacklistSubscribeCallback());
			MqttConnectOptions conOptions = new MqttConnectOptions();
			conOptions.setCleanSession(true);
			blacklistClient.connect(conOptions);
			
			Trace.subPrint("Subscribing to topic \"" + blacklistSubscriber.getTopic()
			+ "\" for client instance \"" + blacklistClient.getClientId()
			+ "\" using QoS " + blacklistSubscriber.getQos() + ". Clean session is "
			+ conOptions.isCleanSession());
			blacklistClient.subscribe(blacklistSubscriber.getTopic(), blacklistSubscriber.getQos());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public boolean isSubscribeConnect() {
		if(client.isConnected())
			return true;
		else 
			return false;
	}
	
	public void subscribeDisconnect() {
		try {
			client.disconnect();
			Trace.subPrint("subscribe disconnected");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isBlacklistSubscribeConnect() {
		if(blacklistClient.isConnected())
			return true;
		else
			return false;
	}
	
	public void blacklistSubscribeDisconnect() {
		try {
			blacklistClient.disconnect();
			Trace.subPrint("blacklist subscribe disconnected");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
}