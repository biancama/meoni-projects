package com.biancama.utils.gui;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

import sun.awt.shell.ShellFolder;

import com.biancama.log.BiancaLogger;
import com.biancama.utils.URLUtils;

public class BiancaImage {
    private static HashMap<String, BufferedImage> BUFFERED_IMAGE_CACHE = new HashMap<String, BufferedImage>();
    private static HashMap<String, ImageIcon> IMAGE_ICON_CACHE = new HashMap<String, ImageIcon>();
    private static HashMap<Icon, Icon> DISABLED_ICON_CACHE = new HashMap<Icon, Icon>();
    private static HashMap<String, Image> SCALED_IMAGE_CACHE = new HashMap<String, Image>();

    public static BufferedImage createEmptyBufferedImage(int w, int h) {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        return gc.createCompatibleImage(w, h, Transparency.BITMASK);
    }

    public static ImageIcon iconToImage(Icon icon) {
        if (icon == null) { return null; }
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon);
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h, Transparency.BITMASK);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return new ImageIcon(image);
        }
    }

    public static ImageIcon getFileIcon(String ext) {
        String id = "ext_" + ext;
        ImageIcon ret = IMAGE_ICON_CACHE.get(id);
        if (ret != null) { return ret; }

        File file = null;
        try {
            file = File.createTempFile("icon", "." + ext);

            ShellFolder shellFolder = ShellFolder.getShellFolder(file);
            ret = new ImageIcon(shellFolder.getIcon(true));
            IMAGE_ICON_CACHE.put(id, ret);
            return ret;
        } catch (Throwable e) {
            return iconToImage(new JFileChooser().getIcon(file));
        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }

    public static ImageIcon getScaledImageIcon(BufferedImage img, int width, int height) {
        return new ImageIcon(getScaledImage(img, width, height));

    }

    public static ImageIcon getScaledImageIcon(ImageIcon img, int width, int height) {
        if (img == null) { return null; }

        String id = img.hashCode() + "_" + width + "x" + height;
        ImageIcon ret = IMAGE_ICON_CACHE.get(id);
        if (ret != null) { return ret; }

        ret = new ImageIcon(getScaledImage((BufferedImage) img.getImage(), width, height));

        IMAGE_ICON_CACHE.put(id, ret);
        return ret;
    }

    public static Image getScaledImage(ImageIcon img, int width, int height) {
        if (img == null) { return null; }

        String id = img.hashCode() + "_" + width + "x" + height;
        Image ret = SCALED_IMAGE_CACHE.get(id);
        if (ret != null) { return ret; }

        ret = getScaledImage((BufferedImage) img.getImage(), width, height);
        SCALED_IMAGE_CACHE.put(id, ret);
        return ret;
    }

    public static Image getScaledImage(BufferedImage img, int width, int height) {
        if (img == null) { return null; }

        String id = img.hashCode() + "_" + width + "x" + height;
        Image ret = SCALED_IMAGE_CACHE.get(id);
        if (ret != null) { return ret; }

        double faktor = Math.min((double) img.getWidth() / width, (double) img.getHeight() / height);
        width = (int) (img.getWidth() / faktor);
        height = (int) (img.getHeight() / faktor);
        if (faktor == 1.0) { return img; }
        ret = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        SCALED_IMAGE_CACHE.put(id, ret);
        return ret;

    }

    /**
     * gibt ein bild zu dem übergebenem pfad zurück. nutzt einen cache
     * 
     * @param imageName
     *            Name des Bildes das zurückgeliefert werden soll (Endung ist
     *            .png)
     * @return Das gewünschte Bild oder null, falls es nicht gefunden werden
     *         kann
     */
    public static BufferedImage getImage(String imageName) {
        BufferedImage ret = BUFFERED_IMAGE_CACHE.get(imageName);
        if (ret != null) { return ret; }

        File file = URLUtils.getResourceFile("easyShipment/img/" + imageName + ".png");
        if (!file.exists()) { return null; }

        try {
            ret = ImageIO.read(file);
        } catch (IOException e) {
            BiancaLogger.exception(e);
            return null;
        }
        BUFFERED_IMAGE_CACHE.put(imageName, ret);
        return ret;
    }

    public static BufferedImage getImage(File file) {
        BufferedImage ret = BUFFERED_IMAGE_CACHE.get(file.getAbsolutePath());
        if (ret != null) { return ret; }

        if (!file.exists()) { return null; }

        try {
            ret = ImageIO.read(file);
        } catch (IOException e) {
            BiancaLogger.exception(e);
            return null;
        }
        BUFFERED_IMAGE_CACHE.put(file.getAbsolutePath(), ret);
        return ret;

    }

    public static ImageIcon getImageIcon(String string) {
        ImageIcon ret = IMAGE_ICON_CACHE.get(string);
        if (ret != null) { return ret; }
        ret = new ImageIcon(getImage(string));
        IMAGE_ICON_CACHE.put(string, ret);
        return ret;
    }

    public static Icon getDisabledIcon(Icon icon) {
        if (icon == null) { return null; }
        Icon ret = DISABLED_ICON_CACHE.get(icon);
        if (ret != null) { return ret; }
        ret = UIManager.getLookAndFeel().getDisabledIcon(null, icon);
        DISABLED_ICON_CACHE.put(icon, ret);
        return ret;
    }

    public static ImageIcon getImageIcon(File pat) {
        ImageIcon ret = IMAGE_ICON_CACHE.get(pat.getAbsolutePath());
        if (ret != null) { return ret; }
        ret = new ImageIcon(getImage(pat));
        IMAGE_ICON_CACHE.put(pat.getAbsolutePath(), ret);
        return ret;
    }

}
