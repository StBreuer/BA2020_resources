#include "rulewerk_clingo_Clingo.h"
#include "ControlClingo.h"
#include "memory"

static ControlClingo *controlClingo;

//TODO optimise it
std::vector<const char*> toCharVector(JNIEnv *env, jobjectArray *stringArray){
    int length = env->GetArrayLength(*stringArray);
    std::vector<const char*> cStrVector;
    cStrVector.reserve(length);
    for (int i = 0; i < length; i++){
        jstring string = (jstring) (env->GetObjectArrayElement(*stringArray, i));
        const char *rule = env->GetStringUTFChars(string,0);
        cStrVector.push_back(rule);
    }

    return cStrVector;
}

JNIEXPORT void JNICALL Java_rulewerk_clingo_Clingo_startSolver(JNIEnv *,jobject jobj){
    controlClingo = new ControlClingo();

}

JNIEXPORT void JNICALL Java_rulewerk_clingo_Clingo_stopSolver(JNIEnv *, jobject){
    controlClingo->statistics();
    delete controlClingo;
}

JNIEXPORT void JNICALL Java_rulewerk_clingo_Clingo_initPart
        (JNIEnv *env, jobject jObj, jstring jPart){
    const char *cPart = env->GetStringUTFChars(jPart, nullptr);
    controlClingo->setPart(cPart);
    //env->ReleaseStringUTFChars(jPart, cPart);
}
JNIEXPORT jstring JNICALL Java_rulewerk_clingo_Clingo_getPart
        (JNIEnv *, jobject, jstring){
    /*jstring NewStringUTF(JNIEnv *env, const char *bytes);
    char *buf =(char*)malloc(10);
*/
    return nullptr;
}

JNIEXPORT void JNICALL Java_rulewerk_clingo_Clingo_addRule(JNIEnv *env,jobject jobj , jstring str){
    const char *cStr;
    env->ReleaseStringUTFChars(str, cStr);
    controlClingo->addRule(cStr);
}
JNIEXPORT void JNICALL Java_rulewerk_clingo_Clingo_addRules(JNIEnv *env ,jobject jobj, jobjectArray jRules){
    //TODO maybe optimise to not copy it
    std::vector<const char*> vector = toCharVector(env, &jRules);
    for (const auto &rule : vector){

        //std::cout << "no string conversion problems" << std::endl;
        //std::cout << rule << std::endl;
        controlClingo->addRule(rule);
    }

}

JNIEXPORT void JNICALL Java_rulewerk_clingo_Clingo_ground(JNIEnv *env,jobject jobj){
    controlClingo->ground();
}

JNIEXPORT void JNICALL Java_rulewerk_clingo_Clingo_solve(JNIEnv *env,jobject obj){
    controlClingo->solve();
}

JNIEXPORT void JNICALL Java_rulewerk_clingo_Clingo_printModel(JNIEnv *env,jobject jobj, jclass cls){
    controlClingo->statistics();
}


//TODO optimise damn is this messy
JNIEXPORT jobject JNICALL Java_rulewerk_clingo_Clingo_termQuery(JNIEnv *env, jobject jobj, jstring predicateName){
    //TODO keep track of deletion
    jboolean isCopy = false;
    const char *cStr = env->GetStringUTFChars(predicateName, &isCopy);
    //std::shared_ptr<const char> cStr(env->GetStringUTFChars(predicateName, &isCopy));

    Clingo::SymbolVector *symbols = controlClingo->getSymbols();
    //REMOVE

    auto *queryTermIterator = new QueryTermIterator(cStr, symbols);

    //Clingo::Model const &model = controlClingo->getSolveHandle()->model();

    //QueryTermIterator *queryTermIterator = new QueryTermIterator(cStr, &model);
    //TODO cleanup
    //QueryTermIterator *queryTermIterator = new QueryTermIterator(cStr, &model);

    jclass jcls = env->FindClass("rulewerk/clingo/TermQueryResultIterator");


    jmethodID mID = env->GetMethodID(jcls, "<init>", "(J)V");

    jobject jObject = env->NewObject(jcls, mID, (jlong)queryTermIterator);


    //TODO muss ich mich drum kümmern
    /*if (isCopy == JNI_TRUE) {
        env->ReleaseStringUTFChars(predicateName, cStr);
    }*/


    return jObject;

}

