package app.bizita.colorrush.model.error;


import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorsModel {

    @SerializedName("errors")
    @Expose
    private ArrayList<String> errors = null;

    public ArrayList<String> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<String> errors) {
        this.errors = errors;
    }
}