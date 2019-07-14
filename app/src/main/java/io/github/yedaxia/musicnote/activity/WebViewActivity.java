package io.github.yedaxia.musicnote.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;
import io.github.yedaxia.musicnote.R;
import io.github.yedaxia.musicnote.app.util.AppUtils;
import io.github.yedaxia.musicnote.app.util.BundleKeys;
import io.github.yedaxia.musicnote.app.util.DeviceUtils;
import io.github.yedaxia.musicnote.data.api.Urls;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/1.
 */

public class WebViewActivity extends BaseActivity {

    private static final int REQ_ACTION_VIEW = 0x11;

    @BindView(R.id.web_view)
    WebView mWebView;

    public static void launch(Context context, String title, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(BundleKeys.WEB_URL, url);
        intent.putExtra(BundleKeys.TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        setTitle(getIntent().getStringExtra(BundleKeys.TITLE));
        enableBack();
        String url = getIntent().getStringExtra(BundleKeys.WEB_URL);
        initWebViewSettings();
        mWebView.loadUrl(url);
    }

    private void initWebViewSettings() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }

                // Otherwise allow the OS to handle things like tel, mailto, etc.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivityForResult(intent, REQ_ACTION_VIEW);
                return true;
            }
        });
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ACTION_VIEW) {
            mWebView.loadUrl(Urls.getAppOrderStatusUrl(DeviceUtils.getDeviceId()));
        }
    }
}
