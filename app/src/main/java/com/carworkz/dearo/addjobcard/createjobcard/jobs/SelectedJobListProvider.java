package com.carworkz.dearo.addjobcard.createjobcard.jobs;

import com.carworkz.dearo.domain.entities.RecommendedJob;

import java.util.List;

/**
 * Created by Farhan on 13/10/17.
 */

public interface SelectedJobListProvider {

    List<RecommendedJob> getRecommendedJobList();

    List<RecommendedJob> getDemandedJobList();

}
