package com.carworkz.dearo.base;


import android.content.DialogInterface;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.carworkz.dearo.R;
import com.carworkz.dearo.utils.Utility;

public abstract class BaseFragment extends Fragment {

    protected ProgressBar progressBar;

    public BaseFragment() {
        // Required empty public constructor
    }

    protected boolean checkIfNetworkAvailable(){
        if (Utility.isNetworkConnected(getActivity())){
            return true;
        }else{
            Toast.makeText(getActivity(), R.string.NO_INTERNET, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    protected void showProgressbar(){
        if (progressBar==null){
//            throw new InitializationException("Progress bar not initialized");
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void dismissProgressbar(){
        if (progressBar==null){
//            throw new InitializationException("Progress bar not initialized");
        }
        progressBar.setVisibility(View.GONE);
    }

    protected void displayError(String message){
        DialogFactory.createGenericErrorDialog(getActivity(),message).show();
    }

    protected void displayError(String message, DialogInterface.OnClickListener onClickListener){
        DialogFactory.createGenericErrorDialog(getActivity(),message,onClickListener).show();
    }

}
