package main.java.async;

import com.victorlaerte.asynctask.AsyncTask;
import main.java.controllers.InventoryController;

public class AsyncTaskManager extends AsyncTask {

    private InventoryController inventoryController;

    public AsyncTaskManager(InventoryController inventoryController) {
        this.inventoryController = inventoryController;
    }

    @Override
    public void onPreExecute() {
        this.inventoryController.notification.setText("Table data is loading .......");
    }

    @Override
    public Object doInBackground(Object[] objects) {
        return this.inventoryController.loadProductStockTable();
    }

    @Override
    public void onPostExecute(Object o) {
        this.inventoryController.notification.setText(null);
    }

    @Override
    public void progressCallback(Object[] objects) {

    }
}
