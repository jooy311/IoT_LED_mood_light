package com.example.eunaecho.gproject;

/**
 * Created by Eunae Cho on 2018-10-21.
 */



public class ListItem {

    private String[] mData;

    public ListItem(String[] data ){
        mData = data;
    }

    public ListItem(String id, String date, String diary){

        mData = new String[3];
        mData[0] = id;
        mData[1] = date;
        mData[2] = diary;

    }

    public String[] getData(){
        return mData;
    }

    public String getData(int index){
        return mData[index];
    }

    public void setData(String[] data){
        mData = data;
    }
}