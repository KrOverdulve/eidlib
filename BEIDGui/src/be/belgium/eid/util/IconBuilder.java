package be.belgium.eid.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Generate Images and ImageIcons from images to use on Labels and such.
 * 
 * @author Kristof Overdulve
 */
public class IconBuilder {
	/**
	 * Create an Image from a file
	 * 
	 * @param path
	 *            The path to the image
	 * @return the Image constructed from the given path
	 * @throws IOException
	 *             indicates that the picture to build couldn't be found
	 */
	public static Image createImage(String path) throws IOException {
		Image image = ImageIO.read(new File(path));
		return image;
	}

	/**
	 * Create an ImageIcon from a file
	 * 
	 * @param path
	 *            The path to the image
	 * @return the ImageIcon constructed from the given path
	 * @throws IOException
	 *             indicates that the picture to build couldn't be found
	 */
	public static ImageIcon createImageIcon(String path) throws IOException {
		return new ImageIcon(createImage(path));
	}
}
