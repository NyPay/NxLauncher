package net.kdt.pojavlaunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** Safe in-app updater. A failed check/download/install never interrupts the launcher. */
public final class NXLauncherUpdater {
    private static final String MANIFEST_URL = "https://raw.githubusercontent.com/NyPay/NxLauncher/NXLauncher-v2.5.6/update.json";
    private static final String UPDATE_APK_NAME = "NXLauncher-update.apk";

    private NXLauncherUpdater() {}

    public static void check(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
        new Thread(() -> {
            HttpURLConnection c = null;
            try {
                c = (HttpURLConnection) new URL(MANIFEST_URL + "?t=" + System.currentTimeMillis()).openConnection();
                c.setConnectTimeout(5000);
                c.setReadTimeout(7000);
                c.setUseCaches(false);
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) return;
                try (InputStream in = c.getInputStream()) {
                    byte[] data = readAll(in);
                    JSONObject json = new JSONObject(new String(data, "UTF-8"));
                    int remoteCode = json.optInt("versionCode", 0);
                    String apkUrl = json.optString("apkUrl", "").trim();
                    String versionName = json.optString("versionName", "NXLauncher Update");
                    if (remoteCode <= BuildConfig.VERSION_CODE || apkUrl.isEmpty()) return;
                    new Handler(Looper.getMainLooper()).post(() -> showDialog(activity, versionName, apkUrl));
                }
            } catch (Throwable ignored) {
                // Network/update errors must never crash NXLauncher.
            } finally {
                if (c != null) c.disconnect();
            }
        }, "NXLauncher-UpdateCheck").start();
    }

    private static void showDialog(Activity activity, String versionName, String apkUrl) {
        if (activity.isFinishing() || activity.isDestroyed()) return;
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(42, 34, 42, 30);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(24, 24, 31));
        bg.setCornerRadius(38);
        root.setBackground(bg);

        TextView title = text(activity, "NXLauncher  •  ԹԱՐՄԱՑՈՒՄ", 22, Color.WHITE);
        TextView message = text(activity, "Հասանելի է նոր տարբերակ՝ " + versionName + ".\n\nԹարմացրու հիմա՝ նոր հնարավորություններն ու սխալների ուղղումները ստանալու համար։", 15, Color.LTGRAY);
        message.setPadding(0, 14, 0, 22);
        root.addView(title);
        root.addView(message);

        LinearLayout actions = new LinearLayout(activity);
        actions.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        Button close = button(activity, "✕");
        Button update = button(activity, "ԹԱՐՄԱՑՆԵԼ");
        actions.addView(close, new LinearLayout.LayoutParams(70, 58));
        LinearLayout.LayoutParams updateParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 58);
        updateParams.leftMargin = 12;
        actions.addView(update, updateParams);
        root.addView(actions);

        AlertDialog dialog = new AlertDialog.Builder(activity).setView(root).create();
        dialog.setCanceledOnTouchOutside(true);
        close.setOnClickListener(v -> dialog.dismiss());
        update.setOnClickListener(v -> downloadAndInstall(activity, dialog, apkUrl));
        dialog.show();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private static void downloadAndInstall(Activity activity, AlertDialog dialog, String apkUrl) {
        LinearLayout box = new LinearLayout(activity);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(42, 34, 42, 34);
        TextView status = text(activity, "Ներբեռնվում է…", 19, Color.WHITE);
        ProgressBar progress = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        box.addView(status);
        box.addView(progress, new LinearLayout.LayoutParams(-1, 12));
        dialog.setView(box);

        new Thread(() -> {
            File apk = new File(activity.getCacheDir(), UPDATE_APK_NAME);
            HttpURLConnection c = null;
            try {
                if (apk.exists() && !apk.delete()) throw new IllegalStateException("Cannot replace old update");
                c = (HttpURLConnection) new URL(apkUrl).openConnection();
                c.setConnectTimeout(10000);
                c.setReadTimeout(30000);
                c.setInstanceFollowRedirects(true);
                c.connect();
                if (c.getResponseCode() < 200 || c.getResponseCode() >= 300) throw new IllegalStateException("HTTP " + c.getResponseCode());
                int total = c.getContentLength();
                try (InputStream in = c.getInputStream(); FileOutputStream out = new FileOutputStream(apk)) {
                    byte[] buffer = new byte[65536];
                    int n, done = 0;
                    while ((n = in.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                        done += n;
                        if (total > 0) {
                            int value = (int) ((done * 100L) / total);
                            new Handler(Looper.getMainLooper()).post(() -> progress.setProgress(value));
                        }
                    }
                }
                if (!apk.isFile() || apk.length() < 1024) throw new IllegalStateException("Invalid APK");
                new Handler(Looper.getMainLooper()).post(() -> install(activity, dialog, apk));
            } catch (Throwable e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    status.setText("Չհաջողվեց ներբեռնել թարմացումը։\nՓորձիր կրկին։");
                    progress.setVisibility(View.GONE);
                });
            } finally {
                if (c != null) c.disconnect();
            }
        }, "NXLauncher-UpdateDownload").start();
    }

    private static void install(Activity activity, AlertDialog dialog, File apk) {
        try {
            Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".nxlauncher.updater", apk);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            dialog.dismiss();
        } catch (Throwable e) {
            TextView error = text(activity, "Չհաջողվեց բացել տեղադրման պատուհանը։", 16, Color.WHITE);
            dialog.setView(error);
        }
    }

    private static TextView text(Context c, String value, int size, int color) {
        TextView v = new TextView(c);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(color);
        return v;
    }

    private static Button button(Context c, String value) {
        Button b = new Button(c);
        b.setText(value);
        b.setTextSize(13);
        b.setAllCaps(false);
        return b;
    }

    private static byte[] readAll(InputStream in) throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
        return out.toByteArray();
    }
}
