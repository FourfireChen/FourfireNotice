package com.example.lenovo.fourfirenotice.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lenovo on 2017/12/7.
 */

public class Now
{
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More
    {
        @SerializedName("txt")
        public String info;
    }
}
