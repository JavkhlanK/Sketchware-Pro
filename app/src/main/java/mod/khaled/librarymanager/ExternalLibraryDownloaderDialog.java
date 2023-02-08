package mod.khaled.librarymanager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.sketchware.remod.R;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FilePathUtil;
import mod.agus.jcoderz.lib.FileUtil;

public class ExternalLibraryDownloaderDialog extends DialogFragment {

    private final ExternalLibraryDownloader externalLibraryDownloader = new ExternalLibraryDownloader();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ExpandedDialogFragment);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (getActivity() instanceof DialogDismissedListener)
            ((DialogDismissedListener) getActivity()).onDismissDownloaderDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.common_dialog_layout, container, false);
        root.setPadding(0, 0, 0, 0);

        ((ImageView) root.findViewById(R.id.common_dialog_icon)).setImageResource(R.drawable.language_download_96);
        ((TextView) root.findViewById(R.id.common_dialog_tv_title)).setText("Download New Library");

        TextView startButton = root.findViewById(R.id.common_dialog_ok_button);
        TextView stopButton = root.findViewById(R.id.common_dialog_cancel_button);
        TextView cancelButton = root.findViewById(R.id.common_dialog_default_button);

        cancelButton.setText("Cancel");
        startButton.setText("Next");
        stopButton.setText("Stop");
        stopButton.setVisibility(View.GONE);

        cancelButton.setOnClickListener((v) -> dismiss());

        View contentLayout = inflater.inflate(R.layout.new_external_library_dialog, container, false);
        ((LinearLayout) root.findViewById(R.id.common_dialog_content)).addView(contentLayout);

        TextInputLayout gradleImplementationInput = contentLayout.findViewById(R.id.gradleImplementationInput);
        LinearLayout libraryDetailsView = contentLayout.findViewById(R.id.libraryDetailsView);
        TextInputLayout libraryNameInput = contentLayout.findViewById(R.id.libraryNameInput);
        TextInputLayout libraryPkgInput = contentLayout.findViewById(R.id.libraryPkgInput);

        LinearLayout libraryDownloadProgressView = contentLayout.findViewById(R.id.libraryDownloadProgressView);
        ProgressBar libraryDownloadProgressBar = libraryDownloadProgressView.findViewById(R.id.libraryDownloadProgressBar);
        TextView libraryDownloadProgressText = libraryDownloadProgressView.findViewById(R.id.libraryDownloadProgressText);
        TextView libraryDownloadMessage = libraryDownloadProgressView.findViewById(R.id.libraryDownloadMessage);

        startButton.setOnClickListener((v) -> {
            String libraryPkg = parseGradleImplementation(gradleImplementationInput.getEditText().getText().toString());
            String libraryName = parseLibraryName(libraryPkg);


            if (libraryPkg.isEmpty()) {
                gradleImplementationInput.setError("Parsing of gradle implementation failed.");
                return;
            }

            if (libraryDetailsView.getVisibility() != View.VISIBLE) {
                libraryDetailsView.setVisibility(View.VISIBLE);
                ((View) gradleImplementationInput.getParent()).setVisibility(View.GONE);
                libraryNameInput.getEditText().setText(libraryName);
                libraryPkgInput.getEditText().setText(libraryPkg);

                ExternalLibraryItem externalLibraryItem = new ExternalLibraryItem(libraryNameInput.getEditText().getText().toString(),
                        libraryPkgInput.getEditText().getText().toString());

                if (FileUtil.isExistFile(FilePathUtil.getExternalLibraryDir(externalLibraryItem.getLibraryPkg()))) {
                    SketchwareUtil.toast("This library already exist in the list. Re-downloading will remove/overwrite the existing.");
                    startButton.setText("Re-Download");
                } else startButton.setText("Download");
                return;
            }

            final ExternalLibraryItem externalLibraryItem = new ExternalLibraryItem(libraryNameInput.getEditText().getText().toString(),
                    libraryPkgInput.getEditText().getText().toString());


            if (libraryDetailsView.getVisibility() == View.VISIBLE && !startButton.getText().equals("Save")) {

                externalLibraryDownloader.
                        startDownloadingLibrary(externalLibraryItem, new ExternalLibraryDownloader.DownloadStatusListener() {
                            @Override
                            public void onDownloadComplete(ExternalLibraryItem libraryItem) {
                                startButton.setText("Save");
                                startButton.setVisibility(View.VISIBLE);
                                stopButton.setVisibility(View.GONE);
                                libraryNameInput.setEnabled(true);

                                //Canceling now should also delete downloaded files
                                cancelButton.setOnClickListener((_v) -> {
                                    externalLibraryItem.deleteLibraryFromStorage();
                                    dismiss();
                                });
                            }

                            @Override
                            public void onError(String errMessage) {
                                SketchwareUtil.toastError(errMessage);
                                startButton.setText("Retry");
                                startButton.setVisibility(View.VISIBLE);
                                stopButton.setVisibility(View.GONE);
                                libraryDownloadProgressView.setVisibility(View.GONE);
                                libraryNameInput.setEnabled(true);
                                libraryPkgInput.setEnabled(true);
                            }

                            @Override
                            public void onProgressChange(int newProgress, String newMessage) {
                                libraryDownloadProgressBar.setProgress(newProgress);
                                libraryDownloadProgressText.setText(String.valueOf(newProgress).concat("%"));
                                if (newMessage != null)
                                    libraryDownloadMessage.setText(newMessage);
                            }
                        });


                stopButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.GONE);
                libraryNameInput.setEnabled(false);
                libraryPkgInput.setEnabled(false);
                libraryDownloadProgressView.setVisibility(View.VISIBLE);
                libraryDownloadMessage.setText("Starting.....");
                libraryDownloadProgressBar.setProgress(0);
                libraryDownloadProgressText.setText("0%");
                return;
            }

            if (libraryDownloadProgressView.getVisibility() == View.VISIBLE && startButton.getText().equals("Save")) {
                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Compiling library...");
                progressDialog.show();
                externalLibraryDownloader.saveLibraryToDisk(requireActivity(), externalLibraryItem, () -> {
                    progressDialog.dismiss();
                    SketchwareUtil.toast("Library " + externalLibraryItem.getLibraryPkg()
                            + " saved successfully with the following name: " + externalLibraryItem.getLibraryName());
                    dismiss();
                });
            }
        });

        stopButton.setOnClickListener((v) -> {
            externalLibraryDownloader.cancelDownloadingLibrary();
            startButton.setText("Start");
            startButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.GONE);
            libraryNameInput.setEnabled(true);
            libraryPkgInput.setEnabled(true);
            libraryDownloadProgressView.setVisibility(View.GONE);
        });

        cancelButton.setOnClickListener((v) -> {
            externalLibraryDownloader.cancelDownloadingLibrary();
            dismiss();
        });

        return root;
    }

    private String parseGradleImplementation(String input) {
        return input.trim(); //TODO
    }

    private String parseLibraryName(String input) {
        return ExternalLibraryItem.generateLibName(input);
    }

    public interface DialogDismissedListener {
        void onDismissDownloaderDialog();
    }
}
