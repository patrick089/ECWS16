package ecws16;


import java.util.ArrayList;

public class Memory {

    private int size;
    private ArrayList<Page> pages;

    public Memory(int size) {
        this.size = size;
        int number = getNumberofPages();
        pages = new ArrayList<>();
        for(int i = 0; i < number; i++){
            pages.add(new Page(5));
        }
    }

    public Memory(Memory memory){
        this.size = memory.getSize();
        pages = new ArrayList<>();
        for(int i = 0; i < memory.getPages().size(); i++){
            pages.add(new Page(memory.getPages().get(i)));
        }

    }

    public int getNumberofPages() {
        return Math.round(size/5);
    }

    public int freeCapacity(){
        int freeCapacity = 0;

        for(Page page : pages){
            freeCapacity += page.isDirty() ? 0 : page.getSize();
        }

        return freeCapacity;
    }

    public void makePageDirty(Request request){

        for (Page page : pages){
            if (page.isDirty() == false){
                page.setDirty(true);
                page.setRequest(request);
                break;
            }
        }

    }
    public int countDirtyPages(){
        int count = 0;

        for(Page page : pages){
            if(page.isDirty() == true){
                count++;
            }
        }
        return count;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Memory memory = (Memory) o;

        if (size != memory.size) return false;
        return pages != null ? pages.equals(memory.pages) : memory.pages == null;

    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + (pages != null ? pages.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Memory{" +
                "size=" + size +
                ", pages=" + pages +
                '}';
    }
}
