package com.prateek.cowinAvailibility.repo;

import com.prateek.cowinAvailibility.entity.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author prateek.mishra Repository Interface for CRUD operations on User Table
 */
public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {

}
