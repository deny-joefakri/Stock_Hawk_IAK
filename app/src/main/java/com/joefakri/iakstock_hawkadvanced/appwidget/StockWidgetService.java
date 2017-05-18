package com.joefakri.iakstock_hawkadvanced.appwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by deny on bandung.
 */

public class StockWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockWidgetFactory(getApplicationContext(), intent);
    }
}
