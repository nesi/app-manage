package nz.org.nesi.appmanage;

import java.io.File;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 30/10/13
 * Time: 10:34 AM
 */
abstract class AppManageModule {

    protected AppManage client;
    protected MainCliParameters mainParams;
    protected File appRoot;
    protected boolean verbose = false;

    abstract public void execute();
    abstract public void validate();

    public void setClient(AppManage client) {
        this.client = client;
        this.mainParams = client.getMainParams();
        this.appRoot = client.getMainParams().getApplicationsRootFile();
        this.verbose = client.getMainParams().isVerbose();
    }

    public void printMessage(String msg, boolean error) {
        if (error) {
            System.err.println(msg);
        } else if (verbose) {
            System.out.println(msg);
        }
    }

    public void printMessage(String msg) {
        printMessage(msg, false);
    }

    AppManage getClient() {
        return client;
    }

    MainCliParameters getMainParams() {
        return mainParams;
    }

    File getAppRoot() {
        return appRoot;
    }

    boolean isVerbose() {
        return verbose;
    }
}
