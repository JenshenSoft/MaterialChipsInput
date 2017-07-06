package com.pchmn.materialchips.adapter.chips.items;

public interface Item {
    int TYPE_EDIT_TEXT = 0;
    int TYPE_ITEM = 1;
    int TYPE_HIDE_BUTTON_ITEM = 2;

    int getType();
}
