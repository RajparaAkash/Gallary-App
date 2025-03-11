package com.gallaryapp.privacyvault.photoeditor.MyUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.gallaryapp.privacyvault.photoeditor.databinding.DialogVolumeBinding;

public class DialogVolume extends AppCompatDialogFragment {

    private AudioManager audioManager;
    private DialogVolumeBinding binding;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        binding = DialogVolumeBinding.inflate(requireActivity().getLayoutInflater());
        builder.setView(binding.getRoot());

        requireActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setupVolumeControls();

        binding.volClose.setOnClickListener(view -> dismiss());

        return builder.create();
    }


    private void setupVolumeControls() {
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        binding.volSeekbar.setMax(maxVolume);
        binding.volSeekbar.setProgress(currentVolume);
        updateVolumeDisplay(currentVolume, maxVolume);

        binding.volSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    updateVolumeDisplay(progress, maxVolume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateVolumeDisplay(int currentVolume, int maxVolume) {
        int volumePercentage = (int) Math.ceil((double) currentVolume / maxVolume * 100);
        binding.volNumber.setText(volumePercentage + "%");
    }
}
