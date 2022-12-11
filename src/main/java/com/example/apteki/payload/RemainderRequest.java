package com.example.apteki.payload;

public class RemainderRequest {
    private String startDate;
    private String endDate;
    private double count;
    private Integer medicamentId;
    private String username;


    public RemainderRequest(String startDate, String endDate, double count, Integer medicamentId, String username) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.count = count;
        this.medicamentId = medicamentId;
        this.username = username;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public Integer getMedicamentId() {
        return medicamentId;
    }

    public void setMedicamentId(Integer medicamentId) {
        this.medicamentId = medicamentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
