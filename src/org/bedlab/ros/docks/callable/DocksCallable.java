package org.bedlab.ros.docks.callable;

import Recognizer.StandardRecognizer;

import javax.sound.sampled.AudioInputStream;

public interface DocksCallable {
    String             getSource();
    AudioInputStream   getStream();
    StandardRecognizer getRecognizer();
    StandardRecognizer getPostProcessor();
}
