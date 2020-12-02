package org.semanticweb.rulewerk.reasoner.clingo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner.InferenceAction;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

/*
 * Class to help Loading external Data from Files into an Knowledgebase 
 */
public class CSVloader implements InferenceAction{
	
	//Used to load Data
	private KnowledgeBase loadKb;
	private VLogReasoner loader;
	
	//Kb that the Data is Loaded to
	private KnowledgeBase myKb;
	
	public CSVloader(KnowledgeBase myKb){
		this.myKb = myKb;
		this.loadKb = new KnowledgeBase();
		
		//TODO maybe try block
		this.loader = new VLogReasoner(this.loadKb);
	}
	//changeback into void
	public void loadCSVsfromFolder(String configFilePath, String dataPath ) throws IOException {
		List<String[]> loadInfo = getLoadInfo(configFilePath);
		
		for(String[] info : loadInfo) {
			final String input = "@source " + info[0] + "[" + info[2] + "]" + ": load-csv(\"" + dataPath + info[1] + "\") .";
		
			try {
				RuleParser.parseInto(this.loadKb, input);
			} catch (ParsingException e) {
				e.printStackTrace();
			}
		}
		this.loader.reason();
		this.loader.forEachInference(this);
		//return this.loader;
		
	}
	public VLogReasoner loadCSVsfromFolderTest(String infoPath, String dataPath ) throws IOException {
		//useless
		//String[] files = getFilesOfFolder(dataPath);
		//Name: // IRI Filename Arity
		List<String[]> loadInfo = getLoadInfo(infoPath);
		
		for(String[] info : loadInfo) {
			final String input = "@source " + info[0] + "[" + info[2] + "]" + ": load-csv(\"" + dataPath + info[1] + "\") .";
		
			try {
				RuleParser.parseInto(this.loadKb, input);
				// TODO maybe use Reasoner here and use threads
			} catch (ParsingException e) {
				e.printStackTrace();
			}
		}
		//TODO uncomment this
		//this.loader.reason();
		//this.loader.forEachInference(this);
		return this.loader;
		
	}
	
	//TODO make this to kind of a visitor pattern/ strategy Design pattern
	List<String[]> getLoadInfo(String path){
		
		List<String[]> importInfo = new ArrayList<String[]>();
		BufferedReader reader = null;
		String line;
		
		try{
			reader = new BufferedReader(new FileReader(path));
			while((line = reader.readLine())!= null) {
				// IRI Filename Arity
				String[] info = line.split(" ");
		
				importInfo.add(info); 
				
			}
	
		} 
		catch(IOException io) {
			io.printStackTrace();
		}
		return importInfo;
	}
	
	
	PositiveLiteral buildQuery(String name, int arity) {
		List<Term> variables = new LinkedList<Term>();
		
		for (int i = 0; i < arity; i++) {
			variables.add(Expressions.makeUniversalVariable("X".concat(String.valueOf(i))));
		}
		
		final PositiveLiteral query = Expressions.makePositiveLiteral(name, variables);
		return query;
	}
	
	String[] getFilesOfFolder(String path) throws IOException{
		File f = new File(path);
		String[] files = {};
		
		files = f.list();
		 
		return files;
	}

	@Override
	public void accept(Predicate predicate, List<Term> termList) throws IOException {
		Statement statement = Expressions.makeFact(predicate, termList);
		
		this.myKb.addStatement(statement);
		
	}

}
