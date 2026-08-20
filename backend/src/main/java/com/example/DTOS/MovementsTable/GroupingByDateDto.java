package com.example.DTOS.MovementsTable;


public class GroupingByDateDto {
    private String datePeriod;
    private Long count;
    private Double totalAmount;

    public GroupingByDateDto(String datePeriod, Long count, Double totalAmount
            ) {
        this.datePeriod = datePeriod;
        this.count = count;
        this.totalAmount = totalAmount;
        
    }

    public GroupingByDateDto() {
    }

    public String getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(String datePeriod) {
        this.datePeriod = datePeriod;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    
}
