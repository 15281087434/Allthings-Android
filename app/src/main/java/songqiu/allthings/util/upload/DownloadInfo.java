package songqiu.allthings.util.upload;

/**
 * Created by Administrator on 2017/6/14.
 */

public class DownloadInfo {
    private int threadId;
    private long blockSize;

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    private long startPos;
    private long endPos;
    private long downBlock;

    private String downloadUrl;
    private String saveFilePath;

    public String getSaveFilePath() {
        return saveFilePath;
    }

    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getDownBlock() {
        return downBlock;
    }

    public void setDownBlock(long downBlock) {
        this.downBlock = downBlock;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }
}
