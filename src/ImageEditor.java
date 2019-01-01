import java.io.*;
//import java.io.FileWriter;
//import java.io.BufferedWriter;
//import java.io.PrintWriter;
import java.lang.StringBuilder;
import java.lang.Math;
import java.util.Scanner;

public class ImageEditor {

	public static void main(String[] args) {

				if (i == 0 || j == 0) {
					v = 128;
				}
				else {
					redDiff = redPixels[i][j] - redPixels[i - 1][j - 1];
					greenDiff = greenPixels[i][j] - greenPixels[i - 1][j - 1];
					blueDiff = bluePixels[i][j] - bluePixels[i - 1][j - 1];
					v = findMaxDifference(redDiff, greenDiff, blueDiff);
					v += 128;

					if (v < 0) {
						v = 0;
					}
					else if (v > 255) {
		File inputFile = null;
		File outputFile = null;
		Scanner scanner = null;
		try {
			inputFile = new File(args[0]);
			FileInputStream fis = new FileInputStream(inputFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			scanner = new Scanner(bis);
			scanner.useDelimiter("((#[^\\n]*\\n)|(\\s+))+"); //take out all the spaces
		} catch (Exception e) {
			System.err.println("Input file not found");
		}

		String header = null;
		int width = 0;
		int height = 0;
		int maxColorValue = 255;
		String pixels = null;

		try {
			header = scanner.next();
			width = scanner.nextInt();
			height = scanner.nextInt();
			maxColorValue = scanner.nextInt();
		} catch (Exception e) {
			System.err.println("Empty or missing info in ppm file");
		}

		try {
			outputFile = new File(args[1]);
		} catch (Exception e) {
			System.err.println("Output file not found");
		}

		try {
			String editType = args[2];
			if (editType.equals("invert")) {
				pixels = invert(scanner);
			}
			else if (editType.equals("grayscale")) {
				pixels = grayscale(scanner);
			}
			else if (editType.equals("emboss")) {
				pixels = emboss(scanner, height, width);
			}
			else if (editType.equals("motionblur")) {
				int blurStrength = 0;
				try {
					blurStrength = Integer.valueOf(args[3]);
				} catch (Exception e) {
					System.err.println("Please enter integer value > 0 for blur strength. You entered: " + args[3]);
				}
				pixels = motionBlur(scanner, height, width, blurStrength);
			}
			else {
				System.out.println("invalid transformation type");
			}
		} catch (Exception e) {
			System.err.println("Something went wrong with the argument " + args[2]);
		}

		try {
			FileWriter fw = new FileWriter(outputFile);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);

			pw.println(header);
			pw.print(width);
			pw.print(" ");
			pw.println(height);
			pw.println(maxColorValue);
			pw.print(pixels.toString());
			pw.close();
		} catch (Exception e) {
			System.err.println("Error writing to the output file");
		} finally {
			scanner.close();
		}
	}

	private static String invert(Scanner scanner) {
		StringBuilder pixels = new StringBuilder();
		int pixel;
		while (scanner.hasNextInt()) {
			pixel = scanner.nextInt();
			pixel = pixel ^ 0xff; // this will invert the pixels
			pixels.append(pixel);
			pixels.append("\n");
		}
		return pixels.toString();
	}

	private static String grayscale(Scanner scanner) {
		StringBuilder pixels = new StringBuilder();
		int red;
		int green;
		int blue;
		int average;
		while (scanner.hasNextInt()) {
			red = scanner.nextInt();
			green = scanner.nextInt();
			blue = scanner.nextInt();
			average = (red + green + blue) / 3;
			pixels.append(average);
			pixels.append("\n");
			pixels.append(average);
			pixels.append("\n");
			pixels.append(average);
			pixels.append("\n");
		}
		return pixels.toString();
	}

	private static String emboss(Scanner scanner, int height, int width) {
		StringBuilder pixels = new StringBuilder();
		int[][] redPixels = new int[height][width];
		int[][] greenPixels = new int[height][width];
		int[][] bluePixels = new int[height][width];
		int redDiff;
		int greenDiff;
		int blueDiff;
		int v = 0;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				redPixels[i][j] = scanner.nextInt();
				greenPixels[i][j] = scanner.nextInt();
				bluePixels[i][j] = scanner.nextInt();

				if (i == 0 || j == 0) {
					v = 128;
				}
				else {
					redDiff = redPixels[i][j] - redPixels[i - 1][j - 1];
					greenDiff = greenPixels[i][j] - greenPixels[i - 1][j - 1];
					blueDiff = bluePixels[i][j] - bluePixels[i - 1][j - 1];
					v = findMaxDifference(redDiff, greenDiff, blueDiff);
					v += 128;

					if (v < 0) {
						v = 0;
					}
					else if (v > 255) {
						v = 255;
					}
				}
				pixels.append(v);
				pixels.append("\n");
				pixels.append(v);
				pixels.append("\n");
				pixels.append(v);
				pixels.append("\n");
			}
		}
		return pixels.toString();
	}

	private static int findMaxDifference(int r, int g, int b) {
		//make sure to pass in the absolute values
		int max = (Math.abs(b) > Math.abs(g)) ? b : g;
		return ((Math.abs(max) > Math.abs(r)) ? max : r);
	}

	private static String motionBlur(Scanner scanner, int height, int width, int n) {
		StringBuilder pixels = new StringBuilder();
		int[][] redPixels = new int[height][width];
		int[][] greenPixels = new int[height][width];
		int[][] bluePixels = new int[height][width];
		int blurIndex;
		int count;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				redPixels[row][col] = scanner.nextInt();
				greenPixels[row][col] = scanner.nextInt();
				bluePixels[row][col] = scanner.nextInt();
			}
			for (int col = 0; col < width; col++) {
				blurIndex = ((col + n) >= width) ? width : (col + n);
				count = 1;
				for (int i = col + 1; i < blurIndex; i++) {
					redPixels[row][col] += redPixels[row][i];
					greenPixels[row][col] += greenPixels[row][i];
					bluePixels[row][col] += bluePixels[row][i];
					count++;
				}
				redPixels[row][col] = redPixels[row][col] / (count);
				greenPixels[row][col] = greenPixels[row][col] / (count);
				bluePixels[row][col] = bluePixels[row][col] / (count);
				pixels.append(redPixels[row][col]);
				pixels.append("\n");
				pixels.append(greenPixels[row][col]);
				pixels.append("\n");
				pixels.append(bluePixels[row][col]);
				pixels.append("\n");
			}
		}
		return pixels.toString();
	}

}
