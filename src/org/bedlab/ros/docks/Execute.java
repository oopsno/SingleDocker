package org.bedlab.ros.docks;

import Data.Result;
import Recognizer.RawGoogleRecognizer;
import Recognizer.StandardRecognizer;
import org.bedlab.ros.docks.callable.DocksCallable;
import org.bedlab.ros.docks.callable.StreamRecognizer;

import javax.sound.sampled.AudioInputStream;

public class Execute {
    private static Result recognize(StandardRecognizer recognizer, String path) {
        return recognizer.recognizeFromFile(path);
    }
    private static Result recognize(StandardRecognizer recognizer, AudioInputStream stream) {
        if (recognizer instanceof RawGoogleRecognizer) {
            RawGoogleRecognizer rawGoogleRecognizer = (RawGoogleRecognizer) recognizer;
            return rawGoogleRecognizer.recognize(stream);
        } else if (recognizer instanceof StreamRecognizer) {
            StreamRecognizer streamRecognizer = (StreamRecognizer) recognizer;
            return streamRecognizer.recognize(stream);
        } else {
            throw new IllegalArgumentException("TNS");
        }
    }

    public static String execute(DocksCallable callable) {
        StandardRecognizer recognizer    = callable.getRecognizer();
        StandardRecognizer postProcessor = callable.getPostProcessor();
        String             filePath      = callable.getSource();
        AudioInputStream   stream        = callable.getStream();
        if (recognizer == null) {
            throw new IllegalArgumentException("Recognizer is null");
        }
        if (postProcessor == null) {
            throw new IllegalArgumentException("PostProcessor is null");
        }
        Result result;
        if (filePath != null) {
            result = recognize(recognizer, filePath);
        } else if (stream != null) {
            result = recognize(recognizer, stream);
        } else {
            throw new IllegalArgumentException("NI");
        }
        return postProcessor.recognizeFromResult(result).getBestResult();
    }
}
