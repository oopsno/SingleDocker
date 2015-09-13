package org.bedlab.ros.docks.callable;

import Data.Result;

import javax.sound.sampled.AudioInputStream;

public interface StreamRecognizer {
    Result recognize(AudioInputStream stream);
}
