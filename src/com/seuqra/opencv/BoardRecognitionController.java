package com.seuqra.opencv;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * The controller associated with the only view of our application.
 * The application logic is implemented here.
 * 
 * @author <a href="mailto:xavier.arques@seuqra.com">Xavier Arques</a>
 * @version 1.0 (2016-05-01)
 * 
 */
public class BoardRecognitionController {
	// the FXML area for showing the current frame
	@FXML
	private ImageView originalFrame;
	// the FXML area for showing the masks
	@FXML
	private ImageView maskImage0,maskImage1,maskImage2,maskImage3,maskImage4,maskImage5,maskImage6,maskImage7,maskImage8;
	@FXML
	private ImageView maskImage9,maskImage10,maskImage11,maskImage12,maskImage13,maskImage14,maskImage15,maskImage16,maskImage17;
	// the FXML area for showing the spectrums
	@FXML
	private ImageView spectrum0,spectrum1,spectrum2,spectrum3,spectrum4,spectrum5,spectrum6,spectrum7,spectrum8;
	@FXML
	private ImageView spectrum9,spectrum10,spectrum11,spectrum12,spectrum13,spectrum14,spectrum15,spectrum16,spectrum17;

	List<Scalar> minValuesList = new ArrayList<Scalar>();
	List<Scalar> maxValuesList = new ArrayList<Scalar>();
	
	private ImageView[] maskImages;
	private ImageView[] spectrums;
	
    private static final int MAX_RECTANGLES_GROUPS = 11;
	private static final int CONTOUR_THICKNESS = 8;

	/**
	 * Init the controller, at start time
	 */
	protected void init() {
		initFX();
		startRecognition();
	}

	private void initFX() {
		// Those arrays cannot be initialized earlier because all @FXML variables are assigned after the creation of maskImages and spectrums arrays
		maskImages = new ImageView[18];
		maskImages[0] = maskImage0;
		maskImages[1] = maskImage1;
		maskImages[2] = maskImage2;
		maskImages[3] = maskImage3;
		maskImages[4] = maskImage4;
		maskImages[5] = maskImage5;
		maskImages[6] = maskImage6;
		maskImages[7] = maskImage7;
		maskImages[8] = maskImage8;
		maskImages[9] = maskImage9;
		maskImages[10] = maskImage10;
		maskImages[11] = maskImage11;
		maskImages[12] = maskImage12;
		maskImages[13] = maskImage13;
		maskImages[14] = maskImage14;
		maskImages[15] = maskImage15;
		maskImages[16] = maskImage16;
		maskImages[17] = maskImage17;
		
		spectrums = new ImageView[18];
		spectrums[0] = spectrum0;
		spectrums[1] = spectrum1;
		spectrums[2] = spectrum2;
		spectrums[3] = spectrum3;
		spectrums[4] = spectrum4;
		spectrums[5] = spectrum5;
		spectrums[6] = spectrum6;
		spectrums[7] = spectrum7;
		spectrums[8] = spectrum8;
		spectrums[9] = spectrum9;
		spectrums[10] = spectrum10;
		spectrums[11] = spectrum11;
		spectrums[12] = spectrum12;
		spectrums[13] = spectrum13;
		spectrums[14] = spectrum14;
		spectrums[15] = spectrum15;
		spectrums[16] = spectrum16;
		spectrums[17] = spectrum17;	
	}
	
	/*
	 * Build a list of scalar representing all the color spectrum
	 * @param minValuesList The list to fill with the min spectrum colors
	 * @param maxValuesList The list to fill with the max spectrum colors
	 */
	private void buildSpectrumScalars(List<Scalar> minValuesList,
			List<Scalar> maxValuesList) {
		int range = 10;
		for (int i = 0; i < 180; i = i + range) {
			//minValuesList.add(new Scalar(i, 92, 49));
			minValuesList.add(new Scalar(i, 92, 49));
			maxValuesList.add(new Scalar(i + range - 1, 255, 255));
		}
	}

	/**
	 * 
	 */
	private void startRecognition() {
		// set a fixed width for all the image to show and preserve image ratio
		Utils.imageViewProperties(this.originalFrame, 1000);
		//Utils.imageViewProperties(this.originalFrame, 500);
		int width = 100;

		System.out.println("BoardRecognitionController.startRecognition() maskImage0 " + maskImage0);
		System.out.println("BoardRecognitionController.startRecognition() maskImage[0] " + maskImages[0]);
		for (int i = 0; i < maskImages.length; i++) {
			Utils.imageViewProperties(maskImages[i], width);
		}

		for (int i = 0; i < spectrums.length; i++) {
			Utils.imageViewProperties(spectrums[i], width);
		}

		buildSpectrumScalars(minValuesList, maxValuesList);
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
		//final Mat image = Imgcodecs.imread("resources/board.jpg");
		//final Mat image = Imgcodecs.imread("resources/board2.jpg");
		//final Mat image = Imgcodecs.imread("resources/2postitPink.jpg");
		//final Mat image = Imgcodecs.imread("resources/boardAC7.jpg");
		//final Mat image = Imgcodecs.imread("resources/board_20160513_085406.jpg");
		final Mat image = Imgcodecs.imread("resources/Office Lens 20160610-181315.jpg");
		
		
		
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
				ImageView fxImage = maskImages[i];
				if (fxImage != null) {
					Utils.onFXThread(fxImage.imageProperty(),
							Utils.mat2Image(mask));
				}
				ImageView spectrumImage = spectrums[i];
				if (spectrumImage != null) {
					Utils.onFXThread(spectrumImage.imageProperty(),
							Utils.mat2Image(Utils.getSpectrum(minColor, maxColor)));
				}
			}
			List<Rect> filteredRectangles = Utils.keepLargestNumberOfRectangles(allRectangles, MAX_RECTANGLES_GROUPS);		
			Utils.drawContours(filteredRectangles, image, CONTOUR_THICKNESS);
			// convert the Mat object (OpenCV) to Image (JavaFX)
			imageToShow = Utils.mat2Image(image);

		} catch (Exception e) {
			// log the (full) error
			System.err.print("ERROR");
			e.printStackTrace();
		}

		return imageToShow;
	}
}
