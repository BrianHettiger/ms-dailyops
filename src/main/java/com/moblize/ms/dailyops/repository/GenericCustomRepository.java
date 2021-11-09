package com.moblize.ms.dailyops.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Repository
public interface GenericCustomRepository {
    Query find(String queryStr);
    <T> TypedQuery<T> find(String queryStr, Class<T> entityName);
}
