package components;

import java.math.BigDecimal;

public class CefFilter {

    public String productID;
    public BigDecimal cost;

    public CefFilter(String productID, double cost) {
        this.productID = productID;
        this.cost = BigDecimal.valueOf(cost);
    }

}
