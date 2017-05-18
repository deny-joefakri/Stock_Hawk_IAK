package com.joefakri.iakstock_hawkadvanced.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by deny on bandung.
 */

@Database(version = QuoteDatabase.VERSION)
public class QuoteDatabase {
    private QuoteDatabase() {
    }

    public static final int VERSION = 9;

    @Table(QuoteColumns.class)
    public static final String QUOTES = "quotes";

    @Table(QuoteHistoricalDataColumns.class)
    public static final String QUOTES_HISTORICAL_DATA = "quotes_historical_data";
}
