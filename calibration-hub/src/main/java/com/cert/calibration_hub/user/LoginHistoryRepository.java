package com.cert.calibration_hub.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findByUserOrderByLoginTimeDesc(User user);

    List<LoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId);
}