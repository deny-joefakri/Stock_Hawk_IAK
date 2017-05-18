package com.joefakri.iakstock_hawkadvanced.service;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.Gson;
import com.joefakri.iakstock_hawkadvanced.data.QuoteColumns;
import com.joefakri.iakstock_hawkadvanced.data.QuoteHistoricalDataColumns;
import com.joefakri.iakstock_hawkadvanced.data.QuoteProvider;
import com.joefakri.iakstock_hawkadvanced.network.APIService;
import com.joefakri.iakstock_hawkadvanced.network.ResponseGetHistoricalData;
import com.joefakri.iakstock_hawkadvanced.network.ResponseGetStock;
import com.joefakri.iakstock_hawkadvanced.network.ResponseGetStocks;
import com.joefakri.iakstock_hawkadvanced.network.StockQuote;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by deny on bandung.
 */

public class StockTaskService extends GcmTaskService {

    private OkHttpClient cl;
    private static String TAG = StockTaskService.class.getSimpleName();
    public final static String TAG_PERIODIC = "periodic";
    private Context mContext;
    private boolean mIsUpdate;

    private final static String dummySymbol = "\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\"";
    private StringBuilder mStoredSymbols = new StringBuilder();

    public StockTaskService(Context context) {
        mContext = context;
    }

    public StockTaskService() {

    }

    @Override
    public int onRunTask(final TaskParams taskParams) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        cl = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();

