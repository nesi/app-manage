package nz.org.nesi.appmanage

import com.beust.jcommander.internal.Lists

/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 1/11/13
 * Time: 4:29 PM
 */
class CreateLoadLevelerJobDescription {

    static List<String> prettyLLjob(def desc) {

        if (desc instanceof String) {
            desc = new File(desc)
        }

        List<String> result = Lists.newLinkedList()

        boolean environment = false
        boolean startStrict = false

        for (String line : desc.readLines()) {
            if (line.equals('# @ environment = \\')) {
                environment = true
                continue
            }

            if (environment) {
                if (line.startsWith('# @')) {
                    environment = false
                } else {
                    continue
                }
            }

            if (line.equals('# @ queue')) {
                startStrict = true
                result.add(line)
                continue
            }

            if (!startStrict) {
                if (!line.startsWith('# @')) {
                    continue
                } else {
                    result.add(line)
                    continue
                }
            } else {
                if (line.startsWith("#")) {
                    continue
                }

                if (line.contains('>.job.ll') || line.contains('>>.job.ll')) {
                    continue
                }
            }

            result.add(line)

        }
        return result
    }

    static void main(String[] args) {

        List<String> ll = prettyLLjob('/home/markus/Desktop/job.ll')

        ll.each {
            println it
        }

//        ServiceInterface si = LoginManager.login("bestgrid")
//
//        GJob gJob = new GJob('/home/markus/src/config/applications/R/jobs/R_MPI_SNOW_simple');
//
//        GrisuJob job = gJob.createJobDescription(si);
//
//        job.submitJob(true)
//        println 'waiting'
//        job.waitForJobToFinish(3)
//
//        File file = job.downloadAndCacheOutputFile('.job.ll')
//
//        println "downloading"
//        FileUtils.moveFile(file, new File('/home/markus/Desktop/job.ll'))
//
//        System.exit(0)
    }

}
