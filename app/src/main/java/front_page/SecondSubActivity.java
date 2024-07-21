package front_page;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import devydev.mirror.net.R;
import subscription.SubscriptionPageActivity;

public class SecondSubActivity extends AppCompatActivity {

    RelativeLayout monthly_button, yearly_button, done;
    String fireball = "Yearly";
    TextView change;
    String customerID;
    String pushID;
    String EphericalKey;
    String ClientSecret;
    String PUBLISH_KEY, SECRET_KEY;
    PaymentSheet paymentSheet;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mSubscriptionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_sub);

        change = findViewById(R.id.change);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        PUBLISH_KEY = getResources().getString(R.string.PUBLISHABLE_KEY);
        SECRET_KEY = getResources().getString(R.string.SECRET_KEY);


        PaymentConfiguration.init(SecondSubActivity.this, PUBLISH_KEY);
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

//        keepRunning();

        done = findViewById(R.id.register_login);
        yearly_button = findViewById(R.id.yearly_button);
        monthly_button = findViewById(R.id.monthly_button);

        monthly_button.setOnClickListener(v -> {
            fireball = "Monthly";
            change.setText(R.string.only_2_99_month);
            yearly_button.setBackground(ContextCompat.getDrawable(SecondSubActivity.this, R.drawable.fade_white));
            monthly_button.setBackground(ContextCompat.getDrawable(SecondSubActivity.this, R.drawable.stateselected));
        });
        yearly_button.setOnClickListener(v -> {
            fireball = "Yearly";
            change.setText(R.string.only_1_99_month);
            yearly_button.setBackground(ContextCompat.getDrawable(SecondSubActivity.this, R.drawable.stateselected));
            monthly_button.setBackground(ContextCompat.getDrawable(SecondSubActivity.this, R.drawable.fade_white));
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handler.removeCallbacks(runnable);
//                Log.d("TAG", "" + fireball);
//                if (fireball.equals("Monthly")) {
//                    paymentFlow();
//                } else if (fireball.equals("Yearly")) {
//                    paymentFlow();
//                }

                LoadStripe();
            }
        });
    }


    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            flagServer();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast toast = Toast.makeText(this, "Payment was cancelled", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
//            handler.post(runnable);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast toast = Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
//            handler.post(runnable);
        }
    }


    private void LoadStripe(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.newprogressbar, (ViewGroup) findViewById(R.id.alert_layout_root));

//        ImageView image = (ImageView) layout.findViewById(R.id.image);
//        image.setImageResource(R.drawable.android); // replace with your own image resource
//
        TextView text = (TextView) layout.findViewById(R.id.analyze);
        text.setText("Initializing payment...");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false) // This makes the AlertDialog non-dismissable
                .create();

        alertDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        alertDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            getEphericalKey(customerID);
                        } catch (JSONException e) {
                            Log.e("PAYMENT", "Error parsing customerID response: " + e.getMessage());
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SecondSubActivity.this);
        requestQueue.add(stringRequest);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (customerID != null && EphericalKey != null){
                    alertDialog.dismiss();
                    paymentFlow();
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }, 5000);
    }

    private void flagServer() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.newprogressbar, (ViewGroup) findViewById(R.id.alert_layout_root));

