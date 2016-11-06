package ecws16;

public class ID {

    static int number = 0;
    int id;

    public ID(){
        number++;
        id = number;
    }

    public ID(ID id){
        this.id = id.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ID{" +
                "id=" + id +
                '}';
    }
}
