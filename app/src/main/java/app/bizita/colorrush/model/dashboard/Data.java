package app.bizita.colorrush.model.dashboard;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class Data {
    @SerializedName("present_game_start")
    @Expose
    private String presentGameStart;
    @SerializedName("present_game_end")
    @Expose
    private String presentGameEnd;
    @SerializedName("present")
    @Expose
    private String present;
    @SerializedName("current_balance")
    @Expose
    private String currentBalance;
    @SerializedName("let_user_bid")
    @Expose
    private Integer letUserBid;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("returning_user")
    @Expose
    private Integer returningUser;
    @SerializedName("game_config")
    @Expose
    private ArrayList<GameConfig> gameConfig = null;
    @SerializedName("future_games")
    @Expose
    private ArrayList<FutureGame> futureGames = null;
    @SerializedName("past_game")
    @Expose
    private PastGame pastGame;
    @SerializedName("user_games")
    @Expose
    private ArrayList<UserGame> userGames = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<GameConfig> getGameConfig() {
        return gameConfig;
    }

    public void setGameConfig(ArrayList<GameConfig> gameConfig) {
        this.gameConfig = gameConfig;
    }

    public ArrayList<FutureGame> getFutureGames() {
        return futureGames;
    }

    public void setFutureGames(ArrayList<FutureGame> futureGames) {
        this.futureGames = futureGames;
    }

    public PastGame getPastGame() {
        return pastGame;
    }

    public void setPastGame(PastGame pastGame) {
        this.pastGame = pastGame;
    }

    public ArrayList<UserGame> getUserGames() {
        return userGames;
    }

    public void setUserGames(ArrayList<UserGame> userGames) {
        this.userGames = userGames;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getLetUserBid() {
        return letUserBid;
    }

    public void setLetUserBid(Integer letUserBid) {
        this.letUserBid = letUserBid;
    }

    public Integer getReturningUser() {
        return returningUser;
    }

    public void setReturningUser(Integer returningUser) {
        this.returningUser = returningUser;
    }

    public String getPresentGameStart() {
        return presentGameStart;
    }

    public void setPresentGameStart(String presentGameStart) {
        this.presentGameStart = presentGameStart;
    }

    public String getPresentGameEnd() {
        return presentGameEnd;
    }

    public void setPresentGameEnd(String presentGameEnd) {
        this.presentGameEnd = presentGameEnd;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }
}