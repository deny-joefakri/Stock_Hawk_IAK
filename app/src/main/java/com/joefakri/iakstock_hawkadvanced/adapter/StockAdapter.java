package com.joefakri.iakstock_hawkadvanced.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joefakri.iakstock_hawkadvanced.R;
import com.joefakri.iakstock_hawkadvanced.data.QuoteColumns;
import com.joefakri.iakstock_hawkadvanced.realm.QuoteModel;
import com.joefakri.iakstock_hawkadvanced.view.StockListActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by dev_deny on 1/25/17.
 */

public class StockAdapter extends RecyclerView.Adapter implements RecyclerView.OnItemTouchListener{

    private ArrayList<QuoteModel> quoteModels;
    ItemListener itemListener;
    private Context context;
    private static int mChangeUnits;
    public RecyclerView recycleView;
    private GestureDetector gestureDetector;

    public StockAdapter(Context context, final RecyclerView recycleView) {
        this.context = context;
        this.recycleView = recycleView;
        quoteModels = new ArrayList<>();
    }

    public void update(ArrayList<QuoteModel> quoteModels) {
        this.quoteModels = quoteModels;
        notifyDataSetChanged();
    }

    public void setChangeUnits(int changeUnits) {
        this.mChangeUnits = changeUnits;
    }

    public void setOnclickListener(ItemListener itemlistener){
        this.itemListener = itemlistener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Holder) holder).setValue(quoteModels.get(position));
    }

    @Override
    public int getItemCount() {
        return quoteModels.size();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_type) ImageView imgType;
        @BindView(R.id.stock_symbol) TextView mSymbol;
        @BindView(R.id.bid_price) TextView mBidPrice;
        @BindView(R.id.stock_change) TextView mChange;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setValue(final QuoteModel quoteModel) {
            mSymbol.setText(quoteModel.getSymbol());
            mBidPrice.setText(quoteModel.getBid_price());
            if (quoteModel.getIs_up().equals("1")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mChange.setBackground(context.getResources().getDrawable(R.drawable.bg_pill_green, context.getTheme()));
                }
                imgType.setImageResource(R.drawable.ic_trending_up);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mChange.setBackground(context.getResources().getDrawable(R.drawable.bg_pill_red, context.getTheme()));
                }
                imgType.setImageResource(R.drawable.ic_trending_down);
            }
            if (mChangeUnits == StockListActivity.CHANGE_UNITS_PERCENTAGES) {
                mChange.setText(quoteModel.getPercent_change());
            } else {
                mChange.setText(quoteModel.getChange());
            }

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    if(itemListener!=null) {
                        itemListener.onLongClick(quoteModel);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onClick(quoteModel);
                }
            });
        }
    }

    public interface ItemListener{
        void onClick(QuoteModel quoteModel);
        void onLongClick(QuoteModel quoteModel);

    }

}
