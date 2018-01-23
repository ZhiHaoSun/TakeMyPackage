package org.team7.TakeMyPackage.model;

/**
 * Created by sunzhihao on 20/1/18.
 */

public class DeliveryPackage {
    private String packageId;
    private String packageName;
    private String date;
    private String time;
    private String remark;
    private String requester;
    private String assigner;
    private String deliveryPhone;
    private String requesterAddress;
    private String requesterPhone;
    private boolean taken = false;

    public DeliveryPackage(String packageName, String date, String time, String remark, String requester,
                   String deliveryPhone, String requesterPhone, String requesterAddress) {
        this.packageName = packageName;
        this.date = date;
        this.time = time;
        this.remark = remark;
        this.requester = requester;
        this.deliveryPhone = deliveryPhone;
        this.requesterAddress = requesterAddress;
        this.requesterPhone = requesterPhone;
    }

    public DeliveryPackage() {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getAssigner() {
        return assigner;
    }

    public void setAssigner(String assigner) {
        this.assigner = assigner;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getRequesterAddress() {
        return requesterAddress;
    }

    public void setRequesterAddress(String requesterAddress) {
        this.requesterAddress = requesterAddress;
    }

    public String getRequesterPhone() {
        return requesterPhone;
    }

    public void setRequesterPhone(String requesterPhone) {
        this.requesterPhone = requesterPhone;
    }
}
