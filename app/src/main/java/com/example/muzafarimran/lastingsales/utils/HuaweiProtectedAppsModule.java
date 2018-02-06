package com.example.muzafarimran.lastingsales.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class HuaweiProtectedAppsModule {

    private final Context mContext;

    public HuaweiProtectedAppsModule(Context context) {
        this.mContext = context;
    }

    public String getName() {
        return "HuaweiProtectedApps";
    }

    public void AlertIfHuaweiDevice(String title, String message, String dontShowAgainText, String positiveText, String negativeText) {
        // read "do not show again" flag
        final SharedPreferences settings = mContext.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        // Show dialog only when "do not show again" hasn't been enabled yet
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            // Check if intent of the Huawei protected apps activity is callable
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                // Prepare dialog
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(mContext);
                dontShowAgain.setText(dontShowAgainText);
                dontShowAgain.setLeft(20);
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();
                    }
                });

                final RelativeLayout layout = new RelativeLayout(mContext);
                layout.setPadding(50, 50, 0, 0);
                layout.addView(dontShowAgain);

                new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(title)
                        .setMessage(message)
                        .setView(layout)
                        .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Launch huawei Protected Apps Activity
                                huaweiProtectedApps();
                            }
                        })
                        .setNegativeButton(negativeText, null)
                        .show();
            } else {
                // Save "do not show again" flag automatically for non-Huawei devices to prevent unnecessary checks
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {

            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            // append user serial to start command as Android >=JellyBean has become multi user
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = mContext.getApplicationContext().getSystemService(Context.USER_SERVICE);
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }
}
