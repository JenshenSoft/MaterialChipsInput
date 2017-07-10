package com.pchmn.materialchips.adapter.chips;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;

import com.pchmn.materialchips.ChipView;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.R;
import com.pchmn.materialchips.adapter.EditTextViewHolder;
import com.pchmn.materialchips.adapter.chips.holders.CollapseItemsViewHolder;
import com.pchmn.materialchips.adapter.chips.holders.ItemViewHolder;
import com.pchmn.materialchips.adapter.chips.items.ChipItem;
import com.pchmn.materialchips.adapter.chips.items.CollapseButtonItem;
import com.pchmn.materialchips.adapter.chips.items.EditChipItem;
import com.pchmn.materialchips.adapter.chips.items.Item;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.util.ViewUtil;
import com.pchmn.materialchips.views.ChipsInputEditText;
import com.pchmn.materialchips.views.DetailedChipView;
import com.pchmn.materialchips.views.FilterableListView;

import java.util.ArrayList;
import java.util.List;

import static com.pchmn.materialchips.adapter.chips.items.Item.TYPE_ITEM;


public class ChipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ChipsAdapter.class.toString();

    private Context mContext;
    private ChipsInput mChipsInput;
    private List<Item> mChipList = new ArrayList<>();
    private String mHintLabel;
    private ChipsInputEditText mEditText;
    private RecyclerView mRecycler;
    private EditChipItem editChipItem;
    @Nullable
    private CollapseButtonItem collapseButtonItem;

    public ChipsAdapter(Context context, ChipsInput chipsInput, RecyclerView recycler, boolean enableCollapsedButton) {
        this.mContext = context;
        this.mChipsInput = chipsInput;
        this.mRecycler = recycler;
        if (enableCollapsedButton) {
            collapseButtonItem = new CollapseButtonItem();
        }
        this.mHintLabel = mChipsInput.getHint();
        this.mEditText = mChipsInput.getEditText();
        setHasStableIds(true);
        initEditText();
    }

    @Override
    public long getItemId(int position) {
        Item item = mChipList.get(position);
        switch (item.getType()) {
            case TYPE_ITEM:
                return ((ChipItem) item).getChip().getId().hashCode();
            default:
                return item.getType();
        }
    }

    @Override
    public int getItemCount() {
        return mChipList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mChipList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Item.TYPE_EDIT_TEXT:
                return new EditTextViewHolder(mEditText);
            case Item.TYPE_HIDE_BUTTON_ITEM:
                return new CollapseItemsViewHolder(mContext, LayoutInflater.from(mContext).inflate(R.layout.collapse_chip_view, parent, false));
            default:
                return new ItemViewHolder(mChipsInput.getChipView());
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Item item = mChipList.get(position);
        switch (mChipList.get(position).getType()) {
            case Item.TYPE_EDIT_TEXT:
                bindEditText();
                break;
            case Item.TYPE_HIDE_BUTTON_ITEM:
                bindCollapseButton((CollapseButtonItem) item, (CollapseItemsViewHolder) holder);
                break;
            default:
                bindChip(((ChipItem) item).getChip(), (ItemViewHolder) holder);
                break;
        }
    }

    /* private methods */

    public void setFilterableListView(FilterableListView filterableListView) {
        if (mEditText != null) {
            mEditText.setFilterableListView(filterableListView);
        }
    }

    public List<ChipInterface> getSelectedChipList() {
        List<ChipInterface> chips = getShowedChipList();
        if (collapseButtonItem != null && collapseButtonItem.isCollapsed() && collapseButtonItem.getItems() != null) {
            chips.addAll(collapseButtonItem.getItems());
        }
        return chips;
    }

    public List<ChipInterface> getShowedChipList() {
        List<ChipInterface> chips = new ArrayList<>();
        for (Item chipItem : mChipList) {
            if (chipItem.getType() == TYPE_ITEM) {
                chips.add(((ChipItem) chipItem).getChip());
            }
        }
        return chips;
    }

    public void addChip(ChipInterface chip) {
        if (!listContains(getShowedChipList(), chip)) {
            mChipList.add(new ChipItem(chip));
            // notify listener
            mChipsInput.onChipAdded(chip, mChipList.size());
            // hide hint
            mEditText.setHint(null);
            // reset text
            mEditText.setText(null);
            // refresh data
            mChipList.remove(editChipItem);
            if (collapseButtonItem != null) {
                mChipList.remove(collapseButtonItem);
                if (collapseButtonItem.isCollapsed() && getSelectedChipList().size() > 1) {
                    mChipList.add(collapseButtonItem);
                }
            }
            mChipList.add(editChipItem);
            notifyDataSetChanged();
        }
    }

    public void addChips(List<ChipInterface> chips) {
        boolean added = false;
        for (ChipInterface chip : chips) {
            List<ChipInterface> chipList = getSelectedChipList();
            if (!listContains(chipList, chip)) {
                added = true;
                mChipList.add(new ChipItem(chip));
                // notify listener
                mChipsInput.onChipAdded(chip, chipList.size());
            }
        }
        if (!added) {
            return;
        }
        // hide hint
        mEditText.setHint(null);
        // reset text
        mEditText.setText(null);
        // refresh data
        mChipList.remove(editChipItem);
        if (collapseButtonItem != null && getSelectedChipList().size() > 1) {
            mChipList.remove(collapseButtonItem);
            mChipList.add(collapseButtonItem);
        }
        mChipList.add(editChipItem);
        notifyDataSetChanged();
    }

    public void removeChip(ChipInterface chip) {
        removeChipById(chip.getId());
    }

    public void removeChipById(Object id) {
        ChipItem chipItem = getChipItem(id);
        int position = mChipList.indexOf(chipItem);
        mChipList.remove(position);
        // if 0 chip
        if (mChipList.isEmpty()) {
            mEditText.setHint(mHintLabel);
        }
        //remove chips from collapsed item
        if (collapseButtonItem != null) {
            if (chipItem != null) {
                removeChipFromCollapsedItem(chipItem.getChip(), collapseButtonItem);
            }
            List<ChipInterface> chipList = getSelectedChipList();
            if (collapseButtonItem.isCollapsed()
                    && collapseButtonItem.getItems() != null
                    && collapseButtonItem.getItems().size() >= 1) {
                int positionCollapseItem = mChipList.indexOf(collapseButtonItem);
                if (positionCollapseItem == 0) {
                    ChipInterface firstChip = collapseButtonItem.getItems().iterator().next();
                    removeChipFromCollapsedItem(firstChip, collapseButtonItem);
                    addChip(firstChip);
                }
            } else if (chipList.size() <= 1) {
                mChipList.remove(collapseButtonItem);
            }
        }
        // refresh data
        notifyDataSetChanged();
    }

    private void removeChipFromCollapsedItem(ChipInterface chip, CollapseButtonItem collapseButtonItem) {
        List<ChipInterface> chipInterfaces = collapseButtonItem.getItems();
        if (chipInterfaces != null) {
            ChipInterface chipToRemoveInterface = null;
            for (ChipInterface chipInterface : chipInterfaces) {
                if (chip.getId().equals(chipInterface.getId())) {
                    chipToRemoveInterface = chipInterface;
                    break;
                }
            }
            if (chipToRemoveInterface != null) {
                chipInterfaces.remove(chipToRemoveInterface);
            }
        }
    }

    /* private methods */

    private ChipItem getChipItem(Object id) {
        for (Item item : mChipList) {
            if (item.getType() == TYPE_ITEM) {
                ChipItem chipItem = (ChipItem) item;
                if (chipItem.getChip().getId().equals(id)) {
                    return chipItem;
                }
            }
        }
        return null;
    }

    private void setDetailedChipViewPosition(DetailedChipView detailedChipView, int[] coord) {
        // window width
        ViewGroup rootView = (ViewGroup) mRecycler.getRootView();
        int windowWidth = ViewUtil.getWindowWidth(mContext);

        // chip size
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewUtil.dpToPx(300),
                ViewUtil.dpToPx(100));

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        // align left window
        if (coord[0] <= 0) {
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(13);
            detailedChipView.alignLeft();
        }
        // align right
        else if (coord[0] + ViewUtil.dpToPx(300) > windowWidth + ViewUtil.dpToPx(13)) {
            layoutParams.leftMargin = windowWidth - ViewUtil.dpToPx(300);
            layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(13);
            detailedChipView.alignRight();
        }
        // same position as chip
        else {
            layoutParams.leftMargin = coord[0] - ViewUtil.dpToPx(13);
            layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(13);
        }

        // show view
        rootView.addView(detailedChipView, layoutParams);
        detailedChipView.fadeIn();
    }

    private boolean listContains(List<ChipInterface> contactList, ChipInterface chip) {
        if (mChipsInput.getChipValidator() != null) {
            for (ChipInterface item : contactList) {
                if (mChipsInput.getChipValidator().areEquals(item, chip))
                    return true;
            }
        } else {
            for (ChipInterface item : contactList) {
                if (chip.getId() != null && chip.getId().equals(item.getId()))
                    return true;
                if (chip.getLabel().equals(item.getLabel()))
                    return true;
            }
        }

        return false;
    }

    private void initEditText() {
        mEditText.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mEditText.setHint(mHintLabel);
        mEditText.setBackgroundResource(android.R.color.transparent);
        // prevent fullscreen on landscape
        mEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEditText.setPrivateImeOptions("nm");
        // no suggestion
        mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // handle back space
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // backspace
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    // remove last chip
                    List<ChipInterface> chipList = getSelectedChipList();
                    if (!chipList.isEmpty() && mEditText.getText().toString().length() == 0) {
                        removeChip(chipList.get(chipList.size() - 1));
                    }
                }
                return false;
            }
        });

        // text changed
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChipsInput.onTextChanged(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (editChipItem == null) {
            editChipItem = new EditChipItem();
            mChipList.add(editChipItem);
        }
    }

    private void autofitEditText() {
        // min width of edit text = 50 dp
        ViewGroup.LayoutParams params = mEditText.getLayoutParams();
        params.width = ViewUtil.dpToPx(50);
        mEditText.setLayoutParams(params);

        // listen to change in the tree
        mEditText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // get right of recycler and left of edit text
                int right = mRecycler.getRight();
                int left = mEditText.getLeft();

                // edit text will fill the space
                ViewGroup.LayoutParams params = mEditText.getLayoutParams();
                params.width = right - left - ViewUtil.dpToPx(8);
                mEditText.setLayoutParams(params);

                // request focus
                mEditText.requestFocus();

                // remove the listener:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mEditText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mEditText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void handleClickOnEditText(final ChipInterface chipInterface, ChipView chipView) {
        // delete chip
        chipView.setOnDeleteClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeChip(chipInterface);
            }
        });

        // show detailed chip
        if (mChipsInput.isShowChipDetailed()) {
            chipView.setOnChipClicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get chip position
                    int[] coord = new int[2];
                    v.getLocationInWindow(coord);

                    final DetailedChipView detailedChipView = mChipsInput.getDetailedChipView(chipInterface);
                    setDetailedChipViewPosition(detailedChipView, coord);

                    // delete button
                    detailedChipView.setOnDeleteClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeChip(chipInterface);
                            detailedChipView.fadeOut();
                        }
                    });
                }
            });
        }
    }

    private void bindEditText() {
        if (mChipList.isEmpty()) {
            mEditText.setHint(mHintLabel);
        }
        // auto fit edit text
        autofitEditText();
    }

    private void bindCollapseButton(final CollapseButtonItem collapseButtonItem, CollapseItemsViewHolder holder) {
        holder.bind(collapseButtonItem, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseButtonItem.setCollapsed(!collapseButtonItem.isCollapsed());
                if (collapseButtonItem.isCollapsed()) {
                    List<ChipInterface> chipItems = new ArrayList<>();
                    List<ChipInterface> chipList = getSelectedChipList();
                    for (int i = 1; i < chipList.size(); i++) {
                        ChipInterface chipInterface = chipList.get(i);
                        ChipItem chipItem = getChipItem(chipInterface.getId());
                        if (chipItem != null) {
                            mChipList.remove(chipItem);
                            chipItems.add(chipItem.getChip());
                        }
                    }
                    if (!chipItems.isEmpty()) {
                        collapseButtonItem.setItems(chipItems);
                    }
                } else {
                    addChips(collapseButtonItem.getItems());
                    collapseButtonItem.setItems(null);
                }
                notifyDataSetChanged();
            }
        });
    }

    private void bindChip(ChipInterface chipInterface, ItemViewHolder itemViewHolder) {
        itemViewHolder.chipView.inflate(chipInterface);
        // handle click
        handleClickOnEditText(chipInterface, itemViewHolder.chipView);
    }
}
