package com.x_creations.qrandbarcode_generatorandscanner.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.x_creations.qrandbarcode_generatorandscanner.BuildConfig;
import com.x_creations.qrandbarcode_generatorandscanner.MainActivity;
import com.x_creations.qrandbarcode_generatorandscanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import java.io.File;
import java.io.FileOutputStream;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    EditText edtTxt;
    TextView warning, qrCopy, barCopy;
    ImageView qrCode, barCode;
    Vibrator vibrator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        if (!MainActivity.VALUE) {
            vibrate();
        } else {
            MainActivity.VALUE = false;
        }

        edtTxt = root.findViewById(R.id.edt_txt);
        warning = root.findViewById(R.id.warning);
        qrCode = root.findViewById(R.id.qr_code);
        barCode = root.findViewById(R.id.bar_code);
        qrCopy = root.findViewById(R.id.copy_qr);
        barCopy = root.findViewById(R.id.copy_bar);

        warning.setVisibility(View.INVISIBLE);
        qrCopy.setVisibility(View.INVISIBLE);
        barCopy.setVisibility(View.INVISIBLE);

        edtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String sText = edtTxt.getText().toString().trim();
                if (sText.length() > 0 && sText.length() < 81) {
                    warning.setVisibility(View.INVISIBLE);
                    MultiFormatWriter writer = new MultiFormatWriter();
                    try {
                        BitMatrix matrixQR = writer.encode(sText, BarcodeFormat.QR_CODE, 500, 500);
                        BitMatrix matrixBAR = writer.encode(sText, BarcodeFormat.CODE_128, 600, 255);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        Bitmap bitmapQR = encoder.createBitmap(matrixQR);
                        Bitmap bitmapBAR = encoder.createBitmap(matrixBAR);

                        qrCopy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                vibrate();

                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());

                                File file = new File(getActivity().getExternalCacheDir() + "/" + "QR Code" + ".png");
                                Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
                                Intent intent;

                                try {
                                    FileOutputStream stream = new FileOutputStream(file);
                                    bitmapQR.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    stream.flush();
                                    stream.close();
                                    intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("image/png");
                                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                } catch (Exception e) {
                                    throw new RuntimeException();
                                }

                                startActivity(Intent.createChooser(intent, "Share Image"));
                            }
                        });

                        barCopy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                vibrate();

                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());

                                File file = new File(getActivity().getExternalCacheDir() + "/" + "BAR Code" + ".png");
                                Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
                                Intent intent;

                                try {
                                    FileOutputStream stream = new FileOutputStream(file);
                                    bitmapBAR.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    stream.flush();
                                    stream.close();
                                    intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("image/png");
                                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                } catch (Exception e) {
                                    throw new RuntimeException();
                                }

                                startActivity(Intent.createChooser(intent, "Share Image"));
                            }
                        });

                        qrCode.setImageBitmap(bitmapQR);
                        barCode.setImageBitmap(bitmapBAR);
                        qrCode.setVisibility(View.VISIBLE);
                        barCode.setVisibility(View.VISIBLE);
                        qrCopy.setVisibility(View.VISIBLE);
                        barCopy.setVisibility(View.VISIBLE);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                } else {
                    warning.setVisibility(View.VISIBLE);
                    qrCode.setVisibility(View.INVISIBLE);
                    barCode.setVisibility(View.INVISIBLE);
                    qrCopy.setVisibility(View.INVISIBLE);
                    barCopy.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return root;
    }

    private void vibrate() {
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(50);
        }
    }
}