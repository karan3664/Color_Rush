
package app.bizita.colorrush.model.withdrawal.cancel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("canelled_withdrawal")
    @Expose
    private CanelledWithdrawal canelledWithdrawal;
    @SerializedName("current_balance")
    @Expose
    private String currentBalance;

    public CanelledWithdrawal getCanelledWithdrawal() {
        return canelledWithdrawal;
    }

    public void setCanelledWithdrawal(CanelledWithdrawal canelledWithdrawal) {
        this.canelledWithdrawal = canelledWithdrawal;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

}