        try {

            if (mContext == null) {
                return GcmNetworkManager.RESULT_FAILURE;
            }

            String query = "select * from yahoo.finance.quotes where symbol in ("
                    + buildUrl(taskParams)
                    + ")";

            cl.newCall(getRequest(query)).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("Failure", e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    Gson gson = new Gson();
                    if (taskParams.getTag().equals(StockIntentService.ACTION_INIT)) {
                        ResponseGetStocks responseGetStock = gson.fromJson(response.body().string(), ResponseGetStocks.class);
                        try {
                            saveQuotes2Database(responseGetStock.getStockQuotes());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }

                    } else {
                        ResponseGetStock responseGetStock = gson.fromJson(response.body().string(), ResponseGetStock.class);
                        try {
                            saveQuotes2Database(responseGetStock.getStockQuotes());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return GcmNetworkManager.RESULT_SUCCESS;

        } catch (IOException e){
            Log.e(TAG, e.getMessage(), e);
            return GcmNetworkManager.RESULT_FAILURE;
        }


        /*try {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIService.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            APIService service = retrofit.create(APIService.class);
            String query = "select * from yahoo.finance.quotes where symbol in ("
                    + buildUrl(taskParams)
                    + ")";

            if (taskParams.getTag().equals(StockIntentService.ACTION_INIT)) {
                Call<ResponseGetStocks> call = service.getStocks(query);
                Response<ResponseGetStocks> response = call.execute();
                ResponseGetStocks responseGetStocks = response.body();
                saveQuotes2Database(responseGetStocks.getStockQuotes());
            } else {
                Call<ResponseGetStock> call = service.getStock(query);
                Response<ResponseGetStock> response = call.execute();
                ResponseGetStock responseGetStock = response.body();
                saveQuotes2Database(responseGetStock.getStockQuotes());
            }

            return GcmNetworkManager.RESULT_SUCCESS;

        } catch (IOException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, e.getMessage(), e);
            return GcmNetworkManager.RESULT_FAILURE;
        }*/

    }

    private Request getRequest(String query) throws UnsupportedEncodingException {

        String fullUrl;
        String url = APIService.BASE_URL + APIService.PARAM_URL;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        Request request;
        Request.Builder requestBuilder = new Request.Builder();

        urlBuilder.addQueryParameter("q", query);
        fullUrl = urlBuilder.build().toString();
        Log.e("fullUrl", fullUrl);
        requestBuilder.url(fullUrl);
        request = requestBuilder.build();

        return request;
    }

    private String buildUrl(TaskParams params) throws UnsupportedEncodingException {
        ContentResolver resolver = mContext.getContentResolver();
        if (params.getTag().equals(StockIntentService.ACTION_INIT) || params.getTag().equals(TAG_PERIODIC)) {
            mIsUpdate = true;
            Cursor cursor = resolver.query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{"Distinct " + QuoteColumns.SYMBOL}, null,
                    null, null);

            if (cursor != null && cursor.getCount() == 0 || cursor == null) {
                return dummySymbol;
            } else {
                DatabaseUtils.dumpCursor(cursor);
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    mStoredSymbols.append("\"");
                    mStoredSymbols.append(cursor.getString(
                            cursor.getColumnIndex(QuoteColumns.SYMBOL)));
                    mStoredSymbols.append("\",");
                    cursor.moveToNext();
                }
                mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), "");
                Log.e("mStoredSymbols", mStoredSymbols.toString());
                return mStoredSymbols.toString();
            }
        } else if (params.getTag().equals(StockIntentService.ACTION_ADD)) {
            mIsUpdate = false;
            String stockInput = params.getExtras().getString(StockIntentService.EXTRA_SYMBOL);

            return "\"" + stockInput + "\"";
        } else {
            throw new IllegalStateException("Action not specified in TaskParams.");
        }
    }

    private void saveQuotes2Database(List<StockQuote> quotes) throws RemoteException, OperationApplicationException {
        ContentResolver resolver = mContext.getContentResolver();

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        for (StockQuote quote : quotes) {

            batchOperations.add(QuoteProvider.buildBatchOperation(quote));
        }

        if (mIsUpdate) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(QuoteColumns.ISCURRENT, 0);
            resolver.update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                    null, null);
        }

        resolver.applyBatch(QuoteProvider.AUTHORITY, batchOperations);

        for (StockQuote quote : quotes) {
            // Load historical data for the quote
            try {
                loadHistoricalData(quote);
            } catch (IOException | RemoteException | OperationApplicationException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }


    private void loadHistoricalData(StockQuote quote) throws IOException, RemoteException,
            OperationApplicationException {

        // Load historic stock data
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(currentDate);
        calEnd.add(Calendar.DATE, 0);

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(currentDate);
        calStart.add(Calendar.MONTH, -1);

        String startDate = dateFormat.format(calStart.getTime());
        String endDate = dateFormat.format(calEnd.getTime());

        String query = "select * from yahoo.finance.historicaldata where symbol=\"" +
                quote.getSymbol() +
                "\" and startDate=\"" + startDate + "\" and endDate=\"" + endDate + "\"";

        cl.newCall(getRequest(query)).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("Failure", e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e("onresponse", "success");
                Gson gson = new Gson();
                ResponseGetHistoricalData responseGetHistoricalData = gson.fromJson(response.body().string(), ResponseGetHistoricalData.class);
                if (responseGetHistoricalData != null) {
                    try {
                        saveQuoteHistoricalData2Database(responseGetHistoricalData.getHistoricData());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.BASE_URL)
                //.client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);
        Call<ResponseGetHistoricalData> call = service.getStockHistoricalData(query);
        Response<ResponseGetHistoricalData> response;
        response = call.execute();
        ResponseGetHistoricalData responseGetHistoricalData = response.body();
        if (responseGetHistoricalData != null) {
            saveQuoteHistoricalData2Database(responseGetHistoricalData.getHistoricData());
        }*/
    }

    private void saveQuoteHistoricalData2Database(List<ResponseGetHistoricalData.Quote> quotes)
            throws RemoteException, OperationApplicationException {
        ContentResolver resolver = mContext.getContentResolver();
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        for (ResponseGetHistoricalData.Quote quote : quotes) {

            // First, we have to delete outdated date from DB.
            resolver.delete(QuoteProvider.QuotesHistoricData.CONTENT_URI,
                    QuoteHistoricalDataColumns.SYMBOL + " = \"" + quote.getSymbol() + "\"", null);

            batchOperations.add(QuoteProvider.buildBatchOperation(quote));
        }

        resolver.applyBatch(QuoteProvider.AUTHORITY, batchOperations);
    }
}
