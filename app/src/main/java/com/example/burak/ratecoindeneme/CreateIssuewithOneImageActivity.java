package com.example.burak.ratecoindeneme;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Created by burak on 16.02.2018.
 */
public class CreateIssuewithOneImageActivity extends AppCompatActivity {
    String USERID,result;
    String c_option1,c_option2,c_option3,c_option4,c_option5,c_description;
    int i=0;

    private ImageView imageoption,imageoption2;
    private LinearLayout mLayout;
    private EditText mEditText;
    private Button mButton,but1;
    private int edittextcount =1;
    EditText ET_DESC_OPTION;
    int check = 1;

    public static final int PICK_IMAGE = 1;
    String[] image_path2 = new String[2];
    String image_name,image_name2,error;
    String URL_TO_HIT;
    String description;
    String c_option[] = new String[5];
    int iduser;

    EditText textIn;
    Button buttonAdd;
    LinearLayout container;

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createissuewithoneimage);

        if (shouldAskPermissions()) {
            askPermissions();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        iduser = prefs.getInt("USERID",-1);

        ET_DESC_OPTION= (EditText)findViewById(R.id.desc_issuewithimage);
        imageoption = (ImageView) findViewById(R.id.imageoption1);
        textIn = (EditText)findViewById(R.id.textin);
        buttonAdd = (Button)findViewById(R.id.add);
        container = (LinearLayout)findViewById(R.id.container);

        URL_TO_HIT = "http://localapi25.atwebpages.com/android_connect/issue_createoneimage.php?id=" + iduser;
        Button but1 = (Button) findViewById(R.id.createissuewithimage);


        but1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new JSONTask().execute();
            }
        });

        imageoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check = 1;
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if(edittextcount <= 4)
                {
                    LayoutInflater layoutInflater =
                            (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View addView = layoutInflater.inflate(R.layout.row, null);
                    final TextView textOut = (TextView) addView.findViewById(R.id.textout);
                    textOut.setText(textIn.getText().toString());

                    c_option[edittextcount] = textOut.getText().toString();
                    textOut.setId(edittextcount);

                    System.out.println(c_option[edittextcount]);

                    Button buttonRemove = (Button) addView.findViewById(R.id.remove);
                    final View.OnClickListener thisListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((LinearLayout) addView.getParent()).removeView(addView);
                            edittextcount = edittextcount - 1;

                        }
                    };
                    if(Objects.equals(textIn.getText().toString(), ""))
                    {
                        Toast.makeText(getApplicationContext(), "Fill option!!", Toast.LENGTH_LONG).show();
                    }
                    buttonRemove.setOnClickListener(thisListener);
                    container.addView(addView);

                    textIn.setText("");
                    edittextcount = edittextcount + 1;

                }
                else {
                    Toast.makeText(getApplicationContext(), "No more options!!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public class JSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @SuppressWarnings("WrongThread")
        @Override
        protected String doInBackground(String... params) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            StringBuilder sb = new StringBuilder();
            HttpURLConnection httpURLConnection = null;

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;

            try {

                if(image_path2[0] == null)
                {
                    error = "Choose an image from gallery.";
                }
                else if(c_option[1] == null || c_option[2] == null)
                {
                    error = "Fill the options.";

                }
                else {
                    URL url = new URL(URL_TO_HIT);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("httpURLConnection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    description = ET_DESC_OPTION.getText().toString();
                    image_name = image_path2[0].substring(image_path2[0].lastIndexOf("/") + 1);

                    FileInputStream fileInputStream;
                    DataOutputStream outputStream;

                    outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                    outputStream.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(description);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                    outputStream.writeBytes("Content-Disposition: form-data; name=\"option_1\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(c_option[1]);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                    outputStream.writeBytes("Content-Disposition: form-data; name=\"option_2\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(c_option[2]);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);

                    if (c_option[3] != null) {
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"option_3\"" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(c_option[3]);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    }
                    if (c_option[4] != null) {
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"option_4\"" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(c_option[4]);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    }
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + image_name + "\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);


                    System.out.println(image_name);

                    fileInputStream = new FileInputStream(image_path2[0]);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);


                    int HttpResult = httpURLConnection.getResponseCode();
                    System.out.println(HttpResult);
                    if (HttpResult == HttpURLConnection.HTTP_OK) {

                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                httpURLConnection.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        JSONObject jsonObj = new JSONObject(sb.toString());
                        error = null;
                        result = jsonObj.getString("message");
                        System.out.println(result);
                        return result;
                    } else {
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        System.out.println(sb.toString());
                        JSONObject jsonObj = new JSONObject(sb.toString());
                        result = jsonObj.getString("message");
                        System.out.println(result);
                        return result;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (error != null) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CreateIssuewithOneImageActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
                    Uri uri = data.getData();

                        image_path2[0] = getRealPathFromURI(this, uri);
                        imageoption.setImageBitmap(rotate(BitmapFactory.decodeFile(image_path2[0]),90));


        }
    }
    private Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Bitmap bmOut;
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            if (bm.getWidth() == bm.getHeight() )
            {
                bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                bmOut = BITMAP_RESIZER(bmOut);
            }
            if (bm.getWidth() >= 3024 && bm.getHeight() >= 3024)
            {
                bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                bmOut = BITMAP_RESIZER(bmOut);
            }
            else {
                bmOut = BITMAP_RESIZER(bm);
            }
            return bmOut;
        }
        return bm;
    }
    public Bitmap BITMAP_RESIZER(Bitmap bitmap) {
        int newWidth = imageoption.getWidth();
        int newHeight = bitmap.getHeight();
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}