package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class DetailsTableDialogController implements Initializable{
	private double xOffset = 0;
	private double yOffset = 0;
	private final ObservableList<Data> creationData = FXCollections.observableArrayList();   
	private final ObservableList<Data> deletionData = FXCollections.observableArrayList();
	
	@FXML
	private AnchorPane root;
	@FXML
	public TableView<Data> creationTable;
	@FXML
	private TableColumn<Data, String> creationNum;
	@FXML
	private TableColumn<Data, String> creationProcessName;
	@FXML
	private TableColumn<Data, String> creationTime;
	@FXML
	public TableView<Data> deletionTable;
	@FXML
	private TableColumn<Data, String> deletionNum;
	@FXML
	private TableColumn<Data, String> deletionProcessName;
	@FXML
	private TableColumn<Data, String> deletionTime;
	@FXML
	public TabPane tabPane;
	@FXML
    public Tab deletionTab;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// grab your root here
		/*root.setOnMousePressed(new EventHandler<MouseEvent>() {
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
				MainApplicationController.getDetailStage().setX(event.getScreenX() - xOffset);
				MainApplicationController.getDetailStage().setY(event.getScreenY() - yOffset);
			}
		});*/
		//creationNum.setCellValueFactory(creationData -> creationData.getValue().getNum());
		//creationProcessName.setCellValueFactory(cellData -> cellData.getValue().getProcessName());
		//creationTime.setCellValueFactory(cellData -> cellData.getValue().getTime());
		
		/*for(int i=0; i<Main.mqttPublish.numberOfProcessesCreated; i++) {
			creationData.add(new Data((i+1+""), Main.mqttPublish.creationProcessName.get(i), Main.mqttPublish.creationProcessTime.get(i)));
		}

		creationNum.setCellValueFactory(new PropertyValueFactory<Data, String>("num"));
		creationProcessName.setCellValueFactory(new PropertyValueFactory<Data, String>("processName"));
		creationTime.setCellValueFactory(new PropertyValueFactory<Data, String>("time"));
		
		creationTable.setItems(creationData);
		//creationTable.getColumns().addAll(creationNum, creationProcessName, creationTime);
		
		
		for(int i=0; i<Main.mqttPublish.numberOfProcessesDeleted; i++) {
			deletionData.add(new Data(i+1+"", Main.mqttPublish.deletionProcessName.get(i), Main.mqttPublish.deletionProcessTime.get(i)));
		}
		
		deletionNum.setCellValueFactory(new PropertyValueFactory<Data, String>("num"));
		deletionProcessName.setCellValueFactory(new PropertyValueFactory<Data, String>("processName"));
		deletionTime.setCellValueFactory(new PropertyValueFactory<Data, String>("time"));
		
		deletionTable.setItems(deletionData);
		//deletionTable.getColumns().addAll(deletionNum, deletionProcessName, deletionTime);
*/	
		setDetailsTable();
	}
	
	public static class Data {
        private  SimpleStringProperty num;
        private  SimpleStringProperty processName;
        private  SimpleStringProperty time;

        private Data(String  num, String  processName, String time) {
        	this.num = new SimpleStringProperty(num);
        	this.processName = new SimpleStringProperty(processName);
        	this.time = new SimpleStringProperty(time);
        }
        
        public String getNum() {
			return num.get();
		}

		public String getProcessName() {
			return processName.get();
		}

		public String getTime() {
			return time.get();
		}

    }
	
	public void setDetailsTable() {
		for(int i=Main.mqttPublish.numberOfProcessesCreated; i>0; i--) {
			creationData.add(new Data((Main.mqttPublish.numberOfProcessesCreated - i+1+""), Main.mqttPublish.creationProcessName.get(i-1), Main.mqttPublish.creationProcessTime.get(i-1)));
		}

		creationNum.setCellValueFactory(new PropertyValueFactory<Data, String>("num"));
		creationProcessName.setCellValueFactory(new PropertyValueFactory<Data, String>("processName"));
		creationTime.setCellValueFactory(new PropertyValueFactory<Data, String>("time"));
		
		creationTable.setItems(creationData);
		//creationTable.getColumns().addAll(creationNum, creationProcessName, creationTime);
		
		
		for(int i=Main.mqttPublish.numberOfProcessesDeleted; i>0; i--) {
			deletionData.add(new Data(Main.mqttPublish.numberOfProcessesDeleted-i+1+"", Main.mqttPublish.deletionProcessName.get(i-1), Main.mqttPublish.deletionProcessTime.get(i-1)));
		}
		
		deletionNum.setCellValueFactory(new PropertyValueFactory<Data, String>("num"));
		deletionProcessName.setCellValueFactory(new PropertyValueFactory<Data, String>("processName"));
		deletionTime.setCellValueFactory(new PropertyValueFactory<Data, String>("time"));
		
		deletionTable.setItems(deletionData);
		//deletionTable.getColumns().addAll(deletionNum, deletionProcessName, deletionTime);
	}
   
	public void updateTable() {
		ObservableList<Data> creationTemp = FXCollections.observableArrayList();  
		ObservableList<Data> deletionTemp = FXCollections.observableArrayList();
		
		for(int i=Main.mqttPublish.numberOfProcessesCreated; i>0; i--) {
			creationTemp.add(new Data((Main.mqttPublish.numberOfProcessesCreated - i+1+""), Main.mqttPublish.creationProcessName.get(Main.mqttPublish.numberOfProcessesCreated - i), Main.mqttPublish.creationProcessTime.get(Main.mqttPublish.numberOfProcessesCreated - i)));
		}
		for(int i=0; i<Main.mqttPublish.numberOfProcessesDeleted; i++) {
			deletionTemp.add(new Data(i+1+"", Main.mqttPublish.deletionProcessName.get(i), Main.mqttPublish.deletionProcessTime.get(i)));
		}
		
		/*creationData.removeAll(creationData);
		FXCollections.copy(creationData, creationTemp);
		creationTable.setItems(creationData);
		
		deletionData.removeAll(deletionData);
		FXCollections.copy(deletionData, deletionTemp);
		deletionTable.setItems(deletionData);*/
		deletionTable.getItems().clear();
		deletionTable.getItems().addAll(deletionTemp);
	}
}


