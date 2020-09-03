public class Entry {

    private String title = "";
    private String[] ID = new String[0];
    private String[] desc = new String[0];
    private int[] quant = new int[0];
    private Double[][] costs = new Double[0][];

    public Entry() {}

    public Double[][] getCosts(){
        return costs;
    }

    public int getSize() {
        int size = quant.length;
        if(desc.length > size)
            size = desc.length;
        return Math.max(ID.length, size);
    }

    public String[] getID() {
        return ID;
    }

    public void setID(String[] ID) {
        this.ID = ID;
    }

    public String[] getDesc() {
        return desc;
    }

    public void setDesc(String[] desc) {
        this.desc = desc;
    }

    public int[] getQuant() {
        return quant;
    }

    public void setQuant(int[] quant) {
        this.quant = quant;
    }

    public void setCosts(Double[][] costs){
        this.costs = costs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getQp(int i) {
        for (Double[] d:
             costs) {
            if(d[0] == (double) i){
                return d[1];
            }
        }
        return -1;
    }
}
