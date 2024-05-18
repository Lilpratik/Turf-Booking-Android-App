package com.pratik.turfbooking;

import android.app.Application;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PayPalCheckout.setConfig(new CheckoutConfig(
                this,
                "AUQ50B-4Xddox1Di7LCyxaDmYveRSIp1uWRksGBbiaSFhpp4PPJzn9x1_kHdIrcjDrL5_g0wv_cpAK8y",
                Environment.SANDBOX,
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                "com.pratik.turfbooking://paypalpay"
        ));
    }
}
