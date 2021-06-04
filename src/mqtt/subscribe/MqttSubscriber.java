package mqtt.subscribe;

public class MqttSubscriber {
	private String brokerIP;
	private String topic; // topic
	private String content; // message content
	private int qos;
	private String clientId;
	
	public String getBrokerIP() {
		return brokerIP;
	}
	
	public void setBrokerIP(String brokerIP) {
		this.brokerIP = brokerIP;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getQos() {
		return qos;
	}
	
	public void setQos(int qos) {
		this.qos = qos;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
