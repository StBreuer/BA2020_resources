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

import org.semanticweb.rulewerk.core.model.api.*;

import java.util.Collection;

final class ModelToClingoConverter {


private static String toClingoRule(Skolemizer skolemizer, final Rule rule, AliasHandler aliasHandler){
	
	String representation = rule.getSyntacticRepresentation();
	String clingoRule = representation.replace("?", "");

	//Skolemization
	if (rule.getExistentialVariables().count() > 0){
		skolemizer.setRule(clingoRule);
		rule.getUniversalVariables().forEach(skolemizer::addToReplacement);
		rule.getExistentialVariables().forEach(skolemizer::replaceExVar);

		clingoRule = skolemizer.getSkolemRule();
	}
	
	return aliasHandler.rewriteRule(rule, clingoRule);
}

static String[] toClingoRuleArray(Skolemizer skolemizer ,final Collection<Rule> rules, AliasHandler aliasHandler){
	String[] clingoRules = new String[rules.size()];
	int i = 0;
	for (Rule rule:rules){
		clingoRules[i] = toClingoRule(skolemizer, rule, aliasHandler);
		i++;
	}
	return clingoRules;
}


private static String toClingoFact(final Fact fact, AliasHandler aliasHandler){
	//String syn= fact.getSyntacticRepresentation();
	return aliasHandler.rewriteFact(fact);
}



static String[] toClingoFactArray(final Collection<Fact> facts, AliasHandler aliasHandler){
	String[] clingoFacts = new String[facts.size()];
	int i = 0;
	for (Fact fact: facts) {
		clingoFacts[i] = toClingoFact(fact, aliasHandler);
		i++;
	}
	return clingoFacts;
}

//TODO maybe useless but maybe good for better understanding;
static String toClingoQuery(PositiveLiteral literal, AliasHandler aliasHandler){
	return aliasHandler.replacePred(literal.getPredicate(), literal.getPredicate().getName());
}



}


