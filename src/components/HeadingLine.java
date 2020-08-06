package components;

public class HeadingLine {
    public int headingLineID;
    public int headingID;
    public String productID;
    public String comment;
    public int quantity;

    public HeadingLine(int headinLineID, int headingID, String productID, String comment,
                       int quantity) {
        this.headingID = headingID;
        this.headingLineID = headinLineID;
        this.productID = productID;
        this.comment = comment;
        this.quantity = quantity;
    }
}

