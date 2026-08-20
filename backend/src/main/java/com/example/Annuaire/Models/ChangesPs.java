package com.example.Annuaire.Models;

import java.util.List;

public class ChangesPs {
    private List<HealthcareProfessional> inserts;
    private List<HealthcareProfessional> updates;
    private List<HealthcareProfessional> deletes;

    public ChangesPs() {
    }

    public ChangesPs(List<HealthcareProfessional> inserts, List<HealthcareProfessional> updates,
            List<HealthcareProfessional> deletes) {
        this.inserts = inserts;
        this.updates = updates;
        this.deletes = deletes;
    }

    public List<HealthcareProfessional> getInserts() {
        return inserts;
    }

    public void setInserts(List<HealthcareProfessional> inserts) {
        this.inserts = inserts;
    }

    public List<HealthcareProfessional> getUpdates() {
        return updates;
    }

    public void setUpdates(List<HealthcareProfessional> updates) {
        this.updates = updates;
    }

    public List<HealthcareProfessional> getDeletes() {
        return deletes;
    }

    public void setDeletes(List<HealthcareProfessional> deletes) {
        this.deletes = deletes;
    }

    @Override
    public String toString() {
        return "ChangesPs{" +
                "inserts=" + inserts +
                ", updates=" + updates +
                ", deletes=" + deletes +
                '}';
    }

}
