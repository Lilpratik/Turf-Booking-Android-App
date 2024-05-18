package com.pratik.turfbooking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;
import com.pratik.turfbooking.models.Booking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TurfDetailActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final int PAYPAL_REQUEST_CODE = 123;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    private Button btnSelectDate;
    private Button btnSelectStartTime;
    private Button btnSelectEndTime;
    private PaymentButtonContainer paymentButtonContainer;
    private FirebaseUser currentUser;
    private DatabaseReference bookingsRef;
    private Calendar selectedDate;
    private int selectedHour;
    private int selectedMinute;
    private int selectedEndHour;
    private int selectedEndMinute;
    private String selectedTime;
    private String selectedEndTime;
    private String name;
    private String turfLocation;
    private Date selectedDateTime;
    private String receiptText;
    private ImageData paypalImageData;
    private ImageData appIconImageData;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turf_details);

        // Request write access permission
        requestWriteAccessPermission();

        // Request location access permission
        requestLocationAccessPermission();

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        bookingsRef = database.getReference("bookings");

        // Initialize current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        paymentButtonContainer = findViewById(R.id.payment_button_container);

        selectedDate = Calendar.getInstance();
        selectedHour = 12;
        selectedMinute = 0;
        selectedEndHour = 13;
        selectedEndMinute = 0;

        // Get intent extras
        name = getIntent().getStringExtra("TURF_NAME");
        turfLocation = getIntent().getStringExtra("TURF_LOCATION");
        String turfImage = getIntent().getStringExtra("TURF_IMAGE");

        Log.d("TurfDetailActivity", "Turf Name: " + name);
        Log.d("TurfDetailActivity", "Selected Date Time: " + selectedDateTime);

        TextView nameTextView = findViewById(R.id.turfNameTextView);
        TextView locationTextView = findViewById(R.id.turfLocationTextView);

        nameTextView.setText(name);
        locationTextView.setText(turfLocation);
        ImageView imageView = findViewById(R.id.turfImageView);
        Glide.with(this).load(turfImage).into(imageView);

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        btnSelectStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(true);
            }
        });

        btnSelectEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(false);
            }
        });

        setupPayPalPayment();

        paymentButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TurfDetailActivity", "Payment button clicked");
                // Perform PayPal payment and turf booking
                if (name != null && selectedDateTime != null) {
                    Log.d("TurfDetailActivity", "Turf Name: " + name);
                    Log.d("TurfDetailActivity", "selectedDateTime: " + selectedDateTime);
                    bookTurf();
                } else {
                    Log.e("TurfDetailActivity", "name or selectedDateTime is null");
                    Toast.makeText(TurfDetailActivity.this, "Error: name or selectedDateTime is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Request write access permission
    private void requestWriteAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Request location access permission
    private void requestLocationAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            // Check if permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with PDF creation
                // Call your method to create the PDF file here
            } else {
                // Permission denied by the user
                // You may handle this case accordingly, e.g., show a message to the user
                Toast.makeText(this, "Permission denied, cannot create PDF file", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateButtonText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        btnSelectDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void showTimePickerDialog(final boolean isStartTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    if (isStartTime) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;
                        updateTimeButtonText(true);
                    } else {
                        selectedEndHour = hourOfDay;
                        selectedEndMinute = minute;
                        updateTimeButtonText(false);
                    }
                },
                isStartTime ? selectedHour : selectedEndHour,
                isStartTime ? selectedMinute : selectedEndMinute,
                false
        );
        timePickerDialog.show();
    }

    private void updateTimeButtonText(boolean isStartTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar timeCalendar = Calendar.getInstance();
        if (isStartTime) {
            timeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            timeCalendar.set(Calendar.MINUTE, selectedMinute);
            selectedTime = sdf.format(timeCalendar.getTime());
            btnSelectStartTime.setText(selectedTime);
        } else {
            timeCalendar.set(Calendar.HOUR_OF_DAY, selectedEndHour);
            timeCalendar.set(Calendar.MINUTE, selectedEndMinute);
            selectedEndTime = sdf.format(timeCalendar.getTime());
            btnSelectEndTime.setText(selectedEndTime);
        }
    }

    private void setupPayPalPayment() {
        paymentButtonContainer.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value("20.00")
                                                        .build()
                                        )
                                        .build()
                        );
                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                                bookTurf();
                            }
                        });
                    }
                }
        );
    }

    private void bookTurf() {
        // Combine selectedDate and selectedTime to create a full timestamp
        Calendar dateTimeCalendar = Calendar.getInstance();
        dateTimeCalendar.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
        dateTimeCalendar.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
        dateTimeCalendar.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH));
        dateTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        dateTimeCalendar.set(Calendar.MINUTE, selectedMinute);

        selectedDateTime = dateTimeCalendar.getTime(); // Update the global variable

        Date currentDateTime = Calendar.getInstance().getTime();

        // Check if selectedDateTime is in the future
        if (selectedDateTime != null && selectedDateTime.after(currentDateTime)) {
            // Check if the slot is available
            checkSlotAvailability(selectedDateTime);
        } else {
            // Selected date and time are in the past or null
            Toast.makeText(this, "Error: Please select a future date and time", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkSlotAvailability(Date selectedDateTime) {
        if (name != null && selectedDateTime != null) { // Ensure name and selectedDateTime are not null
            // Query the database to check if the slot is available
            DatabaseReference turfRef = FirebaseDatabase.getInstance().getReference("footballVenues");
            String dateKey = getDateKey(selectedDateTime);
            String timeKey = getTimeKey(selectedDateTime);
            if (dateKey != null && timeKey != null) { // Ensure dateKey and timeKey are not null
                turfRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String turfId = snapshot.getKey();
                                if (turfId != null) {
                                    turfRef.child(turfId).child("bookedSlots").child(dateKey).child(timeKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // Slot is already booked, inform the user
                                                Toast.makeText(TurfDetailActivity.this, "This slot is already booked. Please select a different time.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Slot is available, proceed with booking
                                                performBooking(selectedDateTime);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle database error
                                            Log.e("TurfDetailActivity", "Database error: " + databaseError.getMessage());
                                        }
                                    });
                                }
                            }
                        } else {
                            Log.e("TurfDetailActivity", "Turf with name " + name + " not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Log.e("TurfDetailActivity", "Database error: " + databaseError.getMessage());
                    }
                });
            } else {
                Log.e("TurfDetailActivity", "dateKey or timeKey is null");
            }
        } else {
            Log.e("TurfDetailActivity", "name or selectedDateTime is null");
        }
    }

    private void performBooking(Date selectedDateTime) {
        Log.d("TurfDetailActivity", "Selected DateTime: " + selectedDateTime);
        Log.d("TurfDetailActivity", "Selected Hour: " + selectedHour);
        Log.d("TurfDetailActivity", "Selected Minute: " + selectedMinute);
        Log.d("TurfDetailActivity", "Selected End Hour: " + selectedEndHour);
        Log.d("TurfDetailActivity", "Selected End Minute: " + selectedEndMinute);

        // Combine selectedDate and selectedTime to create a full timestamp
        Calendar startDateTimeCalendar = Calendar.getInstance();
        startDateTimeCalendar.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
        startDateTimeCalendar.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
        startDateTimeCalendar.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH));
        startDateTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        startDateTimeCalendar.set(Calendar.MINUTE, selectedMinute);

        selectedDateTime = startDateTimeCalendar.getTime(); // Update the global variable

        Calendar endDateTimeCalendar = Calendar.getInstance();
        endDateTimeCalendar.set(Calendar.YEAR, selectedDate.get(Calendar.YEAR));
        endDateTimeCalendar.set(Calendar.MONTH, selectedDate.get(Calendar.MONTH));
        endDateTimeCalendar.set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH));
        endDateTimeCalendar.set(Calendar.HOUR_OF_DAY, selectedEndHour);
        endDateTimeCalendar.set(Calendar.MINUTE, selectedEndMinute);

        selectedEndTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(endDateTimeCalendar.getTime());

        Date currentDateTime = Calendar.getInstance().getTime();

        // Check if selectedDateTime is in the future
        if (selectedDateTime != null && selectedDateTime.after(currentDateTime)) {
            // Check if the slot is available
            saveBookingToDatabase(selectedDateTime);
            generateReceipt(selectedDateTime);
        } else {
            // Selected date and time are in the past or null
            Toast.makeText(this, "Error: Please select a future date and time", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDateKey(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    private String getTimeKey(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    private void saveBookingToDatabase(Date selectedDateTime) {
        // Generate a unique key for the booking entry
        String bookingId = bookingsRef.push().getKey();

        // Check if name is not null
        if (name != null) {
            // Create a Booking object with all the necessary details
            Booking booking = new Booking(currentUser.getUid(), name, selectedDateTime.getTime());

            // Set the value of the booking object under the generated key
            if (bookingId != null) {
                bookingsRef.child(bookingId).setValue(booking)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("TurfDetailActivity", "Booking saved to database");
                            // Generate receipt after booking is saved
                        })
                        .addOnFailureListener(e -> {
                            Log.e("TurfDetailActivity", "Error saving booking to database: " + e.getMessage());
                            e.printStackTrace(); // Print the full stack trace
                        });
            } else {
                Log.e("TurfDetailActivity", "Failed to generate booking ID");
            }
        } else {
            Log.e("TurfDetailActivity", "Turf name is null");
        }
    }

    private void generateReceipt(Date selectedDateTime) {
        // Generate receipt and save as PDF
        // Example code to generate PDF:
        if (selectedDateTime != null) {
            // Generate receipt content using selectedDateTime
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String fullDate = dateFormat.format(selectedDateTime);
            String fullTime = timeFormat.format(selectedDateTime);

            // Merchant information
            String merchantName = "Play Arena Inc.";
            String merchantEmail = "contact@playarena.com";
            String merchantContact = "+91 897-667-1065";

            // Payment gateway information
            String paymentGateway = "PayPal";

            // Support/Contact details
            String supportEmail = "support@playarena.com";
            String supportContact = "+91 897-667-1065";

            String receiptText = "Booking Receipt\n\n";
            receiptText += "Merchant: " + merchantName + "\n";
            receiptText += "Email: " + merchantEmail + "\n";
            receiptText += "Contact: " + merchantContact + "\n\n";
            receiptText += "Turf Name: " + name + "\n";
            receiptText += "Turf Location: " + turfLocation + "\n";
            receiptText += "Date: " + fullDate + "\n";
            receiptText += "Time: " + fullTime + "\n";
            receiptText += "Amount Paid: $20.00\n"; // Assuming a fixed amount for now
            receiptText += "Payment Gateway: " + paymentGateway + "\n\n";
            receiptText += "For support or queries:\n";
            receiptText += "Email: " + supportEmail + "\n";
            receiptText += "Contact: " + supportContact + "\n";
            receiptText += "Thanks for using Play Arena Turf Booking   --Play Arena Inc.";

            // Load PayPal icon as ImageData
            ImageData paypalImageData = null;
            try {
                Context context = getApplicationContext();
                @SuppressLint("ResourceType") InputStream paypalInputStream = context.getResources().openRawResource(R.drawable.paymentsuccess);
                ByteArrayOutputStream paypalOutputStream = new ByteArrayOutputStream();
                byte[] paypalBuffer = new byte[1024];
                int paypalLength;
                while ((paypalLength = paypalInputStream.read(paypalBuffer)) != -1) {
                    paypalOutputStream.write(paypalBuffer, 0, paypalLength);
                }
                paypalImageData = ImageDataFactory.create(paypalOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TurfDetailActivity", "Error loading PayPal icon: " + e.getMessage());
            }

            // Load app icon as ImageData
            ImageData appIconImageData = null;
            try {
                Context context = getApplicationContext();
                @SuppressLint("ResourceType") InputStream appIconInputStream = context.getResources().openRawResource(R.drawable.paypal_icon);
                ByteArrayOutputStream appIconOutputStream = new ByteArrayOutputStream();
                byte[] appIconBuffer = new byte[1024];
                int appIconLength;
                while ((appIconLength = appIconInputStream.read(appIconBuffer)) != -1) {
                    appIconOutputStream.write(appIconBuffer, 0, appIconLength);
                }
                appIconImageData = ImageDataFactory.create(appIconOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TurfDetailActivity", "Error loading app icon: " + e.getMessage());
            }

            // Save receipt as PDF with PayPal icon and app icon
            File pdfFile = createPdfFile(receiptText, paypalImageData, appIconImageData);
            if (pdfFile != null) {
                Log.d("TurfDetailActivity", "PDF file created: " + pdfFile.getAbsolutePath());
                // PDF file created, you can do further operations like sharing or viewing
                // Display a toast message indicating where the receipt is saved
                Toast.makeText(this, "Receipt saved at: " + pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TurfDetailActivity", "PDF file creation failed");
            }
        } else {
            // Handle case where selectedDateTime is null
            Log.e("generateReceipt", "selectedDateTime is null");
            Toast.makeText(this, "Error generating receipt: Selected date and time are null", Toast.LENGTH_SHORT).show();
        }
    }
    private File createPdfFile(String receiptText, ImageData paypalImageData, ImageData appIconImageData) {
        File pdfFile = null;
        try {
            // Create PDF document
            pdfFile = new File(getExternalFilesDir(null), "receipt.pdf");
            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);

            // Add receipt content to the document
            document.add(new Paragraph(receiptText));

            // Add PayPal icon to the document
            if (paypalImageData != null) {
                com.itextpdf.layout.element.Image paypalImage = new com.itextpdf.layout.element.Image(paypalImageData);
                document.add(paypalImage);
            }

            // Close the document
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("TurfDetailActivity", "Error creating PDF file: " + e.getMessage());
        }
        return pdfFile;
    }
}

