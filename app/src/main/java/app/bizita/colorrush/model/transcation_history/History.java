
package app.bizita.colorrush.model.transcation_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("game_sessions_id")
    @Expose
    private Integer gameSessionsId;
    @SerializedName("transaction_id")
    @Expose
    private Object transactionId;
    @SerializedName("payment_mode")
    @Expose
    private Object paymentMode;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("game_id")
    @Expose
    private Integer gameId;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("winloss")
    @Expose
    private String winloss;
    @SerializedName("bidpoint")
    @Expose
    private String bidpoint;
    @SerializedName("datetime")
    @Expose
    private String datetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGameSessionsId() {
        return gameSessionsId;
    }

    public void setGameSessionsId(Integer gameSessionsId) {
        this.gameSessionsId = gameSessionsId;
    }

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
    }

    public Object getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(Object paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWinloss() {
        return winloss;
    }

    public void setWinloss(String winloss) {
        this.winloss = winloss;
    }

    public String getBidpoint() {
        return bidpoint;
    }

    public void setBidpoint(String bidpoint) {
        this.bidpoint = bidpoint;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

}
