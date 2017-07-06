package com.pchmn.materialchips.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

public class EditTextViewHolder extends RecyclerView.ViewHolder {

    final EditText editText;

    EditTextViewHolder(View view) {
        super(view);
        editText = (EditText) view;
    }
}