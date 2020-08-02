package com.wjs.upatetask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.wjs.updatelib.UpdateDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WJS on 2016/9/21.
 */
public class NetworkTask extends AsyncTask<Void,Void,String>
{
    private final String requestUrl;
    private final Context context;
    public NetworkTask(Context context, String url)
    {
        this.requestUrl=url;
        this.context=context;
    }
    @Override
    protected String doInBackground(Void... voids) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try
        {
            url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (conn.getResponseCode() == 200)
            {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1)
                {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else
            {
                throw new RuntimeException(" responseCode is not 200 ... ");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e)
            {
            }
            if(conn!=null) {
                conn.disconnect();
                conn=null;
            }
        }
        return null ;
    }

    @Override
    protected void onPostExecute(String str) {
        try
        {
            if(str!=null) {
                final JSONObject jsonObject = new JSONObject(str);
                boolean bool = jsonObject.getBoolean("status");
                if (bool) {

                    final boolean incremental_update = jsonObject.getBoolean("incremental_update");
                    final int verCode = jsonObject.getInt("verCode");
                    final String verName = jsonObject.getString("verName");
                    final String reqUrl = jsonObject.getString("url");
                    long size=jsonObject.getLong("size");
                    JSONArray array = jsonObject.getJSONArray("updatemessage");
                    List<String> updatemessage = new ArrayList<String>();
                    for (int i = 0; i < array.length(); i++) {
                        String message=array.getString(i);
                        if(message!=null&&!message.trim().equals("")) {
                            updatemessage.add(message);
                        }
                    }
                    UpdateDialog dialog = new UpdateDialog();
                    dialog.updateApp(context, incremental_update, verCode, verName,size, updatemessage, reqUrl,false);
                }
                else
                {
                    String errormessage = jsonObject.getString("message");
                    int errCode=jsonObject.getInt("errCode");
                    switch (errCode){
                        case 106:
                        case 108:
                            break;
                        default:
                            Toast.makeText(context, errormessage, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
