package com.telecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.entity.Operator;

public interface OperatorRepository extends JpaRepository<Operator,Long> {

}
