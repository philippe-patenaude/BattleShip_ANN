package battleship_ann;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import javax.swing.JApplet;
import java.net.URL;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Philippe
 */
public final class ResourceManagerZero {

    public static BufferedImage loadImage(String filepath) {

        File file = new File(filepath);

        if (! file.exists()) {
            try {
                throw new FileNotFoundException("File doesn't exist: \"" + file.getAbsolutePath() + "\"");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        BufferedImage img;

        try {
            img = ImageIO.read(file);
            if (img == null) {
                throw new IllegalArgumentException("File cannot be read: \"" + file.getAbsolutePath() + "\"");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }

        return img;

    }

    public static BufferedImage getHorizontallyFlippedImage(String filepath) {
        return getHorizontallyFlippedImage(loadImage(filepath));
    }

    public static BufferedImage getHorizontallyFlippedImage(BufferedImage sourceImg) {
        return getScaledImage(sourceImg, -1, 1);
    }

    public static BufferedImage getVerticallyFlippedImage(String filepath) {
        return getVerticallyFlippedImage(loadImage(filepath));
    }

    public static BufferedImage getVerticallyFlippedImage(BufferedImage sourceImg) {
        return getScaledImage(sourceImg, 1, -1);
    }
    
    public static BufferedImage getScaledImage(BufferedImage sourceImg, double x, double y) {
        
        BufferedImage img = new BufferedImage((int)(Math.abs(x)*sourceImg.getWidth()), (int)(Math.abs(y)*sourceImg.getHeight()), BufferedImage.TYPE_INT_ARGB);
        
        AffineTransform at;
        at = new AffineTransform();
        at.scale(x, y);
        if (x < 0) {
            if (y < 0) {
                at.translate(-sourceImg.getWidth(), -sourceImg.getHeight());
            } else {
                at.translate(-sourceImg.getWidth(), 0);
            }
        } else {
            if (y < 0) {
                at.translate(0, -sourceImg.getHeight());
            }
        }
        
        Graphics2D g = img.createGraphics();
        g.drawImage(sourceImg, at, null);
        g.dispose();

        return img;
    }

    public static boolean saveImage(BufferedImage img, String filepath) {

        String extension = "";

        // get the extension
        String temp = "";
        for (int i = filepath.length()-1; i >= 0; i--) {
            if (filepath.charAt(i) != '.') {
                temp += filepath.charAt(i);
            } else {
                break;
            }
        }
        for (int i = temp.length()-1; i >= 0; i--) {
            extension += temp.charAt(i);
        }

        File file = new File(filepath);

        try {
            boolean didWrite = ImageIO.write(img, extension, file);
            if (didWrite == false) {
                throw new IllegalArgumentException("Could not save the image: \"" + file.getAbsolutePath() + "\"");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public static AudioClip loadAudioClip(String filepath) {
        
        // known issues: if the file exists but cannot be loaded, you will
        //               end up with an Audio Clip object that doesn't make
        //               any sound when played.
        File file = new File(filepath);
        if (! file.exists() || ! file.isFile()) {
            try {
                throw new FileNotFoundException("Error loading file: \"" + filepath + "\"; File doesn't exist");
            } catch (FileNotFoundException e){
                e.printStackTrace();
                return null;
            }
        }

        URL resourceURL;
        try {
            resourceURL = file.toURI().toURL(); // converts the file to a URI, then the URI into a URL which gets used
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }

        AudioClip sound = JApplet.newAudioClip(resourceURL);
        return sound;

    }

}
