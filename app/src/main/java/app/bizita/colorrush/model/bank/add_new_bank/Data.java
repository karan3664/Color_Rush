
package app.bizita.colorrush.model.bank.add_new_bank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("new_bank")
    @Expose
    private NewBank newBank;

    public NewBank getNewBank() {
        return newBank;
    }

    public void setNewBank(NewBank newBank) {
        this.newBank = newBank;
    }

}
