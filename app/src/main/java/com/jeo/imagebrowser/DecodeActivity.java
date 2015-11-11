package com.jeo.imagebrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jeo.imagebrowser.util.SimpleCrypto;

import java.io.UnsupportedEncodingException;

/**
 * Created by 志文 on 2015/11/11 0011.
 */
public class DecodeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Button btn = new Button(getBaseContext());
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        btn.setLayoutParams(layoutParams);
//        btn.setText("根据序列号生成密钥");
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                debug();
//            }
//        });
//        RelativeLayout contentView = (RelativeLayout) findViewById(R.id.contentView);
//        contentView.addView(btn);


        LayoutInflater factory = LayoutInflater.from(DecodeActivity.this);
        final View textEntryView = factory.inflate(R.layout.decode, null);
        setContentView(textEntryView);
        final EditText keyEdt = (EditText) textEntryView
                .findViewById(R.id.username_edit);
        final EditText monthEdt = (EditText) textEntryView
                .findViewById(R.id.month_edit);
        final EditText passEdt = (EditText) textEntryView
                .findViewById(R.id.password_edit);

        Button generateBtn = (Button)findViewById(R.id.generateBtn);

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String value = keyEdt.getText().toString();
                    final String month = monthEdt.getText().toString();

                    if (TextUtils.isEmpty(value)) {
                        Toast.makeText(getBaseContext(), "请输入序列号！", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (TextUtils.isEmpty(month)) {
                        Toast.makeText(getBaseContext(), "请输入月份大小！", Toast.LENGTH_LONG).show();
                        try {
                            Integer.parseInt(month);
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "月份格式不对！", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    byte[] temp = SimpleCrypto
                            .des3EncodeECB(value
                                    .getBytes("UTF-8"));
                    String message = new String(Base64
                            .encode(temp, Base64.DEFAULT),
                            "UTF-8").trim();

                    byte[] temp1 = SimpleCrypto
                            .des3EncodeECB(month
                                    .getBytes("UTF-8"));
                    String message1 = new String(Base64
                            .encode(temp1, Base64.DEFAULT),
                            "UTF-8").trim();

                    passEdt.setText(message+"@"+message1);

                    ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cbm.setText(message+"@"+message1);

                    Toast.makeText(getBaseContext(), "密钥已经复制到剪贴板", Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void debug() {
        try {

            LayoutInflater factory = LayoutInflater.from(DecodeActivity.this);
            final View textEntryView = factory.inflate(R.layout.decode, null);
            final EditText keyEdt = (EditText) textEntryView
                    .findViewById(R.id.username_edit);
            final EditText monthEdt = (EditText) textEntryView
                    .findViewById(R.id.month_edit);
            final EditText passEdt = (EditText) textEntryView
                    .findViewById(R.id.password_edit);


            AlertDialog dlg = new AlertDialog.Builder(DecodeActivity.this)

                    .setTitle("输入正确的密匙")
                    .setView(textEntryView)
                    .setPositiveButton("生成密钥",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    try {
                                        final String value = keyEdt.getText().toString();
                                        final String month = monthEdt.getText().toString();

                                        if (TextUtils.isEmpty(value)) {
                                            Toast.makeText(getBaseContext(), "请输入序列号！", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        if (TextUtils.isEmpty(month)) {
                                            Toast.makeText(getBaseContext(), "请输入月份大小！", Toast.LENGTH_LONG).show();
                                            try {
                                                Integer.parseInt(month);
                                            } catch (Exception e) {
                                                Toast.makeText(getBaseContext(), "月份格式不对！", Toast.LENGTH_LONG).show();
                                            }
                                            return;
                                        }

                                        byte[] temp = SimpleCrypto
                                                .des3EncodeECB(value
                                                        .getBytes("UTF-8"));
                                        String message = new String(Base64
                                                .encode(temp, Base64.DEFAULT),
                                                "UTF-8").trim();

                                        byte[] temp1 = SimpleCrypto
                                                .des3EncodeECB(month
                                                        .getBytes("UTF-8"));
                                        String message1 = new String(Base64
                                                .encode(temp, Base64.DEFAULT),
                                                "UTF-8").trim();

                                        passEdt.setText(message+"-"+message1);

                                        ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        cbm.setText(message+"-"+message1);

                                        Toast.makeText(getBaseContext(), "密钥已经复制到剪贴板", Toast.LENGTH_LONG).show();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {

                                }
                            }).create();
            dlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
