package io.github.yedaxia.musicnote.ui.adapter;

import android.view.View;

/**
 * @author Darcy yeguozhong@yeah.net
 */
public interface IViewHolder<M> {

    /**
     * 调用findViewById绑定View对象
     * @param viewContainer
     * @param viewType
     */
    void findViews(View viewContainer, int viewType);

    /**
     * 绑定和View相关联的数据
     */
    void bindViewData(int position, M model, int viewType);
}
