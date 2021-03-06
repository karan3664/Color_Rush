package app.bizita.colorrush.model.withdrawal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("current")
    @Expose
    private Integer current;
    @SerializedName("withdrawal_point")
    @Expose
    private Integer withdrawalPoint;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getWithdrawalPoint() {
        return withdrawalPoint;
    }

    public void setWithdrawalPoint(Integer withdrawalPoint) {
        this.withdrawalPoint = withdrawalPoint;
    }

}
