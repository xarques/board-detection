package com.seuqra.opencv;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
	// the FXML area for showing the current frame
	@FXML
	private ImageView originalFrame;
	// the FXML area for showing the mask
	@FXML
	private ImageView maskImage0;
	@FXML
	private ImageView maskImage1;
	@FXML
	private ImageView maskImage2;
	@FXML
	private ImageView maskImage3;
	@FXML
	private ImageView maskImage4;
	@FXML
	private ImageView maskImage5;
	@FXML
	private ImageView maskImage6;
	@FXML
	private ImageView maskImage7;
	@FXML
	private ImageView maskImage8;
	@FXML
	private ImageView maskImage9;
	@FXML
	private ImageView maskImage10;
	@FXML
	private ImageView maskImage11;
	@FXML
	private ImageView maskImage12;
	@FXML
	private ImageView maskImage13;
	@FXML
	private ImageView maskImage14;
	@FXML
	private ImageView maskImage15;
	@FXML
	private ImageView maskImage16;
	@FXML
	private ImageView maskImage17;


	List<Scalar> minValuesList = new ArrayList<Scalar>();
	List<Scalar> maxValuesList = new ArrayList<Scalar>();

	@FXML
	private ImageView spectrum0;
	@FXML
	private ImageView spectrum1;
	@FXML
	private ImageView spectrum2;
	@FXML
	private ImageView spectrum3;
	@FXML
	private ImageView spectrum4;
	@FXML
	private ImageView spectrum5;
	@FXML
	private ImageView spectrum6;
	@FXML
	private ImageView spectrum7;
	@FXML
	private ImageView spectrum8;
	@FXML
	private ImageView spectrum9;
	@FXML
	private ImageView spectrum10;
	@FXML
	private ImageView spectrum11;
	@FXML
	private ImageView spectrum12;
	@FXML
	private ImageView spectrum13;
	@FXML
	private ImageView spectrum14;
	@FXML
	private ImageView spectrum15;
	@FXML
	private ImageView spectrum16;
	@FXML
	private ImageView spectrum17;

	/**
	 * Init the controller, at start time
	 */
	protected void init() {
		startRecognition();
	}

	private void buildScalar(List<Scalar> minValuesList,
			List<Scalar> maxValuesList) {
		int range = 10;
		for (int i = 0; i <= 180; i = i + range) {
			//minValuesList.add(new Scalar(i, 92, 49));
			minValuesList.add(new Scalar(i, 92, 49));
			maxValuesList.add(new Scalar(i + range - 1, 255, 255));
		}
		System.out.println("BoardRecognitionController.buildScalar() minValuesList = "+ minValuesList);
		System.out.println("BoardRecognitionController.buildScalar() maxValuesList = "+ maxValuesList);
	}

	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	private void startRecognition() {
		// set a fixed width for all the image to show and preserve image ratio
		Utils.imageViewProperties(this.originalFrame, 1000);
		//Utils.imageViewProperties(this.originalFrame, 500);
		int width = 100;
		Utils.imageViewProperties(this.maskImage0, width);
		Utils.imageViewProperties(this.maskImage1, width);
		Utils.imageViewProperties(this.maskImage2, width);
		Utils.imageViewProperties(this.maskImage3, width);
		Utils.imageViewProperties(this.maskImage4, width);
		Utils.imageViewProperties(this.maskImage5, width);
		Utils.imageViewProperties(this.maskImage6, width);
		Utils.imageViewProperties(this.maskImage7, width);
		Utils.imageViewProperties(this.maskImage8, width);
		Utils.imageViewProperties(this.maskImage9, width);
		Utils.imageViewProperties(this.maskImage10, width);
		Utils.imageViewProperties(this.maskImage11, width);
		Utils.imageViewProperties(this.maskImage12, width);
		Utils.imageViewProperties(this.maskImage13, width);
		Utils.imageViewProperties(this.maskImage14, width);
		Utils.imageViewProperties(this.maskImage15, width);
		Utils.imageViewProperties(this.maskImage16, width);
		Utils.imageViewProperties(this.maskImage17, width);

		Utils.imageViewProperties(this.spectrum0, width);
		Utils.imageViewProperties(this.spectrum1, width);
		Utils.imageViewProperties(this.spectrum2, width);
		Utils.imageViewProperties(this.spectrum3, width);
		Utils.imageViewProperties(this.spectrum4, width);
		Utils.imageViewProperties(this.spectrum5, width);
		Utils.imageViewProperties(this.spectrum6, width);
		Utils.imageViewProperties(this.spectrum7, width);
		Utils.imageViewProperties(this.spectrum8, width);
		Utils.imageViewProperties(this.spectrum9, width);
		Utils.imageViewProperties(this.spectrum10, width);
		Utils.imageViewProperties(this.spectrum11, width);
		Utils.imageViewProperties(this.spectrum12, width);
		Utils.imageViewProperties(this.spectrum13, width);
		Utils.imageViewProperties(this.spectrum14, width);
		Utils.imageViewProperties(this.spectrum15, width);
		Utils.imageViewProperties(this.spectrum16, width);
		Utils.imageViewProperties(this.spectrum17, width);

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
		//final Mat image = Imgcodecs.imread("resources/postits.jpg");
		final Mat image = Imgcodecs.imread("resources/board.jpg");
		//final Mat image = Imgcodecs.imread("resources/board2.jpg");
		Image imageToShow = extractColors(image, minValuesList, maxValuesList);

		originalFrame.setImage(imageToShow);

	}

	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Image extractColors(Mat originalImage , List<Scalar> minColors,	List<Scalar> maxColors) {
		// init everything
		Image imageToShow = null;

		Mat image = originalImage.clone();
		List<Rect> allRectangles = new ArrayList<Rect>();
		try {
			for (int i = 0; i < minColors.size(); i++) {
				Scalar minColor = minColors.get(i);
				Scalar maxColor = maxColors.get(i);
				Mat mask = Utils.extractColor(originalImage, minColor, maxColor);
				List<Rect> rectangles = Utils.getRectangles(mask);
				allRectangles.addAll(rectangles);
				
				// show the partial output
				ImageView fxImage = getImage(i);
				if (fxImage != null) {
					Utils.onFXThread(fxImage.imageProperty(),
							Utils.mat2Image(mask));
				}
				ImageView spectrumImage = getSpectrum(i);
				if (spectrumImage != null) {
					Utils.onFXThread(spectrumImage.imageProperty(),
							Utils.mat2Image(Utils.getSpectrum(minColor, maxColor)));
				}
			}
			List<Rect> filteredRectangles = Utils.keepNumerousRectangles(allRectangles);		
			Utils.drawContours(filteredRectangles, image);
			// convert the Mat object (OpenCV) to Image (JavaFX)
			imageToShow = Utils.mat2Image(image);

		} catch (Exception e) {
			// log the (full) error
			System.err.print("ERROR");
			e.printStackTrace();
		}

		return imageToShow;
	}

	private ImageView getImage(int i) {
		switch (i) {
		case 0:
			return maskImage0;
		case 1:
			return maskImage1;
		case 2:
			return maskImage2;
		case 3:
			return maskImage3;
		case 4:
			return maskImage4;
		case 5:
			return maskImage5;
		case 6:
			return maskImage6;
		case 7:
			return maskImage7;
		case 8:
			return maskImage8;
		case 9:
			return maskImage9;
		case 10:
			return maskImage10;
		case 11:
			return maskImage11;
		case 12:
			return maskImage12;
		case 13:
			return maskImage13;
		case 14:
			return maskImage14;
		case 15:
			return maskImage15;
		case 16:
			return maskImage16;
		case 17:
			return maskImage17;
		}
		return null;
	}

	private ImageView getSpectrum(int i) {
		switch (i) {
		case 0:
			return spectrum0;
		case 1:
			return spectrum1;
		case 2:
			return spectrum2;
		case 3:
			return spectrum3;
		case 4:
			return spectrum4;
		case 5:
			return spectrum5;
		case 6:
			return spectrum6;
		case 7:
			return spectrum7;
		case 8:
			return spectrum8;
		case 9:
			return spectrum9;
		case 10:
			return spectrum10;
		case 11:
			return spectrum11;
		case 12:
			return spectrum12;
		case 13:
			return spectrum13;
		case 14:
			return spectrum14;
		case 15:
			return spectrum15;
		case 16:
			return spectrum16;
		case 17:
			return spectrum17;
		}
		return null;
	}
}
