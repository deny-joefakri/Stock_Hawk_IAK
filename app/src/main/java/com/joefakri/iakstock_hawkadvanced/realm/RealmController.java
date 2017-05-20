package com.joefakri.iakstock_hawkadvanced.realm;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.joefakri.iakstock_hawkadvanced.data.QuoteColumns;
import com.joefakri.iakstock_hawkadvanced.data.QuoteHistoricalDataColumns;
import com.joefakri.iakstock_hawkadvanced.network.ResponseGetHistoricalData;
import com.joefakri.iakstock_hawkadvanced.network.StockQuote;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Context context) {
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity);
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application.getApplicationContext());
        }
        return instance;
    }

    public static RealmController with(Context context) {

        if (instance == null) {
            instance = new RealmController(context);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }


    /**
     *
     * Quote Table Controller
     *
     * */


    public RealmResults<QuoteTable> getQuoteTables() {
        RealmResults<QuoteTable> result = realm.where(QuoteTable.class).findAll();
        return result;
    }


    public void insertQuote(StockQuote quote, boolean mIsUpdate){
        realm.beginTransaction();
        QuoteTable quoteTable = realm.createObject(QuoteTable.class);
        String change = quote.getChange();
        quoteTable.set_id(id());
        quoteTable.setSymbol(quote.getSymbol());
        quoteTable.setBid_price(truncateBidPrice(quote.getBid()));
        quoteTable.setPercent_change(truncateChange(quote.getChangeInPercent(), true));
        quoteTable.setChange(truncateChange(change, false));
        if (mIsUpdate){
            quoteTable.setIs_current("0");
        } else {
            quoteTable.setIs_current("1");
        }
        if (change.charAt(0) == '-') {
            quoteTable.setIs_up("0");
        } else {
            quoteTable.setIs_up("1");
        }
        quoteTable.setName(quote.getName());
        realm.commitTransaction();
    }

    public int id(){
        Number currentIdNum = realm.where(QuoteTable.class).max("_id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    private static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format(Locale.US, "%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    private static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format(Locale.US, "%.2f", round);
        StringBuilder changeBuffer = new StringBuilder(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }


    /**
     *
     * insert History
     *
     * */

    public void insertHistory(ResponseGetHistoricalData.Quote quote){
        realm.beginTransaction();
        QuoteHistory quoteHistory = realm.createObject(QuoteHistory.class);
        quoteHistory.set_id(idHistory());
        quoteHistory.setSymbol(quote.getSymbol());
        quoteHistory.setBid_price(quote.getOpen());
        quoteHistory.setDate(quote.getDate());
        realm.commitTransaction();
    }

    public int idHistory(){
        Number currentIdNum = realm.where(QuoteHistory.class).max("_id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return nextId;
    }

    public void deleteHistory(String symbol){
        QuoteHistory quoteHistory = realm.where(QuoteHistory.class).equalTo("symbol", symbol).findFirst();
        if (quoteHistory != null)
            quoteHistory.deleteFromRealm();
    }

    /*public Value createValue() {
        clearDataValue();
        return realm.createObject(Value.class);
    }

    public void clearDataValue() {
        realm.beginTransaction();
        getAllValueOnce().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void insertValue(int id, String value, int image, String backup_value){
        realm.beginTransaction();
        Value data = realm.createObject(Value.class);
        data.setId(id);
        data.setOnce(0);
        data.setValue(value);
        data.setImage(image);
        data.setBackup_value(backup_value);
        realm.commitTransaction();
    }

    public void editValueOnce(String key){
        realm.beginTransaction();
        Value value = getValue(key);
        value.setOnce(1);
        realm.commitTransaction();
    }

    *//*public void editAllValueOnceOne(){
        RealmResults<Value> values = getAllValue();
        for (int i = 0; i < values.size(); i++) {
            realm.beginTransaction();
            Value value = values.get(i);
            value.setOnce(1);
            realm.commitTransaction();
        }
    }*//*

    public void editAllValueOnce(){
        RealmResults<Value> values = getAllValue();
        for (int i = 0; i < values.size(); i++) {
            realm.beginTransaction();
            Value value = values.get(i);
            value.setOnce(0);
            realm.commitTransaction();
        }
    }

    public RealmResults<Value> getAllValue() {
        return realm.where(Value.class).findAll();
    }

    public RealmResults<Value> getAllValueOnce() {
        return realm.where(Value.class).equalTo("once", 0).findAll();
    }

    public RealmResults<Value> getAllValueAscending() {
        RealmResults<Value> result = realm.where(Value.class).findAll();
        result.sort("id", Sort.ASCENDING);
        return result;
    }

    public Value getValue(String value) {
        return realm.where(Value.class)
                .equalTo("value", value)
                .findFirst();
    }

    public Number getId(){
        Number currentIdNum = realm.where(Value.class).max("id");
        int nextId;
        if(currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        return currentIdNum;
    }


    *//**
     * Create User
     * *//*

    public User createUser() {
        //clearUser();
        return realm.createObject(User.class);
    }

    public void clearUser() {
        getAllUser().deleteAllFromRealm();
    }

    public RealmResults<User> getAllUser() {
        return realm.where(User.class).findAllSorted("score", Sort.DESCENDING);
    }

    public void insertUser(String name){
        realm.beginTransaction();
        User user = createUser();
        user.setName(name);
        user.setScore(0);
        user.setSoul(5);
        realm.commitTransaction();
    }

    public User getItemUser(String name) {
        return realm.where(User.class)
                .equalTo("name", name)
                .findFirst();
    }

    public void editUserScore(String name, int score){
        realm.beginTransaction();
        User user = getUser(name);
        user.setScore(score);
        realm.commitTransaction();
    }

    public void editUserSoul(String name){
        realm.beginTransaction();
        User user = getUser(name);
        user.setSoul(user.getSoul() - 1);
        realm.commitTransaction();
    }

    public User getUser(String name) {
        return realm.where(User.class)
                .equalTo("name", name)
                .findFirst();
    }



    *//**
     *
     * Pembelajaran
     *
     * *//*

    public void clearDataPembelajaran() {
        realm.beginTransaction();
        getAllPembelajaran().deleteAllFromRealm();
        realm.commitTransaction();
    }


    public void insertPembelajaranWarna(String word, int image){
        realm.beginTransaction();
        Pembelajaran pembelajaran = realm.createObject(Pembelajaran.class);
        pembelajaran.setType("warna");
        pembelajaran.setWord(word);
        pembelajaran.setImage(image);
        realm.commitTransaction();
    }

    public void insertPembelajaranAngka(String word, int image){
        realm.beginTransaction();
        Pembelajaran pembelajaran = realm.createObject(Pembelajaran.class);
        pembelajaran.setType("angka");
        pembelajaran.setWord(word);
        pembelajaran.setImage(image);
        realm.commitTransaction();
    }

    public void insertPembelajaranKata(String word, int image){
        realm.beginTransaction();
        Pembelajaran pembelajaran = realm.createObject(Pembelajaran.class);
        pembelajaran.setType("kata");
        pembelajaran.setWord(word);
        pembelajaran.setImage(image);
        realm.commitTransaction();
    }

    public RealmResults<Pembelajaran> getPembelajaran(String type) {
        return realm.where(Pembelajaran.class).equalTo("type", type).findAll();
    }

    public RealmResults<Pembelajaran> getAllPembelajaran() {
        return realm.where(Pembelajaran.class).findAll();
    }*/


}
