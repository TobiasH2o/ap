package components;

import java.math.BigDecimal;

public class BroughtOut {

    public String productID;
    public BigDecimal cost;
    public double costPrecentage;
    public String carrageCode;
    public String commissionCode;
    public boolean stockItem;
    public BigDecimal costPrice;
    public String flexCode;
    public int size;
    public String sufix;

    public BroughtOut(String productID, double cost, double costPrecentage, String carrageCode, String commissionCode,
                      boolean stockItem, double costPrice, String flexCode, int size, String sufix) {
        this.productID = productID;
        this.cost = BigDecimal.valueOf(cost);
        this.costPrecentage = costPrecentage;
        this.carrageCode = carrageCode;
        this.commissionCode = commissionCode;
        this.stockItem = stockItem;
        this.costPrice = BigDecimal.valueOf(costPrice);
        this.flexCode = flexCode;
        this.size = size;
        this.sufix = sufix;
    }

}
