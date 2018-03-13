package com.yzx.bangbang.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yzx.bangbang.Interface.network.IMain;
import com.yzx.bangbang.model.User;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.NetWork.Retro;
import com.yzx.bangbang.utils.Params;
import com.yzx.bangbang.utils.SpUtil;
import com.yzx.bangbang.utils.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import model.Assignment;

//按照事件发生的顺序给函数排序，顺着看函数名就能知道大概做了什么。舒服
public class NewAssignment extends RxAppCompatActivity implements View.OnClickListener {
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.new_asm_layout);
        ButterKnife.bind(this);
        //setMargin();
    }

    private void pickLocalImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private SimpleDraweeView getImageView() {
        if (num_images >= vector_image_view.length) return null;
        return vector_image_view[num_images++];
    }

    //返回获得的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            String[] prj = {MediaStore.Images.Media.DATA};
            String path = null;
            if (data == null) return;
            Cursor cursor = getContentResolver().query(data.getData(), prj, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                }
            } else return;
            if (num_images > 2) {
                toast("最多只能添加3张图片");
            } else {
                addImage(path);
                paths.add(path);
            }
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addImage(String path) {
        if (path == null) return;
        if (num_images == 0)
            image_container.setVisibility(View.VISIBLE);
        SimpleDraweeView draweeView = getImageView();
        if (draweeView != null) {
            draweeView.setImageURI(Uri.fromFile(new File(path)));
        }
    }

    @OnClick({R.id.main_fr_new_asm_cancel, R.id.main_fr_new_asm_local_image, R.id.new_asm_send})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fr_new_asm_cancel:
                finish();
                break;
            case R.id.main_fr_new_asm_local_image:
                pickLocalImage();
                break;
            case R.id.new_asm_send:
                check();
                break;
        }
    }


    private void check() {
        String inputTitle = title.getText().toString();
        if (inputTitle.equals("")) {
            toast("标题不能为空");
            return;
        }
        String inputPrice = price.getText().toString();
        if (inputPrice.equals("")) {
            toast("价格不能为空");
            return;
        }
        if (!checkPrice(inputPrice)) {
            toast("价格格式错误");
            return;
        }
        btn_send.setClickable(false);
        toast("开始上传");
        upload();
    }

    private boolean checkPrice(String s) {
        boolean full_stop = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 48 && c <= 57)
                continue;
            if (c == '.' && !full_stop) full_stop = true;
            else return false;
        }
        int p = Integer.valueOf(s);
        if (p <= 0) return false;
        return true;
    }

    private void upload() {
        User user = (User) SpUtil.getObject(SpUtil.USER);
        double latitude = 0, longitude = 0;
        if (isLocationEnable.isChecked()) {
            LatLng latLng = (LatLng) SpUtil.getObject(SpUtil.LATLNG);
            if (latLng != null) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
        }
        Assignment assignment = new Assignment(0,
                title.getText().toString(),
                content.getText().toString(),
                user.getId(), user.getName(),
                util.getDate(),
                Float.valueOf(price.getText().toString()),
                0,
                num_images,
                0,
                latitude,
                longitude);
        List<File> image_files = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++)
            image_files.add(new File(paths.get(i)));
        Retro.inst().create(IMain.class)
                .new_assignment(new Gson().toJson(assignment), Retro.files2MultipartBody(image_files))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Integer>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(this::res);
    }

    private void res(int code) {
        if (code == 1) {
            toast("上传成功");
            setResult(Main.RESULT_UPLOAD_SUCCESS);
            finish();
        } else toast("上传失败");
    }

//    private List<File> addImageFile() {
//        List<File> images = new ArrayList<>();
//        for (int i = 0; i < num_images; i++) {
//            try {
//                ByteArrayOutputStream baos = util.compress(util.loadBitmapFromView(vector_image_view[i]), 200);
//                File file = new File(Params.TEMP_DIR + util.getRandomString(8) + ".png");
//                InputStream in = new ByteArrayInputStream(baos.toByteArray());
//                FileOutputStream fos = new FileOutputStream(file);
//                byte[] buffer = new byte[1024];
//                int len;
//                while ((len = in.read(buffer)) > 0) {
//                    fos.write(buffer, 0, len);
//                }
//                images.add(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return images;
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //?
    private void setMargin() {
        //RelativeLayout mainLayout = (RelativeLayout) findViewById(R.parent_id.new_asm_layout);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.new_asm_status_bar);
        //magic
        if (frameLayout == null)
            return;
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        param.height = Params.statusHeight;
        frameLayout.setLayoutParams(param);
    }

    private void toast(String toShow) {
        Toast.makeText(NewAssignment.this, toShow, Toast.LENGTH_SHORT).show();
    }

    private List<String> paths = new ArrayList<>();
    private int num_images = 0;
    @BindView(R.id.main_fr_new_asm_checkbox)
    CheckBox isLocationEnable;
    @BindView(R.id.new_asm_title)
    EditText title;
    @BindView(R.id.new_asm_content)
    EditText content;
    @BindView(R.id.new_asm_price)
    EditText price;
    @BindView(R.id.new_asm_images_container)
    ViewGroup image_container;
    @BindView(R.id.new_asm_image0)
    SimpleDraweeView image0;
    @BindView(R.id.new_asm_image0)
    SimpleDraweeView image1;
    @BindView(R.id.new_asm_image0)
    SimpleDraweeView image2;
    SimpleDraweeView[] vector_image_view = {image0, image1, image2};
    @BindView(R.id.new_asm_send)
    View btn_send;
}