package mqtt.detect;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import mqtt.data.UnallowedList;
import application.Main;
import application.MainApplicationController;


public class EventMessage {
	public static native int wmiInit();

	public static HashMap<String, Integer> temp = new HashMap<String, Integer>();

	// 프로세스 실행 시 콜백 메소드
	public static void creationCallback(String processName) {
		processName = processName.replace(".exe", "");
		System.out.println(processName + " 실행 중 ");
		
		Vector<String> list = new UnallowedList().list; // .txt 파일 읽어와서 저장해놓은 벡터
		
		// 미 허용 프로세스일 경우만
		// value가 -1인 경우는 프로그램 시작 전에 이미 실행 중이던 프로세스
		for (int i = 0; i < list.size(); i++) {
			if(processName.equals(list.get(i))) {
				if(temp.containsKey(processName) && temp.get(processName) == -1) {	// key에 processName이 있고 value가 -1인 경우 -1유지
					temp.put(processName, -1);
				}
				else if(temp.containsKey(processName) && temp.get(processName) != -1) {	// key에 processName이 있으면 value++
					temp.put(processName, temp.get(processName) + 1);
				}
				else {	// 첫 실행 시에 hashmap에 put후에 실행 메시지 publish
					temp.put(processName, 1);
					Main.mqttPublish.count++;
					Main.mqttPublish.numberOfProcessesCreated++;
					if(Main.detecting) {
						Main.mqttPublish.updateTopic("creationDetect");
						Main.mqttPublish.publish(Main.mqttPublish.count + ";" + processName);
						
						Main.controller.numberOfProcessesCreated.setText("start " + Main.mqttPublish.numberOfProcessesCreated);	// MainPanel 감지된 프로세스 개수 찍어주기
						Main.mqttPublish.creationProcessName.add(processName);
						// 마지막 감지 시간 알아오기
						long curr = System.currentTimeMillis();
						SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
						Main.mqttPublish.lastDetectedTime = sd.format(new Date(curr));
						Main.controller.lastDetectedTime.setText(Main.mqttPublish.lastDetectedTime);	// MainPanel에 마지막 감지 시간 찍어주기
						Main.mqttPublish.creationProcessTime.add(Main.mqttPublish.lastDetectedTime);
						
						if(Main.currentMode.equals("B")) {
							Vector<String> v = new Vector<String>();
							v.add(processName);
							//new PopUpDialog(v);
						}
					}
				}
			}
		}			
		// temp hashmap 내용 전체 출력
		Set<String> keys = temp.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			int value = temp.get(key);
			System.out.print("(" + key + ", " + value + ") ");
		}
		System.out.println();
	}

	// 프로세스 종료 시 콜백 메소드
	public static void deletionCallback(String processName) {
		processName = processName.replace(".exe", "");
		System.out.println(processName + " 종료");
		
		if(temp.containsKey(processName)) {	// key에 processName이 있으면 value--
			if(temp.get(processName) == -1) {	// value가 -1이면 running인지 검사
				if (!Process.isRunning(processName + ".exe")) {	// running이 아니면 hashmap에서 삭제 후 종료메시지 publish
					temp.remove(processName);
					Main.mqttPublish.count--;
					Main.mqttPublish.numberOfProcessesDeleted++;
					if(Main.detecting) {
						Main.mqttPublish.updateTopic("deletionDetect");
						Main.mqttPublish.publish(Main.mqttPublish.count + ";" + processName);
						Main.controller.numberOfProcessesDeleted.setText("exit " + Main.mqttPublish.numberOfProcessesDeleted);	// MainPanel 감지된 프로세스 개수 찍어주기
						Main.mqttPublish.deletionProcessName.add(processName);
						// 마지막 감지 시간 알아오기
						long curr = System.currentTimeMillis();
						SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
						Main.mqttPublish.lastDetectedTime = sd.format(new Date(curr));
						Main.controller.lastDetectedTime.setText(Main.mqttPublish.lastDetectedTime);	// MainPanel에 마지막 감지 시간 찍어주기
						Main.mqttPublish.deletionProcessTime.add(Main.mqttPublish.lastDetectedTime);
						
						//Main.detailController.updateTable();
					}
				}
			}
			else if(temp.get(processName) == 1) {	// 마지막 프로세스인 경우 hashmap에서 remove후 종료 메시지 publish
				temp.remove(processName);
				Main.mqttPublish.count--;
				Main.mqttPublish.numberOfProcessesDeleted++;
				if(Main.detecting) {
					Main.mqttPublish.updateTopic("deletionDetect");
					Main.mqttPublish.publish(Main.mqttPublish.count + ";" + processName);
					
					Main.controller.numberOfProcessesDeleted.setText("exit " + Main.mqttPublish.numberOfProcessesDeleted);
					Main.mqttPublish.deletionProcessName.add(processName);
					// 마지막 감지 시간 알아오기
					long curr = System.currentTimeMillis();
					SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
					Main.mqttPublish.lastDetectedTime = sd.format(new Date(curr));
					Main.controller.lastDetectedTime.setText(Main.mqttPublish.lastDetectedTime);	// MainPanel에 마지막 감지 시간 찍어주기
					Main.mqttPublish.deletionProcessTime.add(Main.mqttPublish.lastDetectedTime);
				}
			}
			else {
				temp.put(processName, temp.get(processName) - 1);
			}
		}
		
		// temp hashmap 내용 전체 출력
		Set<String> keys = temp.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			int value = temp.get(key);
			System.out.print("(" + key + ", " + value + ") ");
		}
		System.out.println();
	}
		

	static {
		System.loadLibrary("EventMessage");
	}
}
