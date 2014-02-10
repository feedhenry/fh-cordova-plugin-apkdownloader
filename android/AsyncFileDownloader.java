package com.feedhenry.phonegap.apkdownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.cordova.CordovaPlugin;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class AsyncFileDownloader extends AsyncTask<String, Integer, Uri> {

  private static final String TAG = "AsyncFileDownloader";
  private Apkdownloader mContext;
  private ProgressDialog mProgressDialog = null;
  
  public AsyncFileDownloader(Apkdownloader pContext, ProgressDialog pDialog) {
    this.mContext = pContext;
    this.mProgressDialog = pDialog;
  }
  
  @Override
  protected Uri doInBackground(String... urls) {
    int count;
    File outputFile = null;
    try{
      String urlString = urls[0];
      String[] urlParts = urlString.split("/");
      String fileName = urlParts[urlParts.length - 1];
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection();
      connection.connect();
      int fileSize = connection.getContentLength();
      
      InputStream input = new BufferedInputStream(url.openStream());
      outputFile = new File(Environment.getExternalStorageDirectory(), "/Downloads/" + fileName);
      if(outputFile.exists()){
        outputFile.delete();
      }
      if(!outputFile.getParentFile().exists()){
        outputFile.getParentFile().mkdirs();
      }
      outputFile.createNewFile();
      OutputStream output = new FileOutputStream(outputFile);
      byte[] buffer = new byte[1024];
      long total = 0;
      while( (count = input.read(buffer)) != -1){
        total += count;
        publishProgress((int)total*100/fileSize);
        output.write(buffer, 0, count);
      }
      output.flush();
      output.close();
      input.close();
    } catch (Exception e){
      Log.e(TAG, e.getMessage(), e);
    }
    if(null != outputFile){
      return Uri.fromFile(outputFile);
    } else {
      return null;
    }
    
  }
  
  
  protected void onProgressUpdate(Integer... args){
    mProgressDialog.setProgress(args[0]);
  }
  
  protected void onPostExecute(Uri result) {
    Log.d(TAG, "Download finished. File path is " + result);
    if(mProgressDialog.isShowing()){
      mProgressDialog.dismiss();
    }
    mContext.downloadFinished(result);
  }
  
}
