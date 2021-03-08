package com.jojo.ws.uploader.cache;

import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.breakstore.BreakInfo;

public class Utils {
    private Utils() {
        //no op
    }

    public static BlockInfoRow blockToRow(Block block) {
//        return new BlockInfoRow(0, 0, block.getStart(), block.getSize(), block.getSliceSize(), block.getCtx(), block.getChecksum(), block.getCrc32(), block.getOffset());
        return null;
    }

    public static BreakInfoRow breakToRow(BreakInfo breakInfo) {
        return new BreakInfoRow(0, breakInfo.getUploadToken(), "", breakInfo.getFilePath(), breakInfo.isCreated(), breakInfo.getPartUploadUrl(), "", breakInfo.getLocalFile(), breakInfo.getCurrentBlock());
    }
}
