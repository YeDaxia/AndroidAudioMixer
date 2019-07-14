package io.github.yedaxia.musicnote.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/2/27
 */

public interface IRViewHolder<M> extends IViewHolder<M>{
    View getItemView(LayoutInflater inflater, ViewGroup parentView);
}
