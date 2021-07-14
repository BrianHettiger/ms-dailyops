package com.moblize.ms.dailyops.repository;


import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Repository
public class GenericDataRepositoryImpl implements GenericCustomRepository {
    @PersistenceContext
    EntityManager entityManager;
    @Override
    public Query find(String queryStr) {
        return entityManager.createQuery(queryStr);
    }

    @Override
    public <T> TypedQuery<T> find(String queryStr, Class<T> entityName) {
        return entityManager.createQuery(queryStr, entityName);
    }


}
