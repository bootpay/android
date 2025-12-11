package kr.co.bootpay.android.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserInfo {
    private static volatile UserInfo instance;
    private static WeakReference<Context> contextRef;
    private SharedPreferences encryptedPreferences;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private UserInfo() { }

    public void setContext(Context context) {
        contextRef = new WeakReference<>(context.getApplicationContext());
    }

    public static UserInfo getInstance(Context context) {
        if (instance == null) {
            synchronized (UserInfo.class) {
                if (instance == null) {
                    instance = new UserInfo();
                    Context appContext = context.getApplicationContext();
                    contextRef = new WeakReference<>(appContext);
                    try {
                        MasterKey masterKey = new MasterKey.Builder(appContext)
                                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                .build();

                        instance.encryptedPreferences = EncryptedSharedPreferences.create(
                                appContext,
                                "bootpay_secure_prefs",
                                masterKey,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
                    } catch (GeneralSecurityException | IOException e) {
                        instance.encryptedPreferences = appContext.getSharedPreferences("bootpay_prefs", Context.MODE_PRIVATE);
                    }
                }
            }
        }
        return instance;
    }

    public String getBootpayUuid() {
        String value = encryptedPreferences.getString("bootpay_uuid", "");
        if (value == null || value.isEmpty()) {
            value = UUID.randomUUID().toString();
            setBootpayUuid(value);
        }
        return value;
    }

    public void setBootpayUuid(String bootpay_uuid) {
        encryptedPreferences.edit()
                .putString("bootpay_uuid", bootpay_uuid)
                .apply();
    }

    public Long getBootpayLastTime() {
        return encryptedPreferences.getLong("bootpay_last_time", System.currentTimeMillis());
    }

    public void setBootpayLastTime(Long bootpay_last_time) {
        encryptedPreferences.edit()
                .putLong("bootpay_last_time", bootpay_last_time)
                .apply();
    }

    public String getBootpaySk() {
        return encryptedPreferences.getString("bootpay_sk", "");
    }

    public void setBootpaySk(String bootpay_sk) {
        encryptedPreferences.edit()
                .putString("bootpay_sk", bootpay_sk)
                .apply();
    }

    public void newSk(Long time) {
        encryptedPreferences.edit()
                .putString("bootpay_sk", String.format(Locale.KOREA, "%s_%d", getBootpayUuid(), time))
                .apply();
    }

    public String getBootpayApplicationId() {
        return encryptedPreferences.getString("bootpay_application_id", "");
    }

    public void setBootpayApplicationId(String bootpay_application_id) {
        encryptedPreferences.edit()
                .putString("bootpay_application_id", bootpay_application_id)
                .apply();
    }

    public String getBootpayUserId() {
        return encryptedPreferences.getString("bootpay_user_id", "");
    }

    public void setBootpayUserId(String bootpay_user_id) {
        encryptedPreferences.edit()
                .putString("bootpay_user_id", bootpay_user_id)
                .apply();
    }

    public String getBootpay_receipt_id() {
        return encryptedPreferences.getString("bootpay_receipt_id", "");
    }

    public void setBootpay_receipt_id(String bootpay_receipt_id) {
        encryptedPreferences.edit()
                .putString("bootpay_receipt_id", bootpay_receipt_id)
                .apply();
    }

    public String getDeveloperPayload() {
        return encryptedPreferences.getString("developerPayload", "");
    }

    public void setDeveloperPayload(String developerPayload) {
        encryptedPreferences.edit()
                .putString("developerPayload", developerPayload)
                .apply();
    }

    public Boolean getEnableOneStore() {
        return encryptedPreferences.getBoolean("enable_one_store", false);
    }

    public void setEnableOneStore(Boolean enable_onstore) {
        encryptedPreferences.edit()
                .putBoolean("enable_onstore", enable_onstore)
                .apply();
    }

    private static String getSimOperator(Context context) {
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            TelephonyManager telephonyManager =
                    (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null && telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                return telephonyManager.getSimOperator();
            }
        }
        return "UNKNOWN_SIM_OPERATOR";
    }

    public void setSimOperator(String sim_operator) {
        encryptedPreferences.edit()
                .putString("sim_operator", sim_operator)
                .apply();
    }

    @SuppressWarnings("deprecation")
    private static String getInstallerPackageName(Context context) {
        if (context == null) return "UNKNOWN_INSTALLER";

        Context applicationContext = context.getApplicationContext();
        PackageManager pm = applicationContext.getPackageManager();
        String packageName = applicationContext.getPackageName();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                InstallSourceInfo sourceInfo = pm.getInstallSourceInfo(packageName);
                String installerPackageName = sourceInfo.getInstallingPackageName();
                if (!TextUtils.isEmpty(installerPackageName)) {
                    return installerPackageName;
                }
            } else {
                String installerPackageName = pm.getInstallerPackageName(packageName);
                if (!TextUtils.isEmpty(installerPackageName)) {
                    return installerPackageName;
                }
            }
        } catch (Exception e) {
            // PackageManager.NameNotFoundException or other exceptions
        }
        return "UNKNOWN_INSTALLER";
    }

    public void setInstallPackageMarket(String install_package_market) {
        encryptedPreferences.edit()
                .putString("install_package_market", install_package_market)
                .apply();
    }

    public String getAdId() {
        return encryptedPreferences.getString("ad_id", "");
    }

    public static void UseOneStoreApi(Context context, Boolean enable) {
        if (context == null) return;

        Context appContext = context.getApplicationContext();
        contextRef = new WeakReference<>(appContext);

        if (!enable) {
            UserInfo.getInstance(appContext).setEnableOneStore(enable);
            return;
        }

        UserInfo userInfo = UserInfo.getInstance(appContext);
        userInfo.update();
        userInfo.setEnableOneStore(enable);
        userInfo.setInstallPackageMarket(getInstallerPackageName(appContext));
        userInfo.setSimOperator(getSimOperator(appContext));
        userInfo.fetchAdIdAsync();
    }

    private void fetchAdIdAsync() {
        executor.execute(() -> {
            Context context = contextRef != null ? contextRef.get() : null;
            if (context == null) return;

            String adId = "UNKNOWN_ADID";
            try {
                adId = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
            } catch (Exception e) {
                // Silently handle exception
            }

            final String finalAdId = adId;
            mainHandler.post(() -> setAdId(finalAdId));
        });
    }

    public void setAdId(String ad_id) {
        encryptedPreferences.edit()
                .putString("ad_id", ad_id)
                .apply();
    }

    public void update() {
        if (getBootpayUuid().isEmpty()) setBootpayUuid(UUID.randomUUID().toString());
        if (getBootpaySk().isEmpty()) setBootpaySk(String.format(Locale.KOREA, "%s_%d", getBootpayUuid(), getBootpayLastTime()));

        Long current = System.currentTimeMillis();
        boolean isExpired = current - getBootpayLastTime() > 30 * 60 * 1000L;
        if (isExpired) newSk(current);
        setBootpayLastTime(current);
    }

    public void finish() {
        setBootpayLastTime(System.currentTimeMillis());
    }

    public String getUUID() {
        return encryptedPreferences.getString("uuid", "");
    }

    public void setBiometricSecretKey(String value) {
        encryptedPreferences.edit().putString("biometric_secret_key", value).apply();
    }

    public void setBiometricDeviceId(String value) {
        encryptedPreferences.edit().putString("biometric_device_id", value).apply();
    }

    public String getBiometricSecretKey() {
        return encryptedPreferences.getString("biometric_secret_key", "");
    }

    public String getBiometricDeviceId() {
        return encryptedPreferences.getString("biometric_device_id", "");
    }
}
