package com.yzx.bangbang.Fragment.Main;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yzx.bangbang.activity.Main;
import com.yzx.bangbang.adapter.main.MainAdapter;
import com.yzx.bangbang.adapter.main.MainDistanceSpinnerAdapter;
import com.yzx.bangbang.adapter.main.MainSortSpinnerAdapter;
import com.yzx.bangbang.R;
import com.yzx.bangbang.utils.NetWork.UniversalImageDownloader;
import com.yzx.bangbang.view.mainView.ListItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 我既讨厌写注释，又讨厌看别人不写注释的代码。好了我现在开始看一年前的代码了。
 */
public class FrMain extends Fragment implements View.OnClickListener, ListItem.Listener {
    public static final int IMAGE_DOWNLOAD_COMPLETE = 10;
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_PRICE_ASC = 1;
    public static final int MODE_PRICE_DESC = 2;

    int sort_type = 0;
    public static int distance = 0;

    @BindView(R.id.main_list)
    RecyclerView recyclerView;
    @BindView(R.id.fr_main_spinner_distance)
    Spinner spinner_distance;
    @BindView(R.id.fr_main_spinner_sort)
    Spinner spinner_sort;
    UniversalImageDownloader downloader;
    private View v;
    Map<Integer, View> Id_View;
    public static boolean hasScrollToTop;
    MainAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_main, container, false);
        // v.getViewTreeObserver().addOnGlobalLayoutListener(() -> ((Main) getActivity()).setScrollView(scrollView));
        init();
        return v;
    }

    public void init() {
        ButterKnife.bind(this, v);
        adapter = new MainAdapter(getActivity());
        downloader = new UniversalImageDownloader(getActivity());
        initSpinner();
        //DownloadAssignmentText();
        context().getListener().getAssignment((r) -> {
            adapter.setData(r);
            recyclerView.setAdapter(adapter);
        }, MODE_DEFAULT);
    }

    private void initSpinner() {
        spinner_distance.setAdapter(new MainDistanceSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.distances)));
        spinner_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (distance == i) return;
                distance = i;
                Refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_sort.setAdapter(new MainSortSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.sort)));
        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sort_type == i) return;
                sort_type = i;
                Refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    float[][] dis_scope = {{0, 1000f}, {1000f, 2000f}, {2000f, 5000f}, {5000f, 200000f}};

    public void Refresh() {
//        if (sort_type == 0) DownloadAssignmentText();//默认
//        if (sort_type == 1) DownloadAssignmentTextOrderByPriceAsc();
//        if (sort_type == 2) DownloadAssignmentTextOrderByPriceDesc();
        //adapter.downloader.onRefresh();
        //usingPtr = true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onDestroy() {
        clear();
        super.onDestroy();
    }

    public FrMain inst() {
        return FrMain.this;
    }

    private void clear() {
        if (Id_View != null)
            Id_View.clear();
        Id_View = null;
    }

    public FrMainHandler frMainHandler = new FrMainHandler(this);

    public static class FrMainHandler extends Handler {
        private WeakReference<FrMain> ref;

        public FrMainHandler(FrMain fragment) {
            ref = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FrMain inst = ref.get();
            if (inst != null)
                inst.handleMsg(msg);
        }
    }

    private void handleMsg(Message msg) {
        switch (msg.what) {
            case 1:
                //processInputStream();
                break;
            case IMAGE_DOWNLOAD_COMPLETE:
                LoadImage(msg.getData());
                break;
            default:
                break;
        }
    }


    private void LoadImage(Bundle data) {
        int[] imageId = {R.id.main_item_image0, R.id.main_item_image1, R.id.main_item_image2};
        SimpleDraweeView draweeView;
        View parent = Id_View.get(data.getInt("asm_id"));
        if (parent == null)
            return;
        draweeView = parent.findViewById(imageId[data.getInt("pos")]);
        if (draweeView == null)
            return;
        draweeView.setImageURI(Uri.fromFile(new File(data.getString("path"))));
    }

    @Override
    public void onItemTouched(int position) {
        //Main inst = mainRef.get();
    }

    public Main context() {
        return (Main) super.getActivity();
    }
}
