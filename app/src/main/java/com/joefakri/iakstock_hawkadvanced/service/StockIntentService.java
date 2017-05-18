package com.joefakri.iakstock_hawkadvanced.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by deny on bandung.
 */

public class StockIntentService extends IntentService {

    public static final String EXTRA_TAG = "tag";
    public static final String EXTRA_SYMBOL = "symbol";

    public static final String ACTION_INIT = "init";
    public static final String ACTION_ADD = "add";

    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle args = new Bundle();
        if (intent.getStringExtra(EXTRA_TAG).equals(ACTION_ADD)) {
            args.putString(EXTRA_SYMBOL, intent.getStringExtra(EXTRA_SYMBOL));
        }

        StockTaskService stockTaskService = new StockTaskService(this);
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(EXTRA_TAG), args));
    }
}
