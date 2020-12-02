#include "clingo.hh"
//
// Created by steffen on 8/16/20.
//

#ifndef CLINGO_IN_RULEWERK_RAWTERMITERATOR_H
#define CLINGO_IN_RULEWERK_RAWTERMITERATOR_H


class RawTermIterator {
private:
    //TODO maybe error
    Clingo::SymbolVector *symbols;
    size_t nextArgument = 0;

public:
    RawTermIterator(Clingo::SymbolVector *symbolSpan);
    ~RawTermIterator();

    bool hasNext();
    Clingo::Symbol next();

};


#endif //CLINGO_IN_RULEWERK_RAWTERMITERATOR_H
