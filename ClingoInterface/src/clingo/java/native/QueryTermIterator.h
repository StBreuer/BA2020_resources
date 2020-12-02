#include "clingo.hh"
#include "memory"
#ifndef CLINGO_IN_RULEWERK_QUERYTERMITERATOR_H
#define CLINGO_IN_RULEWERK_QUERYTERMITERATOR_H


class QueryTermIterator {
private:

    //TODO set this (keep track of deletion)
    const char *predicate;
    //TODO delete them;
    Clingo::Model const *model;
    //std::shared_ptr<Clingo::SymbolVector> symbols;
    Clingo::SymbolVector *symbols;
    Clingo::SymbolVector::iterator iterator;
    void findNext();


    void filterSymbols(const char* predicate, Clingo::SymbolVector allSymbols);
    size_t tupleSize = 0;
    int nextArguments;

    // converted model to arguments by predicate
    std::vector<const char**> *predicateArguments;

public:
    QueryTermIterator(const char *predicate, Clingo::Model const *model);
    QueryTermIterator(const char *predicate, Clingo::SymbolVector *symbolSpan);
    ~QueryTermIterator();
    void resetState();

    size_t getTupleSize();
    bool hasNext();
    const char** next();
    void testNext();

};


#endif //CLINGO_IN_RULEWERK_QUERYTERMITERATOR_H
