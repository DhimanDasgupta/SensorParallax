package com.dhiman.sensorparallax;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by dhiman_da on 11/23/2015.
 */
public class MainFragment extends Fragment implements DataPassCallback {
    private ImageView mImageView;
    private TextView mTextViewSensorData;
    private int mHeight;

    public MainFragment() {

    }

    public static MainFragment newInstance() {
        final MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageView = (ImageView) view.findViewById(R.id.image_view);
        if (mImageView != null) {
            Picasso.with(view.getContext()).
                    load(MainData.IMAGE_URL).
                    into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            onImageLoaded();
                        }

                        @Override
                        public void onError() {

                        }
                    });

            ViewTreeObserver vto = mImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    // Remove after the first run so it doesn't fire forever
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mHeight = mImageView.getMeasuredHeight();
                    return true;
                }
            });
        }

        final TextView textViewOne = (TextView) view.findViewById(R.id.text_view_01);
        if (textViewOne != null) {
            textViewOne.setText(MainData.TEXT_ONE);
        }

        mTextViewSensorData = (TextView) view.findViewById(R.id.text_view_02);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity() != null && getActivity() instanceof ActivityFragmentCallback) {
            ((ActivityFragmentCallback) getActivity()).unregister();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mImageView != null) {
            Picasso.with(getActivity()).
                    load(MainData.IMAGE_URL).
                    into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            onImageLoaded();
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    @Override
    public void onOrientationChanged(float azimuth, float pitch, float roll) {
        if (mImageView != null) {
            mImageView.setTranslationY(mHeight * pitch);
        }

        if (mTextViewSensorData != null) {
            mTextViewSensorData.setText(azimuth + ", " + pitch + ", " + roll);
        }
    }

    /**
     * Register the callback here, because we want the parallax effect after the image is loaded
     * */
    private void onImageLoaded() {
        if (getActivity() != null && getActivity() instanceof ActivityFragmentCallback) {
            ((ActivityFragmentCallback) getActivity()).register(this);
        }
    }
}
