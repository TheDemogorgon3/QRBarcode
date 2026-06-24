package com.x_creations.qrandbarcode_generatorandscanner.ui.dashboard;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.x_creations.qrandbarcode_generatorandscanner.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private DecoratedBarcodeView scanner;
    TextView txtTitle, txtResult, txtCopy, txtOpen;
    Vibrator vibrator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        vibrate();

        txtTitle = root.findViewById(R.id.txt_title);
        scanner = root.findViewById(R.id.scanner);
        txtResult = root.findViewById(R.id.txt_result);
        txtCopy = root.findViewById(R.id.copy_txt);
        txtOpen = root.findViewById(R.id.open_txt);

        txtTitle.setVisibility(View.INVISIBLE);
        txtCopy.setVisibility(View.INVISIBLE);
        txtOpen.setVisibility(View.INVISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(new String[]{"From Camera", "From Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                vibrate();
                switch (which) {
                    case 0:
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 50);
                        }
                        scanner.setVisibility(View.VISIBLE);
                        scanner.decodeContinuous(new BarcodeCallback() {
                            @Override
                            public void barcodeResult(BarcodeResult result) {
                                if (result != null && result.getText() != null) {
                                    scanner.pause();
                                    resultManage(result.getText());
                                }
                            }
                        });
                        scanner.resume();
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 100);
                        dialog.dismiss();
                }
            }
        });
        builder.show();

        return root;
    }

    @Override
    public void onDestroy() {
        scanner.pause();
        scanner.setVisibility(View.INVISIBLE);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        scanner.pause();
        scanner.setVisibility(View.INVISIBLE);
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            System.out.println("Parsing");
            Uri uri = data.getData();
            String contents = null;
            Result result = null;
            try {
                Bitmap bMap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                bMap = Bitmap.createScaledBitmap(bMap, 400, 400, false);

                int[] dataArr = new int[bMap.getWidth() * bMap.getHeight()];
                bMap.getPixels(dataArr, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

                LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), dataArr);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                Reader reader = new MultiFormatReader();
                result = reader.decode(bitmap);
                contents = result.getText();
            } catch (Exception e) {
                e.printStackTrace();
            }

            resultManage(contents);
        }
    }

    private void vibrate() {
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(50);
        }
    }

    public void resultManage(String result) {
        txtTitle.setVisibility(View.VISIBLE);
        if (result != null) {
            txtResult.setText(result);
            txtCopy.setVisibility(View.VISIBLE);
        } else {
            txtResult.setText("No QR or Bar Code Found");
        }
        scanner.setVisibility(View.INVISIBLE);

        vibrate();

        if (result != null) {
            txtCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrate();

                    Snackbar snackbar = Snackbar.make(getView(), "Copied To Clipboard", Snackbar.LENGTH_SHORT);
                    View snackView = snackbar.getView();
                    snackView.setBackgroundColor(Color.parseColor("#0088FF"));
                    TextView textView = snackView.findViewById(R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();

                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("scanned_text", result);
                    clipboard.setPrimaryClip(clip);
                }
            });

            if (URLUtil.isValidUrl(result)) {
                txtOpen.setVisibility(View.VISIBLE);

                txtOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vibrate();

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result));
                        startActivity(browserIntent);
                    }
                });
            }
        }
    }
}