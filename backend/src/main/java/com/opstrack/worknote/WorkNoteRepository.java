package com.opstrack.worknote;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkNoteRepository extends JpaRepository<WorkNote, Long> {

    List<WorkNote> findByMaintenanceTaskId(Long maintenanceTaskId);
}