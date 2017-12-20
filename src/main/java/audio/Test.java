package audio;
import audio.AudioMaster;
import audio.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test
{
    public static void main(final String[] arg) throws IOException, InterruptedException
    {
        AudioMaster var = AudioMaster.getInstance();
        var.init();
        var.setListenerData(0, 0, 0);

        final int buffer = var.loadSound("audio/bounce.wav");
        final Source source = new Source();
        source.setLooping(true);
        source.play(buffer);

        float xPos = 8;
        source.setPosition(xPos, 0, 2);

        // Added non-blocking input to neatly finish close the app.
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String c = "";
        while (!"q".equals(c))
        {
            if (bufferedReader.ready())
            {
                c = bufferedReader.readLine();
            }
            xPos -= 0.03f;
            if (xPos < -8f)
            {
                xPos = 8;
            }
            source.setPosition(xPos, 0, 2);
            Thread.sleep(10);
        }

        source.delete();
        var.cleanUp();
    }
}