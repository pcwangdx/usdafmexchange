package com.mahoneydev.usdafmexchange;

import java.util.Hashtable;

/**
 * Created by mahoneydev on 5/25/2016.
 */
public class PageNode {
    public Hashtable<String,String> params;
    public int pageId;
    public PageNode(int id,Hashtable ht)
    {
        params=ht;
        pageId=id;
    }
}
