package ecws16;


public class Page {

    private int size;
    private boolean dirty;

    public Page(int size) {
        this.size = size;
        dirty = false;
    }

    public Page(Page page){
        this.size = page.getSize();
        dirty = page.isDirty();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Page page = (Page) o;

        if (size != page.size) return false;
        return dirty == page.dirty;

    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + (dirty ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Page{" +
                "size=" + size +
                ", dirty=" + dirty +
                '}';
    }
}
