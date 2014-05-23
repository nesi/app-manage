package nz.org.nesi.appmanage

import com.google.common.collect.Sets
import groovy.io.FileType

/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 6/06/13
 * Time: 10:53 AM
 */
class Compare extends nz.org.nesi.appmanage.CompareCliParameters {


    public Compare() {

    }

    public void execute() {

        File targetRoot = new File(getInput())

        List<String> targetDirs = []

        targetRoot.eachFile(FileType.DIRECTORIES, { dir ->
            if ( dir.getName().startsWith(".") ) {
                return;
            }
            targetDirs.add(dir.getName());
        })

        List<String> appDirs = []
        getAppRoot().eachFile(FileType.DIRECTORIES, { dir ->
            if ( dir.getName().startsWith(".") ) {
                return;
            }
            appDirs.add(dir.getName());
        })


        if (showOnlyInModules() || (!showOnlyInApps() && !showOnlyInModules())) {
            List<File> onlyInTargetRoot = targetDirs.grep({ it -> !(appDirs.contains(it)) });
            println "Only in modules directory:\n"

            onlyInTargetRoot.sort( { a, b -> a.compareToIgnoreCase b } ).each { it ->
                println "\t"+it
            }
            println()
        }

        if (showOnlyInApps() || (!showOnlyInModules() && !showOnlyInApps())) {

            println "Only in applications directory:\n"
            List<File> onlyInAppDirs = appDirs.grep({ it -> !(targetDirs.contains(it)) });
            onlyInAppDirs.sort( { a, b -> a.compareToIgnoreCase b } ).each { it ->
                println "\t"+it
            }
            println()
        }

    }

    @Override
    void validate() {

    }
}
