package com.prateek.cowinAvailibility.repo;

import java.util.Date;
import java.util.List;

import com.prateek.cowinAvailibility.entity.Notifications;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author prateek.mishra Repository Interface for CRUD operations on User Table
 */
public interface NotificationsRepo extends JpaRepository<Notifications, Integer> {

    public List<Notifications> findByAlertId(int id);

    public List<Notifications> findByAlertIdAndCreatedAtAfter(int id, Date date);
}
