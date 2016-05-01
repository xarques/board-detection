package com.seuqra.opencv;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Utils {
	
    static Mat getSpectrum(Scalar minColor, Scalar maxColor) {
        double minH = minColor.val[0];
        double maxH = maxColor.val[0];

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }
        Mat spectrumRGB = new Mat();
        // Convert HSV image to RGB format
        Imgproc.cvtColor(spectrumHsv, spectrumRGB, Imgproc.COLOR_HSV2RGB_FULL, 4);
        return spectrumRGB;
    }

	/**
	 * Convert a {@link Mat} object (OpenCV) in the corresponding {@link Image}
	 * for JavaFX
	 * 
	 * @param frame
	 *            the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	static Image mat2Image(Mat frame) {
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
	/*
	 * Remove rectangles and small surfaces. Keep squares.
	 * @param rectangles The rectangles to filter
	 * @return the filtered forms (squares)
	 */
	static List<List<Rect>> groupRectangles(List<Rect> rectangles) {
		List<List<Rect>> groupedBySurfaceRectangles = new ArrayList<List<Rect>>();
		List<Rect> currentGroup = new ArrayList<Rect>();
		groupedBySurfaceRectangles.add(currentGroup);
		for (Rect rect : rectangles) {
			int approximation = 10;
			int widthMax = rect.width + (rect.width * approximation) / 100;
			int widthMin = rect.width - (rect.width * approximation) / 100;
			int heightMax = rect.height + (rect.height * approximation) / 100;
			int heightMin = rect.height - (rect.height * approximation) / 100;
			int surfaceMin = widthMin * heightMin;
			int surfaceMax = widthMax * heightMax;
			if (currentGroup.size() != 0) {
				Rect firstRectangle = currentGroup.get(0);
				int surface = firstRectangle.width * firstRectangle.height;
				if (surface < surfaceMin || surface > surfaceMax) {
					// Not the same surface. Create a new group
					currentGroup = new ArrayList<Rect>();
					groupedBySurfaceRectangles.add(currentGroup);	
				} // else the rectangle will be added in the current group
			} // else the rectangle will be added in the current group
			currentGroup.add(rect);
		}
		System.out.println("BoardRecognitionController.filterRectangles2() Number of groups = "+groupedBySurfaceRectangles.size());
		return groupedBySurfaceRectangles;
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
	static Mat drawContours(List<Rect> rectangles, Mat frame) {
		Scalar color = new Scalar(250, 0, 0);
		int thickness = 8;
				
		for (Rect rect : rectangles) {
			Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(
					rect.x + rect.width, rect.y + rect.height), color,
					thickness);
		}

		return frame;
	}

	/**
	 * Gets all the bounding rectangles of the forms found in the image
	 * 
	 * @param maskedImage
	 *            the binary image to be used as a mask
	 * @return the List of {@link Rect} representing the contours of the
	 *         rectangles found in the images
	 */
	static List<Rect> getRectangles(Mat maskedImage) {
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

	/*
	 * Sort rectangle depending on their areas
	 * @param rectangles The list of rectangles to sort by ascending surface
	 * @return the sorted rectangles
	 */
	static List<Rect> sortRectangles(List<Rect> rectangles) {
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
	 * Sort group of rectangles depending on their quantities
	 * @param rectangles The list of groups of rectangles to sort by descending quantities
	 * @return the sorted list of rectangles
	 */
	static List<List<Rect>> sortGroupsOfRectangles(List<List<Rect>> groupsOfRectangles) {
		// Sorting
		Collections.sort(groupsOfRectangles, new Comparator<List<Rect>>() {
			@Override
			public int compare(List<Rect> listRect1, List<Rect> listRect2) {

				if (listRect1.size() < listRect2.size()) {
					return 1;
				} else if (listRect1.size() < listRect2.size()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		return groupsOfRectangles;
	}

	/*
	 * Remove rectangles and small surfaces. Keep squares.
	 * @param rectangles The rectangles to filter
	 * @return the filtered forms (squares)
	 */
	static List<Rect> filterRectangles(List<Rect> rectangles) {
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
				// Keep squares
				//if ((widthMin < rect.height && rect.height < widthMax)
					//	&& (heightMin < rect.width && rect.width < heightMax)) {
					filteredRectangles.add(rect);
				//}
			}
		}
		return filteredRectangles;
	}


	static List<Rect> keepNumerousRectangles(List<Rect> rectangles) {
		List<Rect> filteredRectangles = new ArrayList<Rect>();
		List<Rect> allRectangles = Utils.sortRectangles(rectangles);
		allRectangles = Utils.filterRectangles(allRectangles);
		List<List<Rect>> allGroupedRectangles  = Utils.groupRectangles(allRectangles);
		List<List<Rect>> sortedGroupedRectangles = Utils.sortGroupsOfRectangles(allGroupedRectangles);
		// Keep only the 2 numbered rectangles family 
		for (int i = 0; i < 2 && i < sortedGroupedRectangles.size(); i++) {
			filteredRectangles.addAll(sortedGroupedRectangles.get(i));
		}
		return filteredRectangles;
	}
	
	/**
	 * Return a black and white image. The black color corresponds to the searched color (between minColor and maxColor)
	 * 
	 * @return the black and white {@link Mat}
	 */
	static Mat extractColor(Mat originalImage, Scalar minColor, Scalar maxColor) {
		Mat image = originalImage.clone();
		// init
		Mat blurredImage = new Mat();
		Mat hsvImage = new Mat();
		Mat mask = new Mat();

		// remove some noise
		Imgproc.blur(image, blurredImage, new Size(7, 7));

		// convert the frame to HSV
		Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

		// threshold HSV image to select color
		Core.inRange(hsvImage, minColor, maxColor, mask);

		return mask;
	}

	/**
	 * Perform morphological operators: dilate with large element, erode with small ones
	 * @param mask the original image
	 * @return the transformed {@link Mat}
	 */
	static Mat morpho(Mat mask) {
		Mat morphOutput = new Mat();

		// morphological operators
		// dilate with large element, erode with small ones
		Mat dilateElement = Imgproc.getStructuringElement(
				Imgproc.MORPH_RECT, new Size(24, 24));
		Mat erodeElement = Imgproc.getStructuringElement(
				Imgproc.MORPH_RECT, new Size(12, 12));

		Imgproc.erode(mask, morphOutput, erodeElement);
		Imgproc.erode(mask, morphOutput, erodeElement);

		Imgproc.dilate(mask, morphOutput, dilateElement);
		Imgproc.dilate(mask, morphOutput, dilateElement);
		
		return morphOutput;
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
	static void imageViewProperties(ImageView image, int dimension) {
		// set a fixed width for the given ImageView
		image.setFitWidth(dimension);
		// preserve the image ratio
		image.setPreserveRatio(true);
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
	static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				property.set(value);
			}
		});
	}
}
