package com.packt.androidconcurrency.chapter6.example1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.packt.androidconcurrency.LaunchActivity;
import com.packt.androidconcurrency.R;

public class PrimesActivity extends Activity {

    private static PrimesHandler handler = new PrimesHandler();
    private static Messenger messenger = new Messenger(handler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ch5_example1_layout);

        Button go = (Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView input = (TextView) findViewById(R.id.prime_to_find);
                String value = input.getText().toString();
                if (value.matches("[1-9]+[0-9]*")) {
                    triggerIntentService(Integer.parseInt(value));
                } else {
                    Toast.makeText(PrimesActivity.this, "not a number!", 5000).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.attach((TextView)findViewById(R.id.result));
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.detach();
    }

    private void triggerIntentService(int primeToFind) {
        Intent intent = new Intent(this, PrimesAsyncTaskIntentService.class);
        intent.putExtra(PrimesAsyncTaskIntentService.PARAM, primeToFind);
        intent.putExtra(PrimesAsyncTaskIntentService.MESSENGER, messenger);
        startService(intent);
    }

    private static class PrimesHandler extends Handler {
        private TextView view;

        @Override
        public void handleMessage(Message message) {
            if (message.what == PrimesAsyncTaskIntentService.RESULT) {
                if (view != null)
                    view.setText(message.obj.toString());
                else
                    Log.i(LaunchActivity.TAG, "received a result, ignoring because we're detached");
            } else if (message.what == PrimesAsyncTaskIntentService.INVALID) {
                if (view != null)
                    view.setText("Invalid request");
            } else {
              super.handleMessage(message);
            }
        }

        public void attach(TextView view) {
            this.view = view;
        }

        public void detach() {
            this.view = null;
        }
    }
}