package rulewerk.clingo;

import java.util.Iterator;

import rulewerk.clingo.Term.TermType;

public class TermQueryResultIterator implements Iterator<Term[]>{

    private final long handle;


    public TermQueryResultIterator(long handle){
        this.handle = handle;
    }

    public boolean hasNext(){
        return hasNext(this.handle);
    }
    //maybe useless
    public Term[] next(){
        String[] sTuple = next(this.handle);
        System.out.println("sTuple in java jni");
        System.out.println(sTuple);
        int length = sTuple.length;
        Term[] tTuple = new Term[length];
        for(int i = 0; i < length; i++){
            TermType type = TermType.CONSTANT;
            tTuple[i] = new Term(type, sTuple[i]);
        }
        return tTuple;
    }

    public void close(){
        remove(this.handle);
    }


    private native boolean hasNext(long handle);
    //main used function
    public native String[] next(long handle);
    private native void remove(long handle);

}