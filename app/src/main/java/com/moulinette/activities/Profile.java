package com.moulinette.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.moulinette.R;
import com.moulinette.databinding.ActivityProfileBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.profile.Data;
import com.moulinette.models.profile.GetProfileResponse;
import com.moulinette.models.profile.UpdateProfileRequest;
import com.moulinette.utilities.Base641;
import com.moulinette.utilities.Config;
import com.moulinette.utilities.FilePath;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.TinyDB;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.ilhasoft.support.validation.Validator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static com.moulinette.utilities.ConstandFunctions.hideKeyboardFrom;

public class Profile extends Activity {

    ActivityProfileBinding binding;
    Validator validator;
    MyClickHandler handlers;
    Activity ac;
    TinyDB tinyDB;
    ProgDialog prog = new ProgDialog();
    String url;
    //Calander
    private int mYear, mDay, mMonth;
    Calendar c;
    //Camera
    String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Uri imageURI = null;
    File final_file;
    private File mTmpFile;
    String profile_img, photo_value;
    Uri profile_Uri;
    boolean update_check = false;
    Data datum;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
        tinyDB = new TinyDB(ac);
        validator = new Validator(binding);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        binding.profileImage.setImageURI(tinyDB.getString("user_img"));
        handlers = new MyClickHandler(ac);
        binding.setClick(handlers);
        getProfile();

    }

