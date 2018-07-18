package edu.osu.cse5236.group9.dieta;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class NewFoodFragment extends Fragment {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDPXTUYwMv0sm4DqbNZsP-pqgiueXftSpI";
    public static final String FILE_NAME = "temp.jpg";
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    public static final int INTERNET_REQUEST = 4;
    private EditText mNameField;
    private Button mButton_Camera;
    private Button mButton_AddFood;
    private Button mButton_Confirm;
    private List<String> mFoodList;
    private Meal mMeal;
    private Food mFood;
    private ImageView mFoodImage;
    private ProgressDialog mProgress;
    private int mAsyncTaskState;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_food, container, false);
        mFoodList = new ArrayList<>();
        mMeal = new Meal();
        mAsyncTaskState = 0;
        mFoodImage = (ImageView) v.findViewById(R.id.food_image);
        mNameField = (EditText) v.findViewById(R.id.food_name);
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFood = new Food(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Intentionally left blank
            }
        });
        mButton_Camera = (Button) v.findViewById(R.id.camera_button);
        mButton_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setMessage(R.string.picture_select_prompt)
                        .setPositiveButton(R.string.picture_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.picture_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });

        mButton_AddFood = (Button) v.findViewById(R.id.add_food_button);
        mButton_AddFood.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mFood!=null) mMeal.addFood(mFood);
            }
        });
        mButton_Confirm = (Button) v.findViewById(R.id.confirm_food_button);
        mButton_Confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mAsyncTaskState==0) {
                    Intent i = new Intent(getActivity(), ConfirmActivity.class);
                    i.putStringArrayListExtra("mFoodList", (ArrayList<String>) mFoodList);
                    i.putExtra("mMeal", mMeal);
                    startActivity(i);
                }
                else {
                    if (mProgress==null) {
                        mProgress = new ProgressDialog(getActivity());
                        mProgress.setTitle("Vision");
                        mProgress.setMessage("Still recognizing...");
                        mProgress.setButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        mProgress.show();
                    }
                    else mProgress.show();
                }
            }
        });

        return v;
    }

    private void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
    }

   
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private List<String> convertResponseToList(BatchAnnotateImagesResponse response) {
        List<String> res = new ArrayList<>();
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                res.add(label.getDescription());
            }
        }
        return res;
    }
}
