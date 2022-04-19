package com.moblize.ms.dailyops.service;

import com.moblize.ms.dailyops.domain.MongoWell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MobMongoQueryService {
    @Autowired
    @Qualifier("mobMongoTemplate")
    private MongoTemplate mobMongoTemplate;
    public List<String> getRigIdsByName(final List<String> rigs) {
        final MatchOperation match = Aggregation.match(
            new Criteria("name").in(rigs));
        final ProjectionOperation project = Aggregation.project().andInclude("_id.$oid").andExclude("_id");
        final Aggregation aggregation = Aggregation.newAggregation(match, project);
        List<String> rigList = new ArrayList<>();

        mobMongoTemplate.
            aggregateStream(aggregation, "rigs", Map.class).forEachRemaining(rig -> {
            rigList.add((String) rig.get("_id"));
        });
        return rigList;
    }

    public List<MongoWell> getWellsByRigIds(final List<String> rigIds) {
        final MatchOperation match = Aggregation.match(Criteria.where("rigs.rigid").in(rigIds));
        final Aggregation aggregation = Aggregation.newAggregation(match);
        return mobMongoTemplate.aggregate(aggregation, "wells", MongoWell.class)
            .getMappedResults();

    }
}
