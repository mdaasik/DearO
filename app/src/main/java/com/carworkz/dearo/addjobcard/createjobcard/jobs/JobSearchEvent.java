package com.carworkz.dearo.addjobcard.createjobcard.jobs;

import com.carworkz.dearo.domain.entities.RecommendedJob;

/**
 * Created by Farhan on 24/8/17.
 */

public class JobSearchEvent {

    private final RecommendedJob job;

    public JobSearchEvent(RecommendedJob job) {
        this.job = job;
    }

    public RecommendedJob getJob() {
        return job;
    }

}
