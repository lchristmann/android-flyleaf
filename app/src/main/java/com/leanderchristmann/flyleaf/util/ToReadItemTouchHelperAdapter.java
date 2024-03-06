package com.leanderchristmann.flyleaf.util;

public interface ToReadItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemSwiped(int position);
}
