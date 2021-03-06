package com.voicerecorder.axet.audiolibrary.encoders;

import android.content.Context;
import android.media.AudioFormat;
import android.os.Build;

import com.github.axet.audiolibrary.R;
import com.voicerecorder.axet.audiolibrary.app.RawSamples;
import com.voicerecorder.axet.audiolibrary.app.Sound;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Arrays;

public class Factory {

    public static int getBitrate(int hz) {
        if (hz < 16000)
            return 32000;
        else if (hz < 44100)
            return 64000;
        else
            return 128000;
    }

    public static CharSequence[] getEncodingTexts(Context context) {
        String[] aa = context.getResources().getStringArray(R.array.encodings_text);
        ArrayList<String> ll = new ArrayList<>(Arrays.asList(aa));
        ll.add("." + FormatFLAC.EXT);
        if (Build.VERSION.SDK_INT >= 18)
            ll.add("." + FormatM4A.EXT);
//        if (Build.VERSION.SDK_INT >= 16)
//            ll.add("."+FormatMKA_AAC.EXT);
        if (!FormatOGG.supported(context)) {
            for (String s : ll) {
                if (s.startsWith("." + FormatOGG.EXT)) {
                    ll.remove(s);
                    break;
                }
            }
        }
        if (FormatMP3.supported(context))
            ll.add("." + FormatMP3.EXT);
        if (Build.VERSION.SDK_INT >= 23) { // Android 6.0 (has ogg/opus support) https://en.wikipedia.org/wiki/Opus_(audio_format)
            if (FormatOPUS_OGG.supported(context))
                ll.add("." + FormatOPUS.EXT);
        } else if (Build.VERSION.SDK_INT >= 21) { // android 5.0 (has mka/opus support only)
            if (FormatOPUS_MKA.supported(context))
                ll.add("." + FormatOPUS.EXT);
        }
        return ll.toArray(new String[]{});
    }

    public static String[] getEncodingValues(Context context) {
        String[] aa = context.getResources().getStringArray(R.array.encodings_values);
        ArrayList<String> ll = new ArrayList<>(Arrays.asList(aa));
        ll.add(FormatFLAC.EXT);
        if (Build.VERSION.SDK_INT >= 18)
            ll.add(FormatM4A.EXT);
//        if (Build.VERSION.SDK_INT >= 16)
//            ll.add(FormatMKA_AAC.EXT);
        if (!FormatOGG.supported(context))
            ll.remove(FormatOGG.EXT);
        if (FormatMP3.supported(context))
            ll.add(FormatMP3.EXT);
        if (Build.VERSION.SDK_INT >= 23) { // Android 6.0 (has ogg/opus support) https://en.wikipedia.org/wiki/Opus_(audio_format)
            if (FormatOPUS_OGG.supported(context))
                ll.add(FormatOPUS.EXT);
        } else if (Build.VERSION.SDK_INT >= 21) { // android 5.0 (has mka/opus support only)
            if (FormatOPUS_MKA.supported(context))
                ll.add(FormatOPUS.EXT);
        }
        return ll.toArray(new String[]{});
    }

    public static Encoder getEncoder(Context context, String ext, RawSamples.Info info, FileDescriptor out) {
        if (ext.equals(FormatWAV.EXT))
            return new FormatWAV(info, out);
        if (ext.equals(Format3GP.EXT))
            return new Format3GP(context, Format3GP.CONTENTTYPE_3GPP, info, out);
        if (ext.equals(FormatM4A.EXT))
            return new FormatM4A(context, info, out);
        if (ext.equals(FormatMKA_AAC.EXT))
            return new FormatMKA_AAC(info, out);
        if (ext.equals(FormatOGG.EXT))
            return new FormatOGG(context, info, out);
        if (ext.equals(FormatMP3.EXT))
            return new FormatMP3(context, info, out);
        if (ext.equals(FormatFLAC.EXT))
            return new FormatFLAC(info, out);
        if (ext.equals(FormatOPUS.EXT)) {
            if (Build.VERSION.SDK_INT >= 23) // Android 6.0 (has ogg/opus support) https://en.wikipedia.org/wiki/Opus_(audio_format)
                return new FormatOPUS_OGG(context, info, out); // android6+ supports ogg/opus
            else if (Build.VERSION.SDK_INT >= 21) // android 5.0 (has mka/opus support only)
                return new FormatOPUS_MKA(context, info, out); // android6+ supports ogg/opus
        }
        return null;
    }

    public static long getEncoderRate(String ext, int rate) {
        if (ext.equals(FormatM4A.EXT)) {
            long y1 = 365723; // one minute sample 16000Hz
            long x1 = 16000; // at 16000
            long y2 = 493743; // one minute sample
            long x2 = 44000; // at 44000
            long x = rate;
            long y = (x - x1) * (y2 - y1) / (x2 - x1) + y1;
            return y / 60;
        }

        if (ext.equals(FormatMKA_AAC.EXT)) { // same codec as m4a, but different container
            long y1 = 365723; // one minute sample 16000Hz
            long x1 = 16000; // at 16000
            long y2 = 493743; // one minute sample
            long x2 = 44000; // at 44000
            long x = rate;
            long y = (x - x1) * (y2 - y1) / (x2 - x1) + y1;
            return y / 60;
        }

        if (ext.equals(FormatOGG.EXT)) {
            long y1 = 174892; // one minute sample 16000Hz
            long x1 = 16000; // at 16000
            long y2 = 405565; // one minute sample
            long x2 = 44000; // at 44000
            long x = rate;
            long y = (x - x1) * (y2 - y1) / (x2 - x1) + y1;
            return y / 60;
        }

        if (ext.equals(FormatMP3.EXT)) {
            long y1 = 376344; // one minute sample 16000Hz
            long x1 = 16000; // at 16000
            long y2 = 464437; // one minute sample
            long x2 = 44000; // at 44000
            long x = rate;
            long y = (x - x1) * (y2 - y1) / (x2 - x1) + y1;
            return y / 60;
        }

        if (ext.equals(FormatFLAC.EXT)) {
            long y1 = 1060832; // one minute sample 16000Hz
            long x1 = 16000; // at 16000
            long y2 = 1296766; // one minute sample
            long x2 = 44000; // at 44000
            long x = rate;
            long y = (x - x1) * (y2 - y1) / (x2 - x1) + y1;
            return y / 60;
        }

        if (ext.equals(FormatOPUS.EXT)) {
            long y1 = 202787; // one minute sample 16000Hz
            long x1 = 16000; // at 16000
            long y2 = 319120; // one minute sample
            long x2 = 44000; // at 44000
            long x = rate;
            long y = (x - x1) * (y2 - y1) / (x2 - x1) + y1;
            return y / 60;
        }

        if (ext.startsWith(Format3GP.EXT)) {
            long y1 = 119481; // one minute sample 16000Hz
            long x1 = 16000; // at 16000
            long y2 = 119481; // one minute sample
            long x2 = 44000; // at 44000
            long x = rate;
            long y = (x - x1) * (y2 - y1) / (x2 - x1) + y1;
            return y / 60;
        }

        if (ext.startsWith("aac")) {
            long y1 = 104276; // one minute sample 16000Hz
            long x1 = 16000; // at 16000
            long y2 = 104276; // one minute sample
            long x2 = 44000; // at 44000
            long x = rate;
            long y = (x - x1) * (y2 - y1) / (x2 - x1) + y1;
            return y / 60;
        }

        // default raw
        int c = Sound.DEFAULT_AUDIOFORMAT == AudioFormat.ENCODING_PCM_16BIT ? 2 : 1;
        return c * rate;
    }
}
