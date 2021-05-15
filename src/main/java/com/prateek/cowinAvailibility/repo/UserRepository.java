package com.prateek.cowinAvailibility.repo;

import com.prateek.cowinAvailibility.entity.user.Users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {

    public Users findByUsername(String username);
}
