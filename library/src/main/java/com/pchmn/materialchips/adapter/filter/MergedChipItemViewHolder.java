package com.pchmn.materialchips.adapter.filter;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pchmn.materialchips.R;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.model.MergedChip;
import com.pchmn.materialchips.util.ChipUtil;
import com.pchmn.materialchips.util.ColorUtil;
import com.pchmn.materialchips.util.LetterTileProvider;

import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class MergedChipItemViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView mAvatar0;
    private CircleImageView mAvatar1;
    private CircleImageView mAvatar2;
    private TextView mLabel;
    private TextView mInfo;
    private final LetterTileProvider letterTileProvider;
    private final ColorStateList backgroundColor;
    private final ColorStateList textColor;
    private final boolean hasAvatarIcon;

    public MergedChipItemViewHolder(View view,
                             LetterTileProvider letterTileProvider,
                             ColorStateList backgroundColor,
                             ColorStateList textColor,
                             boolean hasAvatarIcon) {
        super(view);
        mAvatar0 = (CircleImageView) view.findViewById(R.id.avatar0);
        mAvatar1 = (CircleImageView) view.findViewById(R.id.avatar1);
        mAvatar2 = (CircleImageView) view.findViewById(R.id.avatar2);
        mLabel = (TextView) view.findViewById(R.id.label);
        mInfo = (TextView) view.findViewById(R.id.info);
        this.letterTileProvider = letterTileProvider;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.hasAvatarIcon = hasAvatarIcon;
    }

    public void bindInfo(final MergedChip chip, View.OnClickListener onClickListener) {
        // avatar
        Iterator<ChipInterface> chipInterfaces = chip.getChips().subList(0, 3).iterator();
        ChipInterface nextChip = chipInterfaces.next();
        ChipUtil.addAvatar(mAvatar0, nextChip, letterTileProvider, hasAvatarIcon);
        nextChip = chipInterfaces.next();
        ChipUtil.addAvatar(mAvatar1, nextChip, letterTileProvider, hasAvatarIcon);
        nextChip = chipInterfaces.next();
        ChipUtil.addAvatar(mAvatar2, nextChip, letterTileProvider, hasAvatarIcon);

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
            mInfo.setTextColor(ColorUtil.alpha(backgroundColor.getDefaultColor(), 150));
        }

        // onclick
        itemView.setOnClickListener(onClickListener);
    }
}