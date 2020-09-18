package server.attackgraph.fact;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataLogCommand implements Cloneable {

    /**
     * The pattern of the command
     */
    public static Pattern pattern = Pattern.compile("^([a-zA-Z\\\\=]+)\\((.*)\\)$");

    /**
     * The related fact
     */
    public Fact fact = null;

    /**
     * The dataLog command
     */
    public String command;

    /**
     * The params of the command
     */
    public String[] params;

    /**
     * Create a dataLog Command from a fact string
     *
     * @param fact_string the fact string
     * @param fact fact object
     */
    public DataLogCommand(String fact_string, Fact fact) {
        Matcher matcherRule = pattern.matcher(fact_string);
        if (matcherRule.matches()) {
            this.command = matcherRule.group(1);
            this.params = matcherRule.group(2).split(",");//si une chaine de caractère contient des virgules entre quote, cela ne fonctionne pas
            for (int i = 0; i < this.params.length; i++) { //If the param start and ends with a quote, we delete it.
                if ((this.params[i].startsWith("'") && this.params[i].endsWith("'")) || (this.params[i].startsWith("\"") && this.params[i].endsWith("\""))) {
                    this.params[i] = this.params[i].substring(1, this.params[i].length() - 1);
                }
            }
        }
        this.fact = fact;
    }

    /**
     * Check if a fact string is a DataLog fact
     *
     * @param fact the fact string
     * @return true if the fact string is a DataLog fact else false
     */
    public static boolean isADataLogFact(String fact) {
        Matcher matcherRule = pattern.matcher(fact);
        return matcherRule.matches();
    }

    @Override
    public DataLogCommand clone() throws CloneNotSupportedException {
        DataLogCommand copie = (DataLogCommand) super.clone();
        copie.params = new String[this.params.length];
        System.arraycopy(this.params, 0, copie.params, 0, copie.params.length);
        return copie;
    }

    @Override
    public String toString() {
        return "DatalogFact [command=" + command + ", params="
                + Arrays.toString(params) + "]";
    }
}
