package main.java.barcode;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

public class BarCodeUtility {

    public static String BAR_CODE_PATH = "C:\\Users\\sabi0522\\Documents\\DOCUMENT\\Barcode\\";
    public static String BILL_PATH = "C:\\Users\\sabi0522\\Documents\\DOCUMENT\\Bills\\";

    public static void main(String[] args) throws Exception{
        new BarCodeUtility().prepareProductCode(1,1);
    }

    public static InputStream prepareBarCode(String type, String sequence, String watt) throws IOException {
        Code39Bean bean = new Code39Bean();
        final int dpi = 150;
        bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi)); //makes the narrow bar, width exactly one pixel
        bean.setWideFactor(3);
        bean.doQuietZone(false);
        File outputFile = new File(BAR_CODE_PATH +"out.png");
        OutputStream out = new FileOutputStream(outputFile);

        try {
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            StringBuilder message = new StringBuilder();
            message.append("CT");
            //message.append("-");
            message.append(type);
            //message.append("-");
            message.append(watt.replace("watt","").trim()+"W");
            //message.append("-");
            message.append(get8DigitRandomNumberString());
            bean.generateBarcode(canvas, message.toString() );
            canvas.finish();

            File barcode = new File(BAR_CODE_PATH +"out.png");
            return new FileInputStream(barcode);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    private FileInputStream prepareEAN13Barcode() throws IOException {
       /* EAN13 bean = new EAN13();
        final int dpi = 150;
        File outputFile = new File(barCodePath+"/out.png");
        OutputStream out = new FileOutputStream(outputFile);
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        bean.generateBarcode(canvas, getRandomNumberString());
        canvas.finish();
        File barcode = new File(barCodePath+"/out.png");
        return new FileInputStream(barcode);*/
        return null;
    }

    private static String prepareSequence(String sequence) {
        char[] array = sequence.toCharArray();
        String newSequenceZeroes = "";
        for (int i =array.length; i<6; i++){
            newSequenceZeroes = newSequenceZeroes+"0";
        }
        return newSequenceZeroes + sequence;
    }
    public static String get8DigitRandomNumberString(int codeLength) {
        String nine = "";
        for (int i = 0; i < codeLength; i++) {
            nine += "9";
        }
        Random rnd = new Random();
        int number = rnd.nextInt(Integer.valueOf(nine));
        String randomNumber  = String.format("%06d", number);
        if (randomNumber.length() < codeLength) {
            for (int i = 0; i < codeLength - randomNumber.length(); i++) {
                randomNumber += "0";
            }
        }
        return randomNumber;
    }

    public static String get8DigitRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(99999999);

        return String.format("%06d", number);
    }

    public static String prepareProductCode() {
        String productType = "01";
        String productModel = "01";

        // this will convert any number sequence into 6 character.
        return productType.concat(productModel).concat(get8DigitRandomNumberString());
    }

    public static String prepareProductCode(int productTypeId, int productId) {
        String typeId = productTypeId <= 9 ? "0" + productTypeId : String.valueOf(productTypeId);
        String pId = productId <= 9 ? "0" + productId : String.valueOf(productId);
        String code = typeId + pId;
        code = code.concat(get8DigitRandomNumberString(12-code.length()));
        code = validateCode(code);
        return code;
    }

    private static String validateCode(String code) {
        if (code.length() < 12){
            System.out.println("code < 12 length"+code);
            return code.concat(code+"0");
        } else if (code.length() < 11){
            System.out.println("code < 11 length"+code);
            return code.concat(code+"00");
        }
        return code;
    }

    public static FileInputStream getBarcode(String code) throws ConfigurationException, BarcodeException, IOException {
        EAN13BarcodeGenerator ean13BarcodeGenerator = new EAN13BarcodeGenerator();
        return ean13BarcodeGenerator.generateEAN13Barcode(code);
    }

    public static String getBarcodeSerialNumber(String code) throws ConfigurationException, BarcodeException, IOException {
        EAN13BarcodeGenerator ean13BarcodeGenerator = new EAN13BarcodeGenerator();
        return ean13BarcodeGenerator.generateEAN13BarcodeSerialNumber(code);
    }


}

