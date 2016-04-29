package com.seuqra.opencv;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.event.ListSelectionEvent;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
public class BoardRecognitionController {
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

	List<Scalar> minValuesList = new ArrayList<Scalar>();
	List<Scalar> maxValuesList = new ArrayList<Scalar>();

	// property for object binding
	private ObjectProperty<String> hsvValuesProp;

	/**
	 * Init the controller, at start time
	 */
	protected void init() {
		// startRecognition();
	}

	private void buildScalar(List<Scalar> minValuesList,
			List<Scalar> maxValuesList) {
		for (int i = 0; i <= 169; i = i + 11) {
			minValuesList.add(new Scalar(i, 92, 49));
			maxValuesList.add(new Scalar(i + 10, 255, 255));
		}
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
		// this.imageViewProperties(this.originalFrame, 1000);
		this.imageViewProperties(this.originalFrame, 500);
		this.imageViewProperties(this.maskImage, 200);
		this.imageViewProperties(this.morphImage, 200);

		buildScalar(minValuesList, maxValuesList);
		/*
		 * // Postit orange //minValuesList.add(new Scalar(9, 34, 47));
		 * //maxValuesList.add(new Scalar(15, 255, 255)); // Postit orange
		 * minValuesList.add(new Scalar(15, 60, 50)); maxValuesList.add(new
		 * Scalar(28, 255, 255)); // Postit yellow 2 //minValuesList.add(new
		 * Scalar(28, 34, 47)); //maxValuesList.add(new Scalar(33, 255, 255));
		 * 
		 * // Postit green minValuesList.add(new Scalar(46, 111, 47));
		 * maxValuesList.add(new Scalar(72, 255, 255));
		 * 
		 * // Postit blue minValuesList.add(new Scalar(59, 92, 49));
		 * maxValuesList.add(new Scalar(115, 255, 255));
		 * 
		 * // Postit blue //minValuesList.add(new Scalar(100, 60, 50));
		 * //maxValuesList.add(new Scalar(106, 255, 255));
		 * 
		 * // Postit blue2 minValuesList.add(new Scalar(125, 92, 49));
		 * maxValuesList.add(new Scalar(132, 224, 255));
		 * 
		 * // Postit violet2 minValuesList.add(new Scalar(158, 92, 49));
		 * maxValuesList.add(new Scalar(165, 238, 255));
		 * 
		 * 
		 * // Postit violet minValuesList.add(new Scalar(168, 175, 50));
		 * maxValuesList.add(new Scalar(180, 255, 255));
		 */
		// Postit violet3
		// minValuesList.add(new Scalar(167, 140, 49));
		// maxValuesList.add(new Scalar(177, 255, 255));
		 final Mat image = Imgcodecs.imread("resources/postits.jpg");
		// final Mat image = Imgcodecs.imread("resources/board.jpg");
		//final Mat image = Imgcodecs.imread("resources/board2.jpg");
		Image imageToShow = extractColor(image, minValuesList, maxValuesList);

		originalFrame.setImage(imageToShow);
		/*
		 * Runnable frameGrabber = new Runnable() {
		 * 
		 * @Override public void run() { final Mat image =
		 * Imgcodecs.imread("resources/postits.jpg"); //final Mat image =
		 * Imgcodecs.imread("resources/board.jpg"); //final Mat image =
		 * Imgcodecs.imread("resources/board2.jpg");
		 * 
		 * /* final Scalar minValues = new
		 * Scalar(Math.round(hueStart.getValue()),
		 * Math.round(saturationStart.getValue()),
		 * Math.round(valueStart.getValue())); final Scalar maxValues = new
		 * Scalar(Math.round(hueStop.getValue()),
		 * Math.round(saturationStop.getValue()),
		 * Math.round(valueStop.getValue())); Image imageToShow =
		 * extractColor(image, minValues, maxValues);
		 */
		/*
		 * Image imageToShow = extractColor(image, minValuesList,
		 * maxValuesList);
		 * 
		 * originalFrame.setImage(imageToShow); } };
		 * 
		 * this.timer = Executors.newSingleThreadScheduledExecutor();
		 * this.timer.scheduleAtFixedRate(frameGrabber, 0, 33,
		 * TimeUnit.MILLISECONDS);
		 */
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Image extractColor(Mat image, Scalar minValues, Scalar maxValues) {
		List<Scalar> minValuesList = new ArrayList<Scalar>();
		minValuesList.add(minValues);
		List<Scalar> maxValuesList = new ArrayList<Scalar>();
		maxValuesList.add(maxValues);
		return extractColor(image, minValuesList, maxValuesList);
	}

	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Image extractColor(Mat image, List<Scalar> minValuesList,
			List<Scalar> maxValuesList) {
		// init everything
		Image imageToShow = null;

		List<Rect> allRectangles = new ArrayList<Rect>();
		try {
			for (int i = 0; i < minValuesList.size(); i++) {
				Scalar minValues = minValuesList.get(i);
				Scalar maxValues = maxValuesList.get(i);

				// init
				Mat blurredImage = new Mat();
				Mat hsvImage = new Mat();
				Mat mask = new Mat();
				Mat morphOutput = new Mat();

				// remove some noise
				Imgproc.blur(image, blurredImage, new Size(7, 7));

				// convert the frame to HSV
				Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

				// get thresholding values from the UI
				// remember: H ranges 0-180, S and V range 0-255
				// System.out.println("BoardRecognitionController.grabFrame() this.hueStart.getValue() ="+this.hueStart.getValue());

				// show the current selected HSV range
				String valuesToPrint = "Hue range: " + minValues.val[0] + "-"
						+ maxValues.val[0] + "\tSaturation range: "
						+ minValues.val[1] + "-" + maxValues.val[1]
						+ "\tValue range: " + minValues.val[2] + "-"
						+ maxValues.val[2];
				this.onFXThread(this.hsvValuesProp, valuesToPrint);

				// threshold HSV image to select tennis balls
				Core.inRange(hsvImage, minValues, maxValues, mask);
				// show the partial output
				this.onFXThread(this.maskImage.imageProperty(),
						this.mat2Image(mask));

				// morphological operators
				// dilate with large element, erode with small ones
				Mat dilateElement = Imgproc.getStructuringElement(
						Imgproc.MORPH_RECT, new Size(24, 24));
				Mat erodeElement = Imgproc.getStructuringElement(
						Imgproc.MORPH_RECT, new Size(12, 12));

				morphOutput = mask;

				// Imgproc.erode(mask, morphOutput, erodeElement);
				// Imgproc.erode(mask, morphOutput, erodeElement);

				// Imgproc.dilate(mask, morphOutput, dilateElement);
				// Imgproc.dilate(mask, morphOutput, dilateElement);

				// show the partial output
				this.onFXThread(this.morphImage.imageProperty(),
						this.mat2Image(morphOutput));

				// find the tennis ball(s) contours and show them
				//image = this.findAndDrawContours(morphOutput, image);
				List<Rect> rectangles = getRectangles(morphOutput);

				allRectangles.addAll(rectangles);
				// convert the Mat object (OpenCV) to Image (JavaFX)
			}
			allRectangles = sortRectangles(allRectangles);
			//System.out.println("BoardRecognitionController.findAndDrawContours() rectangles = "+rectangles);
			allRectangles = filterRectangles(allRectangles);
			drawContours(allRectangles, image);
			imageToShow = mat2Image(image);

		} catch (Exception e) {
			// log the (full) error
			System.err.print("ERROR");
			e.printStackTrace();
		}

		return imageToShow;
	}

	/**
	 * Given a binary image containing one or more closed surfaces, use it as a
	 * mask to find and highlight the objects contours
	 * 
	 * @param maskedImage
	 *            the binary image to be used as a mask
	 * @param frame
	 *            the original {@link Mat} image to be used for drawing the
	 *            objects contours
	 * @return the {@link Mat} image with the objects contours framed
	 */
	private Mat findAndDrawContours(Mat maskedImage, Mat frame) {
		Scalar color = new Scalar(250, 0, 0);
		int thickness = 2;
		
		List<Rect> rectangles = computeRectangles(maskedImage);
		
		for (Rect rect : rectangles) {
			Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(
					rect.x + rect.width, rect.y + rect.height), color,
					thickness);
		}

		return frame;
	}

