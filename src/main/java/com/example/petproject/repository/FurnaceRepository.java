package com.example.petproject.repository;

import com.example.petproject.model.Furnace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FurnaceRepository extends JpaRepository<Furnace, Long> {}

