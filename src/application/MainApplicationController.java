package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mqtt.data.UnallowedList;

public class MainApplicationController implements Initializable {
	private static double xOffset = 0;
	private static double yOffset = 0;
	private String userPassword = "0000";
	static Stage secondStage;
	static Stage detailStage;
	@FXML
	private Text titleText;
	@FXML
    public Text clientIdText;
	@FXML
	private Button btnHome;
	@FXML
	private Button btnSetting;
	@FXML
	private Button btnMinimize;
	@FXML
	private Button btnClose;
	@FXML
	private Button btnNext;
	@FXML
	private Pane homePane;
	@FXML
	public ListView<String> blackListView;
	@FXML
	private Pane loginPane;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Pane settingPane;
	@FXML
	private Button resetBtn;
	@FXML
	public Text startTime;
	@FXML
	public Text lastDetectedTime;
	@FXML
    public Text numberOfProcessesCreated;
    @FXML
    public Text numberOfProcessesDeleted;
	@FXML
    private ImageView meerkatImg;
	@FXML
    private VBox lastDetectedTimeVbox;
	


    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		homePane.toFront();
		//meerkatImg.toFront();
		
		
		
		btnMinimize.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Main.getPrimaryStage().setIconified(true);
			}
		});

		btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MySystemTray.hide(Main.getPrimaryStage());
			}
		});

		btnHome.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				homePane.toFront();
			}
		});

		btnSetting.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				loginPane.toFront();
				passwordField.requestFocus();
				//meerkatImg.toFront();
			}
		});

		numberOfProcessesCreated.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				showDetailsDialog();
			}
		});
		numberOfProcessesDeleted.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//Main.detailController.tabPane.getSelectionModel().select(Main.detailController.deletionTab);
				showDetailsDialog();
			}
		});
		
		resetBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				showSettingDialog();
			}
		});

		btnNext.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (passwordField.getText().equals(userPassword)) {
					settingPane.toFront();
					passwordField.setText("");
				}
			}
		});

		passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (passwordField.getText().equals(userPassword) && event.getCode() == KeyCode.ENTER) {
					settingPane.toFront();
					passwordField.setText("");
				} else if (event.getCode() == KeyCode.ENTER) {
					passwordField.setText("");
				}
			}
		});
		
		//lastDetectedTimeVbox.getChildren().add(Main.lastDetectedTime);

		ObservableList<String> items = FXCollections.observableArrayList();
		Vector<String> list = new UnallowedList().list;
		for (int i = 0; i < list.size(); i++) {
			items.add(list.get(i));
		}
		blackListView.setItems(items);
	}
	

	public static void showSettingDialog() {
		Stage newStage = new Stage();
		newStage.setTitle("Setting");
		newStage.initStyle(StageStyle.UNDECORATED);
		
		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(Main.class/*getClass()*/.getResource("SettingDialog.fxml"));
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
					MainApplicationController.getSecondStage().setX(event.getScreenX() - xOffset);
					MainApplicationController.getSecondStage().setY(event.getScreenY() - yOffset);
				}
			});
			newStage.setResizable(false);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.initOwner(Main.getPrimaryStage());

			Scene scene = new Scene(root);
			//scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
			newStage.setScene(scene);
			newStage.showAndWait();
			setSecondStage(newStage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setSecondStage(Stage secondStage) {
	     MainApplicationController.secondStage = secondStage;
	}
	public static Stage getSecondStage() {
	     return secondStage;
	}
	
	public static void showDetailsDialog() {
		Stage detailStage = new Stage();
		detailStage.setTitle("");
		//detailStage.initStyle(StageStyle.UNDECORATED);
		
		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(Main.class.getResource("DetailsTableDialog.fxml"));
			
			detailStage.setResizable(false);
			detailStage.initModality(Modality.APPLICATION_MODAL);
			detailStage.initOwner(Main.getPrimaryStage());

			Scene scene = new Scene(root);
			//scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
			detailStage.setScene(scene);
			detailStage.showAndWait();
			setDetailStage(detailStage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void setDetailStage(Stage detailStage) {
	     MainApplicationController.detailStage = detailStage;
	}
	public static Stage getDetailStage() {
	     return detailStage;
	}

}