//    Men's Ultraforce Mid-top Athletic-Inspired Retro Fashion Casual/Outdoor Sneakers for Man-Gray
    public class MyClickHandler {
        Activity context;

        public MyClickHandler (Activity ac){
            this.context = ac;
        }
        public void updateProfileClick (View view){

            if (!binding.name.getText().toString().equals(datum.getFirstName()) || !binding.email.getText().toString().equals(datum.getEmail()) || !binding.ccp.getSelectedCountryCode().equals(datum.getCountryCode()) || !binding.phoneEdt.getText().toString().equals(datum.getOnlyNumber()) || !binding.addressEdit.getText().toString().equals(datum.getAddress()) || !binding.genderSpiner.getSelectedItem().toString().equals(datum.getGender()) || !binding.bithday.getText().toString().equals(datum.getDob()))
            {
                update_check = true;
            }
            if ( update_check ){
                if ( validator.validate(binding.email) && validator.validate(binding.phoneEdt) ){
                    hideKeyboardFrom(ac, view);
                    updateProfile();
                }
            }else{
                MessageDialog.showAlert(ac, ac.getResources().getString(R.string.no_change), ac.getResources().getString(R.string.ok));
            }
        }

        // Birthday picker for getting date from server
        public void birthdayClick (View view){
            datepickerForDOB();
        }
        public void backClick (View view){
            finish();
        }
        public void changeProfilePic (View view){
            if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ){
                //Permission check and ask for required permission
                Dexter.withActivity(ac)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked (MultiplePermissionsReport report){
                        // check if all permissions are granted
                        if ( report.areAllPermissionsGranted() ){
                            selectImage();
                        }
                        // check for permanent denial of any permission
                        if ( report.isAnyPermissionPermanentlyDenied() ){
                            // permission is denied permenantly, navigate user to app settings
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ac);
                            builder1.setMessage(R.string.select_image);
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick (DialogInterface dialog, int id){
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown (List <PermissionRequest> permissions, PermissionToken token){
                        token.continuePermissionRequest();
                    }
                }).check();

            } else {
                selectImage();
            }
        }
    }
    private void selectImage ( ){
        final CharSequence[] items = { getString(R.string.take_photo), getString(R.string.choose_frm_gallery),
                getString(R.string.cancel) };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ac);
        builder.setTitle(R.string.add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int item){

                if ( items[item].equals(getString(R.string.take_photo)) ){
                    userChoosenTask = getResources().getString(R.string.take_photo);
                    cameraIntent();
                } else if ( items[item].equals(getString(R.string.choose_frm_gallery)) ){
                    userChoosenTask = getResources().getString(R.string.choose_frm_gallery);
                    galleryIntent();
                } else if ( items[item].equals(getString(R.string.cancel)) ){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent (){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_pic)), SELECT_FILE);
        } catch ( Exception e ) {
        }
    }

    private File getTempFile (Context context){
        final File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if ( ! path.exists() ){
            path.mkdir();
        }
        return new File(path, "image.tmp");
    }

    private void cameraIntent ( ){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTmpFile = getTempFile(ac.getApplicationContext());
        Uri uri = Uri.fromFile(mTmpFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        update_check = true;
        if ( requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            cropedImage(result);
        } else {
            try {
                if ( data != null ){
                    Uri selectedImageUri = data.getData();
                    String realPath = FilePath.getPath(ac, selectedImageUri);
                    imageURI = Uri.fromFile(new File(realPath));
                    final_file = new File(realPath);
                } else {
                    imageURI = Uri.fromFile(mTmpFile);
                    final_file = mTmpFile;
                }
                CropImage.activity(imageURI).start(this);
            } catch ( Exception e ) {
            }
        }
    }

    private void cropedImage (CropImage.ActivityResult data){

        Bitmap bm = null;
        Uri tempUri = null;
        if ( data != null ){
            try {
                bm = MediaStore.Images.Media.getBitmap(ac.getContentResolver(), data.getUri());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                byte[] b = bytes.toByteArray();
                photo_value = Base641.encodeBytes(b);
                tempUri = getImageUri(ac, bm);
            } catch ( Exception e ) {
            }
        }
        profile_img = photo_value;
        binding.profileImage.setImageURI(tempUri, ac);
        profile_Uri = tempUri;
        String realPath = FilePath.getPath(ac, profile_Uri);
        imageURI = Uri.fromFile(new File(realPath));
        final_file = new File(realPath);
    }

    public Uri getImageUri (Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void datepickerForDOB ( ){
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ac, R.style.MyDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        String startTime = dayOfMonth + "-" + ( monthOfYear + 1 ) + "-" + year;
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
                            Date newDate = format.parse(startTime);
                            format = new SimpleDateFormat("yyyy-mm-dd");
                            String date = format.format(newDate);
                            binding.bithday.setText(date);
                        } catch ( ParseException e ) {
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    // Get profile
    public void getProfile ( ){

        if ( MainApplication.isNetworkAvailable(ac) ){

            prog.progDialog(ac);
            url = Config.BASE_URL + "authentication/user/" + tinyDB.getString("user_id");
            MainApplication.getApiService().getProfile(url).enqueue(new Callback <GetProfileResponse>() {
                @Override
                public void onResponse (Call <GetProfileResponse> call, Response <GetProfileResponse> response){

                    prog.hideProg();
                    if ( response.code() == 200 ){

                         datum = new Data();
                        datum = response.body().getData();

                        if ( datum.getFirstName() != null ){
                            binding.name.setText(datum.getFirstName());
                            tinyDB.putString("FName", datum.getFirstName());
                        } else {
                            binding.name.setText("");
                        }
                        if ( datum.getUserName() != null && !datum.getUserName().isEmpty()){
                            binding.uName.setText(datum.getUserName());
                        } else {
                            binding.uName.setText(datum.getFirstName());
                        }
                        binding.address.setText(datum.getAddress());

                        if ( datum.getEmail() != null ){
                            binding.email.setText(datum.getEmail());
                        } else {
                            binding.email.setText("");
                        }
                        if ( datum.getDob() != null ){
                            binding.bithday.setText(datum.getDob());
                        } else {
                            binding.bithday.setText("");
                        }
                        if ( datum.getAddress() != null ){
                            binding.addressEdit.setText(datum.getAddress());
                        } else {
                            binding.addressEdit.setText("");
                        }

                        if ( datum.getPicture() != null ){
                            binding.profileImage.setImageURI(datum.getPicture(), this);
                            tinyDB.putString("user_img", datum.getPicture());
                        } else {
//                            binding.profileImage.setText("");
                        }

                        if ( datum.getOnlyNumber() != null ){
                            binding.phoneEdt.setText(datum.getOnlyNumber());
                        }

                        if ( datum.getCountryCode() != null && ! datum.getCountryCode().isEmpty() ){
                            try {
                                binding.ccp.setCountryForPhoneCode(Integer.parseInt(datum.getCountryCode()));
                            } catch ( Exception e ) {
                            }
                        }

                        if ( datum.getGender() != null && ! datum.getGender().isEmpty() ){
                            if ( datum.getGender().equals("Male") ){
                                binding.genderSpiner.setSelection(1);
                            } else {
                                binding.genderSpiner.setSelection(2);
                            }
                        } else {
                            binding.genderSpiner.setSelection(0);
                        }

                    } else {
                        String message = "";
                        try {
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            message = obj.getString("message");
                            MessageDialog.showAlert(ac, message, getResources().getString(R.string.ok));

                        } catch ( Exception e ) {
                            MessageDialog.showAlert(ac, getString(R.string.other_error), getResources().getString(R.string.ok));
                        }
                    }
                }

                @Override
                public void onFailure (Call <GetProfileResponse> call, Throwable t){}
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }
    }

    public void updateProfile ( ){
        if ( MainApplication.isNetworkAvailable(ac) ){

            String imageString = "";
            if ( final_file != null ){

                String filePath = final_file.getPath();
                //encode image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
            updateProfileRequest.setId(tinyDB.getString("user_id"));
            updateProfileRequest.setFirstName(binding.name.getText().toString());
            updateProfileRequest.setEmail(binding.email.getText().toString());
            updateProfileRequest.setCountryCode(binding.ccp.getSelectedCountryCode());
            updateProfileRequest.setOnlyNumber(binding.phoneEdt.getText().toString());
            updateProfileRequest.setMobileNumber(binding.ccp.getSelectedCountryCode() + binding.phoneEdt.getText().toString());
            updateProfileRequest.setAddress(binding.addressEdit.getText().toString());

            if ( ! binding.genderSpiner.getSelectedItem().toString().equals("Select gender") ){
                updateProfileRequest.setGender(binding.genderSpiner.getSelectedItem().toString());
                updateProfileRequest.setDob(binding.bithday.getText().toString());
            }
            if ( imageString != null && ! imageString.isEmpty() ){
                updateProfileRequest.setUserImage(imageString);
            }

            prog.progDialog(ac);
            MainApplication.getApiService().updateUser(updateProfileRequest).enqueue(new Callback <GetProfileResponse>() {
                @Override
                public void onResponse (Call <GetProfileResponse> call, Response <GetProfileResponse> response){
                    prog.hideProg();
                    update_check = false;
                    if ( response.code() == 200 ){
                         datum = new Data();
                        datum = response.body().getData();
                        tinyDB.putString("user_id", datum.getId());
                        tinyDB.putString("user_email", datum.getEmail());
                        tinyDB.putString("user_name", datum.getUserName());
                        try {
                            tinyDB.putString("user_gender", datum.getGender());
                        } catch ( Exception e ) {
                            tinyDB.putString("user_gender", "Male");
                        }
                        tinyDB.putString("user_dob", datum.getDob());
                        tinyDB.putString("user_address", datum.getAddress());

                        if ( datum.getFirstName() != null ){
                            tinyDB.putString("FName", datum.getFirstName());
                        } else {
                            tinyDB.putString("FName", "");
                        }
                        binding.address.setText(datum.getAddress());
                        if ( datum.getPicture() != null ){
                            tinyDB.putString("user_img", datum.getPicture());
                        } else {
                            tinyDB.putString("user_img", "");
                        }
                        MessageDialog.showAlert(ac, ac.getString(R.string.profile_updated), ac.getString(R.string.ok));
                    } else {

                        String message = "";
                        try {
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            try {
                                message = obj.getString("date_of_birth");
                            } catch ( JSONException e ) {
                                try {
                                    message = obj.getString("phone_number");
                                } catch ( JSONException e1 ) {
                                    message = obj.getString("email");
                                }
                            }
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                        System.out.println("sdshdsdhsh   " + message);
                        try {
                            MessageDialog.showAlert(ac, message, ac.getString(R.string.ok));
                        } catch ( Exception e ) {
                            MessageDialog.showAlert(ac, ac.getString(R.string.other_error), ac.getString(R.string.ok));
                        }
                    }
                }

                @Override
                public void onFailure (Call <GetProfileResponse> call, Throwable t){
                    prog.hideProg();
                    t.printStackTrace();
                    MessageDialog.showAlert(ac, ac.getString(R.string.other_error), ac.getString(R.string.ok));
                }
            });

        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }
    }


}
