package com.joefakri.iakstock_hawkadvanced.view;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.joefakri.iakstock_hawkadvanced.R;
import com.joefakri.iakstock_hawkadvanced.data.QuoteColumns;
import com.joefakri.iakstock_hawkadvanced.data.QuoteHistoricalDataColumns;
import com.joefakri.iakstock_hawkadvanced.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by deny on bandung.
 */

public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        TabHost.OnTabChangeListener {


    public static String LOG_TAG = StockDetailFragment.class.getSimpleName();
    public static final String ARG_SYMBOL = "ARG_SYMBOL";
    public static final String EXTRA_CURRENT_TAB = "EXTRA_CURRENT_TAB";
    private static final int CURSOR_LOADER_ID = 1;
    private static final int CURSOR_LOADER_ID_FOR_LINE_CHART = 2;

    private String mSymbol;
    private String mSelectedTab;

    @BindView(R.id.img_type) ImageView imgType;
    @BindView(R.id.stock_name) TextView mNameView;
    @BindView(R.id.stock_symbol) TextView mSymbolView;
    @BindView(R.id.stock_bidprice) TextView mEbitdaView;
    @BindView(R.id.stock_change) TextView mChange;
    @BindView(R.id.tabServices) TabLayout tabServices;
    @BindView(R.id.vpServices) ViewPager vpServices;
    ViewPagerAdapter adapter;

    public StockDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_SYMBOL)) {
            mSymbol = getArguments().getString(ARG_SYMBOL);
        }

        if (getActionBar() != null) {
            getActionBar().setElevation(0);
            if (getActivity() instanceof StockDetailActivity) {
                getActionBar().setTitle("");
            }
        }

        if (savedInstanceState == null) {
            mSelectedTab = getString(R.string.stock_detail_tab1);
        } else {
            mSelectedTab = savedInstanceState.getString(EXTRA_CURRENT_TAB);
        }

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        getLoaderManager().initLoader(CURSOR_LOADER_ID_FOR_LINE_CHART, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_detail, container, false);
        ButterKnife.bind(this, rootView);
        //setupTabs();
        setupViewPager();
        tabServices.setupWithViewPager(vpServices);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_CURRENT_TAB, mSelectedTab);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CURSOR_LOADER_ID) {
            return new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP,
                            QuoteColumns.NAME},
                    QuoteColumns.SYMBOL + " = \"" + mSymbol + "\"",
                    null, null);
        } else if (id == CURSOR_LOADER_ID_FOR_LINE_CHART) {

            String sortOrder = QuoteColumns._ID + " ASC LIMIT 5";
            if (mSelectedTab.equals(getString(R.string.stock_detail_tab2))) {
                sortOrder = QuoteColumns._ID + " ASC LIMIT 14";
            } else if (mSelectedTab.equals(getString(R.string.stock_detail_tab3))) {
                sortOrder = QuoteColumns._ID + " ASC";
            }

            return new CursorLoader(getContext(), QuoteProvider.QuotesHistoricData.CONTENT_URI,
                    new String[]{QuoteHistoricalDataColumns._ID, QuoteHistoricalDataColumns.SYMBOL,
                            QuoteHistoricalDataColumns.BIDPRICE, QuoteHistoricalDataColumns.DATE},
                    QuoteHistoricalDataColumns.SYMBOL + " = \"" + mSymbol + "\"",
                    null, sortOrder);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CURSOR_LOADER_ID && data != null && data.moveToFirst()) {

            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
            mSymbolView.setText(getString(R.string.stock_detail_tab_header, symbol));

            String ebitda = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
            mEbitdaView.setText(ebitda);

            String name = data.getString(data.getColumnIndex(QuoteColumns.NAME));
            mNameView.setText(name);

            String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
            String percentChange = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
            String mixedChange = change + " (" + percentChange + ")";
            mChange.setText(mixedChange);

            if (data.getInt(data.getColumnIndex(QuoteColumns.ISUP)) == 1){
                imgType.setImageResource(R.drawable.ic_trending_up);
            } else {
                imgType.setImageResource(R.drawable.ic_trending_down);
            }

        } else if (loader.getId() == CURSOR_LOADER_ID_FOR_LINE_CHART && data != null &&
                data.moveToFirst()) {
            //updateChart(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to do
    }

    @Override
    public void onTabChanged(String tabId) {
        mSelectedTab = tabId;
        getLoaderManager().restartLoader(CURSOR_LOADER_ID_FOR_LINE_CHART, null, this);
    }

    @Nullable
    private ActionBar getActionBar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            return activity.getSupportActionBar();
        }
        return null;
    }

    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new DaysFragment(), getString(R.string.stock_detail_tab1));
        adapter.addFrag(new WeekFragment(), getString(R.string.stock_detail_tab2));
        adapter.addFrag(new MonthFragment(), getString(R.string.stock_detail_tab3));
        vpServices.setAdapter(adapter);
        vpServices.setOffscreenPageLimit(0);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    /*private void setupTabs() {
        mTabHost.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab1));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab1));
        tabSpec.setContent(android.R.id.tabcontent);
        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab2));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab2));
        tabSpec.setContent(android.R.id.tabcontent);
        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab3));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab3));
        tabSpec.setContent(android.R.id.tabcontent);
        mTabHost.addTab(tabSpec);

        mTabHost.setOnTabChangedListener(this);

        if (mSelectedTab.equals(getString(R.string.stock_detail_tab2))) {
            mTabHost.setCurrentTab(1);
        } else if (mSelectedTab.equals(getString(R.string.stock_detail_tab3))) {
            mTabHost.setCurrentTab(2);
        } else {
            mTabHost.setCurrentTab(0);
        }
    }*/

}
