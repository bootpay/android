package kr.co.bootpay.android.analytics;

import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import kr.co.bootpay.android.constants.BootpayBuildConfig;
import kr.co.bootpay.android.models.statistics.BootStatCall;
import kr.co.bootpay.android.models.statistics.BootStatItem;
import kr.co.bootpay.android.models.statistics.BootStatLogin;
import kr.co.bootpay.android.pref.UserInfo;
import kr.co.bootpay.android.secure.BootpaySimpleAES256;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsPresenter {
    AnalyticsService service;

    public AnalyticsPresenter(AnalyticsService service) {
        this.service = service;
    }

    public void userTrace(String id,
                          String email,
                          String username,
                          int gender,
                          String birth,
                          String phone,
                          String area) {
        BootStatLogin login = new BootStatLogin(
                BootpayBuildConfig.VERSION,
                UserInfo.getInstance(this.service.getContext()).getBootpayApplicationId(),
                id,
                email,
                username,
                gender,
                birth,
                phone,
                area
        );

        String json = new Gson().toJson(login);
        BootpaySimpleAES256 aes = new BootpaySimpleAES256();

        Call<LoginResult> dataCall = service.getApi().login(
                aes.strEncode(json),
                aes.getSessionKey()
        );
        dataCall.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if(response != null && response.body() != null) {
                    UserInfo.getInstance(service.getContext()).setBootpayUserId(response.body().getData().getUserId());
//                    Log.d("bootpay", response.body().getData().toString());
                    Log.d("bootpay", new Gson().toJson(response.body().getData()));
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.d("bootpay", t.getMessage());
            }
        });


//        service.getApi()
//                .login(
//                        aes.strEncode(json),
//                        aes.getSessionKey()
//                )
//                .retry(3)
//                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
//                .subscribe(
//                        new Observer<LoginResult>() {
//                            @Override
//                            public void onComplete() {
//
//                                Log.d("BootpayAnalytics", "user trace onComplete");
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                e.printStackTrace();
//                            }
//
//                            @Override
//                            public void onSubscribe(Disposable d) {}
//
//                            @Override
//                            public void onNext(LoginResult res) {
//                                if(res.getData() != null) UserInfo.getInstance(service.getContext()).setBootpayUserId(res.getData().getUserId());
//                                Log.d("BootpayAnalytics", "user trace onNext " + new Gson().toJson(res));
//                            }
//                        });

    }

    public void pageTrace(String url,
                          String page_type,
                          List<BootStatItem> items) {

        Log.d("BootpayAnalytics", "onComplete");

        BootStatCall call = new BootStatCall(
                BootpayBuildConfig.VERSION,
                UserInfo.getInstance(this.service.getContext()).getBootpayApplicationId(),
                UserInfo.getInstance(this.service.getContext()).getBootpayUuid(),
                url,
                page_type,
                items,
                UserInfo.getInstance(this.service.getContext()).getBootpaySk(),
                UserInfo.getInstance(this.service.getContext()).getBootpayUserId(),
                "");


        String json = new Gson().toJson(call);
        BootpaySimpleAES256 aes = new BootpaySimpleAES256();


        Call<LoginResult> dataCall = service.getApi().call(
                aes.strEncode(json),
                aes.getSessionKey()
        );
        dataCall.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if(response != null && response.body() != null) {
//                    UserInfo.getInstance(service.getContext()).setBootpayUserId(response.body().getData().getUserId());
                    Log.d("bootpay", new Gson().toJson(response.body().getData()));
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.d("bootpay", t.getMessage());
            }
        });

//        service.getApi()
//                .call(
//                        aes.strEncode(json),
//                        aes.getSessionKey()
//                )
//                .retry(3)
//                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
//                .subscribe(
//                        new Observer<LoginResult>() {
//                            @Override
//                            public void onComplete() {
//                                Log.d("BootpayAnalytics", "page trace onComplete");
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                e.printStackTrace();
//                            }
//
//                            @Override
//                            public void onSubscribe(Disposable d) {}
//
//                            @Override
//                            public void onNext(LoginResult res) {
//                                Log.d("BootpayAnalytics", "page trace onNext " + new Gson().toJson(res));
//                            }
//                        });


    }

}
