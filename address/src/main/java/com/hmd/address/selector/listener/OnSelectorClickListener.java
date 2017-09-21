package com.hmd.address.selector.listener;

public interface OnSelectorClickListener {
    void onListItemClick(int position, int tabItem);
    void onTabItemClick(int item);
    void onCloseClick();
}
