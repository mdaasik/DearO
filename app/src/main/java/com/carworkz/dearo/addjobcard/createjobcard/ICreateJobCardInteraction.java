package com.carworkz.dearo.addjobcard.createjobcard;

/**
 * Created by Farhan on 17/8/17.
 */

public interface ICreateJobCardInteraction {

    void onJobSuccess();

    void onJobFailure();

    void onJobVerify();
}
