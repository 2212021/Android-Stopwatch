package app.stopwatch;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    AppCompatActivity activity = this;
    util.threads.Timer timer;
    int[] time = {0,0,0};
    int STATUS_RUNNING = 1;
    int STATUS_STOPPED = 0;
    int STATUS_RESET = -1;
    int status = STATUS_RESET;
    Button button;
    TextView timeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeText = findViewById(R.id.timetext);
        button = findViewById(R.id.button);
        button.setText(getString(R.string.start));
         //centiseconds,seconds,minutes
        class Timer extends util.threads.Timer {
            public Timer(AppCompatActivity activity) {
                super(activity);
            }

            @Override
            protected void updateAction() {
                if (status == STATUS_RUNNING) {
                    try {
                        time[0] += 1;
                        if (time[0]==100) {
                            time[1] += 1;
                            time[0] = 0;
                        }
                        else if (time[1]==60) {
                            time[2] += 1;
                            time[1] = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                setTimeString(time[2],time[1],time[0]);
            }

        }
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (status == STATUS_RESET) {
                  status = STATUS_RUNNING;
                  button.setText(getString(R.string.stop));
                  timer = new Timer(activity);
                  timer.start(10);
              }
              else if (status == STATUS_RUNNING) {
                  status = STATUS_STOPPED;
                  button.setText(getString(R.string.reset));
                  timer.stop();
              }
              else {
                  status = STATUS_RESET;
                  button.setText(getString(R.string.start));
                  setTimeString(0,0,0);
              }
          }
        });
    }

    private void setTimeString(int min, int sec, int centisec) {
        String[] tStr = new String[3];
        tStr[0] = (centisec<10 ? getString(R.string.zero):"") + centisec;
        tStr[1] = (sec<10 ? getString(R.string.zero):"") + sec;
        tStr[2] = (min<10 ? getString(R.string.zero):"") + min;
        String tStrConcatenated = tStr[2]+":"+tStr[1]+":"+tStr[0];
        timeText.setText(tStrConcatenated);
    }

}