package com.infikaa.indibubble;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A placeholder fragment containing a simple view.
 */
public class QRFragment extends Fragment {
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    FragmentManager fragmentManager;

    private SurfaceView cameraView;
    private TextView codeInfo;
    private boolean readCode = false;
    private  CameraSource mCameraSource;
    private String code;
    private Handler mHandler;
    private ProgressDialog progressDialog;
    private int screenWidth;
    Vibrator vibe;
    public QRFragment() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                codeInfo.setText(code);
                vibe.vibrate(100);
                new SendPostRequest().execute(code);
            }
        };
    }
    public static QRFragment newInstance() {

        QRFragment fragment = new QRFragment();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            screenWidth=getArguments().getInt("screenWidth");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qrscanner, container, false);
        vibe = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
        cameraView = (SurfaceView) rootView.findViewById(R.id.camera_view);
        codeInfo = (TextView) rootView.findViewById(R.id.code_info);
        toolbar=(Toolbar)rootView.findViewById(R.id.toolbar_qr);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("QR Scanner");
        }
        fragmentManager=getActivity().getFragmentManager();
        drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentManager.getBackStackEntryCount() > 1){
                    fragmentManager.popBackStack();
                }else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        toggle.syncState();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        mCameraSource = new CameraSource.Builder(getActivity(),barcodeDetector)
                .setRequestedFps(15.0f)
                .setRequestedPreviewSize(1600,1024)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    mCameraSource.start(cameraView.getHolder());
                }catch (SecurityException e){
                    Log.e(QRFragment.class.getSimpleName(),e.getMessage());
                }catch (IOException e){
                    Log.e(QRFragment.class.getSimpleName(),e.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size()>0 && readCode==false){
                    readCode = true;
                    code = barcodes.valueAt(0).displayValue;
                    mHandler.sendEmptyMessage(0);

                }
            }
        });
    }
    @Override
    public void onDestroy() {
        if(mCameraSource!=null) {
            mCameraSource.release();
            mCameraSource = null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //    ((IssueBackButton) getActivity()).issueBackButton(true);
        ((FragmentToActivity) getActivity()).SetNavState(R.id.nav_qrcode);
    }


    public class SendPostRequest extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading..");
            progressDialog.setTitle("Checking Network");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String response="";
            String result="";
            try {
                URL url = new URL("http://waterp.hol.es/cus.completeOrderQR.waterp.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", "subho040995@gmail.com")
                        .appendQueryParameter("orderid",params[0])
                        .appendQueryParameter("user","cus")
                        .appendQueryParameter("atcomplete", "2017-04-07 23:20:50");
                String query = builder.build().getEncodedQuery();


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                }
                else {
                    response="";

                }
                result=response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e){
                e.printStackTrace();
            }
            catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            final JSONObject jbj;
            try {
                jbj=new JSONObject(s);
                if(!s.equals("") && jbj.getString("success").equals("true") && jbj.getString("errorCode").equals("null") ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Order Updated");
                    builder.setMessage("Your order has been successfully completed. Thanks for using Indi Bubble!");
                    builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();
                            getFragmentManager().beginTransaction().replace(R.id.displaycontent, Home.newInstance(screenWidth),"Home").addToBackStack("Home").commit();

                            dialog.dismiss();


                        }
                    });
                    builder.setCancelable(false);
                    builder.show();

                }
                else if(!s.equals("") && jbj.getString("success").equals("false") && jbj.getString("errorCode").equals("7") ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Update Failed");
                    builder.setMessage("Unfortunately your update has failed. Please re try.");
                    builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            readCode=false;
                            dialog.dismiss();


                        }
                    });
                    builder.setCancelable(false);
                    builder.show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
}
