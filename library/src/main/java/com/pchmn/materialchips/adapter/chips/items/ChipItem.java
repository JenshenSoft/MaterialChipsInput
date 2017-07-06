package com.pchmn.materialchips.adapter.chips.items;

import com.pchmn.materialchips.model.ChipInterface;

public class ChipItem implements Item {

    private ChipInterface chipInterface;

    public ChipItem(ChipInterface chipInterface) {
        this.chipInterface = chipInterface;
    }

    public ChipInterface getChip() {
        return chipInterface;
    }

    @Override
    public int getType() {
        return TYPE_ITEM;
    }
}
