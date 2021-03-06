
package app.bizita.colorrush.model.bank.update_single_bank_details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("old_bank")
    @Expose
    private OldBank oldBank;

    public OldBank getOldBank() {
        return oldBank;
    }

    public void setOldBank(OldBank oldBank) {
        this.oldBank = oldBank;
    }

}
