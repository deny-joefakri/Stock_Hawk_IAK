package com.joefakri.iakstock_hawkadvanced;


import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.joefakri.iakstock_hawkadvanced.view.StockDetailActivity;
import com.joefakri.iakstock_hawkadvanced.view.StockDetailFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class StockDetailActivityTest {

    @Rule
    public ActivityTestRule<StockDetailActivity> mActivityTestRule = new ActivityTestRule<>(StockDetailActivity.class, true, false);

    @Before
    public void setup() {
        Intent intent = new Intent();
        intent.putExtra(StockDetailFragment.ARG_SYMBOL, "YHOO");
        mActivityTestRule.launchActivity(intent);
    }

    @Test
    public void stockDActivityTest() {

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.vpServices), isDisplayed()));
        viewPager.perform(swipeRight());

        ViewInteraction viewPager3 = onView(
                allOf(withId(R.id.vpServices), isDisplayed()));
        viewPager3.perform(swipeLeft());

    }

}
