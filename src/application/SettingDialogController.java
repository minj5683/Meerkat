package application;

import java.awt.SystemTray;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mqtt.data.ClientOption;
import mqtt.data.UnallowedList;
import mqtt.publish.MqttPublish;

public class SettingDialogController implements Initializable {
	private String lastTagName = "group0";
	private Vector<String> groupName = new Vector<String>();
	private double xOffset = 0;
	private double yOffset = 0;
	
	Element rootElement;
	Vector<String> temp = new Vector<String>();
	//String selectedItem = "";
	String firstComboSelectedItem = "";
	String secondComboSelectedItem = "";
	String thirdComboSelectedItem = "";
	//String fourthComboSelectedItem = "";
	//String fifthComboSelectedItem = "";
	String brokerIP = "";
	ObservableList<String> firstItems = FXCollections.observableArrayList();
	ObservableList<String> secondItems = FXCollections.observableArrayList();
	ObservableList<String> thirdItems = FXCollections.observableArrayList();
	
	@FXML private Button searchBtn;
	@FXML private Button finishBtn;
	@FXML private Button cancelBtn;
	@FXML private TextField xmlFileTextField;
	@FXML private TextField brokerIPTextField;
	@FXML private VBox defaultGroupPane;
	
	@FXML private VBox oneGroupPane;
	@FXML private Text oneGroupFirstName;
	@FXML private ComboBox<String> oneGroupFirstCombo;
	
	@FXML private VBox twoGroupPane;
	@FXML private Text twoGroupFirstName;
	@FXML private ComboBox<?> twoGroupFirstCombo;
	@FXML private Text twoGroupSecondName;
	@FXML private ComboBox<?> twoGroupSecondCombo;
	
	@FXML private VBox threeGroupPane;
    @FXML private Text threeGroupFirstName;
    @FXML private ComboBox<String> threeGroupFirstCombo;
    @FXML private Text threeGroupSecondName;
    @FXML private ComboBox<String> threeGroupSecondCombo;
    @FXML private Text threeGroupThirdName;
    @FXML private ComboBox<String> threeGroupThirdCombo;
    
    @FXML private VBox fourGroupPane;
    @FXML private Text fourGroupFirstName;
    @FXML private ComboBox<?> fourGroupFirstCombo;
    @FXML private Text fourGroupSecondName;
    @FXML private ComboBox<?> fourGroupSecondCombo;
    @FXML private Text fourGroupThirdName;
    @FXML private ComboBox<?> fourGroupThirdCombo;
    @FXML private Text fourGroupFourthName;
    @FXML private ComboBox<?> fourGroupFourthCombo;
    
    @FXML private VBox fiveGroupPane;
    @FXML private Text fiveGroupFirstName;
    @FXML private ComboBox<?> fiveGroupFirstCombo;
    @FXML private Text fiveGroupSecondName;
    @FXML private ComboBox<?> fiveGroupSecondCombo;
    @FXML private Text fiveGroupThirdName;
    @FXML private ComboBox<?> fiveGroupThirdCombo;
    @FXML private Text fiveGroupFourthName;
    @FXML private ComboBox<?> fiveGroupFourthCombo;
    @FXML private Text fiveGroupFifthName;
    @FXML private ComboBox<?> fiveGroupFifthCombo;
    
    
	public void getXmlInfo(Node n) {	// xml 몇 그룹인지, xml 그룹의 태그이름 (class 속성) 알아내서 groupNum에 저장
	   for(Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
	      if(ch.getNodeType() == Node.ELEMENT_NODE) {
	    	  Element element = (Element) ch;
	    	  System.out.println(element.getTagName() + " " + element.getAttribute("class") + " " + element.getAttribute("name"));		 
	    	  
	    	  boolean flag = false;
	    	  for(int j=0; j<groupName.size(); j++) {
	    		  flag = false;
	    		  if(groupName.get(j).equalsIgnoreCase(element.getAttribute("class"))) {
	    			  flag = true;
	    			  break;
	    		  }
	    	  }
	    	  if(!flag) {
	    		  groupName.add(element.getAttribute("class"));
	    	  }
	    	  
	    	  if(lastTagName.compareToIgnoreCase(element.getTagName()) == -1)
	    		  lastTagName = element.getTagName();
	    	  getXmlInfo(ch);
	      }
	   }
	}
	
