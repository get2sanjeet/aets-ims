package main.java.barcode;

import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.*;

import org.apache.avalon.framework.configuration.DefaultConfiguration;

public class EAN13BarcodeGenerator {

        /**
         * @param code
         * @throws BarcodeException
         * @throws IOException
         */
        public FileInputStream generateEAN13Barcode(String code) throws BarcodeException, IOException, org.apache.avalon.framework.configuration.ConfigurationException {

            BarcodeUtil util = BarcodeUtil.getInstance();
            BarcodeGenerator gen = util.createBarcodeGenerator(buildCfg("ean-13"));

            OutputStream fout = new FileOutputStream(BarCodeUtility.BAR_CODE_PATH +"ean-13.jpg");
            int resolution = 200;
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    fout, "image/jpeg", resolution, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            EAN13CodeBuilder ean13CodeBuilder = new EAN13CodeBuilder(code);
            gen.generateBarcode(canvas, ean13CodeBuilder.fullCode);
            canvas.finish();

            File barcode = new File(BarCodeUtility.BAR_CODE_PATH +"ean-13.jpg");
            return new FileInputStream(barcode);
        }

    public String generateEAN13BarcodeSerialNumber(String code) throws BarcodeException, IOException, org.apache.avalon.framework.configuration.ConfigurationException {

        EAN13CodeBuilder ean13CodeBuilder = new EAN13CodeBuilder(code);
        BarcodeUtil util = BarcodeUtil.getInstance();
        BarcodeGenerator gen = util.createBarcodeGenerator(buildCfg("ean-13"));

        OutputStream fout = new FileOutputStream(BarCodeUtility.BAR_CODE_PATH + ean13CodeBuilder.fullCode +".jpg");
        int resolution = 200;
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                fout, "image/jpeg", resolution, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        gen.generateBarcode(canvas, ean13CodeBuilder.fullCode);
        canvas.finish();
        return ean13CodeBuilder.fullCode;
    }

        private static DefaultConfiguration buildCfg(String type) {
            DefaultConfiguration cfg = new DefaultConfiguration("barcode");

            //Bar code type
            DefaultConfiguration child = new DefaultConfiguration(type);
            cfg.addChild(child);

            //Human readable text position
            DefaultConfiguration attr = new DefaultConfiguration("human-readable");
            DefaultConfiguration subAttr = new DefaultConfiguration("placement");
            subAttr.setValue("bottom");
            attr.addChild(subAttr);

            child.addChild(attr);
            return cfg;
        }
    }

