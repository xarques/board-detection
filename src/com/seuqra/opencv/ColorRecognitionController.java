package com.seuqra.opencv;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the image segmentation process.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 1.5 (2015-11-26)
 * @since 1.0 (2015-01-13)
 * 
 */
public class ColorRecognitionController {
	// FXML camera button
	@FXML
	private Button cameraButton;
	// the FXML area for showing the current frame
	@FXML
	private ImageView originalFrame;
	// the FXML area for showing the mask
	@FXML
	private ImageView maskImage;
	// the FXML area for showing the output of the morphological operations
	@FXML
	private ImageView morphImage;
	@FXML
	private ImageView spectrum;
	// FXML slider for setting HSV ranges
	@FXML
	private Slider hueStart;
	@FXML
	private Slider hueStop;
	@FXML
	private Slider saturationStart;
	@FXML
	private Slider saturationStop;
	@FXML
	private Slider valueStart;
	@FXML
	private Slider valueStop;
	// FXML label to show the current values set with the sliders
	@FXML
	private Label hsvCurrentValues;

	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;

	// property for object binding
	private ObjectProperty<String> hsvValuesProp;

	/**
	 * Init the controller, at start time
	 */
	protected void init() {
		startRecognition();
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	private void startRecognition() {
		// bind a text property with the string containing the current range of
		// HSV values for object detection
		hsvValuesProp = new SimpleObjectProperty<>();
		this.hsvCurrentValues.textProperty().bind(hsvValuesProp);

		// set a fixed width for all the image to show and preserve image ratio
		//this.imageViewProperties(this.originalFrame, 1000);
		Utils.imageViewProperties(this.originalFrame, 500);
		Utils.imageViewProperties(this.maskImage, 200);
		Utils.imageViewProperties(this.morphImage, 200);
		Utils.imageViewProperties(this.spectrum, 200);
		
		Runnable frameGrabber = new Runnable() {
			@Override
			public void run() {
				//final Mat image = Imgcodecs.imread("resources/postits.jpg"); 
				final Mat originalImage = Imgcodecs.imread("resources/board.jpg");
				// final Mat image = Imgcodecs.imread("resources/board2.jpg");

				// Get the color spectrum from the UI
				final Scalar minColor = new Scalar(Math.round(hueStart
						.getValue()), Math.round(saturationStart.getValue()),
						Math.round(valueStart.getValue()));
				final Scalar maxColor = new Scalar(Math.round(hueStop
						.getValue()), Math.round(saturationStop.getValue()),
						Math.round(valueStop.getValue()));
				
				// Extract the black and white image
				Mat mask = Utils.extractColor(originalImage, minColor, maxColor);
				
				// Apply morpho
				//Mat morphOutput = Utils.morpho(mask);
				Mat morphOutput = mask;
				// Extract rectangles
				List<Rect> rectangles = Utils.getRectangles(morphOutput);
				
				// Keep the numerous rectangles
				//rectangles = Utils.keepNumerousRectangles(rectangles);		
				
				// Display image with contours
				Image imageToShow = Utils.mat2Image(Utils.drawContours(rectangles, originalImage));
				originalFrame.setImage(imageToShow);
				
				// JavaFX updates
				// show the current selected HSV range
				String valuesToPrint = "Hue range: " + minColor.val[0] + "-"
						+ maxColor.val[0] + "\tSaturation range: "
						+ minColor.val[1] + "-" + maxColor.val[1]
						+ "\tValue range: " + minColor.val[2] + "-"
						+ maxColor.val[2];
				Utils.onFXThread(hsvValuesProp, valuesToPrint);

				// show the partial output
				Utils.onFXThread(maskImage.imageProperty(),
						Utils.mat2Image(mask));

				// show the morph output
				Utils.onFXThread(morphImage.imageProperty(),
						Utils.mat2Image(morphOutput));

				// show the colr spectrum
				Utils.onFXThread(spectrum.imageProperty(),
						Utils.mat2Image(Utils.getSpectrum(minColor, maxColor)));
			}
		};

		this.timer = Executors.newSingleThreadScheduledExecutor();
		this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
	}
}
