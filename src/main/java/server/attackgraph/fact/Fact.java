package server.attackgraph.fact;

import server.attackgraph.Vertex;


public class Fact implements Cloneable {
    /**
     * The string of the fact
     */
    public String factString = "";
    /**
     * The type of fact
     */
    public FactType type;
    /**
     * If the fact contains a rule, refer to this rule
     */
    public Rule factRule = null;
    /**
     * If the fact contains a Datalog command, refer to this Datalog command
     */
    public DataLogCommand datalogCommand = null;
    /**
     * The related attack graph vertex
     */
    public Vertex attackGraphVertex = null;

    /**
     * Create a new fact from a string
     *
     * @param fact the fact string
     */
    public Fact(String fact, Vertex vertex) {
        this.factString = fact;
        if (Rule.isARule(fact)) {
            factRule = new Rule(fact);
            type = FactType.RULE;
        } else if (DataLogCommand.isADataLogFact(fact)) {
            this.datalogCommand = new DataLogCommand(fact, this);
            type = FactType.DATALOG_FACT;
        }
        this.attackGraphVertex = vertex;
    }

    @Override
    public Fact clone() throws CloneNotSupportedException {
        Fact copie = (Fact) super.clone();
        if (copie.factRule != null)
            copie.factRule = this.factRule.clone();
        if (this.datalogCommand != null) {
            copie.datalogCommand = this.datalogCommand.clone();
            copie.datalogCommand.fact = copie;
        }

        return copie;
    }

    @Override
    public String toString() {
        return "Fact [factDatalog=" + datalogCommand + ", factRule=" + factRule
                + ", factString=" + factString + ", type=" + type + "]";
    }

}
