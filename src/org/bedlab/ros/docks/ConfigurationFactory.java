package org.bedlab.ros.docks;


import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;
import com.sun.tools.javac.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class ConfigurationFactory {
    private enum Tag {
        CLASS("class"),
        INPUT("input"),
        RECOGNIZER("recognizer"),
        POSTPROCESSOR("postprocessor"),
        TYPE("type"),
        ARGUMENT("argument");

        private String literal;

        Tag(String literal) {
            this.literal = literal;
        }

        @Override
        public String toString() {
            return literal;
        }
    }

    private static class Triple<T, Y, U> {
        public T fst;
        public Y snd;
        public U trd;

        public Triple(T fst, Y snd, U trd) {
            this.fst = fst;
            this.snd = snd;
            this.trd = trd;
        }
    }

    private static HashMap<String, Class> builtinClasses = null;
    private static HashMap<String, Function<String, Object>> argParsers = null;
    private static HashMap<String, Class> argTypes = null;

    private static void init() {
        builtinClasses = new HashMap<>();
        builtinClasses.put("SphinxRecognizer",          Recognizer.SphinxRecognizer.class);
        builtinClasses.put("RawGoogleRecognizer",       Recognizer.RawGoogleRecognizer.class);
        builtinClasses.put("SphinxBasedPostProcessor",  PostProcessor.SphinxBasedPostProcessor.class);
        builtinClasses.put("WordlistPostProcessor",     PostProcessor.WordlistPostProcessor.class);
        builtinClasses.put("SentencelistPostProcessor", PostProcessor.WordlistPostProcessor.class);

        argParsers = new HashMap<>();
        argParsers.put("int",    Integer::parseInt);
        argParsers.put("double", Double::parseDouble);
        argParsers.put("float",  Float::parseFloat);
        argParsers.put("string", (s) -> s);

        argTypes = new HashMap<>();
        argTypes.put("int",    int.class);
        argTypes.put("double", double.class);
        argTypes.put("float",  float.class);
        argTypes.put("string", String.class);
    }

    private static boolean matchTag(Node node, Tag tag) {
        String nodename  = node.getNodeName();
        String localname = node.getLocalName();
        String tagname   = String.valueOf(tag);
        return (nodename != null && nodename.equals(tagname))
                || (localname != null && localname.equals(tagname));
    }
    private static Pair<Class, Object> parseArgument(Node node) {
        if (matchTag(node, Tag.ARGUMENT)) {
            String typename = ((DeferredElementNSImpl) node).getAttribute(String.valueOf(Tag.TYPE));
            String literal  = node.getTextContent();
            Class type = argTypes.getOrDefault(typename, null);
            Object arg = argParsers.getOrDefault(typename, (x) -> null).apply(literal);
            return new Pair<>(type, arg);
        } else {
            return null;
        }
    }

    static class DocksConfigureErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }

    private static Document readXML(String path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilder.setErrorHandler(new DocksConfigureErrorHandler());
        return documentBuilder.parse(new File(path));
    }

    private static Triple<Class, Object[], Class[]> getField(NodeList nodeList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int length = nodeList.getLength();
        Class clazz = null;
        ArrayList<Object> args  = new ArrayList<>();
        ArrayList<Class>  types = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < length; i++) {
           Node node =  nodeList.item(i);
            // class
            if (matchTag(node, Tag.CLASS)) {
                clazz = builtinClasses.getOrDefault(node.getTextContent(), null);
            } else {
            // argument or junk
                Pair<Class, Object> result = parseArgument(node);
                if (result == null)
                    continue;
                types.add(result.fst);
                args.add(result.snd);
            }
        }

        Object[] argsArray = new Object[args.size()];
        Class[] typesArray = new Class[types.size()];
        argsArray = args.toArray(argsArray);
        typesArray = types.toArray(typesArray);

        return new Triple<>(clazz, argsArray, typesArray);
    }

    public static Configuration newConfigure(String path) throws IOException, SAXException, ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (builtinClasses == null) {
            init();
        }
        Document document = readXML(path);
        Node recognizer = document.getElementsByTagName(String.valueOf(Tag.RECOGNIZER)).item(0);
        Node postProcessor = document.getElementsByTagName(String.valueOf(Tag.POSTPROCESSOR)).item(0);
        Node input = document.getElementsByTagName(String.valueOf(Tag.INPUT)).item(0);
        NodeList recognizerChildren = recognizer.getChildNodes();
        NodeList postProcessorChildren = postProcessor.getChildNodes();
        Triple<Class, Object[], Class[]> rpair = getField(recognizerChildren);
        Triple<Class, Object[], Class[]> ppair = getField(postProcessorChildren);
        Configuration configuration = new Configuration();
        configuration.inputPath             = input.getTextContent();
        configuration.recognizer            = rpair.fst;
        configuration.recognizerArgs        = rpair.snd;
        configuration.recognizerArgTypes    = rpair.trd;
        configuration.postProcessor         = ppair.fst;
        configuration.postProcessorArgs     = ppair.snd;
        configuration.postProcessorArgTypes = ppair.trd;
        return configuration;
    }
}
