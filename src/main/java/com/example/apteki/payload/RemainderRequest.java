package com.example.apteki.payload;

public class RemainderRequest {
    private int id;
    private String startDate;
    private String endDate;
    private String time;
    private double count;
    private Integer medicamentId;
    private String username;


    public RemainderRequest(int id, String startDate, String endDate, String time, double count, Integer medicamentId, String username) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
