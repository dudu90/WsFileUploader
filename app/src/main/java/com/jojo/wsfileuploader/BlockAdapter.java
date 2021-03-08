package com.jojo.wsfileuploader;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.jojo.ws.uploader.core.breakstore.Block;

import org.jetbrains.annotations.NotNull;

public class BlockAdapter extends BaseQuickAdapter<Block, BaseViewHolder> {
    public BlockAdapter() {
        super(R.layout.item_layout_block);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, Block block) {
        if (block.isUploadSuccess()) {
            baseViewHolder.findView(R.id.uploadBlock).setBackgroundColor(getContext().getColor(android.R.color.holo_blue_dark));
        } else {
            baseViewHolder.findView(R.id.uploadBlock).setBackgroundColor(getContext().getColor(android.R.color.holo_blue_bright));
        }
    }
}
