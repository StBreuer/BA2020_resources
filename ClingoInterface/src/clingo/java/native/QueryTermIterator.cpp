//
// Created by steffen on 6/11/20.
//

#include "QueryTermIterator.h"
QueryTermIterator::QueryTermIterator(const char *predicate ,Clingo::Model const *model) {

    this->model = model;
    //std::cout<< &model << std::endl;

    //this->symbols = model->symbols();
    //this->filterSymbols(predicate, symbols);
}

QueryTermIterator::QueryTermIterator(const char *predicate, Clingo::SymbolVector *symbols) {
    this->predicate = predicate;
    this->symbols = symbols;
    this->iterator = symbols->begin();
    this->findNext();
    if(hasNext()){
        this->tupleSize = this->iterator->arguments().size();
    }

    //this->filterSymbols(predicate, *symbols);
     }


//Since predicateArguments and the const char* it's containing are safed on the heap they have to be destroyed
QueryTermIterator::~QueryTermIterator() {
    //std::cout << "delete queryIterator" << std::endl;
    /*for(const char** x: *this->predicateArguments){
        delete x;
    }
    delete this->predicateArguments;
    delete this->predicate;*/
}
void QueryTermIterator::resetState() {}

bool QueryTermIterator::hasNext() {
    return this->iterator != this->symbols->end();
}


void QueryTermIterator::testNext(){
    std::cout << "TESTNEXT" << std::endl;

    if(hasNext()){
        std::cout << "get into test" << std::endl;
        const char* *nextElem = this->next();
        for (int i = 0; i < this->tupleSize; ++i) {
            std::cout << nextElem[i] << std::endl;
        }
    }
}
const char ** QueryTermIterator::next() {
    if (hasNext()){
        auto arguments = this->iterator->arguments();
        auto *rtn = new const char* [arguments.size()];

        for (int i = 0; i < arguments.size(); i++){
            rtn[i] = arguments[i].name();
        }
        this->iterator++;
        this->findNext();

        //std::cout << "got to the end" << std::endl;
        return rtn;
    }

    /*
    if (hasNext()){
        //TODO optimise
        const char** rtn = this->predicateArguments->at(nextArguments);
        this->nextArguments--;
        return rtn;
    }
     */
    return NULL;
}
void QueryTermIterator::findNext() {
    if (this->hasNext()){
        while(this->hasNext() && strcmp(this->iterator->name(), this->predicate) != 0){
            this->iterator++;
            if(!this->hasNext()){
                break;
            }
        }
    }
}

void QueryTermIterator::filterSymbols(const char *predicate, Clingo::SymbolVector allSymbols) {
    auto *vector = new std::vector<const char**>();
    //TODO maybe fix it but first write own filter method
    //std::copy_if(this->symbols.begin(), this->symbols.end(), std::back_inserter(vector), [predicate](Clingo::Symbol i){return i.name() == predicate;});
    //TODO optimise it


    //const char **arguments;
    for (Clingo::Symbol symbol:allSymbols){
        if(std::strcmp(symbol.name(), predicate) == 0){

            Clingo::SymbolSpan args = symbol.arguments();
            this->tupleSize = args.size();

            //arguments = new const char*[args.size()];
            auto *arguments = new const char* [args.size()];
            for (int i = 0; i < this->tupleSize; i++){
                arguments[i] = args[i].name();
            }
            vector->push_back(arguments);
        }
    }
    this->predicateArguments = vector;
    this->nextArguments = vector->size() - 1;

}

size_t QueryTermIterator::getTupleSize() {
    return this->tupleSize;
}

