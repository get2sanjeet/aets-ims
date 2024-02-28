package main.java.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.io.IOException;

public enum GridHeader {
    LP(20, 380, 20, 20, "Sn.", Color.decode("#f0f0f0")),
    NAME(40, 380, 200, 20, "Name of the product/service", Color.decode("#f0f0f0")),
    QTY(240, 380, 25, 20, "Qty.", Color.decode("#f0f0f0")),
    NET_PRICE(265, 380, 50, 20, "Unit price", Color.decode("#f0f0f0")),
    GROSS_PRICE(315, 380, 85, 20, "Total price", Color.decode("#f0f0f0"));

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final String text;
    private final Color color;

    GridHeader(float x, float y, float width, float height, String text, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.color = color;
    }

    public void createCell(PDPageContentStream contentStream) throws IOException {
        InvoiceGenerator.createCell(contentStream, x, y, width, height, text, color);
    }
}
