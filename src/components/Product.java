package components;

import java.math.BigDecimal;

public class Product {

    private final String productID;
    private final String description;
    private final double makeTime;
    private final BigDecimal salesCost;
    private final String productType;
    private final Boolean skilled;

    public Product(String productID, String description, double salesCost, double makeTime,
            String productType, Boolean skilled) {
        this.productID = productID;
        this.description = description;
        this.makeTime = makeTime;
        this.salesCost = BigDecimal.valueOf(salesCost);
        this.productType = productType;
        this.skilled = skilled;
    }

    public String getProductID() {
        return productID;
    }

    public String getDescription() {
        return description;
    }

    public double getMakeTime() {
        return makeTime;
    }

    public BigDecimal getSalesCost() {
        return salesCost;
    }

    public String getProductType() {
        return productType;
    }

    public Boolean getSkilled() {
        return skilled;
    }


}
