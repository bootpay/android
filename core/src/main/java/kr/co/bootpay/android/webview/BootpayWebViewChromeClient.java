package kr.co.bootpay.android.webview;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import kr.co.bootpay.android.constants.BootpayBuildConfig;

public class BootpayWebViewChromeClient extends WebChromeClient {
    protected Context mContext;

    public BootpayWebViewChromeClient(Context context) {
        this.mContext = context;
    }

    WebView mainView;
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {


        mainView = view;
        BootpayWebView newWindow = new BootpayWebView(view.getContext());
        BootpayWebView webview = (BootpayWebView) view;
        newWindow.setEventListener(webview.getEventListener());
        newWindow.setInjectedJS(webview.getInjectedJS());
        newWindow.setInjectedJSBeforePayStart(webview.getInjectedJSBeforePayStart());

        try {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.scrollTo(0, 0);
                }
            }, 50);
        }catch (Exception ignored){}

        view.addView(newWindow,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.NO_GRAVITY)
        );

        final WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWindow);
        resultMsg.sendToTarget();

        return true;
    }



    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
        if(mainView != null) {
            mainView.removeView(window);
        }
//        main
        window.setVisibility(View.GONE);
//        mainView.re
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if(BootpayBuildConfig.DEBUG) {
            return super.onConsoleMessage(consoleMessage);
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPermissionRequest(final PermissionRequest request) {
        String[] requestedResources = request.getResources();
        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> grantedPermissions = new ArrayList<String>();
        for (int i = 0; i < requestedResources.length; i++) {
            if (requestedResources[i].equals(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            } else if (requestedResources[i].equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            // TODO: RESOURCE_MIDI_SYSEX, RESOURCE_PROTECTED_MEDIA_ID.
        }

        for (int i = 0; i < permissions.size(); i++) {
            if (ContextCompat.checkSelfPermission(mContext, permissions.get(i)) != PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            if (permissions.get(i).equals(Manifest.permission.RECORD_AUDIO)) {
                grantedPermissions.add(PermissionRequest.RESOURCE_AUDIO_CAPTURE);
            } else if (permissions.get(i).equals(Manifest.permission.CAMERA)) {
                grantedPermissions.add(PermissionRequest.RESOURCE_VIDEO_CAPTURE);
            }
        }

        if (grantedPermissions.isEmpty()) {
            request.deny();
        } else {
            String[] grantedPermissionsArray = new String[grantedPermissions.size()];
            grantedPermissionsArray = grantedPermissions.toArray(grantedPermissionsArray);
            request.grant(grantedPermissionsArray);
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
    }
}
