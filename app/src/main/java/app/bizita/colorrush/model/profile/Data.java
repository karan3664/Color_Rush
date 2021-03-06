package app.bizita.colorrush.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("user_balance")
    @Expose
    private String userBalance;
    @SerializedName("month_spent")
    @Expose
    private String monthSpent;
    @SerializedName("month_buyed")
    @Expose
    private String monthBuyed;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(String userBalance) {
        this.userBalance = userBalance;
    }

    public String getMonthSpent() {
        return monthSpent;
    }

    public void setMonthSpent(String monthSpent) {
        this.monthSpent = monthSpent;
    }

    public String getMonthBuyed() {
        return monthBuyed;
    }

    public void setMonthBuyed(String monthBuyed) {
        this.monthBuyed = monthBuyed;
    }

}