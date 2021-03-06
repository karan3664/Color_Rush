
package app.bizita.colorrush.model.withdrawal.list;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("current_balance")
    @Expose
    private String currentBalance;
    @SerializedName("current_withdrawal_array")
    @Expose
    private ArrayList<CurrentWithdrawalArray> currentWithdrawalArray = null;

    public ArrayList<CurrentWithdrawalArray> getCurrentWithdrawalArray() {
        return currentWithdrawalArray;
    }

    public void setCurrentWithdrawalArray(ArrayList<CurrentWithdrawalArray> currentWithdrawalArray) {
        this.currentWithdrawalArray = currentWithdrawalArray;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }
}
