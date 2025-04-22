package ru.giv13.javacrm.system;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCropper {
    public static byte[] cropToSquare(byte[] imageBytes, int outputSize, String format) throws IOException {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));
        int width = original.getWidth();
        int height = original.getHeight();
        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        BufferedImage cropped = original.getSubimage(x, y, size, size);
        if (outputSize > 0 && outputSize < size) {
            BufferedImage resized = new BufferedImage(outputSize, outputSize, original.getType());
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(cropped, 0, 0, outputSize, outputSize, null);
            g.dispose();
            cropped = resized;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(cropped, format, baos);
        return baos.toByteArray();
    }
}
