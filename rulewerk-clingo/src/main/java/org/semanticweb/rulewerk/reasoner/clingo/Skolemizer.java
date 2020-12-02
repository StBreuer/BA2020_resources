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

import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;

public class Skolemizer {
	private final String functionSymbolEnd = ")";
	private final String commmata = ", ";

	private boolean firstconcat;
	private String functionSymbol;

	private String rule;

	private Integer functionCount;

	Skolemizer(){
		functionCount = 0;
		functionSymbol = "";
		firstconcat = true;
	}

	void addToReplacement(UniversalVariable var){
		//getting ride of the "?"
		String toAdd = var.getSyntacticRepresentation().substring(1);
		if (firstconcat){
			this.functionSymbol = this.functionSymbol.concat(toAdd);
			this.firstconcat = false;
		}else {
			functionSymbol = functionSymbol.concat(commmata.concat(toAdd));
		}
	}

	//returns the function that will replace some existential variable
	String getFunctionSymbol() {

		String rtn = "skol".concat(functionCount.toString()).concat("(").concat(this.functionSymbol).concat(functionSymbolEnd);
		functionCount++;

		return rtn;
	}

	void replaceExVar(ExistentialVariable var){
		this.rule = this.rule.replaceAll(var.getSyntacticRepresentation(), this.getFunctionSymbol() );
	}

	String getSkolemRule(){
		return rule;
	}
	
	void setRule(String rule){
		this.rule = rule;
		this.functionSymbol = "";
		this.firstconcat = true;
	}
}
