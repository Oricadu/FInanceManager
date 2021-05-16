package com.oricadu.financemanager.ui.scan;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.oricadu.financemanager.R;
import com.oricadu.financemanager.ui.expenses.ExpenseFragment;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ScanFragment extends Fragment {

    private ScanViewModel mViewModel;

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private TextView textView;
    private BarcodeDetector barcodeDetector;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_scan, container, false);




        surfaceView = root.findViewById(R.id.camera);
        textView = root.findViewById(R.id.text);

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE).build();

        CameraSource.Builder builder = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(640, 480);

        builder = builder.setAutoFocusEnabled(true);

        cameraSource = builder.build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode = detections.getDetectedItems();
                Log.i("scan", "" + qrcode.valueAt(0).displayValue);



                String regex = "(t=\\d{8}T\\d{6}&s=\\d+\\.\\d{2}&fn=\\d{16}&i=\\d{5}&fp=\\d{10}&n=\\d)";
                /*Pattern pattern = Pattern.compile();
                Matcher matcher = pattern.matcher(qrcode.valueAt(0).displayValue);
*/


                Log.i("Scan", "ismatch " + Pattern.matches(regex, qrcode.valueAt(0).displayValue));


                if ((qrcode.size() != 0) && (Pattern.matches(regex, qrcode.valueAt(0).displayValue))) {
                    barcodeDetector.release();
                    ExpenseFragment.addExpense("с чека1", 50, "Еда");
                    ExpenseFragment.addExpense("с чека2", 520, "Еда");
                    ExpenseFragment.addExpense("с чека3", 530, "Транспорт");

                    NavController navController = Navigation.findNavController((Activity) root.getContext(), R.id.nav_host_fragment);
                    ExpenseFragment nextFrag= new ExpenseFragment();
                    cameraSource.stop();
                    navController.navigate(R.id.navigation_expenses);

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(qrcode.valueAt(0).displayValue);


//                            FnsCheck check = null;
                            /*try {
                                check = FNS.getCheck(qrcode.valueAt(0).displayValue,
                                        "+79168713361",
                                        "243117");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (check != null) {
                                textView.setText(check.toString());
                                System.out.println(check);

                            }*/
                        }
                    });
                }
            }
        });

        return root;
    }



}