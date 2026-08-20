package com.example.Annuaire.Models;

import java.util.Objects;

public class DoctorAdherentPair {
    private final Long doctorId;
    private final Long adherentId;

    public DoctorAdherentPair(Long doctorId, Long adherentId) {
        this.doctorId = doctorId;
        this.adherentId = adherentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DoctorAdherentPair that = (DoctorAdherentPair) o;
        return Objects.equals(doctorId, that.doctorId) &&
                Objects.equals(adherentId, that.adherentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, adherentId);
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public Long getAdherentId() {
        return adherentId;
    }
}