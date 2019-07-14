package io.github.yedaxia.musicnote.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.lang.ref.WeakReference;

import io.github.yedaxia.musicnote.R;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/23.
 */

public class BaseActivity extends AppCompatActivity {

    private UiHandler mUiHandler;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUiHandler = new UiHandler(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null){
            setSupportActionBar(toolbar);
            mToolbar = toolbar;
        }
    }

    protected Toolbar getToolbar(){
        return mToolbar;
    }

    /**
     * 设置可返回
     */
    protected void enableBack(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 发送UI消息
     *
     * @param what
     */
    protected final void sendEmptyUiMessage(int what) {
        mUiHandler.sendEmptyMessage(what);
    }

    /**
     * 发送延时UI消息
     *
     * @param what
     * @param delayMillis
     */
    protected final void sendEmptyUiMessageDelayed(int what, long delayMillis) {
        mUiHandler.sendEmptyMessageDelayed(what, delayMillis);
    }

    /**
     * 发送UI消息
     *
     * @param message
     */
    protected final void sendUiMessage(Message message) {
        mUiHandler.sendMessage(message);
    }

    /**
     * 发送UI消息
     *
     * @param runnable
     */
    protected final void sendUiRunnable(Runnable runnable) {
        mUiHandler.post(runnable);
    }

    protected final void sendUiRunnable(Runnable runnable, long delayMillis) {
        mUiHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * 发送延时UI消息
     *
     * @param msg
     * @param delayMillis
     * @return
     */
    protected final void sendUiMessageDelayed(Message msg, long delayMillis) {
        mUiHandler.sendMessageDelayed(msg, delayMillis);
    }

    /**
     * 移除消息
     *
     * @param what
     */
    protected final void removeUiMessage(int what) {
        mUiHandler.removeMessages(what);
    }

    /**
     * 处理消息,父类也可能有用到handleUiMessage，所以最好要调用super.handleUiMessage()
     *
     * @param msg
     */
    @CallSuper
    protected void handleUiMessage(Message msg) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleMe(Message msg) {
        handleUiMessage(msg);
    }

    private static class UiHandler extends Handler {
        private WeakReference<BaseActivity> wfFragment;

        UiHandler(BaseActivity activity) {
            wfFragment = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = wfFragment.get();
            if (activity != null && !activity.isDestroyed()) {
                activity.handleMe(msg);
            }
        }
    }
}
