package com.leqienglish.data.version;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.leqienglish.R;
import com.leqienglish.data.DataCacheAbstract;
import com.leqienglish.database.ExecuteSQL;
import com.leqienglish.sf.LQService;
import com.leqienglish.sf.LoadFile;
import com.leqienglish.util.LOGGER;
import com.leqienglish.util.LQHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import xyz.tobebetter.entity.Consistent;
import xyz.tobebetter.version.Version;

import static com.leqienglish.database.Constants.VERSION_CACHE_TYPE;

/**
 * 每天只提醒一次
 * 当前版本信息的缓存
 */
public class VersionDataCache extends DataCacheAbstract<Version> {
    private LOGGER logger = new LOGGER(VersionDataCache.class);
    private long currentNo = 0L;

    private Version newsetVersion;

    private static VersionDataCache versionDataCache;



    private VersionDataCache() {

    }


    public static VersionDataCache getInstance() {
        if (versionDataCache != null) {
            return versionDataCache;
        }
        synchronized (VersionDataCache.class) {
            if (versionDataCache == null) {
                versionDataCache = new VersionDataCache();
            }
        }

        return versionDataCache;
    }

    @Override
    protected String getUpdateTimeType() {
        return "VersionDataCache_update";
    }

    @Override
    protected Version getFromCache() {
        List<Version> versionList = ExecuteSQL.getDatasByType(VERSION_CACHE_TYPE,Version.class);
        if(versionList == null || versionList.isEmpty()){
            return null;
        }
        return versionList.get(0);
    }

    @Override
    protected void putCache(Version version) {

        ExecuteSQL.insertLearnE(Arrays.asList(version),null,VERSION_CACHE_TYPE);
        logger.d("insert====");
    }

    @Override
    protected Version getFromService() {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("type", Consistent.ANDROID+"");
        try {
            Version version = this.getRestClient().get("/version/findNewestByType", param, Version.class);
            this.newsetVersion = version;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载最新的版本，如果返回null，没有最新的版本，负责有
     * @param consumer
     */
    public void loadNewest(LQHandler.Consumer<Version> consumer){
        if(!LQService.isConnect){
            return;
        }
        AsyncTask asyncTask = new AsyncTask<Object, Object, Boolean>() {
            @Override
            protected Boolean doInBackground(Object[] objects) {

              return hasNewest();
            }

            @Override
            protected void onPostExecute(Boolean t) {
                super.onPostExecute(t);
                if(consumer == null){
                    return;
                }
                if(!t){
                    consumer.accept(null);
                }else{
                    consumer.accept(newsetVersion);
                }

            }
        };

        asyncTask.execute();
    }

    public void initNew(Context context){
        VersionDataCache.getInstance().loadNewest(new LQHandler.Consumer<Version>() {
            @Override
            public void accept(Version version) {
                if(version!=null){
                    openNewVersionDialog(version,context);
                }
            }
        });
    }

    public void openNewVersionDialog(Version version, Context context){

        View myView = LayoutInflater.from(context).inflate(R.layout.newversion_dialog, null);
        TextView textView = myView.findViewById(R.id.new_version_message);
        Spanned spanned = Html.fromHtml(version.getMessage());

        textView.setText(spanned);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.upgreade)
                .setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VersionDataCache.getInstance().installNewApk(context,version);

                    }
                }).setNeutralButton(R.string.install_next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .setNegativeButton(R.string.install_ignore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VersionDataCache.getInstance().add(version);
                    }
                }).setView(myView)
                .create();

        builder.show();
    }

    private boolean hasNewest(){
       Version version = this.getFromCache();

       long currentVersionNumber = 0;

       if(version!= null){
           logger.d("====getFromCache");
           currentVersionNumber = version.getVersionNo();
       }else{
           logger.d("====currentNo");
           currentVersionNumber = currentNo;
       }
        logger.d("====currentNo "+currentVersionNumber);
       Version newVersion = this.getFromService();
       if(newVersion == null){
           return false;
       }
        logger.d("====new "+newVersion.getVersionNo());
       //return true;

       return newVersion.getVersionNo() > currentVersionNumber;
    }

    @Override
    public void add(Version version) {
        this.putCache(version);
    }

    public void installNewApk(Context context, Version version){
        this.putCache(version);
        LoadFile.loadFile(version.getFilePath(), new LQHandler.Consumer<String>() {
            @Override
            public void accept(String s) {
                File apkFile = new File(s);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void clearData() {
        ExecuteSQL.delete(VERSION_CACHE_TYPE);
    }


}
