package com.pchmn.materialchips.adapter.filter;


import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.R;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.model.MergedChip;
import com.pchmn.materialchips.util.LetterTileProvider;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class FilterableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = FilterableAdapter.class.toString();
    private static final int TYPE_MERGED_CHIP = 0;
    private static final int TYPE_CHIP = 1;
    // context
    private Context mContext;
    // list
    private List<ChipInterface> mOriginalList = new ArrayList<>();
    private List<ChipInterface> mChipList = new ArrayList<>();
    private List<ChipInterface> mFilteredList = new ArrayList<>();
    private List<ChipInterface> mSelectableItems = new ArrayList<>();
    private ChipFilter mFilter;
    private ChipsInput mChipsInput;
    private LetterTileProvider mLetterTileProvider;
    private ColorStateList mBackgroundColor;
    private ColorStateList mTextColor;
    // recycler
    private RecyclerView mRecyclerView;
    private final boolean mSelectable;
    // sort
    private Comparator<ChipInterface> mComparator;
    private Collator mCollator;


    public FilterableAdapter(Context context,
                             RecyclerView recyclerView,
                             List<? extends ChipInterface> chipList,
                             ChipsInput chipsInput,
                             ColorStateList backgroundColor,
                             ColorStateList textColor,
                             boolean selectable) {
        mContext = context;
        mRecyclerView = recyclerView;
        mSelectable = selectable;
        mCollator = Collator.getInstance(Locale.getDefault());
        mCollator.setStrength(Collator.PRIMARY);
        mComparator = new Comparator<ChipInterface>() {
            @Override
            public int compare(ChipInterface o1, ChipInterface o2) {
                return mCollator.compare(o1.getLabel(), o2.getLabel());
            }
        };
        // remove chips that do not have label
        Iterator<? extends ChipInterface> iterator = chipList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getLabel() == null)
                iterator.remove();
        }
        sortList(chipList);
        mOriginalList.addAll(chipList);
        mChipList.addAll(chipList);
        mFilteredList.addAll(chipList);
        mLetterTileProvider = new LetterTileProvider(mContext);
        mBackgroundColor = backgroundColor;
        mTextColor = textColor;
        mChipsInput = chipsInput;

        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                removeChip(chip);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                addChip(chip);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                mRecyclerView.scrollToPosition(0);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        final ChipInterface chip = getItem(position);
        if (chip.getId() == MergedChip.ID_MERGED_CHIP) {
            return TYPE_MERGED_CHIP;
        }
        return TYPE_CHIP;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MERGED_CHIP:
                return new MergedChipItemViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.item_list_filterable_merged, parent, false),
                        mLetterTileProvider,
                        mBackgroundColor,
                        mTextColor,
                        mChipsInput.chipHasAvatarIcon());
            default:
                return new ChipItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_filterable, parent, false),
                        mLetterTileProvider,
                        mBackgroundColor,
                        mTextColor,
                        mChipsInput.chipHasAvatarIcon(),
                        mSelectable);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ChipInterface chip = getItem(position);
        if (chip.getId() == MergedChip.ID_MERGED_CHIP) {
            final MergedChipItemViewHolder itemViewHolder = (MergedChipItemViewHolder) holder;
            final MergedChip mergedChip = (MergedChip) chip;
            itemViewHolder.bindInfo(mergedChip, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChipsInput.addChips(mergedChip.getChips());
                }
            });
        } else {
            ChipItemViewHolder itemViewHolder = (ChipItemViewHolder) holder;
            itemViewHolder.bindInfo(chip, mSelectableItems, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectable) {
                        mSelectableItems.add(chip);
                        notifyItemChanged(position);
                    } else {
                        if (mChipsInput != null)
                            mChipsInput.addChip(chip);
                    }
                }
            });
        }
    }

    public void addMergedChipItem() {
        boolean hasMergedChip = false;
        for (ChipInterface chipInterface : mFilteredList) {
            if (chipInterface.getId() == MergedChip.ID_MERGED_CHIP) {
                hasMergedChip = true;
                break;
            }
        }
        if (!hasMergedChip) {
            mFilteredList.add(0, new MergedChip(mContext, mFilteredList));
            notifyItemInserted(0);
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    private ChipInterface getItem(int position) {
        return mFilteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null)
            mFilter = new ChipFilter(this, mChipList);
        return mFilter;
    }

    private class ChipFilter extends Filter {

        private FilterableAdapter adapter;
        private List<ChipInterface> originalList;
        private List<ChipInterface> filteredList;

        public ChipFilter(FilterableAdapter adapter, List<ChipInterface> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(new ArrayList<>(originalList));
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (ChipInterface chip : originalList) {
                    if (chip.getLabel().toLowerCase().contains(filterPattern)) {
                        filteredList.add(chip);
                    } else if (chip.getInfo() != null && chip.getInfo().toLowerCase().replaceAll("\\s", "").contains(filterPattern)) {
                        filteredList.add(chip);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredList.clear();
            List<ChipInterface> values = new ArrayList<>((ArrayList<ChipInterface>) results.values);
            mFilteredList.addAll(values);
            notifyDataSetChanged();
        }
    }

    private void removeChip(ChipInterface chip) {
        int position = mFilteredList.indexOf(chip);
        if (position >= 0)
            mFilteredList.remove(position);

        position = mChipList.indexOf(chip);
        if (position >= 0)
            mChipList.remove(position);

        notifyDataSetChanged();
    }

    private void addChip(ChipInterface chip) {
        if (contains(chip)) {
            mChipList.add(chip);
            mFilteredList.add(chip);
            // sort original list
            sortList(mChipList);
            // sort filtered list
            sortList(mFilteredList);

            notifyDataSetChanged();
        }
    }

    private boolean contains(ChipInterface chip) {
        for (ChipInterface item : mOriginalList) {
            if (item.equals(chip))
                return true;
        }
        return false;
    }

    private void sortList(List<? extends ChipInterface> list) {
        Collections.sort(list, mComparator);
    }
}
