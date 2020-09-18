package server.scoring;

import org.apache.commons.math3.stat.descriptive.summary.Sum;
import server.attackgraph.ImpactMetric;


public class ScoringFormulas {

    /**
     * The Sum.
     */
    private Sum sum = new Sum();

    /**
     * Compute the global score.
     *
     * @param attackPath the attack path
     * @return the gobal score
     */
    public double globalScore(Graph attackPath) {
        Vertex[] vertices = attackPath.getVertices();
        Arc[] arcs = attackPath.getArcs();
        double RiskScore = riskScore(vertices, arcs);
        double ImpactScore = impactScore(attackPath);
        return RiskScore + ImpactScore;
    }

    /**
     * Compute the risk score.
     *
     * @param vertices the graph vertices
     * @param arcs     the graph arcs
     * @return the risk score
     */
    public double riskScore(Vertex[] vertices, Arc[] arcs) {
        double[] RANDTable;
        double[] RORTable;
        double[] RLEAFTable;
        double RAND = 0, ROR = 0, RLEAF = 0, a = 0, o = 0, l = 0;
        Vertex[] ANDVertices = Graph.getVerticesOnType(vertices, "AND");
        Vertex[] ORVertices = Graph.getVerticesOnType(vertices, "OR");
        Vertex[] LEAFVertices = Graph.getVerticesOnType(vertices, "LEAF");
        if (ANDVertices != null) {
            a = ANDVertices.length;
        }
        if (ORVertices != null) {
            o = ORVertices.length;
        }
        if (LEAFVertices != null) {
            l = LEAFVertices.length;
        }
        if (ANDVertices != null) {
            RANDTable = new double[ANDVertices.length];
            for (int i = 0; i < ANDVertices.length; i++) {
                double OutgoingArcs = Graph.getOutgoingArcsNumber(arcs, ANDVertices[i].getID());
                double IngoingArcs = Graph.getIngoingArcsNumber(arcs, ANDVertices[i].getID());
                double CumulativeScore = ANDVertices[i].getMulvalMetric();
                RANDTable[i] = (CumulativeScore * (OutgoingArcs / IngoingArcs)) / a;
            }
            RAND = getSum().evaluate(RANDTable, 0, RANDTable.length);
        }
        if (ORVertices != null) {
            RORTable = new double[ORVertices.length];
            for (int i = 0; i < ORVertices.length; i++) {
                double OutgoingArcs = Graph.getOutgoingArcsNumber(arcs, ORVertices[i].getID());
                double IngoingArcs = Graph.getIngoingArcsNumber(arcs, ORVertices[i].getID());
                double CumulativeScore = ORVertices[i].getMulvalMetric();
                RORTable[i] = CumulativeScore * OutgoingArcs * IngoingArcs * o;
            }
            ROR = getSum().evaluate(RORTable, 0, RORTable.length);
        }
        if (LEAFVertices != null) {
            RLEAFTable = new double[LEAFVertices.length];
            for (int i = 0; i < LEAFVertices.length; i++) {
                double OutgoingArcs = Graph.getOutgoingArcsNumber(arcs, LEAFVertices[i].getID());
                RLEAFTable[i] = OutgoingArcs / l;//IngoingArcs=0 and CumulativeScore=1 for all LEAF vertices
            }
            RLEAF = getSum().evaluate(RLEAFTable, 0, RLEAFTable.length);
        }
        return RAND + ROR + RLEAF;
    }

    /**
     * Compute the impact score.
     *
     * @param attackPath the graph to score
     * @return the impact score
     */
    public double impactScore(Graph attackPath) {
        double[] ImpactTable = new double[attackPath.getVertices().length];

        for (int i = 0; i < attackPath.getVertices().length; i++) {
            ImpactTable[i] = 0.;
            Vertex vertex = attackPath.getVertices()[i];
            if (vertex.getImpactMetrics() != null) {
                double impactElement = 0.;
                for (int j = 0; j < vertex.getImpactMetrics().length; j++) {
                    ImpactMetric impactMetric = vertex.getImpactMetrics()[j];
                    impactElement += impactMetric.getValue() * impactMetric.getWeight();
                }
                ImpactTable[i] = impactElement;
            }
        }
        return getSum().evaluate(ImpactTable, 0, ImpactTable.length);
    }

    /**
     * Compute min max.
     *
     * @param globalRawScore the global raw score
     * @param maxGlobalScore the max global score
     * @return the min max.
     */
    public double MinMax(double globalRawScore, double maxGlobalScore) {
        return (globalRawScore / (1. * maxGlobalScore));
    }

    /**
     * Gets sum.
     *
     * @return the sum
     */
    public Sum getSum() {
        return sum;
    }

}
