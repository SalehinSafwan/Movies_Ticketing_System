package com.example.desktop;

public class RevenueModel {
    private final String date;
    private final Integer revenue;
    private final Integer cancellationloss;

    public RevenueModel(String date, Integer revenue, Integer cancellationloss) {
        this.date = date;
        this.revenue = revenue;
        this.cancellationloss = cancellationloss;
    }

    public String getDate() {
        return date;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public Integer getCancellationloss() {
        return cancellationloss;
    }
}
