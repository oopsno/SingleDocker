package org.bedlab.ros.docks;

import Data.Result;
import Recognizer.RawGoogleRecognizer;
import Recognizer.StandardRecognizer;
import org.bedlab.ros.docks.callable.DocksCallable;
import org.bedlab.ros.docks.callable.StreamRecognizer;
import org.xml.sax.SAXException;

import javax.sound.sampled.AudioInputStream;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

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

    public static String execute(Configuration configuration) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<StandardRecognizer> consr = configuration.recognizer.getConstructor(configuration.recognizerArgTypes);
        Constructor<StandardRecognizer> consp = configuration.postProcessor.getConstructor(configuration.postProcessorArgTypes);
        StandardRecognizer r = consr.newInstance(configuration.recognizerArgs);
        StandardRecognizer p = consp.newInstance(configuration.postProcessorArgs);
        Result result = r.recognizeFromFile(configuration.inputPath);
        return p.recognizeFromResult(result).getBestResult();
    }

    public static String execute(String path) throws ParserConfigurationException, SAXException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (path.endsWith(".xml")) {
            return execute(ConfigurationFactory.newConfigure(path));
        } else {
            throw new IllegalArgumentException(path + ": Unknown file type");
        }
    }

    public static String execute(String jarPath, String className) throws ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException {
        return execute(CallableLoader.load(jarPath, className));
    }
}
