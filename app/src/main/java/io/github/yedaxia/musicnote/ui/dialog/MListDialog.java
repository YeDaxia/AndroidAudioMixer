package io.github.yedaxia.musicnote.ui.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.ArrayRes;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Collection;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/2/27.
 */

public class MListDialog extends MaterialDialog {

    MListDialog(MaterialDialog.Builder builder) {
        super(builder);
    }

    public static class Builder extends MDialogBuilder<Builder>{

        public Builder(Context context) {
            super(context);
        }


        public Builder items(@ArrayRes int itemsRes){
            builder.items(itemsRes);
            return this;
        }

        public Builder items(Collection items){
            builder.items(items);
            return this;
        }

        public Builder onItemClick(final OnItemClickListener l){
            builder.itemsCallback(new ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                    l.onItemClick((MListDialog)dialog,position, text);
                }
            });
            return this;
        }

        public MListDialog build(){
            return new MListDialog(builder);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(MListDialog dialog, int position, CharSequence text );
    }
}
