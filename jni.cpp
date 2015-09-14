#include <iostream>
#include <algorithm>
#include <cstring>
#include <jni.h>
#include <unistd.h>

using namespace std;

void printJVMException(JNIEnv * env) {
    jthrowable exc = env->ExceptionOccurred();
    if (exc) {
        jclass newExcCls;
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
}

int main(int args, const char * argv[]) {
    char optionString[] = "-Djava.class.path=/Users/nerdwizard/Documents/XDU/Salt/SingleDocker/out/production/SingleDocker/";
    JavaVM * jvm;       /* denotes a Java VM */
    JNIEnv * env;       /* pointer to native method interface */
    JavaVMInitArgs vm_args; /* JDK/JRE 6 VM initialization arguments */
    JavaVMOption * options = new JavaVMOption[1];
    size_t optionStringSize = strlen(optionString) + 1;
    options[0].optionString = new char[optionStringSize];
    copy(optionString, optionString + optionStringSize, options[0].optionString);
    vm_args.version = JNI_VERSION_1_6;
    vm_args.nOptions = 1;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = (jboolean) false;
    /* load and initialize a Java VM, return a JNI interface
     * pointer in env */
    JNI_CreateJavaVM(&jvm, (void **) &env, &vm_args);
    delete   options;
    auto className = "org/bedlab/ros/docks/Execute";
    if (env == nullptr) {
        cerr << "WTF?!" << endl;
        return -1;
    }
    jclass cls = env->FindClass(className);
    if (cls == nullptr) {
        printJVMException(env);
        cerr << className << ": Class Not Found" << endl;
        return -1;
    }
    auto methodName = "execute";
    jmethodID mid = env->GetStaticMethodID(cls, methodName, "(Ljava/lang/String) Ljava/lang/String");
    if (mid == nullptr) {
        printJVMException(env);
        cerr << methodName << ": MethodNot Found" << endl;
        return -1;
    }
    string path = string(getcwd(NULL, 0)) + string(argv[1]);
    cout << "Working on: " << path << endl;
    jstring jpath = env->NewString((const jchar *) path.c_str(), (jsize) path.length());
    jstring ret = (jstring) env->CallStaticObjectMethod(cls, mid, jpath);
    cout << ">> " << ret << endl;
    /* We are done. */
    jvm->DestroyJavaVM();
    return 0;
}
