package org.semanticweb.rulewerk.reasoner.clingo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Rule;


public class AliasHandler {

		private final Map<Term, String> termAliasesForIRIs;
		private final Map<String, Term> termIRIsforaliases;

		private final Map<Predicate,String> predAliasesForIRIs;
		private final Map<String, Predicate> predIRIsforaliases;
		
		int aliasNumber;
		
		public AliasHandler() {
			aliasNumber = 0;
			
			termAliasesForIRIs = new HashMap<>();
			termIRIsforaliases = new HashMap<>();
			
			predAliasesForIRIs = new HashMap<>();
			predIRIsforaliases = new HashMap<>();
		}
		
		
		String rewriteFact(Fact fact){
			Predicate predicate = fact.getPredicate();
			List<Term> terms = fact.getArguments();
			String predAlias;
			List<String> argumentAliases = new LinkedList<>();
			
			if(this.predAliasesForIRIs.containsKey(predicate)) {
				predAlias = this.predAliasesForIRIs.get(predicate);
			} else {
				predAlias = "pred".concat(String.valueOf(aliasNumber));
				aliasNumber = aliasNumber + 1;
				
				this.predAliasesForIRIs.put(predicate, predAlias);
				this.predIRIsforaliases.put(predAlias, predicate);
			}
			
			for (Term term : terms) {
				if(this.termAliasesForIRIs.containsKey(term)) {
					argumentAliases.add(this.termAliasesForIRIs.get(term));
				} else {
					String termAlias = "const".concat(String.valueOf(aliasNumber));
					aliasNumber = aliasNumber + 1 ;
					
					argumentAliases.add(termAlias);
					this.termAliasesForIRIs.put(term, termAlias);
					this.termIRIsforaliases.put(termAlias, term);
				}
			}
			
			return makeFactStr(predAlias, argumentAliases);
		}
		
		private String makeFactStr(String predAlias, List<String> argumentAliases) {
			predAlias = predAlias.concat("(");
			for (String arg : argumentAliases) {
				predAlias = predAlias.concat(arg).concat(", ");
				
			}
			
			//TODO change
			
			predAlias = predAlias.substring(0, predAlias.length() - 2);
			return predAlias.concat(") .");
		}
		
		String rewriteRule2(Rule rule) {
			//list of aliases
			List<String> headPredAliases = new ArrayList<String>();
			List<String> bodyPredAliases = new ArrayList<String>();
			List<PositiveLiteral> headLiterals = rule.getHead().getLiterals();
			List<Literal> bodyLiterals = rule.getBody().getLiterals();
			
			
			
			//ADD head
			for (java.util.Iterator<PositiveLiteral> iterator = headLiterals.iterator(); iterator.hasNext();) {
				
				PositiveLiteral literal = iterator.next();
				Predicate predicate = literal.getPredicate();
				
				if(this.predAliasesForIRIs.containsKey(predicate)) {
					headPredAliases.add(this.toPredStr(predicate, this.predAliasesForIRIs.get(predicate)));
				}
				else {
					
					String newAlias = "pred".concat(String.valueOf(this.aliasNumber));
					this.aliasNumber = this.aliasNumber + 1;
					
					headPredAliases.add(this.toPredStr(predicate, newAlias));
					predAliasesForIRIs.put(predicate, newAlias);
				}
			}
			
			//ADD body
			Conjunction<Literal> literals = rule.getBody();
			for (java.util.Iterator<Literal> iterator = literals.iterator(); iterator.hasNext();) {
				Literal literal = iterator.next();
				
				Predicate predicate = literal.getPredicate();
				if(this.predAliasesForIRIs.containsKey(predicate)) {
					bodyPredAliases.add(this.toPredStr(predicate, this.predAliasesForIRIs.get(predicate)));
				}
				else {
					
					String newAlias = "pred".concat(String.valueOf(this.aliasNumber));
					this.aliasNumber = this.aliasNumber + 1;
					
					bodyPredAliases.add(this.toPredStr(predicate, newAlias));
					predAliasesForIRIs.put(predicate, newAlias);
				}

			}

			return toRuleStr(headPredAliases, bodyPredAliases);
		}
		
		
		private String toPredStr(Predicate pred, String alias){
			int arity = pred.getArity();
			alias = alias.concat("(");
			
			for (int i = 0; i < arity; i++) {
				//mabye need new variable names
				alias =alias.concat("X".concat(String.valueOf(i)));
				
				if(i < arity -1) {
					alias = alias.concat(", ");
				}
			}
			
			return alias.concat(")");
		}
		
		private String toRuleStr(List<String> head, List<String> body) {
			String rule = "";
			
			for (String headLiteral : head) {
				rule = rule.concat(headLiteral).concat(", ");
			} 
			rule = rule.substring(0, rule.length() - 2).concat(" :- ");
			
			for(String bodyLiteral: body) {
				rule = rule.concat(bodyLiteral).concat(", ");
			}
			return rule.substring(0, rule.length() - 2).concat(" .");
		}
		
		String rewriteRule(Rule rule, String strRule) {
			List<PositiveLiteral> headLiterals = rule.getHead().getLiterals();
			List<Literal> bodyLiterals = rule.getBody().getLiterals();
			
			//rewrite head
			for (Literal literal : headLiterals) {
				Predicate predicate = literal.getPredicate();
				strRule = this.replacePred(predicate, strRule);
			}
			
			//rewrite body
			
			for (Literal literal : bodyLiterals) {
				Predicate predicate = literal.getPredicate();
				strRule = this.replacePred(predicate, strRule);
			}
			
			return strRule;
		}
		
		String replacePred(Predicate predicate, String strRule) {
			if(this.predAliasesForIRIs.containsKey(predicate)) {
				strRule = strRule.replace(predicate.getName(), this.predAliasesForIRIs.get(predicate));
				strRule = strRule.replace("<", "");
				strRule = strRule.replace(">", "");
			}else {
				String newAlias = "pred".concat(String.valueOf(this.aliasNumber));
				this.aliasNumber = this.aliasNumber + 1;
				
				strRule = strRule.replace(predicate.getName(), newAlias);
				strRule = strRule.replace("<", "");
				strRule = strRule.replace(">", "");
				
				predAliasesForIRIs.put(predicate, newAlias);
				predIRIsforaliases.put( newAlias, predicate);
			}
			
			return strRule;
		}
		
		void printAliase() {
			this.predAliasesForIRIs.forEach((k,v)->{
				System.out.println(k.getName() + " " + v);
			});
		}
		
		//Back to Rulewerk

		List<Term> clingoToRulewerkTerms(List<Term> terms) {
			List<Term> newTerms = new ArrayList<Term>();
			for(Term term : terms) {
				if(this.termIRIsforaliases.containsKey(term.getName())) {
					newTerms.add(termIRIsforaliases.get(term.getName()));
				} else {					
					newTerms.add(term);
				}
				
			}
			return newTerms;
		}
		Set<Predicate> getPredicates(){
			return this.predAliasesForIRIs.keySet();
		}
		
		
}
