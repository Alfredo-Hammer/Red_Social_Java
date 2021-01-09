package com.hammer67.ajecimface.models;

import java.util.ArrayList;

public class FCMResponse {

    private long multicast_id;
    private int success;
    private int failure;
    private int cononical_ids;
    ArrayList<Object> result = new ArrayList<Object>();

    public FCMResponse(long multicast_id, int success, int failure, int cononical_ids, ArrayList<Object> result) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.cononical_ids = cononical_ids;
        this.result = result;
    }

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCononical_ids() {
        return cononical_ids;
    }

    public void setCononical_ids(int cononical_ids) {
        this.cononical_ids = cononical_ids;
    }

    public ArrayList<Object> getResult() {
        return result;
    }

    public void setResult(ArrayList<Object> result) {
        this.result = result;
    }
}
