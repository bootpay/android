package kr.co.bootpay.android.events;

import android.webkit.JavascriptInterface;

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
}
