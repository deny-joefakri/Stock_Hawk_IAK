package com.joefakri.iakstock_hawkadvanced.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.joefakri.iakstock_hawkadvanced.R;
import com.joefakri.iakstock_hawkadvanced.widget.LanguageHelper;
import com.joefakri.iakstock_hawkadvanced.widget.SPref;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by deny on bandung.
 */

public class LanguageActivity extends AppCompatActivity {

    @BindView(R.id.btnEnglish) LinearLayout btnEnglish;
    @BindView(R.id.btnEgypt) LinearLayout btnEgypt;
    @BindView(R.id.imgEnglish) ImageView imgEnglish;
    @BindView(R.id.imgEgypt) ImageView imgEgypt;
    Locale locale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.textview_language));
        }

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgEnglish.setVisibility(View.VISIBLE);
                imgEgypt.setVisibility(View.GONE);
                LanguageHelper.changeLocale(getResources(),"en");
                SPref.setPref(LanguageActivity.this, getString(R.string.textview_language),"en");
                finish();
            }
        });

        btnEgypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgEgypt.setVisibility(View.VISIBLE);
                imgEnglish.setVisibility(View.GONE);
                LanguageHelper.changeLocale(getResources(),"ar");
                SPref.setPref(LanguageActivity.this, getString(R.string.textview_language),"ar");
                finish();
            }
        });

        if (SPref.getPref(this, getString(R.string.textview_language),"").equals("ar")){
            imgEnglish.setVisibility(View.GONE);
            imgEgypt.setVisibility(View.VISIBLE);
        } else {
            imgEgypt.setVisibility(View.GONE);
            imgEnglish.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
