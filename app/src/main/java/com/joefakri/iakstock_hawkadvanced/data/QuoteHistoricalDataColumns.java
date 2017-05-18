package com.joefakri.iakstock_hawkadvanced.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by deny on bandung.
 */
public class QuoteHistoricalDataColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String SYMBOL = "symbol";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String DATE = "date";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String BIDPRICE = "bid_price";
}
