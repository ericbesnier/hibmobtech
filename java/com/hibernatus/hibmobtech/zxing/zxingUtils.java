package com.hibernatus.hibmobtech.zxing;

import android.app.Activity;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;


public class zxingUtils extends Activity {
    public static final String TAG = zxingUtils.class.getSimpleName();
/*    private static Activity activity;

    public zxingUtils(Activity activity) {
        this.activity = activity;
    }*/

    public void scanBarcode(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    /*public void scanBarcodeCustomLayout(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan something");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    public void scanBarcodeFrontCamera(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        integrator.initiateScan();
    }

    public void scanContinuous(View view) {
        Intent intent = new Intent(this, ContinuousCaptureActivity.class);
        activity.startActivity(intent);
    }

    public void scanToolbar(View view) {
        new IntentIntegrator(this).setCaptureActivity(ToolbarCaptureActivity.class).initiateScan();
    }

    public void scanCustomScanner(View view) {
        new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();
    }

    public void scanMarginScanner(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(SmallCaptureActivity.class);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("zxingUtils", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("zxingUtils", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Scanned: result.getContents()= " + result.getContents());
                Log.d(TAG, "Scanned: result.getRawBytes()= " + result.getRawBytes().toString());
                Log.d(TAG, "Scanned: result.getBarcodeImagePath()= " + result.getBarcodeImagePath());
                Log.d(TAG, "Scanned: result.getFormatName()= " + result.getFormatName());
                Log.d(TAG, "Scanned: result.getErrorCorrectionLevel()= " + result.getErrorCorrectionLevel());
                Log.d(TAG, "Scanned: result: " + result.toString());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    // Sample of scanning from a Fragment

    public static class ScanFragment extends Fragment {
        private String toast;

        public ScanFragment() {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            displayToast();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_scan, container, false);
            Button scan = (Button) view.findViewById(R.id.scan_from_fragment);
            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scanFromFragment();
                }
            });
            return view;
        }

        public void scanFromFragment() {
            IntentIntegrator.forSupportFragment(this).initiateScan();
        }

        private void displayToast() {
            if(getForegroundActivity() != null && toast != null) {
                Toast.makeText(getForegroundActivity(), toast, Toast.LENGTH_LONG).show();
                toast = null;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    toast = "Cancelled from fragment";
                } else {
                    toast = "Scanned from fragment: " + result.getContents();
                }

                // At this point we may or may not have a reference to the activity
                displayToast();
            }
        }
    }*/
}
