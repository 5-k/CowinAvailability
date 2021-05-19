package com.prateek.cowinAvailibility.repo;

import com.prateek.cowinAvailibility.entity.Metrics;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricsRepo extends JpaRepository<Metrics, Integer> {

}