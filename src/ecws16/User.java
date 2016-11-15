package ecws16;

/**
 * Created by Julia on 03.11.16.
 */
public class User {

    private int id;
    private Location location;
    private Request request;

    public User(int id, double x, double y, long timeStep){
        this.id = id;
        this.location = new Location(x,y);
        request = new Request(x,y,timeStep);
        request.setUserId(id);
    }

    public Request getRequest(){
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
