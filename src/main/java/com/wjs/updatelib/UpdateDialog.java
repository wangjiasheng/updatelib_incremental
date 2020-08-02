package com.wjs.updatelib;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 家胜 on 2016/3/18.
 */
public class UpdateDialog
{
    /**
     * @param context 上下文对象
     * @param serverVersion 升级的版本号码
     * @param serverName 上级的版本名称
     * @param updateMessage 升级的错误列表
     * @param downUrl 下载的ApkURL
     */
    public void updateApp(Context context,boolean incremental_update, int serverVersion,String serverName,long size,List<String> updateMessage, String downUrl,boolean simple) {
        int localVersion = Util.getVerCode(context);
        if (serverVersion > localVersion)
        {
            if(simple)
            {
                showConfigDialog(context,incremental_update,serverName,downUrl);
            }
            else
            {
                showConfigDialog(context,incremental_update,serverName,size,updateMessage,downUrl);
            }
        }
    }

    /**
     * @param context 上下文对象
     * @param versionName 上级的版本名称
     * @param downUrl 下载的ApkURL
     */
    private void showConfigDialog(final Context context, final boolean incremental_update, String versionName, final String downUrl) {
        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(Util.getVerName(context));
        sb.append("\n");
        sb.append("发现新版本:");
        sb.append(versionName);
        sb.append("\n");
        sb.append("是否更新?");
        Dialog dialog = new AlertDialog.Builder(context).setTitle("软件更新").setMessage(sb.toString())
                .setPositiveButton("软件更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, MyService.class);
                        intent.putExtra(MyService.URLPath, downUrl);
                        intent.putExtra(MyService.INCREMENTAL,incremental_update);
                        context.startService(intent);
                    }
                })
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    /**
     * @param context 上下文对象
     * @param versionName 上级的版本名称
     * @param updateMessage 升级的错误列表
     * @param downUrl 下载的ApkURL
     */
    private void showConfigDialog(final Context context, final boolean incremental_update, String versionName, List<String> updateMessage, final String downUrl) {
        LinearLayout layout= (LinearLayout) LayoutInflater.from(context).inflate(R.layout.updatelayout,null);
        TextView currentVersion= (TextView) layout.findViewById(R.id.currentVersion);
        TextView newVersion= (TextView) layout.findViewById(R.id.newVersion);
        TextView newUpdate= (TextView) layout.findViewById(R.id.newUpdate);
        TextView newUpdateMessage= (TextView) layout.findViewById(R.id.newUpdateMessage);
        currentVersion.setText("当前版本:"+Util.getVerName(context));
        newVersion.setText("发现新版本:" + versionName);
        if(updateMessage!=null&&updateMessage.size()!=0)
        {
            StringBuilder builder=new StringBuilder();
            for (int i = 0;i<updateMessage.size();i++)
            {
                builder.append("【" + (i + 1) + "】" + updateMessage.get(i));
                if(i!=updateMessage.size()-1) {
                    builder.append("\r\n");
                }
            }
            newUpdateMessage.setText(builder.toString());
        }
        else
        {
            newUpdateMessage.setVisibility(View.GONE);
        }
        newUpdate.setText("是否更新?");
        Dialog dialog = new AlertDialog.Builder(context).setTitle("软件更新")
                .setPositiveButton("软件更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, MyService.class);
                        intent.putExtra(MyService.URLPath, downUrl);
                        intent.putExtra(MyService.INCREMENTAL,incremental_update);
                        context.startService(intent);
                    }
                })
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setView(layout).create();
        dialog.show();
    }
    /**
     * @param context 上下文对象
     * @param versionName 上级的版本名称
     * @param updateMessage 升级的错误列表
     * @param downUrl 下载的ApkURL
     */
    private void showConfigDialog(final Context context, final boolean incremental_update, String versionName,long size, List<String> updateMessage, final String downUrl) {
        LinearLayout layout= (LinearLayout) LayoutInflater.from(context).inflate(R.layout.update,null);
        TextView currentVersion= (TextView) layout.findViewById(R.id.currentVersion);
        TextView newVersion= (TextView) layout.findViewById(R.id.newVersion);
        TextView newVersionmethod= (TextView) layout.findViewById(R.id.versionmethod);
        TextView newUpdate= (TextView) layout.findViewById(R.id.newUpdate);
        TextView newVersionsize= (TextView) layout.findViewById(R.id.versionsize);
        TextView newUpdateMessage= (TextView) layout.findViewById(R.id.newUpdateMessage);
        currentVersion.setText("发现版本:"+versionName);
        String str="<font color='\" + color + \"'>\" + text + \"</font>";
        newVersion.setText("升级方式:");
        newVersionmethod.setText(incremental_update?"增量":"完整");
        newVersionsize.setText(size(size));
        if(updateMessage!=null&&updateMessage.size()!=0)
        {
            StringBuilder builder=new StringBuilder();
            for (int i = 0;i<updateMessage.size();i++)
            {
                builder.append("【" + (i + 1) + "】" + updateMessage.get(i));
                if(i!=updateMessage.size()-1) {
                    builder.append("\r\n");
                }
            }
            newUpdateMessage.setText(builder.toString());
        }
        else
        {
            newUpdateMessage.setVisibility(View.GONE);
        }
        newUpdate.setText("是否更新?");
        Dialog dialog = new AlertDialog.Builder(context).setTitle("软件更新")
                .setPositiveButton("软件更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, MyService.class);
                        intent.putExtra(MyService.URLPath, downUrl);
                        intent.putExtra(MyService.INCREMENTAL,incremental_update);
                        context.startService(intent);
                    }
                })
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setView(layout).create();
        dialog.show();
    }
    public static String size(long size)
    {
        double kiloByte = size/1024;
        if(kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte/1024;
        if(megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte/1024;
        if(gigaByte < 1) {
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte/1024;
        if(teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}
