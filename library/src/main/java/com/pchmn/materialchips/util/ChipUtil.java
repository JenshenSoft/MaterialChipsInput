package com.pchmn.materialchips.util;

import android.view.View;
import android.widget.ImageView;

import com.pchmn.materialchips.model.ChipInterface;

import static android.view.View.GONE;

public class ChipUtil {

    public static void addAvatar(ImageView mAvatar,
                                 ChipInterface chip,
                                 LetterTileProvider tileProvider,
                                 boolean chipHasAvatarIcon) {
        if (chipHasAvatarIcon && chip.getAvatarUri() != null) {
            mAvatar.setVisibility(View.VISIBLE);
            mAvatar.setImageURI(chip.getAvatarUri());
        } else if (chipHasAvatarIcon && chip.getAvatarDrawable() != null) {
            mAvatar.setVisibility(View.VISIBLE);
            mAvatar.setImageDrawable(chip.getAvatarDrawable());
        } else if (chipHasAvatarIcon) {
            mAvatar.setVisibility(View.VISIBLE);
            mAvatar.setImageBitmap(tileProvider.getLetterTile(chip.getLabel()));
        } else {
            mAvatar.setVisibility(GONE);
        }
    }
}
