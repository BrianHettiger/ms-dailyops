package com.moblize.ms.dailyops.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.Query;

@Repository
public interface GenericCustomRepository {
    Query find(String queryStr);
}
