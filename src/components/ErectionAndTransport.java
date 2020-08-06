package components;

import java.math.BigDecimal;

public class ErectionAndTransport {

    public String productID;
    public BigDecimal cost;

    public ErectionAndTransport(String productID, double cost) {
        this.productID = productID;
        this.cost = BigDecimal.valueOf(cost);
    }

}
