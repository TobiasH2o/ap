package components;

import java.math.BigDecimal;

public class Product {

    private final String productID;
    private final String description;
    private final BigDecimal salesCost;
    private final String productType;
    private final boolean skilled;
    private final double makeTime;


    public Product(String productID, String description, double salesCost, String productType, boolean skilled, double makeTime) {
        this.productID = productID;
        this.description = description;
        this.salesCost = BigDecimal.valueOf(salesCost);
        this.productType = productType;
        this.skilled = skilled;
        this.makeTime = makeTime;
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
