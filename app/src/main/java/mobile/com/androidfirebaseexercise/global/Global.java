package mobile.com.androidfirebaseexercise.global;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.provider.Settings;

/**
 * Created by rauzan on 31/10/18.
 */
public class Global {
    public static ProgressDialog dialog = null;

    public static AlertDialog.Builder alertDialog =null;
    public static String PositiveButton ="Yes";
    public static String OkButton ="Okay";
    public static String NegativeButton ="Cancel";

    public static void showDialog(Context context){
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = ProgressDialog.show(context, "", "Please wait..", true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    public static void dismissDialog(){
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static String getAndroidID(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void showConfirmDialog(Context context,String title,String messages){
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(messages);
        alertDialog.setCancelable(false);

    }

}