	public Vector<String> getChildrenNames(Node n) {
		Vector<String> children = new Vector<String>();
		
	   for(Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
	      if(ch.getNodeType() == Node.ELEMENT_NODE) {
	    	  Element element = (Element) ch;
	    	  //System.out.println(element.getTagName() + " " + element.getAttribute("class") + " " + element.getAttribute("name"));		 
	    	  children.add(element.getAttribute("name"));
	    	  //getNode(ch);
	      }
	   }
	   System.out.println(children.size());
	   return children;
	}
	
	
	/*public Node getNodeByItem(Node root, String item) {
		Node currentNode = null;
		
		for(Node ch = root.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
			if(ch.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) ch;
				System.out.println(element.getAttribute("name"));
				if(item.equals(element.getAttribute("name"))) {
					currentNode = ch;
					break;
				} 
			}
		}
		return currentNode;
	}*/
	
	
	public void setValueOfNodeByItem(Node root, String item) {
		//Vector<String> temp = new Vector<String>();
		for(Node ch = root.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
			if(ch.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) ch;
				if(item.equals(element.getAttribute("name"))) {
					temp = getChildrenNames(ch);
					return;
				}
				else 
					setValueOfNodeByItem(ch, item);
			}
		}
	}
	
	public void readXmlFile(File xmlFile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			/*Element */rootElement = doc.getDocumentElement();
			getXmlInfo(rootElement);
			System.out.println(lastTagName);
			
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public Vector<String> getVector() { return temp; }
	
	
	public void setThreeGroupPane(Node root) {
		
		Vector<String> temp = getChildrenNames(root);
		for (int i = 0; i < temp.size(); i++) {
			firstItems.add(temp.get(i));
		}
		
		threeGroupFirstCombo.setItems(firstItems);
		threeGroupFirstCombo.getSelectionModel().select(0);
		
		firstComboSelectedItem = threeGroupFirstCombo.getItems().get(0);
		setValueOfNodeByItem(root, firstComboSelectedItem);
		Vector<String> temp2 = getVector();
		for (int i = 0; i < getVector().size(); i++) {
			secondItems.add(temp2.get(i));
			//System.out.println(temp2.get(i) + "**");
		}
		threeGroupSecondCombo.setItems(secondItems);
		threeGroupSecondCombo.getSelectionModel().select(0);
		
		threeGroupFirstCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				threeGroupSecondCombo.getItems().clear();
				threeGroupThirdCombo.getItems().clear();
				firstComboSelectedItem = threeGroupFirstCombo.getSelectionModel().getSelectedItem();
				System.out.println("second" + firstComboSelectedItem);
				//Node currentNode = getNodeByItem(rootElement, selectedItem);
				//Node currentNode = getNodeByItem(rootElement, selectedItem);
				setValueOfNodeByItem(root, firstComboSelectedItem);
				Vector<String> temp = getVector();
				for (int i = 0; i < getVector().size(); i++) {
					secondItems.add(temp.get(i));
					//System.out.println(temp.get(i) + "**");
				}
				threeGroupSecondCombo.setItems(secondItems);
				threeGroupSecondCombo.getSelectionModel().select(0);
			}
			
		});
		
		secondComboSelectedItem = threeGroupSecondCombo.getItems().get(0);
		System.out.println("third" + secondComboSelectedItem);
		//Node currentNode = getNodeByItem(rootElement, selectedItem);
		//Vector<String> temp3 = getChildrenNames(currentNode);
		
		setValueOfNodeByItem(root, secondComboSelectedItem);
		Vector<String> temp3 = getVector();
		//System.out.println(temp3.size() + "******");
		for (int i = 0; i < temp3.size(); i++) {
			thirdItems.add(temp3.get(i));
			//System.out.println(temp3.get(i) + "****");
		}
		threeGroupThirdCombo.setItems(thirdItems);
		threeGroupThirdCombo.getSelectionModel().select(0);
		
		threeGroupSecondCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				threeGroupThirdCombo.getItems().clear();
				secondComboSelectedItem = threeGroupSecondCombo.getSelectionModel().getSelectedItem();
				System.out.println("third" + secondComboSelectedItem);
				//Node currentNode = getNodeByItem(rootElement, selectedItem);
				//Vector<String> temp3 = getChildrenNames(currentNode);
				
				setValueOfNodeByItem(root, secondComboSelectedItem);
				Vector<String> temp = getVector();
				System.out.println(temp.size() + "******");
				for (int i = 0; i < temp.size(); i++) {
					thirdItems.add(temp.get(i));
					//System.out.println(temp.get(i) + "****");
				}
				threeGroupThirdCombo.setItems(thirdItems);
				threeGroupThirdCombo.getSelectionModel().select(0);
			}
			
		});
		
		thirdComboSelectedItem = threeGroupThirdCombo.getItems().get(0);
		threeGroupThirdCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				thirdComboSelectedItem = threeGroupThirdCombo.getSelectionModel().getSelectedItem();
			}
			
		});
		
		
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		defaultGroupPane.toFront();
		xmlFileTextField.setEditable(false);
		searchBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				FileChooser fileChooser = new FileChooser();

		        // 확장자 필터를 설정한다
		        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		        fileChooser.getExtensionFilters().add(extFilter);

		        // Save File Dialog를 보여준다
		        File xmlFile = fileChooser.showOpenDialog(Main.getPrimaryStage());
				
		        if (xmlFile != null) {
		           /*// 정확한 확장자
		            if (!file.getPath().endsWith(".xml")) {
		                file = new File(file.getPath() + ".xml");
		            }*/
		            xmlFileTextField.setText(xmlFile.getName());
		            
		            readXmlFile(xmlFile);
		            int groupNum = new Integer(lastTagName.replace("group", ""));
		           
		            System.out.println(groupNum);
		            
		            switch(groupNum) {
		            case 1: 
		            	oneGroupPane.toFront();
		            	oneGroupFirstName.setText(groupName.get(0));
		            	break;
		            case 2: 
		            	twoGroupFirstName.setText(groupName.get(0));
		            	twoGroupSecondName.setText(groupName.get(1));
		            	twoGroupPane.toFront();
		            	break;
		            case 3: 
		            	setThreeGroupPane(rootElement);
		            	threeGroupFirstName.setText(groupName.get(0));
		            	threeGroupSecondName.setText(groupName.get(1));
		            	threeGroupThirdName.setText(groupName.get(2));
		            	threeGroupPane.toFront();
		            	break;
		            case 4:
		            	fourGroupFirstName.setText(groupName.get(0));
		            	fourGroupSecondName.setText(groupName.get(1));
		            	fourGroupThirdName.setText(groupName.get(2));
		            	fourGroupFourthName.setText(groupName.get(3));
		            	fourGroupPane.toFront();
		            	break;
		            case 5: 
		            	fiveGroupFirstName.setText(groupName.get(0));
		            	fiveGroupSecondName.setText(groupName.get(1));
		            	fiveGroupThirdName.setText(groupName.get(2));
		            	fiveGroupFourthName.setText(groupName.get(3));
		            	fiveGroupFifthName.setText(groupName.get(4));
		            	fiveGroupPane.toFront();
		            	break;
		            }
		            
		        }
		        lastTagName = "group0";
			}
		});
		
		finishBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println(firstComboSelectedItem + "/" + secondComboSelectedItem + "/" + thirdComboSelectedItem);
				
				//테스트(웹페이지 화면 캡쳐해야해서 잠시 주석)
				//Main.mqttPublish.updateTopic("alive");
				//Main.mqttPublish.publish("n");
				
				// brokerIPTf가 빈칸이면 원래 브로커아이피로 (입력 양식에 오류가 있어도 원래 브로커아이피 가져와서 프로그램에
				// 문제없게 고칠 수도)
				if (brokerIPTextField.getText().equals("")) {
					brokerIP = ClientOption.properties.getProperty("brokerIP");
				} else {
					brokerIP = brokerIPTextField.getText();
				}
				
				// properties 설정
				ClientOption.properties.setProperty("brokerIP", brokerIP);
				ClientOption.properties.setProperty("floor", firstComboSelectedItem);
				ClientOption.properties.setProperty("classroom", secondComboSelectedItem);
				ClientOption.properties.setProperty("id", thirdComboSelectedItem);
				
				// properties file에 갱신된 정보 write
				ClientOption.writeClientOption();
				
				ClientOption.clientInit();	// 바뀐정보로 mqttSubscribe와 mqttPublish의 init()갱신
				
				
				// 이미 subscribe되어있으면 disconnect한 후 갱신된 정보로 subscribe
				if(Main.mqttSubscribe.isSubscribeConnect())
					Main.mqttSubscribe.subscribeDisconnect();
				Main.mqttSubscribe.subscribeRun();
				
				if(Main.mqttSubscribe.isBlacklistSubscribeConnect())
					Main.mqttSubscribe.blacklistSubscribeDisconnect();
				Main.mqttSubscribe.blacklistSubscribeRun();
				 
				Main.mqttPublish.updateTopic("alive");
				Main.mqttPublish.publish("y");
				Main.mqttPublish.firstDetectionPublish();
				
				Main.controller.clientIdText.setText(ClientOption.getClientId());
				
				Stage st = (Stage) finishBtn.getScene().getWindow();
	            st.hide();
			}
		});
		
		cancelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//MainApplicationController.getSecondStage().close();
				Stage st = (Stage) cancelBtn.getScene().getWindow();
	            st.hide();
			}
		});
		
		
		
	}

	// tag값의 정보를 가져오는 메소드
	private static String getTagValue(String tag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		if(nValue == null) 
			return null;
		return nValue.getNodeValue();
	}
	
}
