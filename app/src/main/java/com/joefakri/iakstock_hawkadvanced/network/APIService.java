package com.joefakri.iakstock_hawkadvanced.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by deny on bandung.
 */

public interface APIService {

    String BASE_URL = "https://query.yahooapis.com";
    String PARAM_URL = "/v1/public/yql?format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=&";

    @GET("/v1/public/yql?" +
            "format=json&diagnostics=true&" +
            "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseGetStocks> getStocks(@Query("q") String query);

    @GET("/v1/public/yql?" +
            "format=json&diagnostics=true&" +
            "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseGetStock> getStock(@Query("q") String query);

    @GET("/v1/public/yql?" +
            "format=json&diagnostics=true&" +
            "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseGetHistoricalData> getStockHistoricalData(@Query("q") String query);
}