	/**
	 * Highlight the objects contours based on the rectangles list
	 * 
	 * @param rectangles
	 *            the contours to draw
	 * @param frame
	 *            the original {@link Mat} image to be used for drawing the
	 *            objects contours
	 * @return the {@link Mat} image with the objects contours framed
	 */
	private Mat drawContours(List<Rect> rectangles, Mat frame) {
		Scalar color = new Scalar(250, 0, 0);
		int thickness = 2;
				
		for (Rect rect : rectangles) {
			Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(
					rect.x + rect.width, rect.y + rect.height), color,
					thickness);
		}

		return frame;
	}
	
	private List<Rect> computeRectangles(Mat maskedImage) {
		List<Rect> rectangles = getRectangles(maskedImage);
		rectangles = sortRectangles(rectangles);
		System.out.println("BoardRecognitionController.findAndDrawContours() rectangles = "+rectangles);
		rectangles = filterRectangles(rectangles);
		return rectangles;
	}

	/**
	 * Gets all the bounding rectangles of the forms found in the image
	 * 
	 * @param maskedImage
	 *            the binary image to be used as a mask
	 * @return the List of {@link Rect} representing the contours of the
	 *         rectangles found in the images
	 */
	private List<Rect> getRectangles(Mat maskedImage) {
		// init
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		List<Rect> rectangles = new ArrayList<Rect>();
		// find contours
		Imgproc.findContours(maskedImage, contours, hierarchy,
				Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		// if any contour exist...
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
			// for each contour, display it in blue
			for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
				rectangles.add(Imgproc.boundingRect(contours.get(idx)));
			}
		}

		return rectangles;
	}

	private void drawRectangleOld(Mat frame, MatOfPoint contours, Scalar color,
			int thickness) {
		/*
		 * MatOfPoint2f approxCurve = new MatOfPoint2f(); MatOfPoint2f contour2f
		 * = new MatOfPoint2f( contours.toArray() ); //Processing on mMOP2f1
		 * which is in type MatOfPoint2f double approxDistance =
		 * Imgproc.arcLength(contour2f, true)*0.02;
		 * Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
		 * 
		 * //Convert back to MatOfPoint MatOfPoint points = new MatOfPoint(
		 * approxCurve.toArray() );
		 */
		int minSize = 180;
		int maxSize = 220;

		// Get bounding rect of contour
		// Rect rect = Imgproc.boundingRect(points);
		Rect rect = Imgproc.boundingRect(contours);
		// if(rect.height > 50 && rect.height < 100) {
		// System.out.println("DrawContourDetectionController.getRectangles() rect.height= "+rect.height);
		// System.out.println("DrawContourDetectionController.getRectangles() rect.width= "+rect.width);
		if (rect.height > minSize && rect.height < maxSize
				&& rect.width > minSize && rect.width < maxSize) {
			// draw enclosing rectangle (all same color, but you could use
			// variable i to make them unique)
			// Core.rectangle(destination, new Point(rect.x,rect.y), new
			// Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255, 0,
			// 255), 3);
			// Imgproc.rectangle(croppedImage, new Point(rect.x, rect.y), new
			// Point(rect.x+rect.width, rect.y+rect.height), new Scalar(255, 0,
			// 255), 3);
			Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(
					rect.x + rect.width, rect.y + rect.height), color,
					thickness);
		}
	}

	/*
	 * Sort rectangle depending on their areas
	 */
	private List<Rect> sortRectangles(List<Rect> rectangles) {
		List<Rect> sortedRectangles = new ArrayList<>(rectangles);
		// Sorting
		Collections.sort(sortedRectangles, new Comparator<Rect>() {
			@Override
			public int compare(Rect rect1, Rect rect2) {

				if (rect1.height * rect1.width < rect2.height * rect2.width) {
					return -1;
				} else if (rect1.height * rect1.width > rect2.height
						* rect2.width) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return sortedRectangles;
	}

	/*
	 * Remove rectangles and small surfaces. Keep squares.
	 * @param rectangles The rectangles to filter
	 * @return the filtered forms (squares)
	 */
	private List<Rect> filterRectangles(List<Rect> rectangles) {
		List<Rect> filteredRectangles = new ArrayList<Rect>();
		for (Rect rect : rectangles) {
			int approximation = 30;
			int widthMax = rect.width + (rect.width * approximation) / 100;
			int widthMin = rect.width - (rect.width * approximation) / 100;
			int heightMax = rect.height + (rect.height * approximation) / 100;
			int heightMin = rect.height - (rect.height * approximation) / 100;
			int minSize = 70;
			int maxSize = 220;
			if (rect.height > minSize && rect.width > minSize) {
				if ((widthMin < rect.height && rect.height < widthMax)
						&& (heightMin < rect.width && rect.width < heightMax)) {
					filteredRectangles.add(rect);
				}
			}
		}
		return filteredRectangles;
	}

	/**
	 * Set typical {@link ImageView} properties: a fixed width and the
	 * information to preserve the original image ration
	 * 
	 * @param image
	 *            the {@link ImageView} to use
	 * @param dimension
	 *            the width of the image to set
	 */
	private void imageViewProperties(ImageView image, int dimension) {
		// set a fixed width for the given ImageView
		image.setFitWidth(dimension);
		// preserve the image ratio
		image.setPreserveRatio(true);
	}

	/**
	 * Convert a {@link Mat} object (OpenCV) in the corresponding {@link Image}
	 * for JavaFX
	 * 
	 * @param frame
	 *            the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	private Image mat2Image(Mat frame) {
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}

	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property
	 *            a {@link ObjectProperty}
	 * @param value
	 *            the value to set for the given {@link ObjectProperty}
	 */
	private <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				property.set(value);
			}
		});
	}

}
