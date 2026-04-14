package com.yadot.api.repository;

import com.yadot.api.model.CheckinModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckinRepository extends JpaRepository<CheckinModel, Long> {
}
