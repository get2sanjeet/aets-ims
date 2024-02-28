package main.java.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class InvoiceGenerator {
    private static final LocalDate date = LocalDate.now();
    private static final PDType1Font normalFont = PDType1Font.HELVETICA;
    private static final PDType1Font boldFont = PDType1Font.HELVETICA_BOLD;
    private static int invoiceCounter;
    private final String currency;
    private final String invoiceNumber;
    private final Timestamp orderDate;
    private final CompanyInfo buyer;
    private final CompanyInfo seller;
    private final List<InvoiceData> invoiceData;

    public InvoiceGenerator(String currency, CompanyInfo buyer, CompanyInfo seller, List<InvoiceData> invoiceData, String invoiceNumber, Timestamp orderDate) {
        this.currency = currency;
        this.buyer = buyer;
        this.seller = seller;
        this.invoiceData = invoiceData;
        this.invoiceNumber = invoiceNumber;
        this.orderDate = orderDate;
    }

    public void createInvoice(String fullPath) {
        try {

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A5);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            generateInvoice(document, contentStream);
            document.save(fullPath);
            document.close();
            System.out.println("Invoice Created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPath() {
        return "INVOICE_"+date + ".pdf";
    }

    private void generateInvoice(PDDocument document, PDPageContentStream contentStream) throws IOException {
        //Add image
        createInvoiceImage(contentStream, document);
        writeBoldText(contentStream, 24f, 300, 550, "INVOICE");
        //createCellWithInvoiceNumber(contentStream);
        addText_12(contentStream, "Invoice Number: " + invoiceNumber, 250, 530);
        addText_12(contentStream, "Invoice Date: " + new SimpleDateFormat("dd/MM/yyyy").format(orderDate), 250, 510);
        //addText_14_With_Line(contentStream, "Seller", 10, 470, 200);
        addCompanyInfo(contentStream, buyer,250 , 450);
        addText_14_With_Line(contentStream, "Billing Address.", 250, 470, 200);
        addCompanyInfo(contentStream, seller, 20, 500);


        for (GridHeader cell : GridHeader.values()) {
            cell.createCell(contentStream);
        }
        int startY = 350;
        float sumOfUnitPrices = 0;
        float sumOfNetPrice = 0;
        float sumAmountOfVat = 0;
        float sumOfGrossValue = 0;

        for (InvoiceData x : invoiceData) {
            addInvoiceDataRow(contentStream, x, startY);

            sumOfUnitPrices += x.getUnitPrice();
            sumOfNetPrice += x.getTotalPrice();
            sumAmountOfVat += x.getTotalPrice();
            sumOfGrossValue += x.getTotalPrice();
            startY -= 30;
        }
        createSummaries(contentStream, startY, sumOfUnitPrices, sumOfNetPrice, sumAmountOfVat, sumOfGrossValue);
        //createCell(contentStream, 300, 100, 250, 100, "Signature", Color.decode("#f0f0f0"));
        startY -= 30;
        createPaymentMethod(contentStream, startY, sumOfGrossValue);
        //addText_9(contentStream, "The document was created using the Apache PDFBox library and had a learning purpose.", 50, 50);
        //addText_9(contentStream, "The source code is available in the public repository at the following link: https://github.com/OwidiuszZielinski/Invoice-Generator ", 50, 38);

        contentStream.close();

    }

    private void createPaymentMethod(PDPageContentStream contentStream, int startY, float sumOfGrossValue) throws IOException {
        int y = startY;
        String payment = "Transfer";
        addText_12(contentStream, "Payment Method : " + payment, 50, y);
        y -= 30;
        addText_12_Bold(contentStream, "Sum to pay : " + stringWithCurrencyConverter(sumOfGrossValue), 50, y);
        y -= 30;
        String words = NumberToWordsConverter.convertPriceToWords(sumOfGrossValue);
        addText_12_Bold(contentStream, "In words : " + words, 50, y);

    }

    private void createSummaries(PDPageContentStream contentStream, int startY, float sumOfNetPrice, float sumOfGrossPrice, float sumAmountOfVat, float sumOfGrossValue) throws IOException {
        int axisy = startY+10;
        addCellData(contentStream, 240, axisy, 75, 20, "Total Price");
        addCellData(contentStream, 240, axisy-20, 75, 20, "GST (18%)");
        addCellData(contentStream, 240, axisy-40, 75, 20, "Total price + tax");

        addCellData(contentStream, 315, axisy, 85, 20, "1278.00");
        addCellData(contentStream, 315, axisy-20, 85, 20, "120.00");
        addCellData(contentStream, 315, axisy-40, 85, 20, "1478.00");
        writeBoldText(contentStream, 9f, 150, startY + 14, "Summary : ");
    }

    private static void createCellWithInvoiceNumber(PDPageContentStream contentStream) throws IOException {
        invoiceCounter++;
        //addCellWithOnlyTopAndBottomBorder(contentStream, 350, 700, 200, 40, Color.decode("#f0f0f0"));
        addText_24(contentStream, "Invoice #" + invoiceCounter, 390, 710);
    }

    private static void createInvoiceImage(PDPageContentStream contentStream, PDDocument document) throws IOException {
        PDImageXObject image = PDImageXObject.createFromFile("src/main/resources/icons/white.png", document);
        contentStream.drawImage(image, 20, 520, 100, 55);
    }


    private static void writeBoldText(PDPageContentStream contentStream, float fontSize, int tx, int ty, String text) throws IOException {
        contentStream.setFont(boldFont, fontSize);
        contentStream.setLeading(1.5f * fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset(tx, ty);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void addText_24(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.BLACK);
        float fontSize = 24;
        contentStream.newLineAtOffset(x, y);
        contentStream.setFont(normalFont, fontSize);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void addText_14_With_Line(PDPageContentStream contentStream, String text, float x, float y, float width) throws IOException {
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.BLACK);
        float fontSize = 8;
        contentStream.newLineAtOffset(x, y + 5);
        contentStream.setFont(boldFont, fontSize);
        contentStream.showText(text);
        contentStream.endText();
        /*contentStream.setStrokingColor(Color.BLACK);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();*/

    }

    public static void addText_12(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.BLACK);
        float fontSize = 9;
        contentStream.newLineAtOffset(x, y);
        contentStream.setFont(boldFont, fontSize);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void addText_12_Bold(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.BLACK);
        float fontSize = 9;
        contentStream.newLineAtOffset(x, y);
        contentStream.setFont(boldFont, fontSize);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void addText_9(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.BLACK);
        float fontSize = 9;
        contentStream.newLineAtOffset(x, y);
        contentStream.setFont(normalFont, fontSize);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void addCompanyInfo(PDPageContentStream contentStream, CompanyInfo companyInfo, float x, float y) throws IOException {
        float fontSize = 8;
        float lineSpacing = 3.0f;
        contentStream.beginText();
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.setFont(normalFont, fontSize);
        contentStream.newLineAtOffset(x, y + 15);
        contentStream.showText(companyInfo.companyName());
        contentStream.newLine();
        contentStream.newLineAtOffset(0, fontSize * lineSpacing);
        contentStream.showText(companyInfo.address());
        contentStream.newLine();
        contentStream.newLineAtOffset(0, fontSize * lineSpacing);
        contentStream.showText("GSTIN : " + companyInfo.TaxIdentificationNumber());
        contentStream.newLine();
        contentStream.newLineAtOffset(0, fontSize * lineSpacing);
        contentStream.showText("Phone. " + companyInfo.phoneNumber());
        contentStream.newLine();
        contentStream.newLineAtOffset(0, fontSize * lineSpacing);
        contentStream.showText("Email. " + companyInfo.email());
        contentStream.endText();
    }


    public static void addCellWithOnlyTopAndBottomBorder(
            PDPageContentStream contentStream,
            float x,
            float y,
            float width,
            float height,
            Color borderColor) throws IOException {

        // Ustawienia koloru linii
        contentStream.setNonStrokingColor(borderColor);
        contentStream.fillRect(x, y, width, height);
        contentStream.setStrokingColor(Color.BLACK);

        // Linia górna komórki
        contentStream.moveTo(x, y + height);
        contentStream.lineTo(x + width, y + height);
        contentStream.stroke();

        // Linia dolna komórki
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();
    }

    public static void createCell(
            PDPageContentStream contentStream,
            float x,
            float y,
            float cellWidth,
            float cellHeight,
            String text,
            Color borderColor) throws IOException {

        contentStream.setNonStrokingColor(borderColor);
        contentStream.fillRect(x, y, cellWidth, cellHeight);
        contentStream.setStrokingColor(Color.BLACK);

        // Linia górna komórki
        contentStream.moveTo(x, y + cellHeight);
        contentStream.lineTo(x + cellWidth, y + cellHeight);
        contentStream.stroke();

        // Linia dolna komórki
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + cellWidth, y);
        contentStream.stroke();
        // Linia lewa komórki
        contentStream.moveTo(x, y);
        contentStream.lineTo(x, y + cellHeight);
        contentStream.stroke();

        // Linia prawa komórki
        contentStream.moveTo(x + cellWidth, y);
        contentStream.lineTo(x + cellWidth, y + cellHeight);
        contentStream.stroke();
        addText_9(contentStream, text, x + 3, y + cellHeight / 2);
    }

    public static void addCellData(
            PDPageContentStream contentStream,
            float x,
            float y,
            float cellWidth,
            float cellHeight,
            String text) throws IOException {
        // Linia dolna komórki
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + cellWidth, y);
        contentStream.stroke();
        // Linia lewa komórki
        contentStream.moveTo(x, y);
        contentStream.lineTo(x, y + cellHeight);
        contentStream.stroke();

        // Linia prawa komórki
        contentStream.moveTo(x + cellWidth, y);
        contentStream.lineTo(x + cellWidth, y + cellHeight);
        contentStream.stroke();
        addText_9(contentStream, text, x + 3, y + cellHeight / 2);

    }
    private void addInvoiceDataRow(PDPageContentStream contentStream, InvoiceData data, int startY) throws IOException {
        addCellData(contentStream, 20, startY, 20, 30, String.valueOf(data.getSerial()));
        addCellData(contentStream, 40, startY, 200, 30, data.getProductName());
        addCellData(contentStream, 240, startY, 25, 30, String.valueOf(data.getQuantity()));
        addCellData(contentStream, 265, startY, 50, 30, stringWithCurrencyConverter(data.getUnitPrice()));
        addCellData(contentStream, 315, startY, 85, 30, stringWithCurrencyConverter(data.getTotalPrice()));
    }

    private String stringWithCurrencyConverter(Object object) {
        return object + currency;
    }


}