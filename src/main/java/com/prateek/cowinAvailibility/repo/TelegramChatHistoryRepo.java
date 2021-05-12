package com.prateek.cowinAvailibility.repo;

import com.prateek.cowinAvailibility.entity.TelegramChatHistory;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author prateek.mishra Repository Interface for CRUD operations on User Table
 */
public interface TelegramChatHistoryRepo extends JpaRepository<TelegramChatHistory, Integer> {

}
