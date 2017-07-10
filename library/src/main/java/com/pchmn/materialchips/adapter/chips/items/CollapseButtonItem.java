package com.pchmn.materialchips.adapter.chips.items;

import android.support.annotation.Nullable;

import com.pchmn.materialchips.model.ChipInterface;

import java.util.List;

public class CollapseButtonItem implements Item {

    private boolean collapsed;
    private List<ChipInterface> items;

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    @Override
    public int getType() {
        return TYPE_HIDE_BUTTON_ITEM;
    }

    public void setItems(List<ChipInterface> items) {
        this.items = items;
    }

    @Nullable
    public List<ChipInterface> getItems() {
        return items;
    }
}
