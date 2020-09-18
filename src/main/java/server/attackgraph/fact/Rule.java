package server.attackgraph.fact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rule implements Cloneable{

	static Pattern pattern = Pattern.compile("^RULE (\\d+) \\((.*)\\)$");
	/**
	 * The text of the rule
	 */
	public String ruleText = "";
	/**
	 * The number of the rule
	 */
	int number = 0;
	
	/**
	 * Create a rule from a fact string
	 * @param fact the fact string
	 */
	public Rule(String fact) {
		Matcher matcherRule = pattern.matcher(fact);
		if(matcherRule.matches()) {
			this.number = Integer.parseInt(matcherRule.group(1));
			this.ruleText = matcherRule.group(2);
		}
	}
	
	/**
	 * Check if a fact string is a rule
	 * @param fact the fact string
	 * @return true if the fact string is a rule else false
	 */
	public static boolean isARule(String fact) {
		Matcher matcherRule = pattern.matcher(fact);
		return matcherRule.matches();
	}

	@Override
	public Rule clone() throws CloneNotSupportedException {
		Rule copie = (Rule)super.clone();

		return copie;
	}

	@Override
	public String toString() {
		return "Rule [number=" + number + ", ruleText=" + ruleText + "]";
	}
}
