package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.gallaryapp.privacyvault.photoeditor.databinding.DialogBrightnessBinding;

public class DialogBrightness extends AppCompatDialogFragment {

    private DialogBrightnessBinding binding;
    private boolean isPermissionRequested = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        binding = DialogBrightnessBinding.inflate(requireActivity().getLayoutInflater());
        builder.setView(binding.getRoot());

        setupBrightnessControls();
        binding.brtClose.setOnClickListener(view -> dismiss());

        return builder.create();
    }

    private void setupBrightnessControls() {
        Context context = requireContext();
        ContentResolver contentResolver = context.getContentResolver();

        int brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0);
        binding.brtNumber.setText(String.valueOf(brightness));
        binding.brtSeekbar.setProgress(brightness);

        binding.brtSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) updateBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateBrightness(int brightness) {
        Context context = requireContext();
        ContentResolver contentResolver = context.getContentResolver();

        if (Settings.System.canWrite(context)) {
            binding.brtNumber.setText(String.valueOf(brightness));
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        } else {
            if (!isPermissionRequested) {
                isPermissionRequested = true;
                requestWriteSettingsPermission();
            }
        }
    }

    private void requestWriteSettingsPermission() {
        Context context = requireContext();
        Toast.makeText(context, "Enable write settings for brightness control", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        startActivity(intent);
    }
}
