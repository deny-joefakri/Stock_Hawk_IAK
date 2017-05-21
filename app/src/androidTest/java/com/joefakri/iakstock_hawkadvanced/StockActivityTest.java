package com.joefakri.iakstock_hawkadvanced;


import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.joefakri.iakstock_hawkadvanced.view.StockListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class StockActivityTest {

    @Rule
    public ActivityTestRule<StockListActivity> mActivityTestRule = new ActivityTestRule<>(StockListActivity.class, true, true);

    @Test
    public void stockListActivityTest() {

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.btnFab), withContentDescription("Add a new stock quote"),
                        withParent(allOf(withId(R.id.clBaseView),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction mDButton = onView(
                allOf(withId(R.id.buttonDefaultNegative), withText("Cancel"), isDisplayed()));
        mDButton.perform(click());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.btnFab), withContentDescription("Add a new stock quote"),
                        withParent(allOf(withId(R.id.clBaseView),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(android.R.id.input), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(android.R.id.input), isDisplayed()));
        appCompatEditText2.perform(replaceText("fb"), closeSoftKeyboard());

        ViewInteraction mDButton2 = onView(
                allOf(withId(R.id.buttonDefaultPositive), withText("Add"), isDisplayed()));
        mDButton2.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.action_change_units), withContentDescription("Change Units"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.action_change_units), withContentDescription("Change Units"), isDisplayed()));
        actionMenuItemView2.perform(click());

        //openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withText("YHOO"), isDisplayed()));
        appCompatTextView.perform(click());

        pressBack();

        ViewInteraction appCompatTextView2 = onView(
                allOf(withText("AAPL"), isDisplayed()));
        appCompatTextView2.perform(click());

        pressBack();

        ViewInteraction appCompatTextView3 = onView(
                allOf(withText("GOOG"), isDisplayed()));
        appCompatTextView3.perform(click());

        pressBack();

        ViewInteraction appCompatTextView4 = onView(
                allOf(withText("GOOG"), isDisplayed()));
        appCompatTextView4.perform(longClick());

        ViewInteraction appCompatTextView5 = onView(
                allOf(withText("AAPL"), isDisplayed()));
        appCompatTextView5.perform(longClick());


        //mActivityTestRule.getActivity().turnOff();

        Intent intent = new Intent();
        mActivityTestRule.launchActivity(intent);

        ViewInteraction floatingActionButton4= onView(
                allOf(withId(R.id.btnFab), withContentDescription("Add a new stock quote"),
                        withParent(allOf(withId(R.id.clBaseView),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        floatingActionButton4.perform(click());

    }


}
