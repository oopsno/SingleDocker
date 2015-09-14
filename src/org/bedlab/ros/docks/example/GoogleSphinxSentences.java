package org.bedlab.ros.docks.example;

import PostProcessor.SphinxBasedPostProcessor;
import Recognizer.RawGoogleRecognizer;
import Recognizer.StandardRecognizer;
import org.bedlab.ros.docks.callable.DocksCallable;

import javax.sound.sampled.AudioInputStream;

public class GoogleSphinxSentences implements DocksCallable {
    @Override
    public String getSource() {
        return "data/back_fs_1387386033021_m1.wav";
    }

    @Override
    public AudioInputStream getStream() {
        return null;
    }

    @Override
    public StandardRecognizer getRecognizer() {
        return new RawGoogleRecognizer("AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw");
    }

    @Override
    public StandardRecognizer getPostProcessor() {
        String configBase = "config/elpmaxe/elpmaxe";
        String config = configBase + ".pgrammarsentences.xml";
        String words  = configBase + ".words";
        return new SphinxBasedPostProcessor(
                config,
                words,
                0,
                0,
                0);
    }
}
