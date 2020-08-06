package components;

import java.math.BigDecimal;

public class Qproduct {

    public int qID;
    public int headingLineID;
    public BigDecimal cost;
    public String type;

    public Qproduct(int qID, int headingLineID, double cost, String type) {
        this.qID = qID;
        this.headingLineID = headingLineID;
        this.cost = BigDecimal.valueOf(cost);
        this.type = type;
    }


}
