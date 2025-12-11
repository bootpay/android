package kr.co.bootpay.android.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.UUID;

public class UserInfo {
    private static Context context;
    private static UserInfo instance;
    private SharedPreferences encryptedPreferences;

    private UserInfo() { }

    public void setContext(Context context) {
        UserInfo.context = context;
    }

    public static UserInfo getInstance(Context context) {
        if(instance == null) {
            instance = new UserInfo();
            try {
                MasterKey masterKey = new MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                instance.encryptedPreferences = EncryptedSharedPreferences.create(
                        context,
                        "bootpay_secure_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (GeneralSecurityException | IOException e) {
                // Fallback to regular SharedPreferences if encryption fails
                instance.encryptedPreferences = context.getSharedPreferences("bootpay_prefs", Context.MODE_PRIVATE);
            }
        }

        return instance;
    }

    public String getBootpayUuid() {
        String value = instance.encryptedPreferences.getString("bootpay_uuid", "");
        if(value == null || value.isEmpty()) {
            value = UUID.randomUUID().toString();
            setBootpayUuid(value);
        }
        return value;
    }

    public void setBootpayUuid(String bootpay_uuid) {
        instance.encryptedPreferences.edit()
                .putString("bootpay_uuid", bootpay_uuid)
                .apply();
    }

    public Long getBootpayLastTime() {
        return instance.encryptedPreferences.getLong("bootpay_last_time", System.currentTimeMillis());
    }

    public void setBootpayLastTime(Long bootpay_last_time) {
        instance.encryptedPreferences.edit()
                .putLong("bootpay_last_time", bootpay_last_time)
                .apply();
    }

    public String getBootpaySk() {
        return instance.encryptedPreferences.getString("bootpay_sk", "");
    }

    public void setBootpaySk(String bootpay_sk) {
        instance.encryptedPreferences.edit()
                .putString("bootpay_sk", bootpay_sk)
                .apply();
    }

    public void newSk(Long time) {
        instance.encryptedPreferences.edit()
                .putString("bootpay_sk", String.format(Locale.KOREA, "%s_%d", getBootpayUuid(), time))
                .apply();
    }

    public String getBootpayApplicationId() {
        return instance.encryptedPreferences.getString("bootpay_application_id", "");
    }

    public void setBootpayApplicationId(String bootpay_application_id) {
        instance.encryptedPreferences.edit()
                .putString("bootpay_application_id", bootpay_application_id)
                .apply();
    }

    public String getBootpayUserId() {
        return instance.encryptedPreferences.getString("bootpay_user_id", "");
    }

    public void setBootpayUserId(String bootpay_user_id) {
        instance.encryptedPreferences.edit()
                .putString("bootpay_user_id", bootpay_user_id)
                .apply();
    }

    public String getBootpay_receipt_id() {
        return instance.encryptedPreferences.getString("bootpay_receipt_id", "");
    }

    public void setBootpay_receipt_id(String bootpay_receipt_id) {
        instance.encryptedPreferences.edit()
                .putString("bootpay_receipt_id", bootpay_receipt_id)
                .apply();
    }

    public String getDeveloperPayload() {
        return instance.encryptedPreferences.getString("developerPayload", "");
    }

    public void setDeveloperPayload(String developerPayload) {
        instance.encryptedPreferences.edit()
                .putString("developerPayload", developerPayload)
                .apply();
    }

    public Boolean getEnableOneStore() {
        return instance.encryptedPreferences.getBoolean("enable_one_store", false);
    }

    public void setEnableOneStore(Boolean enable_onstore) {
        instance.encryptedPreferences.edit()
                .putBoolean("enable_onstore", enable_onstore)
                .apply();
    }

    private static String getSimOperator(Context context) {
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            TelephonyManager telephonyManager =
                    (TelephonyManager)
                            applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

            if (telephonyManager != null && telephonyManager.getSimState()
                    == TelephonyManager.SIM_STATE_READY) {
                return telephonyManager.getSimOperator();
            }
        }
        return "UNKNOWN_SIM_OPERATOR";
    }

    public void setSimOperator(String sim_operator) {
        instance.encryptedPreferences.edit()
                .putString("sim_operator", sim_operator)
                .apply();
    }

    private static String getInstallerPackageName(Context context) {
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            PackageManager pm = applicationContext.getPackageManager();
            final String installPackageName =
                    pm.getInstallerPackageName(applicationContext.getPackageName());

            if (!TextUtils.isEmpty(installPackageName)) {
                return installPackageName;
            }
        }
        return "UNKNOWN_INSTALLER";
    }

    public void setInstallPackageMarket(String install_package_market) {
        instance.encryptedPreferences.edit()
                .putString("install_package_market", install_package_market)
                .apply();
    }

    public String getAdId() {
        return instance.encryptedPreferences.getString("ad_id", "");
    }

    public static void UseOneStoreApi(Context context, Boolean enable) {
        UserInfo.context = context;

        if(enable == false) {
            UserInfo.getInstance(context).setEnableOneStore(enable);
            return;
        }

        UserInfo.getInstance(context).update();
        UserInfo.getInstance(context).setEnableOneStore(enable);
        UserInfo.getInstance(context).setInstallPackageMarket(getInstallerPackageName(context));
        UserInfo.getInstance(context).setSimOperator(getSimOperator(context));
        new GoogleAppIdTask().execute();
    }


    private static class GoogleAppIdTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(final Void... params) {
            if(context == null) return "UNKNOWN_ADID";

            try {
                return AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }  catch (Exception ex) {
                ex.printStackTrace();
            }
            return "UNKNOWN_ADID";
        }

        protected void onPostExecute(String adId) {
            if(context == null) return;
            UserInfo.getInstance(context).setAdId(adId);
        }
    }

    public void setAdId(String ad_id) {
        instance.encryptedPreferences.edit()
                .putString("ad_id", ad_id)
                .apply();
    }

    public void update() {
        if(getBootpayUuid().isEmpty()) setBootpayUuid(UUID.randomUUID().toString());
        if(getBootpaySk().isEmpty()) setBootpaySk(String.format(Locale.KOREA, "%s_%d", getBootpayUuid(), getBootpayLastTime()));

        Long current = System.currentTimeMillis();

        boolean isExipred = current - getBootpayLastTime() > 30 * 60 * 1000l;
        if(isExipred) newSk(current);
        setBootpayLastTime(current);
    }

    public void finish() {
        setBootpayLastTime(System.currentTimeMillis());
    }

    public String getUUID() {
        return instance.encryptedPreferences.getString("uuid", "");
    }

    public void setBiometricSecretKey(String value) {
        instance.encryptedPreferences.edit().putString("biometric_secret_key", value).apply();
    }

    public void setBiometricDeviceId(String value) {
        instance.encryptedPreferences.edit().putString("biometric_device_id", value).apply();
    }

    public String getBiometricSecretKey() {
        return instance.encryptedPreferences.getString("biometric_secret_key", "");
    }

    public String getBiometricDeviceId() {
        return instance.encryptedPreferences.getString("biometric_device_id", "");
    }
}
