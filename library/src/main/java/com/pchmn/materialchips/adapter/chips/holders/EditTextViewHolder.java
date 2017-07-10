package com.pchmn.materialchips.adapter.chips.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

public class EditTextViewHolder extends RecyclerView.ViewHolder {

    final EditText editText;

    public EditTextViewHolder(View view) {
        super(view);
        editText = (EditText) view;
    }
}