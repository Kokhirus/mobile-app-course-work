package ru.kokhirus.m_course_work;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    GoogleSignInClient googleSignInClient;
    ShapeableImageView imageView;
    TextView name, mail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = (ShapeableImageView) view.findViewById(R.id.profileImage);
        name = (TextView) view.findViewById(R.id.nameTV);
        mail = (TextView) view.findViewById(R.id.mailTV);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();

            Glide.with(getActivity()).load(Objects.requireNonNull(acct).getPhotoUrl()).into(imageView);
            Globals.USER_DISPLAY_NAME = acct.getDisplayName();
            name.setText(Globals.USER_DISPLAY_NAME);
            mail.setText(acct.getEmail());
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        MaterialButton signOut = (MaterialButton) view.findViewById(R.id.signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInClient mGoogle = GoogleSignIn.getClient(getActivity() , gso);
                mGoogle.signOut();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "Signed out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}