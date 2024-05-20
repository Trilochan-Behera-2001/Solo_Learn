package com.solo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solo.entity.FreeContent;

public interface IFreeContentRepo extends JpaRepository<FreeContent, Integer> {

}
