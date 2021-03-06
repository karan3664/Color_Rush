
package app.bizita.colorrush.model.bank.all_bank_list;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("bank_list")
    @Expose
    private ArrayList<BankList> bankList = null;

    public ArrayList<BankList> getBankList() {
        return bankList;
    }

    public void setBankList(ArrayList<BankList> bankList) {
        this.bankList = bankList;
    }

}
