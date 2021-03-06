package com.prateek.cowinAvailibility.dto.cowinResponse;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CowinResponse implements Serializable {
    private List<CowinResponseCenter> centers;

    @Override
    public String toString() {
        return "CowinResponse [centers=" + centers + "]";
    }

    public List<CowinResponseCenter> getCenters() {
        return centers;
    }

    public void setCenters(List<CowinResponseCenter> centers) {
        this.centers = centers;
    }

}
