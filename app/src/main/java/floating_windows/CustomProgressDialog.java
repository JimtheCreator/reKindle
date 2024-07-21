package floating_windows;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import devydev.mirror.net.R;

public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newprogressbar);

        TextView text = (TextView) findViewById(R.id.analyze);
        text.setText("Initializing...");

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    // Add any additional methods or customization as needed
}


