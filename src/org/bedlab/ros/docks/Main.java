package org.bedlab.ros.docks;

import org.bedlab.ros.docks.callable.DocksCallable;
import org.bedlab.ros.docks.example.GoogleSphinxSentences;

public class Main {
    public static void main(String[] args) {
        DocksCallable callable = new GoogleSphinxSentences();
        System.out.println(Execute.execute(callable));
    }
}
