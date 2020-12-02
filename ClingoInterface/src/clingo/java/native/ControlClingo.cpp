//
// Created by steffen on 5/20/20.
//
#include <chrono>
#include <thread>
#include "ControlClingo.h"
#include "clingo.h"

ControlClingo::ControlClingo() {
    control = new Clingo::Control();
    std::cout << "solver started" << std::endl;
    //control->configuration()["solve"]["models"] = "1";
    //std::cout << "start test" << std::endl;
    //this->testRestrictModels();
}
void ControlClingo::testRestrictModels() {
    this->setPart("test");
    this->addRule("b :- not c.");
    this->addRule("c :- not b.");
    std::cout << this->getPart()  << std::endl;

    std::cout << "start ground" << std::endl;
    this->ground();
    std::cout << "start solve" << std::endl;
    this->solve();

}



ControlClingo::~ControlClingo() {
    //TODO clean up :::: do i need to check if they are a nullpointer?
    if (this->control != nullptr){
        delete this->control;
    }

    if (this->solveHandle != nullptr){
        delete this->solveHandle;
    }

    if (this->symbols != nullptr){
        delete this->symbols;
    }

    if (this->part != nullptr){
        delete part;
    }

}

const char * ControlClingo::getPart() {
    return this->part;
}
void ControlClingo::setPart(const char *part) {
    this->part = part;
}

Clingo::SolveHandle* ControlClingo::getSolveHandle() {
    return this->solveHandle;
}
void ControlClingo::setSolveHandle(Clingo::SolveHandle* solveHandle) {
    this->solveHandle = solveHandle;
}
void ControlClingo::setSymbols(Clingo::SymbolVector *symbols){
    this->symbols = symbols;
}
Clingo::SymbolVector * ControlClingo::getSymbols() {
    return this->symbols;
}

void ControlClingo::addRule(const char *part, const char *rule) {
    Clingo::StringSpan span = {};
    control->add(part, span, rule);
}
void ControlClingo::addRule(const char *rule) {
    //std::cout << rule << std::endl;
    Clingo::StringSpan span = {};
    control->add(this->getPart(), span, rule);
    //std::cout << "no control add problem" << std::endl;
}

void ControlClingo::ground(const char *part) {
    Clingo::SymbolSpan params = {};

    //TODO test if I need a new object
    Clingo::Part *partObj = new Clingo::Part(part, params);
    Clingo::PartSpan partSpan = {*partObj};
    control->ground(partSpan);
}

void ControlClingo::ground() {
    Clingo::SymbolSpan params = {};


    //TODO keep track of deletion
    auto *partObj = new Clingo::Part(this->getPart(), params);
    Clingo::PartSpan partSpan = {*partObj};
    control->ground(partSpan);
}

Clingo::SolveHandle ControlClingo::solve() {

    Clingo::LiteralSpan sspan = {};
    //Clingo::SolveEventHandler *eventHandler = new Clingo::SolveEventHandler();
    //Clingo::SolveHandle solveHandle = control->solve(sspan, eventHandler, true, false);
    Clingo::SolveHandle solveHandle = control->solve(sspan);
    //std::cout << "the new solve settings work" << std::endl;


    //Clingo::Statistics statistics = control->statistics();
   // Clingo::StatisticsBase<true> statBase = statistics.at("Models");
    //std::cout << statBase.at(0) <<
    /*std::cout << "getting the number of models really worked!!!!!" << std::endl;
    for(int i = 0 ;i < 10; i++ ){
        std::cout << i << std::endl;
        //Clingo::Model const &model0 = solveHandle.model();
        std::cout << "model" << std::endl;
        //std::cout << model0 << std::endl;
        std::cout << "next" << std::endl;
        Clingo::Model const &model1 = solveHandle.next();
        std::cout << model1 << std::endl;
        std::cout << "nextloop" << std::endl;
    }*/

    Clingo::Model const  &model = solveHandle.model();

    //TODO change Symbols to an array of symbols

    //TODO change to normal Constructor but don't know yet how this works
    /*Clingo::Detail::AssignOnce pointer;
    auto *heapSolveHandle = new Clingo::SolveHandle(solveHandle.to_c(), pointer);
    std::cout << "copy to heap did work" << std::endl;
*/

    //this->setSolveHandle(&solveHandle);

    //TODO clean up
    auto *span = new Clingo::SymbolVector(solveHandle.model().symbols());
    this->setSymbols(span);

    //this->setSolveHandle(heapSolveHandle);


    return solveHandle;
}

void ControlClingo::printModel() {
    Clingo::Model const &model = this->getSolveHandle()->model();
    std::vector<Clingo::Symbol> symbols = model.symbols();
    for(int i = 0; i < symbols.size(); i++){
        std::cout << symbols.at(i) << std::endl;
    }
}

//TODO change again
void ControlClingo::printModel(Clingo::SolveHandle *handle) {

    Clingo::Model const &model = handle->model();

    //printing stuff
    std::vector<Clingo::Symbol> symbols = model.symbols();
    for(int i = 0; i < symbols.size(); i++){
        std::cout << symbols.at(i) << std::endl;
    }


}

void ControlClingo::statistics() {
    uint64_t stats_key;
    auto statistics = this->control->statistics();
    statistics.to_c();
    clingo_statistics_root(statistics.to_c(), &stats_key);
    print_statistics(statistics.to_c(), stats_key, 0);
}
bool ControlClingo::print_statistics(const clingo_statistics_t *stats, uint64_t key, int depth) {
    bool ret = true;
    clingo_statistics_type_t type;
    // get the type of an entry and switch over its various values
    if (!clingo_statistics_type(stats, key, &type)) {}
    switch ((enum clingo_statistics_type) type) {
        // print values
        case clingo_statistics_type_value: {
            double value;
            // print value (with prefix for readability)
            print_prefix(depth);
            if (!clingo_statistics_value_get(stats, key, &value)) {}
            printf("%g\n", value);
            break;
        }
            // print arrays
        case clingo_statistics_type_array: {
            size_t size;
            // loop over array elements
            if (!clingo_statistics_array_size(stats, key, &size)) {}
            for (size_t i = 0; i < size; ++i) {
                uint64_t subkey;
                // print array offset (with prefix for readability)
                if (!clingo_statistics_array_at(stats, key, i, &subkey)) {}
                print_prefix(depth);
                printf("%zu:\n", i);
                // recursively print subentry
                if (!print_statistics(stats, subkey, depth + 1)) {}
            }
            break;
        }
            // print maps
        case clingo_statistics_type_map: {
            size_t size;
            // loop over map elements
            if (!clingo_statistics_map_size(stats, key, &size)) {}
            for (size_t i = 0; i < size; ++i) {
                char const *name;
                uint64_t subkey;
                // get and print map name (with prefix for readability)
                if (!clingo_statistics_map_subkey_name(stats, key, i, &name)) {}
                if (!clingo_statistics_map_at(stats, key, name, &subkey)) {}
                print_prefix(depth);
                printf("%s:\n", name);
                // recursively print subentry
                if (!print_statistics(stats, subkey, depth + 1)) {}
            }
        }
            // this case won't occur if the statistics are traversed like this
        case clingo_statistics_type_empty: {
        }
    }
}



void ControlClingo::print_prefix(int depth){
    for (int i = 0; i < depth; ++i) {
        printf("  ");
    }

}
