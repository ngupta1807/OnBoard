package menuapp.activity.util;

import menuapp.activity.R;
import menuapp.activity.service.LoadLan;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

public class HandlerDemo extends Activity
{
  ProgressBar bar;
  Handler handler = new Handler()
  {
    @Override
    public void handleMessage(Message msg)
    {
      bar.incrementProgressBy(5);
    }
  };
  boolean isRunning = false;

  @Override
  public void onCreate(Bundle icicle)
  {
    super.onCreate(icicle);
    setContentView(R.layout.progresbar);
    bar = (ProgressBar) findViewById(R.id.progress);
  }

  public void onStart()
  {
    super.onStart();
    bar.setProgress(0);

    Thread background = new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          for (int i = 0; i < 20 && isRunning; i++)
          {
            Thread.sleep(1000);
            handler.sendMessage(handler.obtainMessage());
          }
        }
        catch (Throwable t)
        {
          // just end the background thread
        }
      }
    });

    isRunning = true;
    background.start();
  }

  public void onStop()
  {
    super.onStop();
    isRunning = false;
  
  }
}