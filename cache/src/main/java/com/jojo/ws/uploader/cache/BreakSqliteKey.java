package com.jojo.ws.uploader.cache;

public interface BreakSqliteKey {
    String ID = "id";
    String UPLOADBATCH = "uploadBatch";
    String INDEX = "block_index";
    String UPLOADTOKEN = "uploadToken";
    String TYPE = "type";
    String FILEPATH = "filePath";
    String CREATED = "created";
    String PARTUPLOADURL = "partUploadUrl";
    String DIRECTUPLOADURL = "directUploadUrl";
    String LOCALFILE = "localFile";
    String CURRENTBLOCK = "currentBlock";


    String TASKID = "taskId";
    String START = "start";
    String SIZE = "size";
    String SLICESIZE = "slicesize";
    String UPLOAD_SUCCESS = "upload_success";
    String CTX = "ctx";
    String CHECKSUM = "checksum";
    String CRC32 = "crc32";
    String OFFSET = "off_set";
}
