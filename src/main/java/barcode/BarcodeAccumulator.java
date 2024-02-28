package main.java.barcode;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.input.KeyEvent;
import main.java.controllers.Base;
import main.java.controllers.StockScanController;

import java.util.Objects;

public final class BarcodeAccumulator {

    public interface OnBarcodeListener {
        void onBarcodeScanned(String barcode);
    }

    private static final char BACKSPACE = '\b';
    private static final char CONTROL_V = 0x0016;
    private static final char CONTROL_Z = 0x001A;
    private static final char CARRIAGE_RETURN = 0x000d;

    private final StringBuffer buffer = new StringBuffer();

    private OnBarcodeListener barcodeListener;

    public void setBarcodeListener(OnBarcodeListener barcodeListener) {
        this.barcodeListener = barcodeListener;
    }

    public void accumulate(JFXTextField barcodeSerialNumber, KeyEvent event, Base base) throws Exception {
        for (char character : event.getCharacter().toCharArray()) {
            if (Character.isISOControl(character)) {
                if (barcodeSerialNumber.isFocused()) {
                    if (character == BACKSPACE) {
                        if (buffer.length() > barcodeSerialNumber.getCaretPosition()) {
                            remove(barcodeSerialNumber.getCaretPosition());
                        }
                        break;
                    }
                    if (character == CONTROL_V) {
                        add(barcodeSerialNumber.getText());
                        break;
                    }
                    if (character == CONTROL_Z) {
                        clear();
                        add(barcodeSerialNumber.getText());
                        break;
                    }
                    if (character == CARRIAGE_RETURN) {
                        notifyListener(buffer.toString());
                        base.fetchAndPopulateBarcodeData();
                        clear();
                        barcodeSerialNumber.setText("");
                        break;
                    }
                }
                continue;
            }
            add(String.valueOf(character));
        }
    }

    public void add(String character) {
        buffer.append(character);
    }

    public void remove(int position) {
        buffer.deleteCharAt(position);
    }

    public void clear() {
        buffer.delete(0, buffer.length());
    }

    private void notifyListener(String barcode) {
        if (Objects.nonNull(barcodeListener)) barcodeListener.onBarcodeScanned(barcode);
    }
}
