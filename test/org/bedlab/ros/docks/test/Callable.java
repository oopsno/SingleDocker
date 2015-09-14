package org.bedlab.ros.docks.test;

import org.bedlab.ros.docks.Execute;
import org.bedlab.ros.docks.example.GoogleSphinxSentences;

public class Callable {
    public static void main(String[] args) {
        Execute.execute(new GoogleSphinxSentences());
    }
}
