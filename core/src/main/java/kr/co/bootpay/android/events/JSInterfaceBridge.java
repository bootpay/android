package kr.co.bootpay.android.events;

import android.webkit.JavascriptInterface;

import kr.co.bootpay.android.models.widget.WidgetData;

public interface JSInterfaceBridge {
    @JavascriptInterface
    void error(String data);

    @JavascriptInterface
    void close(String data);

    @JavascriptInterface
    void cancel(String data);

    @JavascriptInterface
    void issued(String data);

    @JavascriptInterface
    String confirm(String data);

    @JavascriptInterface
    void done(String data);

    @JavascriptInterface
    void redirectEvent(String data);

    @JavascriptInterface
    void readyWatch();

    @JavascriptInterface
    void resizeWatch(double height);

    @JavascriptInterface
    void changeMethodWatch(WidgetData data);

    @JavascriptInterface
    void changeTermsWatch(WidgetData data);
}
