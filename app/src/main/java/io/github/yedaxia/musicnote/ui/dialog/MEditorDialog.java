package io.github.yedaxia.musicnote.ui.dialog;

import android.content.Context;
import android.text.Editable;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/23.
 */

public class MEditorDialog extends MaterialDialog {

    MEditorDialog(MaterialDialog.Builder builder) {
        super(builder);
    }

    public Editable getText(){
        return getInputEditText().getText();
    }

    public static class Builder extends MDialogBuilder<Builder>{

        public Builder(Context context) {
            super(context);
            inputHint("");
        }

        /**
         * 设置输入文字个数
         * @param minLen
         * @param maxLen
         * @return
         */
        public MEditorDialog.Builder range(int minLen, int maxLen){
            builder.inputRange(minLen, maxLen);
            return this;
        }

        /**
         * 设置提示文字
         * @param hintText
         * @return
         */
        public MEditorDialog.Builder inputHint(String hintText){
            builder.input(hintText, null, new InputCallback() {
                @Override
                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                }
            });
            return this;
        }

        public MEditorDialog.Builder inputText(String text){
            builder.input("", text, new InputCallback() {
                @Override
                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                }
            });
            return this;
        }

        public MEditorDialog build() {
            return new MEditorDialog(builder);
        }
    }
}