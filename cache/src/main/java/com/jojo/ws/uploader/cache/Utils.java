package com.jojo.ws.uploader.cache;

import com.jojo.ws.uploader.core.breakstore.Block;
import com.jojo.ws.uploader.core.breakstore.BreakInfo;

public class Utils {
    private Utils() {
        //no op
    }

    public static BlockInfoRow blockToRow(int id, Block block) {
        return new BlockInfoRow(0, block.getIndex(), id, block.getStart(), block.getSize(), block.getSliceSize(), block.getCtx(), block.getChecksum(), block.getCrc32(), block.getOffset());
    }

    public static BreakInfoRow breakToRow(BreakInfo breakInfo) {
        return new BreakInfoRow(breakInfo.getId(), breakInfo.getUploadToken(), "", breakInfo.getFilePath(), breakInfo.isCreated(), breakInfo.getPartUploadUrl(), breakInfo.getLocalFile());
    }
}
