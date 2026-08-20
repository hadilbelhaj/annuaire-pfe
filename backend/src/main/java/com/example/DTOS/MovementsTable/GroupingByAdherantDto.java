package com.example.DTOS.MovementsTable;

public class GroupingByAdherantDto {
    private String adherantName;
    private Long adherantDeductible;
    private String adherantEmail;
    private Long totalVisits;
    private Double totalAmount;

    public GroupingByAdherantDto(String adherantName, Long adherantDeductible, String adherantEmail, Long totalVisits,
            Double totalAmount) {
        this.adherantName = adherantName;
        this.adherantDeductible = adherantDeductible;
        this.adherantEmail = adherantEmail;
        this.totalVisits = totalVisits;
        this.totalAmount = totalAmount;
    }

    public String getAdherantName() {
        return adherantName;
    }

    public void setAdherantName(String adherantName) {
        this.adherantName = adherantName;
    }

    public Long getAdherantDeductible() {
        return adherantDeductible;
    }

    public void setAdherantDeductible(Long adherantDeductible) {
        this.adherantDeductible = adherantDeductible;
    }

    public Long getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(Long totalVisits) {
        this.totalVisits = totalVisits;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAdherantEmail() {
        return adherantEmail;
    }

    public void setAdherantEmail(String adherantEmail) {
        this.adherantEmail = adherantEmail;
    }

    @Override
    public String toString() {
        return "GroupingByAdherantDto{" +
                "adherantName='" + adherantName + '\'' +
                ", adherantDeductible=" + adherantDeductible +
                ", adherantEmail='" + adherantEmail + '\'' +
                ", totalVisits=" + totalVisits +
                ", totalAmount=" + totalAmount +
                '}';
    }

}
