/*
 * Copyright 2016.  Dmitry Malkovich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joefakri.iakstock_hawkadvanced.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joefakri.iakstock_hawkadvanced.R;
import com.joefakri.iakstock_hawkadvanced.data.QuoteColumns;
import com.joefakri.iakstock_hawkadvanced.data.QuoteHistoricalDataColumns;
import com.joefakri.iakstock_hawkadvanced.data.QuoteProvider;
import com.joefakri.iakstock_hawkadvanced.view.StockListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by deny on bandung.
 */

public class QuoteCursorAdapter extends CursorRecyclerViewAdapter<QuoteCursorAdapter.ViewHolder>
        implements ItemTouchHelperCallback.SwipeListener {

    private static Context mContext;
    private int mChangeUnits;

    public QuoteCursorAdapter(Context context, Cursor cursor, int changeUnits) {
        super(cursor);
        mContext = context;
        mChangeUnits = changeUnits;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
        viewHolder.mSymbol.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.SYMBOL)));
        viewHolder.mBidPrice.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.BIDPRICE)));
        if (cursor.getInt(cursor.getColumnIndex(QuoteColumns.ISUP)) == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.mChange.setBackground(
                        mContext.getResources().getDrawable(R.drawable.bg_pill_green,
                                mContext.getTheme()));
            }
            viewHolder.imgType.setImageResource(R.drawable.ic_trending_up);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.mChange.setBackground(
                        mContext.getResources().getDrawable(R.drawable.bg_pill_red,
                                mContext.getTheme()));
            }
            viewHolder.imgType.setImageResource(R.drawable.ic_trending_down);
        }
        if (mChangeUnits == StockListActivity.CHANGE_UNITS_PERCENTAGES) {
            viewHolder.mChange.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
        } else {
            viewHolder.mChange.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE)));
        }
    }

    @Override
    public void onItemDismiss(int position) {
        String symbol = getSymbol(position);
        mContext.getContentResolver().delete(QuoteProvider.Quotes.withSymbol(symbol), null, null);
        mContext.getContentResolver().delete(QuoteProvider.QuotesHistoricData.CONTENT_URI,
                QuoteHistoricalDataColumns.SYMBOL + " = \"" + symbol + "\"", null);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public String getSymbol(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        return c.getString(c.getColumnIndex(QuoteColumns.SYMBOL));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperCallback.ItemTouchHelperViewHolder, View.OnClickListener {
        @BindView(R.id.img_type) public ImageView imgType;
        @BindView(R.id.stock_symbol) public TextView mSymbol;
        @BindView(R.id.bid_price) public TextView mBidPrice;
        @BindView(R.id.stock_change) public TextView mChange;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
        }

        @Override
        public void onClick(View v) {

        }
    }

    public void setChangeUnits(int changeUnits) {
        this.mChangeUnits = changeUnits;
    }
}
