package com.feedhenry.phonegap.apkdownloader;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

public class Apkdownloader extends CordovaPlugin {

  private static final String LOG_TAG = "APKDOWNLOADER";
  
  @Override
  public boolean onOverrideUrlLoading(String url) {
    final String downloadUrl = url;
    final Apkdownloader downloaderPlugin = this;
    if(url.toLowerCase().endsWith(".apk")){
      LOG.d(LOG_TAG, "load apk file, start to download");
      this.cordova.getActivity().runOnUiThread(new Runnable() {
        
        @Override
        public void run() {
          ProgressDialog progress = new ProgressDialog(cordova.getActivity());
          progress.setMessage("Downloading");
          progress.setIndeterminate(false);
          progress.setMax(100);
          progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
          progress.show();
          AsyncFileDownloader downloader = new AsyncFileDownloader(downloaderPlugin, progress);
          downloader.execute(downloadUrl);
        }
      });
      
      return true;
    } else {
      return false;
    }
  }
  
  public void downloadFinished(Uri pResult) {
    LOG.d(LOG_TAG, "File downloaded to " + pResult);
    if (pResult.toString().toLowerCase().endsWith(".apk")) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(pResult, "application/vnd.android.package-archive");
      this.cordova.getActivity().startActivity(intent);
    }
  }

}
