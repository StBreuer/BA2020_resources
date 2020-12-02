package org.semanticweb.rulewerk.reasoner.clingo;

/*-
 * #%L
 * Rulewerk Clingo Reasoner Support
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

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.*;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class QueryTest {
	@Test
	public void BasicQuery(){
		final Predicate friend = Expressions.makePredicate("friend" , 2);
		final Constant adam = Expressions.makeAbstractConstant("adam");
		final Constant isabel = Expressions.makeAbstractConstant("isabel");
		final Constant mark = Expressions.makeAbstractConstant("mark");
		final Fact fAI = Expressions.makeFact(friend, Arrays.asList(adam, isabel));
		final Fact fIM = Expressions.makeFact(friend, Arrays.asList(isabel, mark));

		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Variable z = Expressions.makeUniversalVariable("Z");
		final PositiveLiteral fXZ = Expressions.makePositiveLiteral(friend, x, z);
		final PositiveLiteral fXY = Expressions.makePositiveLiteral(friend, x, y);
		final PositiveLiteral fYZ = Expressions.makePositiveLiteral(friend, y, z);
		final Rule transitivity = Expressions.makeRule(fXZ, fXY,fYZ);
		final KnowledgeBase kb = new KnowledgeBase();

		//add facts
		kb.addStatements(fAI, fIM);

		//add rule
		kb.addStatements(transitivity);

		ClingoReasoner reasoner = new ClingoReasoner(kb);

		reasoner.load();
		reasoner.runClingo();
		for(PositiveLiteral literal : reasoner.getPosLitToQuery()) {
			System.out.println(literal.getPredicate().getName());
		}
		
		QueryResultIterator iterator = reasoner.answerQuery(fXZ, false);

		while (iterator.hasNext()){
			System.out.println("new object");
			for( Term term: iterator.next().getTerms()){
				System.out.println(term.getName());
			}
		}
		iterator.close();
		System.out.println("reasoner.close");
		reasoner.close();
		System.out.println("finished");

	}

	@Test
	public void addexistentialQuantifiedRules(){
		final String predicate = "<p>";
		final String predicate2 = "<p2>";
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeExistentialVariable("Y");
		final Variable z = Expressions.makeExistentialVariable("Z");
		final PositiveLiteral pYY = Expressions.makePositiveLiteral(predicate, y, y);
		final PositiveLiteral pYZ = Expressions.makePositiveLiteral(predicate, y, z);
		final Rule pX__pYY_pYZ = Expressions.makeRule(Expressions.makePositiveConjunction(pYY, pYZ),
			Expressions.makeConjunction(Expressions.makePositiveLiteral(predicate2, x)));

		String notInSkolem = pX__pYY_pYZ.getSyntacticRepresentation();
		System.out.println(notInSkolem);


		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(pX__pYY_pYZ);
		kb.addStatement(Expressions.makeFact(predicate2, Arrays.asList(Expressions.makeAbstractConstant("c"))));

		final ClingoReasoner reasoner = new ClingoReasoner(kb);
		reasoner.load();
		reasoner.runClingo();
		
		QueryResultIterator iterator = reasoner.answerQuery(pYY, false);

		while (iterator.hasNext()){
			System.out.println("new object");
			for( Term term: iterator.next().getTerms()){
				System.out.println(term.getName());
			}
		}
		reasoner.close();
		
		System.out.println("finished");
	}


	// Test for existential rules
	@Test
	public void existentailFather(){
		final String human = "human";
		final String hasFather = "hasFather";

		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable v = Expressions.makeExistentialVariable("V");
		final PositiveLiteral humanX = Expressions.makePositiveLiteral(human,x);
		final PositiveLiteral hasFatherXV = Expressions.makePositiveLiteral(hasFather, x, v);
		final Rule hThenHF = Expressions.makeRule(hasFatherXV, humanX);

		final Constant petra = Expressions.makeAbstractConstant("petra");
		final Fact humanPetra = Expressions.makeFact(human, petra);

		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(hThenHF, humanPetra);

		final ClingoReasoner reasoner = new ClingoReasoner(kb);
		reasoner.load();
		reasoner.runClingo();

		QueryResultIterator iterator = reasoner.answerQuery(hasFatherXV, false);
		while (iterator.hasNext()){
			System.out.println("new object");
			for( Term term: iterator.next().getTerms()){
				System.out.println(term.getName());
			}
		}
	}
	
	@Test
	public void addIRI() {
		
		
		
		
		final String masterDegreeFrom = "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#mastersDegreeFrom>";
		final String test = "test";
		final Constant a = Expressions.makeAbstractConstant("a");
		final Constant b = Expressions.makeAbstractConstant("b");
		
		
		final Constant prof7 = Expressions.makeAbstractConstant("<http://www.Department12.University25.edu/AssociateProfessor7>");
		final Constant edu = Expressions.makeAbstractConstant("<http://www.University163.edu>");
		
		final Fact fact = Expressions.makeFact(test, a, b);
		final Fact replacement = Expressions.makeFact(masterDegreeFrom, prof7, edu);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(replacement);
		
		
		
		final ClingoReasoner reasoner = new ClingoReasoner(kb);
		
		reasoner.load();
		reasoner.runClingo();
		System.out.println("ready");
		reasoner.close();
		
	}
	
	@Test
	public void backToAntialias() {
		final String masterDegreeFrom = "<http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#mastersDegreeFrom>";
		final String test = "test";
		final Constant a = Expressions.makeAbstractConstant("a");
		final Constant b = Expressions.makeAbstractConstant("b");
		
		
		final Constant prof7 = Expressions.makeAbstractConstant("<http://www.Department12.University25.edu/AssociateProfessor7>");
		final Constant edu = Expressions.makeAbstractConstant("<http://www.University163.edu>");
		
		final Fact fact = Expressions.makeFact(test, a, b);
		final Fact replacement = Expressions.makeFact(masterDegreeFrom, prof7, edu);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(replacement);
		
		
		
		final ClingoReasoner reasoner = new ClingoReasoner(kb);
		
		reasoner.load();
		reasoner.runClingo();
		QueryResultIterator iterator = reasoner.answerQuery(replacement, true);
		while(iterator.hasNext()) {
			QueryResult result = iterator.next();
			List<Term> terms = result.getTerms();
			for(Term term : terms) {
				System.out.println(term.getName());
			}
		}
		System.out.println("ready");
		reasoner.close();
	}
	
	//DOSE not work this way
	@Test
	public void restrictModels() {
		
		//in order to work add startsolver in the reasoner constructor
		KnowledgeBase kb = new KnowledgeBase();
		final ClingoReasoner reasoner = new ClingoReasoner(kb);
		
		//reasoner.load();
		//reasoner.reason2();
		//reasoner.close();
	}
	
	@Test
	public void longToLong() {
		long test = 1;
		List<Long> testlist = new ArrayList<Long>();
		testlist.add(test);
		System.out.println(testlist.get(0));
	}
}
