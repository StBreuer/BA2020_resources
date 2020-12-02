#include "rulewerk_clingo_TermQueryResultIterator.h"
#include "QueryTermIterator.h"

JNIEXPORT jboolean JNICALL Java_rulewerk_clingo_TermQueryResultIterator_hasNext(JNIEnv *env, jobject jobj, jlong ref){
    QueryTermIterator *queryTermIterator = (QueryTermIterator *) ref;
    if (queryTermIterator == NULL){
        return (jboolean) false;
    }
    return (jboolean) queryTermIterator->hasNext();
}

JNIEXPORT jobjectArray JNICALL Java_rulewerk_clingo_TermQueryResultIterator_next(JNIEnv *env, jobject jobj, jlong ref){
    QueryTermIterator *iterator = (QueryTermIterator *)ref;
    if (iterator == NULL){
        return NULL;
    }
    size_t tupleSize = iterator->getTupleSize();
    const char* *cRes = iterator->next();
    // declare StringArray
    jobjectArray jRes = (jobjectArray) env->NewObjectArray(tupleSize, env->FindClass("java/lang/String"),env->NewStringUTF(""));

    for (int i = 0; i < tupleSize; i++) {
        env->SetObjectArrayElement(jRes, i, env->NewStringUTF(cRes[i]));
    }
    //std::cout << "end of next" << std::endl;
    delete cRes;
    return jRes;
}

JNIEXPORT void JNICALL Java_rulewerk_clingo_TermQueryResultIterator_remove (JNIEnv *, jobject, jlong ref){
    QueryTermIterator *iterator = (QueryTermIterator *)ref;
    delete iterator;
}