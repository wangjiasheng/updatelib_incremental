package com.wjs.upatetask;

import android.content.Context;

import com.wjs.updatelib.MD5Utils;
import com.wjs.updatelib.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WJS on 2016/9/22.
 */
public class URLBuilder
{
    private Context context;
    private String updateurl;
    private Map<String,String> urlmap=new HashMap<String,String>();
    public URLBuilder(Context context)
    {
        if(context==null)
        {
            throw new NullPointerException("Context not NULL");
        }
        this.context=context.getApplicationContext();
    }
    public URLBuilder builderHost(String url)
    {
        StringBuilder builder=new StringBuilder();
        builder.append("http://");
        builder.append(url);
        builder.append("/CheckUpdate?packageName="+ Util.getPackageName(context));
        builder.append("&verCode="+Util.getVerCode(context));
        builder.append("&apkmd5="+ MD5Utils.getFileMD5(new File(context.getPackageCodePath())));
        urlmap.put("url",builder.toString());
        urlmap.put("incremental_update","true");
        return this;
    }
    public URLBuilder incremental_update(boolean incremental_update)
    {
        urlmap.put("incremental_update",""+incremental_update);
        return this;
    }
    public String build()
    {
        String url=urlmap.get("url");
        String param=urlmap.get("incremental_update");
        String reurl=url+"&incremental_update="+param;
        return reurl;
    }
}