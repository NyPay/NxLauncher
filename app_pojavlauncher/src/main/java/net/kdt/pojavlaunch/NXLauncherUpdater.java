package net.kdt.pojavlaunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
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

/** Lightweight in-app updater. It never blocks launcher startup and only shows when a newer APK exists. */
public final class NXLauncherUpdater {
    private static final String MANIFEST_URL = "https://raw.githubusercontent.com/NyPay/NxLauncher/nxlauncher-v1.0.0/update.json";
    private static final int CURRENT_VERSION_CODE = 10000;
    private static final String PREFS = "nxlauncher_updater";
    private static final String PREF_SKIPPED_VERSION = "skipped_version";

    private NXLauncherUpdater() {}

    public static void check(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        new Thread(() -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(MANIFEST_URL).openConnection();
                c.setConnectTimeout(5000);
                c.setReadTimeout(7000);
                c.setRequestProperty("Cache-Control", "no-cache");
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) return;
                InputStream in = c.getInputStream();
                byte[] data = readAll(in);
                in.close();
                c.disconnect();
                JSONObject json = new JSONObject(new String(data, "UTF-8"));
                int version = json.getInt("versionCode");
                String url = json.getString("apkUrl");
                String name = json.optString("versionName", "NXLauncher Update");
                if (version <= CURRENT_VERSION_CODE || url.trim().isEmpty()) return;
                int skipped = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(PREF_SKIPPED_VERSION, -1);
                if (skipped == version) return;
                new Handler(Looper.getMainLooper()).post(() -> showDialog(activity, version, name, url));
            } catch (Exception ignored) {
                // Update checking must never crash or interrupt the launcher.
            }
        }, "NXLauncher-UpdateCheck").start();
    }

    private static void showDialog(Activity activity, int version, String versionName, String apkUrl) {
        if (activity.isFinishing() || activity.isDestroyed()) return;

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(42, 32, 42, 28);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(24, 24, 31));
        bg.setCornerRadius(38);
        root.setBackground(bg);

        TextView title = text(activity, "NXLauncher  •  ԹԱՐՄԱՑՈՒՄ", 22, Color.WHITE);
        TextView message = text(activity, "Հասանելի է NXLauncher-ի նոր տարբերակ՝ " + versionName + "։\n\nԹարմացրու հիմա՝ նոր հնարավորություններն ու ուղղումները ստանալու համար։", 15, Color.LTGRAY);
        message.setPadding(0, 14, 0, 22);
        root.addView(title);
        root.addView(message);

        LinearLayout actions = new LinearLayout(activity);
        actions.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button close = button(activity, "✕");
        Button update = button(activity, "ԹԱՐՄԱՑՆԵԼ");
        actions.addView(close, new LinearLayout.LayoutParams(70, 58));
        LinearLayout.LayoutParams upLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 58);
        upLp.leftMargin = 12;
        actions.addView(update, upLp);
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
        TextView t = text(activity, "Ներբեռնվում է…", 19, Color.WHITE);
        ProgressBar p = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        p.setMax(100);
        box.addView(t);
        box.addView(p, new LinearLayout.LayoutParams(-1, 12));
        dialog.setView(box);
        new Thread(() -> {
            File apk = new File(activity.getCacheDir(), "NXLauncher-update.apk");
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(apkUrl).openConnection();
                c.setConnectTimeout(10000);
                c.setReadTimeout(20000);
                c.connect();
                int total = c.getContentLength();
                InputStream in = c.getInputStream();
                FileOutputStream out = new FileOutputStream(apk);
                byte[] b = new byte[65536];
                int n, done = 0;
                while ((n = in.read(b)) != -1) {
                    out.write(b, 0, n);
                    done += n;
                    if (total > 0) {
                        int progress = (int) ((done * 100L) / total);
                        new Handler(Looper.getMainLooper()).post(() -> p.setProgress(progress));
                    }
                }
                out.close();
                in.close();
                c.disconnect();
                new Handler(Looper.getMainLooper()).post(() -> install(activity, dialog, apk));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    t.setText("Չհաջողվեց ներբեռնել թարմացումը։\nԿարող ես նորից փորձել։");
                    p.setVisibility(View.GONE);
                });
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
        } catch (Exception e) {
            TextView error = text(activity, "Չհաջողվեց բացել տեղադրման պատուհանը։", 16, Color.WHITE);
            dialog.setView(error);
        }
    }

    private static TextView text(Context c, String s, int size, int color) {
        TextView v = new TextView(c);
        v.setText(s);
        v.setTextSize(size);
        v.setTextColor(color);
        return v;
    }

    private static Button button(Context c, String s) {
        Button b = new Button(c);
        b.setText(s);
        b.setTextSize(13);
        b.setAllCaps(false);
        return b;
    }

    private static byte[] readAll(InputStream in) throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] b = new byte[8192];
        int n;
        while ((n = in.read(b)) != -1) out.write(b, 0, n);
        return out.toByteArray();
    }
}
