package com.ourartag.ag1;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class Javascript {
    Context mContext;
    String data;
    Boolean isAnyData;
    Javascript(Context ctx){
        this.mContext=ctx;
        this.isAnyData = false;
        this.data = "";
    }
    public String getMyData() {
        return this.data;
    }
    public Boolean isThereAnyData() {
        return this.isAnyData;
    }
    public void setIsAnyDataFalse() {
        this.isAnyData = false;
    }
    @JavascriptInterface
    public void sendData(String data) {
        //Get the string value to process
        if(data.equals(this.data)){

        }else{
            this.data=data;
            this.isAnyData = true;
        }

    }
}
