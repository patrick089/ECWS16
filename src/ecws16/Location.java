package ecws16;

public class Location {

    private double x;
    private double y;

    public Location(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double distanceTo(Location location){
        return Math.abs(this.getX() - location.getX()) + Math.abs(this.getY() - location.getY());
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
