package com.yadot.api.repository;

import com.yadot.api.model.CheckinModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckinRepository extends JpaRepository<CheckinModel, Long> {
}
