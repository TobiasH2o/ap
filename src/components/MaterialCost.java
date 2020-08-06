package components;

import java.math.BigDecimal;

public class MaterialCost {

    public String suffix;
    public String size;
    public BigDecimal cost;

    public MaterialCost(String suffix, String size, double cost) {
        this.suffix = suffix;
        this.size = size;
        this.cost = BigDecimal.valueOf(cost);
    }

}
