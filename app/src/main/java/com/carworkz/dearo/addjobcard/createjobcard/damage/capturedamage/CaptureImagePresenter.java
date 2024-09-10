package com.carworkz.dearo.addjobcard.createjobcard.damage.capturedamage;

import com.carworkz.dearo.base.ErrorWrapper;
import com.carworkz.dearo.data.DataSource;
import com.carworkz.dearo.data.DearODataRepository;
import com.carworkz.dearo.domain.entities.FileObject;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by kush on 23/8/17.
 */

public class CaptureImagePresenter implements CaptureImageContract.Presenter {

    private final DearODataRepository dearODataRepository;
    private CaptureImageContract.View view;

    @Inject
    CaptureImagePresenter(DearODataRepository dearODataRepository, CaptureImageContract.View view) {
        this.dearODataRepository = dearODataRepository;
        this.view = view;
    }

    @Override
    public void saveDamageImage(@NotNull FileObject image) {
        dearODataRepository.saveDamageImage(image, new DataSource.OnResponseCallback<FileObject>() {
            @Override
            public void onSuccess(FileObject obj) {
                if (view != null)
                    view.onDamageUploadFinish();
                Timber.d("image upload success");

            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                if (view != null)
                    view.onDamageUploadFinish();
                Timber.d("image upload fail");

            }
        });
    }

    @Override
    public void savePDCImage(@NotNull FileObject image) {
        dearODataRepository.savePDCImage(image, new DataSource.OnResponseCallback<FileObject>() {
            @Override
            public void onSuccess(FileObject obj) {
                if (view != null)
                    view.onDamageUploadFinish();
                Timber.d("image upload success");

            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                if (view != null)
                    view.onDamageUploadFinish();
                Timber.d("image upload fail");

            }
        });
    }

    @Override
    public void saveDocument(@NotNull FileObject file) {
        dearODataRepository.uploadDocument(file, new DataSource.OnResponseCallback<FileObject>() {
            @Override
            public void onSuccess(FileObject obj) {
                if (view != null)
                    view.onDamageUploadFinish();
                Timber.d("image upload success");
                Timber.d("Uploaded");
            }

            @Override
            public void onError(@NotNull ErrorWrapper error) {
                if (view != null)
                    view.onDamageUploadFinish();
                Timber.d("image upload fail");
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void detachView() {
        view = null;

    }
}
