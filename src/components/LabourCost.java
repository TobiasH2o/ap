package components;

import java.math.BigDecimal;

public class LabourCost {
    public String labourType;
    public BigDecimal costPerHou;

    public LabourCost(String labourType, double costPerHour) {
        this.labourType = labourType;
        this.costPerHou = BigDecimal.valueOf(costPerHour);
    }
}
