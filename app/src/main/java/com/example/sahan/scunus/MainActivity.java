package com.example.sahan.scunus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private Button senderView;
    private Button receiverView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senderView = (Button) findViewById(R.id.sender_button);
        senderView.setOnClickListener(this);

        receiverView = (Button) findViewById(R.id.receiver_button);
        receiverView.setOnClickListener(this);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.receiver_button){
            Intent i = new Intent(this, ReceiverActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, SenderActivity.class);
            startActivity(i);
        }
    }
}
