package com.joefakri.iakstock_hawkadvanced.view;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.joefakri.iakstock_hawkadvanced.R;
import com.joefakri.iakstock_hawkadvanced.adapter.ItemTouchHelperCallback;
import com.joefakri.iakstock_hawkadvanced.adapter.QuoteCursorAdapter;
import com.joefakri.iakstock_hawkadvanced.adapter.RecyclerViewItemClickListener;
import com.joefakri.iakstock_hawkadvanced.data.QuoteColumns;
import com.joefakri.iakstock_hawkadvanced.data.QuoteProvider;
import com.joefakri.iakstock_hawkadvanced.service.StockIntentService;
import com.joefakri.iakstock_hawkadvanced.service.StockTaskService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StockListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerViewItemClickListener.OnItemClickListener{

    @BindView(R.id.clBaseView) CoordinatorLayout clBaseView;
    @BindView(R.id.rvStockList) RecyclerView rvStockList;
    @BindView(R.id.pbStockList) ProgressBar pbStockList;
    @BindView(R.id.view_no_connection) View view_no_connection;
    @BindView(R.id.view_no_stocks) View view_no_stocks;

    private MaterialDialog mDialog;
    private QuoteCursorAdapter mAdapter;
    private boolean mTwoPane;

    public static final int CHANGE_UNITS_DOLLARS = 0;
    public static final int CHANGE_UNITS_PERCENTAGES = 1;
    private static final int CURSOR_LOADER_ID = 0;
    private final String EXTRA_CHANGE_UNITS = "EXTRA_CHANGE_UNITS";
    private final String EXTRA_ADD_DIALOG_OPENED = "EXTRA_ADD_DIALOG_OPENED";
    private int mChangeUnits = CHANGE_UNITS_DOLLARS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        if (findViewById(R.id.stock_detail_container) != null) {
            mTwoPane = true;
        }


        if (savedInstanceState == null) {
            Intent stackServiceIntent = new Intent(this, StockIntentService.class);
            stackServiceIntent.putExtra(StockIntentService.EXTRA_TAG, StockIntentService.ACTION_INIT);
            if (isNetworkAvailable()) {
                startService(stackServiceIntent);
            } else {
                Snackbar.make(clBaseView, getString(R.string.msg_no_internet_connection),
                        Snackbar.LENGTH_LONG).show();
            }
        } else {
            mChangeUnits = savedInstanceState.getInt(EXTRA_CHANGE_UNITS);
            if (savedInstanceState.getBoolean(EXTRA_ADD_DIALOG_OPENED, false)) {
                showDialogForAddingStock();
            }
        }


        rvStockList.setLayoutManager(new LinearLayoutManager(this));
        rvStockList.addOnItemTouchListener(new RecyclerViewItemClickListener(this, this));

        mAdapter = new QuoteCursorAdapter(this, null, mChangeUnits);
        rvStockList.setAdapter(mAdapter);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvStockList);

        PeriodicTask periodicTask = new PeriodicTask.Builder()
                .setService(StockTaskService.class)
                .setPeriod(/* 1h */ 60 * 60)
                .setFlex(/* 10s */ 10)
                .setTag(StockTaskService.TAG_PERIODIC)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();
        GcmNetworkManager.getInstance(this).schedule(periodicTask);

    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_CHANGE_UNITS, mChangeUnits);
        if (mDialog != null) {
            outState.putBoolean(EXTRA_ADD_DIALOG_OPENED, mDialog.isShowing());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_units) {
            if (mChangeUnits == CHANGE_UNITS_DOLLARS) {
                mChangeUnits = CHANGE_UNITS_PERCENTAGES;
            } else {
                mChangeUnits = CHANGE_UNITS_DOLLARS;
            }
            mAdapter.setChangeUnits(mChangeUnits);
            mAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        view_no_connection.setVisibility(View.GONE);
        view_no_stocks.setVisibility(View.GONE);
        pbStockList.setVisibility(View.VISIBLE);
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pbStockList.setVisibility(View.GONE);
        mAdapter.swapCursor(data);

        if (mAdapter.getItemCount() == 0) {
            if (!isNetworkAvailable()) {
                view_no_connection.setVisibility(View.VISIBLE);
            } else {
                view_no_stocks.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            view_no_connection.setVisibility(View.GONE);
            view_no_stocks.setVisibility(View.GONE);
        }

        if (!isNetworkAvailable()) {
            Snackbar.make(clBaseView, getString(R.string.msg_offline),
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.msg_try_again, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, StockListActivity.this);
                }
            }).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @OnClick(R.id.btnFab)
    public void showDialogForAddingStock() {
        if (isNetworkAvailable()) {
            mDialog = new MaterialDialog.Builder(this).title(R.string.textview_add_symbol)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .autoDismiss(true)
                    .positiveText(R.string.btn_add)
                    .negativeText(R.string.btn_disagree)
                    .input(R.string.msg_hint, R.string.input_pre_fill, false,
                            new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    addStockQuote(input.toString());
                                }
                            }).build();
            mDialog.show();

        } else {
            Snackbar.make(clBaseView, getString(R.string.msg_no_internet_connection),
                    Snackbar.LENGTH_LONG).setAction(R.string.msg_try_again, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogForAddingStock();
                }
            }).show();
        }
    }

    private void addStockQuote(final String stockQuote) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                Cursor cursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns.SYMBOL},
                        QuoteColumns.SYMBOL + "= ?",
                        new String[]{stockQuote},
                        null);
                if (cursor != null) {
                    cursor.close();
                    return cursor.getCount() != 0;
                }
                return Boolean.FALSE;
            }

            @Override
            protected void onPostExecute(Boolean stockAlreadySaved) {
                if (stockAlreadySaved) {
                    Snackbar.make(clBaseView, R.string.msg_stock_already_saved,
                            Snackbar.LENGTH_LONG).show();
                } else {
                    Intent stockIntentService = new Intent(StockListActivity.this,
                            StockIntentService.class);
                    stockIntentService.putExtra(StockIntentService.EXTRA_TAG, StockIntentService.ACTION_ADD);
                    stockIntentService.putExtra(StockIntentService.EXTRA_SYMBOL, stockQuote);
                    startService(stockIntentService);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(StockDetailFragment.ARG_SYMBOL, mAdapter.getSymbol(position));
            StockDetailFragment fragment = new StockDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.stock_detail_container, fragment)
                    .commit();
        } else {
            Context context = v.getContext();
            Intent intent = new Intent(context, StockDetailActivity.class);
            intent.putExtra(StockDetailFragment.ARG_SYMBOL, mAdapter.getSymbol(position));
            context.startActivity(intent);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
