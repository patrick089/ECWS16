package ecws16;

public class Request {

    private Location location;
    private int size;
    private long timestamp;
    private int duration;
    private int userId;
    private int edgeId;
    private int pmId;
    private int vmId;
    //TODO: add size of the request or something?

    public Request(int x, int y, long timestamp) {
        location = new Location(x,y);
        size = 5;
        this.timestamp = timestamp;
        duration = 10;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(int edgeId) {
        this.edgeId = edgeId;
    }

    public int getPmId() {
        return pmId;
    }

    public void setPmId(int pmId) {
        this.pmId = pmId;
    }

    public int getVmId() {
        return vmId;
    }

    public void setVmId(int vmId) {
        this.vmId = vmId;
    }

    @Override
    public String toString() {
        return "Request{" +
                "location=" + location +
                ", size=" + size +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                ", userId=" + userId +
                ", edgeId=" + edgeId +
                ", pmId=" + pmId +
                ", vmId=" + vmId +
                '}';
    }
}
