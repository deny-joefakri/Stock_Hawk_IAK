package com.joefakri.iakstock_hawkadvanced.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deny on bandung.
 */

public class ResponseGetStocks {

    @SerializedName("query")
    private Results mResults;

    public class Results {

        @SerializedName("count")
        private String mCount;

        @SerializedName("results")
        private Quotes mQuote;

        public Quotes getQuote() {
            return mQuote;
        }
    }

    public class Quotes {

        @SerializedName("quote")
        private List<StockQuote> mStockQuotes = new ArrayList<>();

        public List<StockQuote> getStockQuotes() {
            return mStockQuotes;
        }
    }

    public List<StockQuote> getStockQuotes() {
        List<StockQuote> result = new ArrayList<>();
        List<StockQuote> stockQuotes = mResults.getQuote().getStockQuotes();
        for (StockQuote stockQuote : stockQuotes) {
            if (stockQuote.getBid() != null && stockQuote.getChangeInPercent() != null
                    && stockQuote.getChange() != null) {
                result.add(stockQuote);
            }
        }
        return result;
    }
}
