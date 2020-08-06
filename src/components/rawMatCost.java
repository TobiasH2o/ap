package components;

import java.math.BigDecimal;

public class rawMatCost {

    public String material;
    public BigDecimal pricePerTon;
    public BigDecimal pricePerTonScrap;
    public BigDecimal pricePerLB;

    public rawMatCost(String material, double pricePerTon, double pricePerTonScrap, double pricePerLB) {
        this.material = material;
        this.pricePerLB = BigDecimal.valueOf(pricePerLB);
        this.pricePerTon = BigDecimal.valueOf(pricePerTon);
        this.pricePerTonScrap = BigDecimal.valueOf(pricePerTonScrap);
    }

}
