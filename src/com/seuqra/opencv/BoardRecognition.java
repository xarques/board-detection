package com.seuqra.opencv;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class BoardRecognition extends Application {
	/**
	 * The main class for a JavaFX application. It creates and handles the main
	 * window with its resources (style, graphics, etc.).
	 * 
	 * @author <a href="mailto:xavier.arques@seuqra.com">Xavier Arques</a>
	 * @version 1.0 (2016-05-01)
	 * 
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource(
					"BoardRecognition.fxml"));
			BorderPane root = (BorderPane) loader.load();

			// set a whitesmoke background
			root.setStyle("-fx-background-color: whitesmoke;");
			// create and style a scene
			Scene scene = new Scene(root, 1600, 1200);
			scene.getStylesheets().add(
					getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("Object Recognition");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();

			BoardRecognitionController controller = loader.getController();
			controller.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
}