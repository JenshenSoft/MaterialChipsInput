package com.pchmn.materialchips.model;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MergedChip implements ChipInterface {

    public static final String ID_MERGED_CHIP = "merged_chip";
    private Object id;
    private List<Uri> avatarUri;
    private List<Drawable> avatarDrawable;
    private String label;
    private String info;
    private boolean selected;
    private List<ChipInterface> chips;

    public MergedChip(Context context, List<ChipInterface> filteredList) {
        this.id = ID_MERGED_CHIP;
        this.label = context.getString(R.string.add_all);
        String info = "";
        for (ChipInterface chipInterface : filteredList) {
            if (chipInterface.getLabel() != null) {
                info = info.concat(chipInterface.getLabel());
            }
        }
        this.info = info;
        this.chips = new CopyOnWriteArrayList<>(filteredList);
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        throw new NoSuchMethodError();
    }

    @Override
    public Drawable getAvatarDrawable() {
        throw new NoSuchMethodError();
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<ChipInterface> getChips() {
        return chips;
    }
}
