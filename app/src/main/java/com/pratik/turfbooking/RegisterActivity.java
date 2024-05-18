package com.pratik.turfbooking;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText, birthDateEditText, otpEditText, phoneNumberEditText, editTextDate;
    private RadioButton maleRadioButton, femaleRadioButton;
    private Button registerButton, pickDateButton, sendOTPButton, verifyOTPButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Calendar selectedDate;
    private MaterialDatePicker<Long> materialDatePicker;
    private String verificationId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Database reference and Authentication
        databaseReference = FirebaseDatabase.getInstance().getReference("/User");
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        nameEditText = findViewById(R.id.editTextText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.againpasswordEditText);
        birthDateEditText = findViewById(R.id.editTextDate);
        maleRadioButton = findViewById(R.id.radioButton);
        femaleRadioButton = findViewById(R.id.radioButton3);
        registerButton = findViewById(R.id.registerButton);
        pickDateButton = findViewById(R.id.pickDateButton);
        otpEditText = findViewById(R.id.otpEditText);
        sendOTPButton = findViewById(R.id.sendOTPButton);
        verifyOTPButton = findViewById(R.id.verifyOTPButton);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        editTextDate = findViewById(R.id.editTextDate);

        // Initialize MaterialDatePicker with DateRangePicker
        materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birth Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        // Set listener to handle selected date
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate.setTimeInMillis(selection);
            updateBirthDateText();
        });

        // Set click listener to show MaterialDatePicker
        pickDateButton.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        // Initialize selectedDate
        selectedDate = Calendar.getInstance();

        // Set click listener for registerButton
        registerButton.setOnClickListener(v -> registerUser());

        // Set click listener for pickDateButton
        pickDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Set click listener for sendOTPButton
        sendOTPButton.setOnClickListener(v -> sendOTP());

        // Set click listener for verifyOTPButton
        verifyOTPButton.setOnClickListener(v -> verifyOTP());
    }

    private void registerUser() {
        // Retrieve user input
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        String gender = maleRadioButton.isChecked() ? "Male" : "Female";

        // Validate input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(birthDate)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password, confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with email and password in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User registered successfully, update UI or perform additional actions
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Store user information in the Realtime Database with push method
                            User user = new User(name, email, phone, password, birthDate, gender);
                            DatabaseReference userReference = databaseReference.push(); // Push to generate a unique ID
                            userReference.setValue(user);

                            // Update this block to save all user information
                            userReference.child("userId").setValue(userId); // Save userId for future reference
                            userReference.child("name").setValue(name);
                            userReference.child("email").setValue(email);
                            userReference.child("phone").setValue(phone);
                            userReference.child("birthDate").setValue(birthDate);
                            userReference.child("gender").setValue(gender);

                            // Show success message
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            // Redirect to another activity
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish(); // Finish this activity to prevent going back
                        }
                    } else {
                        // If registration fails, display a message to the user.
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }





    private boolean isValidBirthDate(String birthDate) {
        return !TextUtils.isEmpty(birthDate);
    }


    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        return password.trim().equals(confirmPassword.trim());
    }
    private boolean isValidPhoneNumber(CharSequence target){
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateBirthDateText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateBirthDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        birthDateEditText.setText(sdf.format(selectedDate.getTime()));
    }

    private void sendOTP() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        sendVerificationCode(phoneNumber);
    }

    private void verifyOTP() {
        String otp = otpEditText.getText().toString().trim();
        // Verify if OTP is correct
        if (TextUtils.isEmpty(otp)) {
            Toast.makeText(RegisterActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
        } else {
            // Perform OTP verification here
            verifyVerificationCode(otp);
        }
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                java.util.concurrent.TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@androidx.annotation.NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Automatically detects and verifies the code without user interaction.
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@androidx.annotation.NonNull com.google.firebase.FirebaseException e) {
                        // Verification failed
                        Toast.makeText(RegisterActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@androidx.annotation.NonNull String s, @androidx.annotation.NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        // OTP code has been sent to the provided number, now the user can enter the code.
                    }
                });
    }

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User signed in successfully
                            FirebaseUser user = task.getResult().getUser();
                            // Show verification success message
                            Toast.makeText(RegisterActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // Sign in failed
                            Toast.makeText(RegisterActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
