package com.iset.tp7.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.tp7.R;

public class MyDialogFragment extends DialogFragment {

    public interface OnTeacherAddedListener {
        void onTeacherAdded(String name, String email);
    }

    private OnTeacherAddedListener listener;

    public void setListener(OnTeacherAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.activity_my_dialog_fragment, null, false);
        builder.setView(dialogView);

        //recuperer edit text du dialog view
        final EditText nom = (EditText) dialogView.findViewById(R.id.edit_text);
        final EditText email = (EditText) dialogView.findViewById(R.id.email);

        builder.setTitle("Ajouter Nouveau Enseignant")
                .setMessage("Donner nom et email de l'enseignant")
                .setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Action à effectuer lors de la validation
                        String name = nom.getText().toString();
                        String emailText = email.getText().toString();
                        if (!name.isEmpty() && !emailText.isEmpty()) {
                            listener.onTeacherAdded(name, emailText);
                            // Ajouter l'enseignant à la liste (implémentation à adapter)
//                            Teacher newTeacher = new Teacher(name, emailText);
//                            ((MainActivity) requireActivity()).addTeacher(newTeacher);
                        }
                    }
                })
                .setNegativeButton("Annuler", (dialog, whichButton) -> dialog.dismiss());
        return builder.create();
    }
}