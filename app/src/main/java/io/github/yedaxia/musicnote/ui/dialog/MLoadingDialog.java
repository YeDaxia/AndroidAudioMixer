package io.github.yedaxia.musicnote.ui.dialog;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/3/1.
 */

public class MLoadingDialog extends MaterialDialog {

    public MLoadingDialog(MaterialDialog.Builder builder) {
        super(builder);
    }

    public static class Builder extends MDialogBuilder<Builder>{

        public Builder(Context context) {
            super(context);
        }

        public Builder progress(boolean indeterminate, int max) {
            builder.progress(indeterminate, max);
            return this;
        }

        public Builder progressIndeterminateStyle(boolean horizontal) {
            builder.progressIndeterminateStyle(horizontal);
            return this;
        }

        public Builder canceledOnTouchOutside(boolean cancel) {
            builder.canceledOnTouchOutside(cancel);
            return this;
        }

        public MLoadingDialog build(){
            return new MLoadingDialog(builder);
        }
    }

}
