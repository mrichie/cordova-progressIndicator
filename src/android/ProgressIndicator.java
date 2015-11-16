package org.apache.cordova.indicator;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.util.Log;
import android.os.CountDownTimer;
import android.content.DialogInterface;
import android.view.WindowManager;

import org.apache.cordova.indicator.AndroidProgressHUD;

public class ProgressIndicator extends CordovaPlugin {

    private ProgressDialog progressIndicator = null;
    private AndroidProgressHUD activityIndicator = null;
    private String text = null;
    private boolean showLock = false;
    private Integer progress = 0;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (this.cordova.getActivity().isFinishing()) {
            return true;
        }
        if (action.equals("show")) {
            String type = args.getString(0); //bar
            String title = args.getString(1);
            String text = args.getString(2);
            Integer pro = args.getInt(3);
            show(type, title, text, pro);
            callbackContext.success();
        } else if (action.equals("hide")) {
            hide();
            callbackContext.success();
        }else if (action.equals("showSimple")){
            boolean dim = args.getBoolean(0);
            this.showSimple(dim);
        }else if (action.equals("showSimpleWithLabel")){
            boolean dim = args.getBoolean(0);
            String text = args.getString(1);
            this.showSimpleWithLabel(dim, text);
        }else if (action.equals("showDeterminateBar")){
            boolean dim = args.getBoolean(0);
            Integer timeout = args.getInt(1);
            this.showBar(dim, timeout);
        }else if (action.equals("showProgressBar")){
            String title = args.getString(0);
            String message = args.getString(1);
            this.showProgressBar(title, message);
        }
        else if (action.equals("showDeterminateBarWithLabel")){
            boolean dim = args.getBoolean(0);
            Integer timeout = args.getInt(1);
            String message = args.getString(2);
            this.showBarWithLabel(dim, timeout, message);
        } else if (action.equals("setProgress")) {
            this.setProgress(args.getDouble(0));
        } else if (action.equals("setTitle")) {
            this.setTitle(args.getString(0));
        } else if (action.equals("setMessage")) {
            this.setMessage(args.getString(0));
        }

        return true;
    }

    /**
     * This show the ProgressDialog
     *
     * @param text - Message to display in the Progress Dialog
     */
    public void show(String type, String title, String text, Integer pro) {
        
    }

    /**
     * This hide the ProgressDialog
     */
    public void hide() {
        if (progressIndicator != null) {
            progressIndicator.dismiss();
            progressIndicator = null;
        }
        if (activityIndicator != null) {
            activityIndicator.dismiss();
            activityIndicator = null;
        } else {
            this.showLock = false;
        }
    }

    public void showSimple(Boolean dim){
        this.showLock = true;
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (ProgressIndicator.this.showLock)
                    activityIndicator = AndroidProgressHUD.show(
                            ProgressIndicator.this.cordova.getActivity(), null, true, false, null);
            }
        });
    }

    public void showSimpleWithLabel(Boolean dim, String text){
        this.text = text;
        this.showLock = true;

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (ProgressIndicator.this.showLock)
                    activityIndicator = AndroidProgressHUD.show(
                            ProgressIndicator.this.cordova.getActivity(), ProgressIndicator.this.text, true, false, null);
            }
        });
    }

    public void showBar(Boolean dim, Integer timeout){
        progressIndicator = new ProgressDialog(this.cordova.getActivity());
        progressIndicator.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressIndicator.setIndeterminate(false);
        if(dim) {
            WindowManager.LayoutParams lp = progressIndicator.getWindow().getAttributes();
            lp.dimAmount = 0.8f;
            progressIndicator.getWindow().setAttributes(lp);
        }
        progressIndicator.setCancelable(false);
        progressIndicator.show();
        CountDownTimer mCountDownTimer;
        progress = 0;
        mCountDownTimer=new CountDownTimer(timeout, timeout/100) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ progress + millisUntilFinished);
                progress++;
                progressIndicator.setProgress(progress);
            }
            @Override
            public void onFinish() {
                progress++;
                progressIndicator.setProgress(progress);
                progressIndicator.dismiss();
            }
        };
        mCountDownTimer.start();
    }

    public void showBarWithLabel(Boolean dim, Integer timeout, String message){
        progressIndicator = new ProgressDialog(this.cordova.getActivity());
        progressIndicator.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressIndicator.setIndeterminate(false);
        progressIndicator.setMessage(message);
        progressIndicator.setCancelable(false);
        progressIndicator.show();
        CountDownTimer mCountDownTimer;
        progress = 0;
        mCountDownTimer=new CountDownTimer(timeout, timeout/100) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ progress + millisUntilFinished);
                progress++;
                progressIndicator.setProgress(progress);
            }
            @Override
            public void onFinish() {
                progress++;
                progressIndicator.setProgress(progress);
                progressIndicator.dismiss();
            }
        };
        mCountDownTimer.start();
    }

    public void showProgressBar(final String title, final String message){
        final CordovaInterface cordova = this.cordova;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progressIndicator = new ProgressDialog(cordova.getActivity());
                progressIndicator.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressIndicator.setIndeterminate(false);
                progressIndicator.setTitle(title);
                progressIndicator.setMessage(message);
                progressIndicator.setCancelable(false);
                progressIndicator.show();
            }
        };
        this.cordova.getActivity().runOnUiThread(runnable);
    }

    /**
     * Set the value of the progress bar when progress style is "HORIZONTAL"
     *
     * @param rawArgs
     */
    private void setProgress(final Double rawArgs) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressIndicator != null) {
                    int value = (int) (rawArgs * 100);
                    progressIndicator.setProgress(value);
                }
            };
        };
        this.cordova.getActivity().runOnUiThread(runnable);
    }

    /**
     * Set the title of the progress dialog
     *
     * @param rawArgs
     */
    private void setTitle(final String title) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressIndicator != null) {
                    progressIndicator.setTitle(title.replaceAll("^\"|\"$", ""));
                }
            };
        };
        this.cordova.getActivity().runOnUiThread(runnable);
    }

    /**
     * Set the message of the progress dialog
     *
     * @param rawArgs
     */
    private void setMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressIndicator != null) {
                    progressIndicator.setMessage(message.replaceAll("^\"|\"$", ""));
                }
            };
        };
        this.cordova.getActivity().runOnUiThread(runnable);
    }


}
