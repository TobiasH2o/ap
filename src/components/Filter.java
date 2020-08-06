package components;

import java.math.BigDecimal;

public class Filter {

    public String productID;
    public int duty;
    public String filterUplift;
    public String dropOutBox;
    public String coneCode;
    public String structureCode;
    public BigDecimal cost;

    public Filter(String productID, int duty, String filterUplift, String dropOutBox, String coneCode,
                  String structureCode, double cost) {
        this.productID = productID;
        this.duty = duty;
        this.filterUplift = filterUplift;
        this.dropOutBox = dropOutBox;
        this.coneCode = coneCode;
        this.structureCode = structureCode;
        this.cost = BigDecimal.valueOf(cost);
    }

}
