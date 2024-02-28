package main.java.barcode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BarcodeProcessor {
    public static void process(String barcodeSerialNumber, String productName, String model, int price) throws IOException {
        int height = 175;
        int width = height * 2;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.black);
        g2d.setFont(new Font("Verdana", Font.BOLD, 12));
        g2d.drawString("CROPTECH - "+productName, 50, 10);
        g2d.drawString("Model: "+model+"                 Unit Price: "+price, 50, 25);
        g2d.dispose();

        String barcodeTempPath = BarCodeUtility.BAR_CODE_PATH + barcodeSerialNumber+".png";

        File file = new File(barcodeTempPath);
        ImageIO.write(bufferedImage, "png", file);

        BufferedImage barcode = ImageIO.read(new File(BarCodeUtility.BAR_CODE_PATH+barcodeSerialNumber+".jpg"));
        BufferedImage finalBarcode = ImageIO.read(new File(barcodeTempPath));
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = combined.getGraphics();
        graphics.drawImage(finalBarcode, 0, 0, null);
        graphics.drawImage(barcode, 30, 30, null);

        File outputImage = new File(barcodeTempPath);
        ImageIO.write(combined, "PNG", outputImage);
    }
}
