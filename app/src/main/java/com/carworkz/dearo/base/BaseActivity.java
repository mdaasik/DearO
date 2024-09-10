package com.carworkz.dearo.base;

import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.carworkz.dearo.R;
import com.carworkz.dearo.utils.Utility;

public abstract class BaseActivity extends AppCompatActivity {

    private ActivityCompat.OnRequestPermissionsResultCallback permissionsResultCallback;


    protected abstract View getProgressView();

    public void showProgressBar() {
        Utility.checkNotNull(getProgressView(), "Progress bar not initialized");
        getProgressView().setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void dismissProgressBar() {
        Utility.checkNotNull(getProgressView(), "Progress bar not initialized");
        //  if (getProgressView().isShown()) {
        getProgressView().setVisibility(View.GONE);
        //   }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    public void setPermissionsResultCallback(ActivityCompat.OnRequestPermissionsResultCallback permissionsResultCallback) {
        this.permissionsResultCallback = permissionsResultCallback;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)  {
        if (this.permissionsResultCallback != null) {
            this.permissionsResultCallback.onRequestPermissionsResult(requestCode, permissions, grantResults);
            this.permissionsResultCallback = null;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void displayError(String message) {
        if (!this.isFinishing()) {
            DialogFactory.createGenericErrorDialog(this, message).show();
        }
    }
    protected void displayInfo(String message) {
        if (!this.isFinishing()) {
            DialogFactory.createGenericErrorDialog(this,"Info !!!", message).show();
        }
    }

    protected void displayError(String message, DialogInterface.OnClickListener onClickListener) {
        if (!this.isFinishing()) {
            DialogFactory.createGenericErrorDialog(this, message, onClickListener).show();
        }
    }

    protected void displayError(String title, String message, DialogInterface.OnClickListener onClickListener) {
        if (!this.isFinishing()) {
            DialogFactory.createGenericErrorDialog(this, title, message, onClickListener).show();
        }
    }

    protected boolean checkIfNetworkAvailable() {
        if (Utility.isNetworkConnected(BaseActivity.this)) {
            return true;
        } else {
            Toast.makeText(this, R.string.NO_INTERNET, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void toast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
