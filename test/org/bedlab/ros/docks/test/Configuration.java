package org.bedlab.ros.docks.test;

import org.bedlab.ros.docks.Execute;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Configuration {
    public static void main() throws NoSuchMethodException, IllegalAccessException, SAXException, InstantiationException, ParserConfigurationException, InvocationTargetException, IOException {
        Execute.execute("example.xml");
    }
}