//        ImageView image = (ImageView) layout.findViewById(R.id.image);
//        image.setImageResource(R.drawable.android); // replace with your own image resource
//
//        TextView text = (TextView) layout.findViewById(R.id.text);
//        text.setText("Hello! This is a custom AlertDialog!");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(false) // This makes the AlertDialog non-dismissable
                .create();

        // Perform null check on the window
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        alertDialog.show();

        sendDataToServer(alertDialog);
    }

    private void sendDataToServer(AlertDialog alertDialog) {
        if (fireball.equals("Monthly")){
            long thirtyOneDaysMillis = TimeUnit.DAYS.toMillis(33);
            long addition = System.currentTimeMillis()+thirtyOneDaysMillis;

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Ispaid", true);
            hashMap.put("Timepaid", String.valueOf(System.currentTimeMillis()));
            hashMap.put("Package", fireball);
            hashMap.put("Expiring", String.valueOf(addition));

            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            finish();
                            alertDialog.dismiss();

                            HashMap<String, Object> objectHashMap = new HashMap<>();
                            objectHashMap.put("Userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            objectHashMap.put("Timepaid", String.valueOf(System.currentTimeMillis()));
                            objectHashMap.put("Package", fireball);
                            objectHashMap.put("Expiring", String.valueOf(addition));

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Admin")
                                    .child("Mapped Vision");

                            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(objectHashMap);
                            showToast();
                        }
                    }).addOnFailureListener(e -> {

                    });
        }else {
            long thirtyOneDaysMillis = TimeUnit.DAYS.toMillis(367);
            long addition = System.currentTimeMillis()+thirtyOneDaysMillis;

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Ispaid", true);
            hashMap.put("Timepaid", String.valueOf(System.currentTimeMillis()));
            hashMap.put("Package", fireball);
            hashMap.put("Expiring", String.valueOf(addition));

            FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            finish();
                            alertDialog.dismiss();

                            HashMap<String, Object> objectHashMap = new HashMap<>();
                            objectHashMap.put("Userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            objectHashMap.put("Timepaid", String.valueOf(System.currentTimeMillis()));
                            objectHashMap.put("Package", fireball);
                            objectHashMap.put("Expiring", String.valueOf(addition));

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Admin")
                                    .child("Mapped Vision");

                            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(objectHashMap);
                            showToast();
                        }
                    }).addOnFailureListener(e -> {

                    });
        }

    }

    private void showToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.status_payment, (ViewGroup) findViewById(R.id.toast_layout_root));

//        ImageView image = (ImageView) layout.findViewById(R.id.image);
//        image.setImageResource(R.drawable.android);
//
//        TextView text = (TextView) layout.findViewById(R.id.text);
//        text.setText("Hello! This is a custom toast!");

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void getEphericalKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");

                            Log.d("PAYMENT", "Epherical " + EphericalKey);
                            if (fireball.equals("Monthly")){
                                getMonthlyClientSecretKey(customerID, EphericalKey);
                            }else {
                                getYearlyClientSecretKey(customerID, EphericalKey);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                header.put("Stripe-Version", "2023-10-16");

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SecondSubActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getMonthlyClientSecretKey(String customerID, String Epherical) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");

                            Log.d("PAYMENT", "ClientSecret " + ClientSecret);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String mm = getResources().getString(R.string.monthly_forstripe);
                Map<String, String> params = new HashMap<>();
                Log.d("TAG", "" + fireball);

                params.put("customer", customerID);
                params.put("amount", mm);
                params.put("currency", "gbp");
                params.put("automatic_payment_methods[enabled]", "true");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SecondSubActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getYearlyClientSecretKey(String customerID, String ephericalKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");

                            Log.d("PAYMENT", "ClientSecret " + ClientSecret);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);

                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String year = getResources().getString(R.string.yearly_forstripe);
                Map<String, String> params = new HashMap<>();
                Log.d("TAG", "" + fireball);

                params.put("customer", customerID);
                params.put("amount", year);
                params.put("currency", "gbp");
                params.put("automatic_payment_methods[enabled]", "true");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SecondSubActivity.this);
        requestQueue.add(stringRequest);


    }

    private void paymentFlow() {
        if (customerID != null && EphericalKey != null) {
            paymentSheet.presentWithPaymentIntent(
                    ClientSecret, new PaymentSheet.Configuration("reKindle",
                            new PaymentSheet.CustomerConfiguration(
                                    customerID,
                                    EphericalKey
                            )));
        } else {
            // Handle the case where customerID or EphericalKey is null.
            // Log an error, throw an exception, or take appropriate action.
            Log.e("PAYMENT", "customerID or EphericalKey is null");
            // You might want to show a toast or display an error message to the user.
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}