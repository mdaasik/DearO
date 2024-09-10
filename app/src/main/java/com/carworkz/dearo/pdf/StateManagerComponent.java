package com.carworkz.dearo.pdf;


import com.carworkz.dearo.data.DataRepositoryModule;
import com.carworkz.dearo.data.DearODataRepository;
import com.carworkz.dearo.injection.ApplicationScoped;

import dagger.Component;

@ApplicationScoped
@Component( modules = DataRepositoryModule.class)
public interface StateManagerComponent {
   // void inject(GatePassStateManager stateManager);

    DearODataRepository getRepository();
}
