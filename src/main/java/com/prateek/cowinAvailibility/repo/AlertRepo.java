package com.prateek.cowinAvailibility.repo;

import java.util.List;

import com.prateek.cowinAvailibility.entity.Alerts;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author prateek.mishra Repository Interface for CRUD operations on User Table
 */
public interface AlertRepo extends JpaRepository<Alerts, Integer> {

    public List<Alerts> findByName(String name);

    public List<Alerts> findByPhoneNumber(String phoneNumber);

    public List<Alerts> findByPhoneNumberAndActiveTrue(String phoneNumber);

    public List<Alerts> findByPhoneNumberContainingAndActiveTrue(String phoneNumberSubstring);

    public List<Alerts> findByActiveTrue();
}
