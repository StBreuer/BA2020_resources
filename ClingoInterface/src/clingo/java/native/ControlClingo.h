#include "clingo.hh"
#include "QueryTermIterator.h"

#ifndef CLINGO_IN_RULEWERK_LOADDATASOURCEWITHOUTFILE_H
#define CLINGO_IN_RULEWERK_LOADDATASOURCEWITHOUTFILE_H


class ControlClingo {

private:

    //TODO cleanup;
    Clingo::Control *control = nullptr;
    const char *part = nullptr; // make this an array
    Clingo::SolveHandle *solveHandle = nullptr;
    Clingo::SymbolVector *symbols = nullptr;

public:
    ControlClingo();
    ~ControlClingo();
    const char* getPart();
    void testRestrictModels();
    void setPart(const char *part);
    void addRule(const char* part, const char *rule);
    Clingo::SolveHandle* getSolveHandle();
    void setSolveHandle(Clingo::SolveHandle *solveHandle);
    void setSymbols(Clingo::SymbolVector *symbols);
    Clingo::SymbolVector *getSymbols();
    void addRule(const char *rule); //rename to add statement
    void ground();
    void ground(const char* part);
    Clingo::SolveHandle solve();
    void printModel();
    void printModel(Clingo::SolveHandle *handle);
    //vector<> toRulewerkGroundRule();
    void statistics();
    bool print_statistics(const clingo_statistics_t *stats, uint64_t key, int depth);
    void print_prefix(int depth);





};


#endif //CLINGO_IN_RULEWERK_LOADDATASOURCEWITHOUTFILE_H
