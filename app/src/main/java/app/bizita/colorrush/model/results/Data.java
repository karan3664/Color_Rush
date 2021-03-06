
package app.bizita.colorrush.model.results;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("Bids_array")
    @Expose
    private ArrayList<BidsArray> bidsArray = null;
    @SerializedName("current_balance")
    @Expose
    private String currentBalance;
    @SerializedName("win_amount")
    @Expose
    private Integer winAmount;
    @SerializedName("loss_amount")
    @Expose
    private Integer lossAmount;

    public ArrayList<BidsArray> getBidsArray() {
        return bidsArray;
    }

    public void setBidsArray(ArrayList<BidsArray> bidsArray) {
        this.bidsArray = bidsArray;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(Integer winAmount) {
        this.winAmount = winAmount;
    }

    public Integer getLossAmount() {
        return lossAmount;
    }

    public void setLossAmount(Integer lossAmount) {
        this.lossAmount = lossAmount;
    }

}
