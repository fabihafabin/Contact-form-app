package edu.ewubd.contactform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;



public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    EditText name, email, phone_home, phone_office;
    ImageView image;
    Button save, cancel;
    SharedPreferences sp;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp= getSharedPreferences("MyPreference", Context.MODE_PRIVATE);

        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        phone_home = findViewById(R.id.et_phone_home);
        phone_office = findViewById(R.id.et_phone_office);
        image = findViewById(R.id.iv_image);

        cancel = findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(v->finish());

        save = findViewById(R.id.btn_save);
        save.setOnClickListener(v->saveInfo());

        image.setOnClickListener(v->openFileChooser());

        SharedPreferences spCheck = getApplicationContext().getSharedPreferences("MyPreference",Context.MODE_PRIVATE);
        String name_1= spCheck.getString("name","");
        String email_1 = spCheck.getString("email","");
        String phone_home_1 = spCheck.getString("phone_home","");
        String phone_office_1 = spCheck.getString("phone_office","");

        if(name_1 != null || email_1 != null || phone_home_1 != null || phone_office_1 != null){
           name.setText(name_1);
           email.setText(email_1);
           phone_home.setText(phone_home_1);
           phone_office.setText(phone_office_1);

        }
    }

    private void saveInfo() {
        String ErrorMassage="";
        if(name.getText().toString().trim().length()<5){
            ErrorMassage += "Invalid name\n";
        }
        if(!isValidEmail(email.getText().toString().trim())){
            ErrorMassage += "Invalid email\n";
        }
        if(phone_home.getText().toString().trim().length() < 14){
            ErrorMassage += "Invalid phone (Home)\n";
        }
        if(phone_office.getText().toString().trim().length() > 0){
            if(phone_office.getText().toString().trim().length() < 14){
                ErrorMassage += "Invalid phone (Office)\n";
            }
            if(!isCountryCodeRight(phone_office.getText().toString().trim())){
                ErrorMassage += "Invalid country code for phone (Office)\n";
            }
        }

        if(phone_home.getText().toString().trim().equals(phone_office.getText().toString().trim())){
            ErrorMassage += "Both phone number can not be same\n";
        }


        if(!isCountryCodeRight(phone_home.getText().toString().trim())){
            ErrorMassage += "Invalid country code for phone (Home)\n";
        }

        if(ErrorMassage.length()>0){
            showDialog(ErrorMassage,"Error","OK","Back");
            System.out.println(ErrorMassage);
        }
        else{
            showDialog("Do you want to Save this info?","Info","Yes","No");
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

     boolean isCountryCodeRight(@NonNull String number){
        String cc = "";
        for (int i = 0; i < number.length(); i++){
            cc += number.charAt(i);
            if(i == 3) break;
        }
        if(cc.equals("+880")){
            return true;
        }
        return false;
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(image);
        }
    }

    public String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    private void showDialog(String message, String title,String btn1,String btn2){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);

        builder.setCancelable(false).setPositiveButton(btn1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(btn1.equals("Yes")){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name", name.getText().toString().trim());
                    editor.putString("email", email.getText().toString().trim());
                    editor.putString("phone_home", phone_home.getText().toString().trim());
                    editor.putString("phone_office", phone_office.getText().toString().trim());

                    editor.apply();

                    Toast.makeText(MainActivity.this,"Contact Information Saved",Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
            }
        }).setNegativeButton(btn2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                System.out.println("No Button was pressed");
                dialog.cancel();
            }
        });
        AlertDialog alert= builder.create();
        alert.show();

    }
}