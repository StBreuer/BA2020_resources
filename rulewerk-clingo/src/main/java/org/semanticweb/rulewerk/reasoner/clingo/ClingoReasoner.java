package org.semanticweb.rulewerk.reasoner.clingo;

import org.semanticweb.rulewerk.core.model.api.Fact;

import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.*;
import rulewerk.clingo.Clingo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;



/*
 * #%L
 * Rulewerk Clingo Reasoner Support //TODO I think I need to edit this
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
public class ClingoReasoner implements Reasoner {
	//TODO gives a warning
	private static Logger LOGGER = LoggerFactory.getLogger(ClingoReasoner.class);

	private final Skolemizer skolemizer;
	private final KnowledgeBase knowledgeBase;
	private final Clingo clingo;
	private final AliasHandler aliasHandler;

	private ReasonerState reasonerState = ReasonerState.KB_NOT_LOADED;


	/**
	 * Constructor
	 * @param knowledgeBase
	 */
	public ClingoReasoner(KnowledgeBase knowledgeBase){
		super();
		
		this.knowledgeBase = knowledgeBase;
		this.skolemizer = new Skolemizer();
		this.aliasHandler = new AliasHandler();
		this.clingo = new Clingo();
		
		
		//REMOVE AGAIN
		//clingo.startSolver();

		/* TEST
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		s = s + "/src/main/resources";
		System.out.println("Current relative path is: " + s);

		clingo.run(s);


		 */
	}

	public void load() { // maybe add throws IOException but since for now I just construct strings it should be fine
		//TODO validateNotClosed();
		switch (this.reasonerState){
			case KB_NOT_LOADED:
				loadKnowledgeBase();
				break;
			case KB_LOADED:
				break;
			case MATERIALISED:
				break;
			case KB_CHANGED:
				resetReasoner();
				loadKnowledgeBase();
				break;
			default:
				break;
		}
	}
	
	//TODO delete
	public void printAliase() {
		aliasHandler.printAliase();
	}

	/**
	 * Load KB to ClingoKB
	 */
	void loadKnowledgeBase(){
		LOGGER.info("Started loading knowledge base ...");
		final ClingoKnowledgeBase clingoKB = new ClingoKnowledgeBase(this.knowledgeBase);

		if (!clingoKB.hasData()){
			LOGGER.warn("No data statements (facts or datasource declarations) have been provided.");
		}

		//loadClingoDataSource(clingoKB);
		clingo.startSolver();
		clingo.initPart("part1");
		loadFacts(clingoKB);
		loadRules(clingoKB);
		this.reasonerState = ReasonerState.KB_LOADED;

	}

	//TODO add config options and state consistency checks
	void loadClingoDataSource(final ClingoKnowledgeBase clingoKB){
		;
	}
	
	void loadFacts(ClingoKnowledgeBase clingoKB){
		final Map<Predicate,List<Fact>> directedEdbFacts = clingoKB.getFacts();
		directedEdbFacts.forEach((k, v) -> {
			final String[] facts = ModelToClingoConverter.toClingoFactArray(v, this.aliasHandler);
			//TODO change methode
			clingo.addRules(facts);
			
			//test whats faster
			/*for (String predicate:predicates){
				clingo.addRule(predicate);
			}*/
			//TODO add debug option
		});	
	}

	void loadRules(final ClingoKnowledgeBase clingoKB){
		String[] rules = ModelToClingoConverter.toClingoRuleArray(skolemizer, clingoKB.getRules(), aliasHandler);
		clingo.addRules(rules);
	}
	public List<PositiveLiteral> getPosLitToQuery(){
		List<PositiveLiteral> literals = new ArrayList<PositiveLiteral>();
		
		//change this to get predicates to the knowledge base 
		Set<Predicate> predicates = this.aliasHandler.getPredicates();
		
		for(Predicate predicate : predicates) {
			literals.add(predToPosLit(predicate));
		}
		return literals;
	}

	private PositiveLiteral predToPosLit(Predicate predicate) {
		int arity = predicate.getArity();
		List<Term> terms = new ArrayList<Term>();
		for(int i = 0; i < arity; i++) {
			terms.add(Expressions.makeUniversalVariable("X")); //does not matter how the terms of these literals actually look like
		}
		return Expressions.makePositiveLiteral(predicate, terms);
		
	}
	
	/*public void printStats() {
		this.clingo.printModel();
	}*/

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.knowledgeBase;
	}

	@Override
	public Correctness forEachInference(InferenceAction action) throws IOException {
		return null;
	}

	@Override
	public Correctness writeInferences(OutputStream stream) throws IOException {
		return null;
	}

	@Override
	public Correctness getCorrectness() {
		return null;
	}

	@Override
	public void setAlgorithm(Algorithm algorithm) {

	}

	@Override
	public Algorithm getAlgorithm() {
		return null;
	}

	@Override
	public void setReasoningTimeout(Integer seconds) {

	}

	@Override
	public Integer getReasoningTimeout() {
		return null;
	}

	@Override
	public void setRuleRewriteStrategy(RuleRewriteStrategy ruleRewritingStrategy) {

	}

	@Override
	public RuleRewriteStrategy getRuleRewriteStrategy() {
		return null;
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {

	}

	@Override
	public LogLevel getLogLevel() {
		return null;
	}

	@Override
	public void setLogFile(String filePath) {

	}

	@Override
	public CyclicityResult checkForCycles() {
		return null;
	}

	@Override
	public boolean isJA() {
		return false;
	}

	@Override
	public boolean isRJA() {
		return false;
	}

	@Override
	public boolean isMFA() {
		return false;
	}

	@Override
	public boolean isRMFA() {
		return false;
	}

	@Override
	public boolean isMFC() {
		return false;
	}

	//TODO remove
	public boolean runClingo(){
		clingo.ground();
		clingo.solve();
		//clingo.printModel();
		this.reasonerState = ReasonerState.MATERIALISED;
		return true;
	}

	@Override
	public boolean reason() throws IOException {
		switch(this.reasonerState) {
		case KB_NOT_LOADED:
			this.load();
			
			return this.runClingo();
		case KB_LOADED:
			return this.runClingo();
		case KB_CHANGED:
			this.resetReasoner();
			return false;
		case MATERIALISED:
			return this.runClingo();
		default:
			return false;
		}
	}

	@Override
	public QueryResultIterator answerQuery(PositiveLiteral query, boolean includeNulls) {
		String clingoQuery = ModelToClingoConverter.toClingoQuery(query, aliasHandler);
		//TODO test reasoner state
		ClingoQueryResultIterator iterator = new ClingoQueryResultIterator(this.clingo.termQuery(clingoQuery), this.aliasHandler);
		return iterator;

	}

	@Override
	public QueryAnswerCount countQueryAnswers(PositiveLiteral query, boolean includeNulls) {
		return null;
	}

	@Override
	public Correctness exportQueryAnswersToCsv(PositiveLiteral query, String csvFilePath, boolean includeNulls) throws IOException {
		return null;
	}

	@Override
	public void resetReasoner() {
		this.clingo.stopSolver();
		this.reasonerState = ReasonerState.KB_NOT_LOADED;
		//this.clingo.startSolver();

	}

	@Override
	public void close() {
		//TODO set LOGGER
		this.clingo.stopSolver();
		this.reasonerState = ReasonerState.CLOSED;
	}

	@Override
	public void onStatementAdded(Statement statementAdded) {
		
	}

	@Override
	public void onStatementsAdded(List<Statement> statementsAdded) {

	}

	@Override
	public void onStatementRemoved(Statement statementRemoved) {

	}

	@Override
	public void onStatementsRemoved(List<Statement> statementsRemoved) {

	}
}
