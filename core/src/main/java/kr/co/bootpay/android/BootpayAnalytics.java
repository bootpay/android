package kr.co.bootpay.android;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import kr.co.bootpay.android.analytics.AnalyticsPresenter;
import kr.co.bootpay.android.analytics.AnalyticsService;
import kr.co.bootpay.android.models.statistics.BootStatItem;
import kr.co.bootpay.android.pref.UserInfo;


public class BootpayAnalytics {
    private static AnalyticsService restService;
    private static AnalyticsPresenter presenter;

    public static void init(Context context, String applicationID) {
        if (applicationID.isEmpty()) throw new RuntimeException("Application ID is empty.");
        if (presenter == null) {
            restService = new AnalyticsService(context);
            presenter = new AnalyticsPresenter(restService);
        }

        UserInfo.getInstance(context).setBootpayApplicationId(applicationID);
    }

    public static void userTrace(
            String id
    ) {
        userTrace(id, null);
    }

    public static void userTrace(
            String id,
            String email
    ) {
        userTrace(id, email, null);
    }

    public static void userTrace(
            String id,
            String email,
            String userName
    ) {
        userTrace(id, email, userName, -1);
    }

    public static void userTrace(
            String id,
            String email,
            String userName,
            int gender
    ) {
        userTrace(id, email, userName, gender, null);
    }

    public static void userTrace(
            String id,
            String email,
            String userName,
            int gender,
            String birth
    )  {
        userTrace(id, email, userName, gender, birth, null);
    }


    public static void userTrace(
            String id,
            String email,
            String userName,
            int gender,
            String birth,
            String phone
    ) {
        userTrace(id, email, userName, gender, birth, phone, null);
    }

    public static void userTrace(
            String id,
            String email,
            String userName,
            int gender,
            String birth,
            String phone,
            String area) {

        if (presenter == null) throw new IllegalStateException("Analytics is not initialized.");
        else presenter.userTrace(id, email, userName, gender, birth, phone, area);
    }


    public static void pageTrace(String url, String page_type) {
        pageTrace(url, page_type, new ArrayList<BootStatItem>());
    }

    public static void pageTrace(String url, String page_type, List<BootStatItem> items) {
        if (presenter == null) throw new IllegalStateException("Analytics is not initialized.");
        else presenter.pageTrace(url, page_type, items);
    }
}

