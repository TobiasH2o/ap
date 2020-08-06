package components;

import java.math.BigDecimal;

public class PipeOrBend {

    public String productID;
    public double size;
    public String suffix;
    public BigDecimal cost;

    public PipeOrBend(String productID, double size, String suffix, double cost) {
        this.productID = productID;
        this.size = size;
        this.suffix = suffix;
        this.cost = BigDecimal.valueOf(cost);
    }

}
