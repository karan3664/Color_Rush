package app.bizita.colorrush.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("email_verified_at")
    @Expose
    private Object emailVerifiedAt;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("phone")
    @Expose
    private Object phone;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("bank_name")
    @Expose
    private Object bankName;
    @SerializedName("bank_account_number")
    @Expose
    private Object bankAccountNumber;
    @SerializedName("bank_branch_name")
    @Expose
    private Object bankBranchName;
    @SerializedName("bank_ifsc_code")
    @Expose
    private Object bankIfscCode;
    @SerializedName("aadhar_number")
    @Expose
    private Object aadharNumber;
    @SerializedName("pan_number")
    @Expose
    private Object panNumber;
    @SerializedName("aadhar_file")
    @Expose
    private Object aadharFile;
    @SerializedName("pan_file")
    @Expose
    private Object panFile;
    @SerializedName("profile_file")
    @Expose
    private Object profileFile;
    @SerializedName("google_id")
    @Expose
    private Object googleId;
    @SerializedName("facebook_id")
    @Expose
    private Object facebookId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Object emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Object getBankName() {
        return bankName;
    }

    public void setBankName(Object bankName) {
        this.bankName = bankName;
    }

    public Object getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(Object bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public Object getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(Object bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public Object getBankIfscCode() {
        return bankIfscCode;
    }

    public void setBankIfscCode(Object bankIfscCode) {
        this.bankIfscCode = bankIfscCode;
    }

    public Object getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(Object aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public Object getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(Object panNumber) {
        this.panNumber = panNumber;
    }

    public Object getAadharFile() {
        return aadharFile;
    }

    public void setAadharFile(Object aadharFile) {
        this.aadharFile = aadharFile;
    }

    public Object getPanFile() {
        return panFile;
    }

    public void setPanFile(Object panFile) {
        this.panFile = panFile;
    }

    public Object getProfileFile() {
        return profileFile;
    }

    public void setProfileFile(Object profileFile) {
        this.profileFile = profileFile;
    }

    public Object getGoogleId() {
        return googleId;
    }

    public void setGoogleId(Object googleId) {
        this.googleId = googleId;
    }

    public Object getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Object facebookId) {
        this.facebookId = facebookId;
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

}