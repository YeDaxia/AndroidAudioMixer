package io.github.yedaxia.musicnote.ui.dialog;

import android.content.Context;
import androidx.annotation.StringRes;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/27.
 */

public class MAlertDialog extends MaterialDialog {

    public MAlertDialog(MaterialDialog.Builder builder) {
        super(builder);
    }

    public static class Builder extends MDialogBuilder<Builder> {

        public Builder(Context context) {
            super(context);
        }

        public Builder message(@StringRes int resId) {
            builder.content(resId);
            return this;
        }

        public Builder message(String message) {
            builder.content(message);
            return this;
        }

        public MAlertDialog build() {
            return new MAlertDialog(builder);
        }
    }
}
