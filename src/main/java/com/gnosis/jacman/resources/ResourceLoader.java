/**
 *
 */
package com.gnosis.jacman.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.gnosis.jacman.engine.Constants;

/**
 * This class loads different resources from the resource package. Currently
 * the only items in the package are images, however, methods can later be
 * added to this class to allow loading other items, such as sound files.
 *
 * @author Brendan McGloin
 */
public class ResourceLoader implements Constants {

    public static final String IMAGE_PATH_PREFIX = "resources/images/";

    public String getFullPath(String name) {
        URL path = getClass().getResource(name);
        if (path != null) {
            return path.getPath();
        }
        return null;
    }

    public URI getURI(String path) {
        try {
            return getClass().getResource(path).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BufferedImage loadImage(String path) {
        try {
            path = IMAGE_PATH_PREFIX + path;
            if (getClass().getClassLoader().getResource(path) == null) {
                System.err.println("Couldn't find image: " + path);
            }
            return ImageIO.read(getClass().getClassLoader().getResource(path));
        } catch (IOException e) {
            e.printStackTrace(System.err); //log the error into the default error stream
            return null;
        }
    }

}
