package com.gallaryapp.privacyvault.photoeditor.BgRemover;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.util.ArrayList;
import java.util.List;


public class Config {

    public static int DURATION = 500;

    public static void removeBg(final Activity activity, Bitmap bitmap, final OnRemoved onRemoved) {
        if (bitmap == null) {
            displayFailure(activity);
            return;
        }
        MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(new MLImageSegmentationSetting.Factory().setAnalyzerType(0).create()).asyncAnalyseFrame(new MLFrame.Creator().setBitmap(bitmap).create()).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object obj) {
                removeBgSuccess(onRemoved, activity, (MLImageSegmentation) obj);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exc) {
                Config.displayFailure(activity);
            }
        });
    }

    public static void removeBgSuccess(OnRemoved onRemoved, Activity activity, MLImageSegmentation mLImageSegmentation) {
        if (mLImageSegmentation != null) {
            onRemoved.onRemoved(mLImageSegmentation);
        } else {
            displayFailure(activity);
        }
    }

    public static void displayFailure(Activity activity) {
        Toast.makeText(activity, "Human detection is failed. Please try again.", Toast.LENGTH_SHORT).show();
    }

    public static List<OptionTool> eTool(Context context, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        TypedArray obtainTypedArray = context.getResources().obtainTypedArray(i);
        String[] stringArray = context.getResources().getStringArray(i2);
        for (int i3 = 0; i3 < obtainTypedArray.length(); i3++) {
            OptionTool optionTool = new OptionTool();
            optionTool.image = obtainTypedArray.getResourceId(i3, -1);
            optionTool.name = stringArray[i3];
            arrayList.add(optionTool);
        }
        return arrayList;
    }
}