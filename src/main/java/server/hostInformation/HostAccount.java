package server.hostInformation;

import server.topology.component.Host;

/**
 * Class that represents a user account
 *
 */
public class HostAccount implements Cloneable {
    /**
     * The machine on which is the account
     */
    private Host machine;

    /**
     * The user name
     */
    private String name;

    /**
     * Create an account from a account name
     *
     * @param name the name of the account
     */
    public HostAccount(String name) {
        this.setName(name);
    }

    /**
     * Get the machine on which is the account
     */
    public Host getMachine() {
        return machine;
    }

    /**
     * Set the machine of the account
     *
     * @param machine the machine to set to this account
     */
    public void setMachine(Host machine) {
        this.machine = machine;
    }

    /**
     * Get the username
     */
    public String getName() {
        return name;
    }

    /**
     * Set the user name
     *
     * @param name the user name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMachine() == null) ? 0 : getMachine().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public HostAccount clone() throws CloneNotSupportedException {
        HostAccount copie = (HostAccount) super.clone();
        return copie;
    }
}
