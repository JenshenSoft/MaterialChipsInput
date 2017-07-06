package com.pchmn.materialchips.adapter.holders;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pchmn.materialchips.R;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.util.ChipUtil;
import com.pchmn.materialchips.util.ColorUtil;
import com.pchmn.materialchips.util.LetterTileProvider;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class ChipItemViewHolder extends RecyclerView.ViewHolder {

    private CheckBox checkBox;
    private CircleImageView mAvatar;
    private TextView mLabel;
    private TextView mInfo;
    private final LetterTileProvider letterTileProvider;
    private final ColorStateList backgroundColor;
    private final ColorStateList textColor;
    private final boolean hasAvatarIcon;
    private final boolean selectable;

    public ChipItemViewHolder(View view,
                              LetterTileProvider letterTileProvider,
                              ColorStateList backgroundColor,
                              ColorStateList textColor,
                              boolean hasAvatarIcon,
                              boolean selectable) {
        super(view);
        checkBox = (CheckBox) view.findViewById(R.id.selected);
        mAvatar = (CircleImageView) view.findViewById(R.id.avatar);
        mLabel = (TextView) view.findViewById(R.id.label);
        mInfo = (TextView) view.findViewById(R.id.info);
        this.letterTileProvider = letterTileProvider;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.hasAvatarIcon = hasAvatarIcon;
        this.selectable = selectable;
    }

    public void bindInfo(final ChipInterface chip, List<ChipInterface> selectableItems, View.OnClickListener onClickListener) {
        //selectable
        if (selectable) {
            boolean contains = selectableItems.contains(chip);
            checkBox.setChecked(contains);
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(GONE);
        }

        // avatar
        ChipUtil.addAvatar(mAvatar, chip, letterTileProvider, hasAvatarIcon);

        // label
        mLabel.setText(chip.getLabel());

        // info
        if (chip.getInfo() != null) {
            mInfo.setVisibility(View.VISIBLE);
            mInfo.setText(chip.getInfo());
        } else {
            mInfo.setVisibility(GONE);
        }

        // colors
        if (backgroundColor != null)
            itemView.getBackground().setColorFilter(backgroundColor.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
        if (textColor != null) {
            mLabel.setTextColor(textColor);
            mInfo.setTextColor(ColorUtil.alpha(textColor.getDefaultColor(), 150));
        }

        // onclick
        itemView.setOnClickListener(onClickListener);
    }
}