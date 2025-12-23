package com.example.desktop;

public class RevenueModel {
    private final String date;
    private final Double revenue;
    private final Double cancellationloss;

    public RevenueModel(String date, Double revenue, Double cancellationloss) {
        this.date = date;
        this.revenue = revenue;
        this.cancellationloss = cancellationloss;
    }

    public String getDate() {
        return date;
    }

    public Double getRevenue() {
        return revenue;
    }

    public Double getCancellationloss() {
        return cancellationloss;
    }
}
