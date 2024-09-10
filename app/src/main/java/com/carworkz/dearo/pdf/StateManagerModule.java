package com.carworkz.dearo.pdf;

import com.carworkz.dearo.data.DearODataRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
final class StateManagerModule {

    private DearODataRepository repository;

    @Inject
    StateManagerModule(DearODataRepository dearODataRepository) {
        repository = dearODataRepository;
    }

    @Provides
    @Singleton
    DearODataRepository providesRepository() {
        return repository;
    }
}
