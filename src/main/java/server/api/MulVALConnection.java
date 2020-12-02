package server.api;

import java.io.BufferedReader;
import server.attackgraph.AttackGraph;
import server.attackgraph.MulvalAttackGraph;
import server.database.Database;
import server.hostInformation.InformationSystem;
import server.properties.ProjectProperties;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulVALConnection {

    /**
     * Load the information system from an XML File
     *
     * @param XMLInformationSystemFile the XML file path
     * @return the InformationSystem object
     */
    public static InformationSystem loadTopologyXMLFile(String XMLInformationSystemFile, Database db) {
        InformationSystem result = new InformationSystem();
        try {
            result.loadFromXMLFile(XMLInformationSystemFile, db);
            return result;
        } catch (Exception e) {
            Logger.getLogger(MulVALConnection.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    /**
     * call MulVAL attack Graph Generation file
     *
     * @return the associated attack graph object
     */
    public static AttackGraph generateAttackGraphWithMulValUsingAlreadyGeneratedMulVALInputFile() {
        try {
            //Load all input informations
            String mulvalPath = ProjectProperties.getInstance().getProperty("MulVAL");
            String xsbPath = ProjectProperties.getInstance().getProperty("xsb");
            String outputFolderPath = ProjectProperties.getInstance().getProperty("tmp-output");

            File mulvalInputFile = new File(ProjectProperties.getInstance().getProperty("MulVAL-input"));

            File mulvalOutputFile = new File(outputFolderPath + "/AttackGraph.xml");
            if (mulvalOutputFile.exists()) {
                mulvalOutputFile.delete();
            }

            Logger.getLogger(MulVALConnection.class.getName()).log(Level.INFO, "Call MulVAL attack Generation");
            ProcessBuilder processBuilder = new ProcessBuilder(mulvalPath + "utils/graph_gen.sh", mulvalInputFile.getAbsolutePath(), "-l");

            if (ProjectProperties.getInstance().getProperty("MulVAL-rules") != null) {
                processBuilder.command().add("-r");
                processBuilder.command().add(ProjectProperties.getInstance().getProperty("MulVAL-rules"));
            }

            processBuilder.command().add("-o");
            processBuilder.command().add(outputFolderPath + "/AttackGraph");

            processBuilder.directory(new File(outputFolderPath));
            processBuilder.environment().put("MULVALROOT", mulvalPath);
            String path = System.getenv("PATH");
            processBuilder.environment().put("PATH", mulvalPath + "utils/:" + xsbPath + ":" + path);
            Logger.getAnonymousLogger().log(Level.INFO, processBuilder.command().toString());
            try {
                Process process = processBuilder.start();
                StringBuilder output = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }

                System.out.println(line);
                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    System.out.println("Success!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!mulvalOutputFile.exists()) {
                Logger.getLogger(MulVALConnection.class.getName()).log(Level.INFO, "have some problem , MulVAL can't generate attack Graph!");
                return null;
            }

            MulvalAttackGraph ag = new MulvalAttackGraph(mulvalOutputFile.getAbsolutePath());

            return ag;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * call python program that help generate MulVAL input file
     *
     * @return boolean true if the execution was right
     */
    public static boolean prepareMulVALInputs(boolean isTest) {
        try {
            //Load python path
            String pythonPath = ProjectProperties.getInstance().getProperty("python");
            //Load MulVAL input generation python main file
            String mulvalInputScriptFolder = ProjectProperties.getInstance().getProperty("server-helper");
            String mulvalInputScriptPath = mulvalInputScriptFolder + "main.py";

            String hosts, vlans, routing, networkFirewall, nessusReport, attackerLocation;

            //load inputs file that get from user
            if (isTest) {
                String test = "-test";
                hosts = ProjectProperties.getInstance().getProperty("hosts" + test);
                vlans = ProjectProperties.getInstance().getProperty("vlans" + test);
                routing = ProjectProperties.getInstance().getProperty("routing" + test);
                networkFirewall = ProjectProperties.getInstance().getProperty("network-firewall" + test);
                nessusReport = ProjectProperties.getInstance().getProperty("nessus-report" + test);
                attackerLocation = ProjectProperties.getInstance().getProperty("attacker-location" + test);
            } else {
                hosts = ProjectProperties.getInstance().getProperty("hosts");
                vlans = ProjectProperties.getInstance().getProperty("vlans");
                routing = ProjectProperties.getInstance().getProperty("routing");
                networkFirewall = ProjectProperties.getInstance().getProperty("network-firewall");
                nessusReport = ProjectProperties.getInstance().getProperty("nessus-report");
                attackerLocation = ProjectProperties.getInstance().getProperty("attacker-location");
            }
            String MulVALInput = ProjectProperties.getInstance().getProperty("MulVAL-input");
            String topologyFile = ProjectProperties.getInstance().getProperty("output-topology");

            File mulvalInputFile = new File(MulVALInput);
            if (mulvalInputFile.exists()) {
                mulvalInputFile.delete();
            }

            Logger.getLogger(MulVALConnection.class.getName()).log(Level.INFO, "Genering MulVAL inputs");

            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, mulvalInputScriptPath,
                    "--hosts-interfaces-file", hosts,
                    "--vlans-file", vlans,
                    "--flow-matrix-file", networkFirewall,
                    "--vulnerability-scan", nessusReport,
                    "--routing-file", routing,
                    "--mulval-output-file", mulvalInputFile.getAbsolutePath(),
                    "--attackerlocation", attackerLocation,
                    "--to-fiware-xml-topology", topologyFile,
                    "-vv"
            );
            processBuilder.directory(new File(mulvalInputScriptFolder));
            StringBuilder command = new StringBuilder();
            for (String str : processBuilder.command()) {
                command.append(str + " ");
            }
            Logger.getLogger(MulVALConnection.class.getName()).log(Level.INFO, "Call MulVAL input generation pythin files with command : \n" + command.toString());
            processBuilder.redirectOutput(new File(ProjectProperties.getInstance().getProperty("tmp-output") + "/input-generation.log"));
            processBuilder.redirectError(new File(ProjectProperties.getInstance().getProperty("tmp-output") + "/input-generation-err.log"));
            Process process = processBuilder.start();
            process.waitFor();

            if (!mulvalInputFile.exists()) {
                Logger.getAnonymousLogger().log(Level.WARNING, "have some problem for generate MulVAL input file , check log file");
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
