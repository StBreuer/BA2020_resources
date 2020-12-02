package rulewerk.clingo;
import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
public class Clingo
{
    static {
        try {
        System.load("/home/steffen/eclipse-workspace/rulewerk/rulewerk-clingo/lib/libclingo-java.so");
        } catch (UnsatisfiedLinkError e) {
        System.err.println("Native code library failed to load.\n" + e);
        System.exit(1);
    }

       // System.loadLibrary("org_example_APP");
    }

    public Clingo(){}
    /**
    runs grounder and solver of clingo and prints the result to the command line
*/
    public native void startSolver();
    public native void stopSolver();
    public native void initPart(String part);
    public native String getPart(String part);
    public native void addRule(String rule);
    public native void addRules(String[] rule);
    public native void addData(String[] data);
    public native void ground();
    public native void solve();
    public native String getRule();
    public native void printModel();
    public native TermQueryResultIterator termQuery(String predicate);



    public void run(String path){
        return;
    }



    /*public static void main( String[] args )
    {



    }*/
}
