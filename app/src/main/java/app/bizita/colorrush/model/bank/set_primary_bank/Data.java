
package app.bizita.colorrush.model.bank.set_primary_bank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("primary_bank")
    @Expose
    private PrimaryBank primaryBank;

    public PrimaryBank getPrimaryBank() {
        return primaryBank;
    }

    public void setPrimaryBank(PrimaryBank primaryBank) {
        this.primaryBank = primaryBank;
    }

}
