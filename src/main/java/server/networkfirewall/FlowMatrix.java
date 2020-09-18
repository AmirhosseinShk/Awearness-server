package server.networkfirewall;
import server.topology.Topology;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to represent the flow matrix. A flow matrix is a set of flow matrix lines.
 *
 */
public class FlowMatrix {
    /*
     * The flow matrix lines
     */
    private List<FlowMatrixLine> flowMatrixLines = new ArrayList<>();

    /**
     * Create a flow matrix from a XML DOM element
     *
     * @param element  the XML DOM element
     * @param topology the network topology
     */
    public FlowMatrix(Element element, Topology topology) {
        if (element != null) {
            for (Element flowMatrixLinesElement : element.getChildren("flow-matrix-line")) {
                getFlowMatrixLines().add(new FlowMatrixLine(flowMatrixLinesElement, topology));
            }
        }
    }

    /**
     * @return all lines of the flow matrix
     */
    public List<FlowMatrixLine> getFlowMatrixLines() {
        return flowMatrixLines;
    }

}
