//
// Created by steffen on 8/16/20.
//

#include "RawTermIterator.h"

RawTermIterator::RawTermIterator(Clingo::SymbolVector *symbolSpan) {
    this->symbols = symbolSpan;
    this->nextArgument = symbolSpan->size();

}

RawTermIterator::~RawTermIterator() {}

bool RawTermIterator::hasNext() {
    if(this->nextArgument > 0){
        return true;
    }
    return false;
}

Clingo::Symbol RawTermIterator::next() {
    if(hasNext()){
        nextArgument - 1;
        return this->symbols->at(nextArgument);
    }
}