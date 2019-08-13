package lecheng.com.furible_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

/* 此页为引导页,跟连接电子秤无任何关系 */
public class GuideActivity extends AppCompatActivity {
    Button btnSkip;
    FrameLayout flSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        btnSkip = findViewById(R.id.btnSkip);
        flSkip = findViewById(R.id.flSkip);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GuideActivity.this, ScanActivity.class));
                finish();
            }
        });
        flSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flSkip.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.vGuide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation aa = new AlphaAnimation(0f, 1.0f);
                aa.setDuration(1000);
                aa.setFillAfter(true);
                flSkip.startAnimation(aa);
                flSkip.setVisibility(View.VISIBLE);
            }
        });
    }
}
