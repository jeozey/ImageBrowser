package com.jeo.imagebrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jeo.imagebrowser.util.SimpleCrypto;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            String outPut = this.getIntent().getExtras().get(MediaStore.EXTRA_OUTPUT).toString();
//            String outPut = "file:///sdcard/1.jpg";
            if (!TextUtils.isEmpty(outPut)) {
                outPut = outPut.replace("file:///", "");
                mPhotoFile = new File(outPut);
                if (!mPhotoFile.getParentFile().isDirectory() && !mPhotoFile.getParentFile().mkdirs()) {
                    Toast.makeText(getBaseContext(), "文件夹无法创建", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mPhotoFile.exists()) {
                    mPhotoFile.createNewFile();
                }

                try {
                    final String value = DeviceUtil.getUUID(MainActivity.this);
                    byte[] temp = SimpleCrypto.des3EncodeECB(value.getBytes("UTF-8"));
                    String message = new String(Base64.encode(temp, Base64.DEFAULT),
                            "UTF-8").trim();

                    String v = getSharedPreferences("asdf", MODE_PRIVATE).getString(
                            "k", "ff");

                    if (message.equals(v)) {
                        String n = getSharedPreferences("asdf",
                                MODE_PRIVATE).getString("n", "");
                        String t = new String(Base64.decode(n.getBytes(), Base64.DEFAULT));
                        Date old = new SimpleDateFormat("yyyyMMdd").parse(t);
                        Date now = new Date();
                        int mon = getSharedPreferences("asdf",
                                MODE_PRIVATE).getInt("m", 0);

                        long l = now.getTime() - old.getTime();

                        long day = l / (24 * 60 * 60 * 1000);
                        if (day >= mon) {
                            Toast.makeText(getBaseContext(), "密钥过期,请重新注册!", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            Log.e("test", "day:" + (mon - day));
                        }
                        start();
                    } else {
                        debug();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "运行出错", Toast.LENGTH_LONG).show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("test", "###3:" + e.getLocalizedMessage());
            Toast.makeText(getBaseContext(), "出错了", Toast.LENGTH_LONG).show();
        }
    }

    private void debug() {
        try {
            final String value = DeviceUtil.getUUID(MainActivity.this);

            LayoutInflater factory = LayoutInflater.from(MainActivity.this);
            final View textEntryView = factory.inflate(R.layout.debug, null);
            EditText keyEdt = (EditText) textEntryView
                    .findViewById(R.id.username_edit);
            keyEdt.setText(value);

            AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)

                    .setTitle("输入正确的密匙")
                    .setView(textEntryView)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    try {
                                        byte[] temp = SimpleCrypto
                                                .des3EncodeECB(value
                                                        .getBytes("UTF-8"));
                                        String message = new String(Base64
                                                .encode(temp, Base64.DEFAULT),
                                                "UTF-8").trim();
                                        EditText valEdt = (EditText) textEntryView
                                                .findViewById(R.id.password_edit);
                                        String v = valEdt.getText().toString()
                                                .trim();
                                        if (TextUtils.isEmpty(v) || v.indexOf("@") == -1) {
                                            Toast.makeText(MainActivity.this,
                                                    "激活失败,请确认密钥正确!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        String[] vs = v.split("@");
                                        if (vs.length != 2) {
                                            Toast.makeText(MainActivity.this,
                                                    "激活失败,请确认密钥正确!", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        byte[] b = Base64.decode(vs[1].getBytes(), Base64.DEFAULT);
                                        byte[] c = SimpleCrypto.ees3DecodeECB(b);
                                        String mon = new String(c);
                                        try {
                                            Integer.valueOf(mon);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.this,
                                                    "激活失败,请确认密钥正确!", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        if (!TextUtils.isEmpty(v)
                                                && !TextUtils.isEmpty(message)
                                                && message.equalsIgnoreCase(vs[0])) {
                                            Toast.makeText(MainActivity.this,
                                                    "激活成功,时间为" + mon + "天!", Toast.LENGTH_LONG).show();
                                            getSharedPreferences("asdf",
                                                    MODE_PRIVATE).edit()
                                                    .putString("k", vs[0]).commit();

                                            String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
                                            getSharedPreferences("asdf",
                                                    MODE_PRIVATE).edit()
                                                    .putString("n", new String(Base64.encode(date.getBytes(), Base64.DEFAULT))).commit();


                                            getSharedPreferences("asdf",
                                                    MODE_PRIVATE).edit()
                                                    .putInt("m", Integer.valueOf(mon)).commit();


                                        } else {
                                            Toast.makeText(MainActivity.this,
                                                    "激活失败,请确认密钥正确!", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                    )
                    .

                            setNegativeButton("取消",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {

                                        }
                                    }

                            ).

                            create();

            dlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void start() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 9999);
    }

    private File mPhotoFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9999 && resultCode == RESULT_OK && null != data) {
            try {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Log.e("Fuck", "picturePath:" + picturePath);
                File file = new File(picturePath);
                fileChannelCopy(file, mPhotoFile);

                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "获取图片出错", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */

    public void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri,
                proj,                 // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null);                 // Order-by clause (ascending by name)

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
