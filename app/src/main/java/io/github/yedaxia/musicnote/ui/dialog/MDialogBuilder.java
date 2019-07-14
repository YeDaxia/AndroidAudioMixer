package io.github.yedaxia.musicnote.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/23.
 */

public abstract class MDialogBuilder<B extends MDialogBuilder> {

    protected MaterialDialog.Builder builder;

    public MDialogBuilder(Context context){
        builder = new MaterialDialog.Builder(context);
    }

    /**设置标题*/
    public B title(String title){
        builder.title(title);
        return (B) this;
    }

    /**设置标题*/
    public B title(@StringRes int id){
        builder.title(id);
        return (B)this;
    }

    /**设置Positive Button的文字*/
    public B positiveText(String text){
        builder.positiveText(text);
        return (B) this;
    }

    /**设置Positive Button的文字*/
    public B positiveText(@StringRes int id){
        builder.positiveText(id);
        return (B)this;
    }

    /**设置Negative Button的文字*/
    public B negativeText(String text){
        builder.negativeText(text);
        return (B) this;
    }

    /**设置Negative Button的文字*/
    public B negativeText(@StringRes int id){
        builder.negativeText(id);
        return (B) this;
    }

    public B canceledOnTouchOutside(boolean cancel){
        builder.canceledOnTouchOutside(cancel);
        return (B) this;
    }

    /**Positive Button点击监听*/
    public B positiveClickListener(final OnButtonClickListener l){
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                l.onClick(dialog);
            }
        });
        return (B) this;
    }

    /**设置Negative Button点击监听*/
    public B negativeClickListener(final OnButtonClickListener l){
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if(l!=null){
                    l.onClick(dialog);
                }
            }
        });
        return (B) this;
    }

    public interface OnButtonClickListener{
        void onClick(Dialog dialog);
    }
}
