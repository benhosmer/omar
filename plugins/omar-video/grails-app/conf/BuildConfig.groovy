grails.work.dir="${System.env.OMAR_DEV_HOME}/.grails"
grails.dependency.cache.dir = "${System.env.OMAR_DEV_HOME}/.grails/ivy-cache"

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
        mavenRepo "http://repo.grails.org/grails/plugins/"

        //grailsCentral()
        mavenCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.21'
    }

    plugins {

    compile( ":flash-player:1.4" ) {
      excludes "resources"
    }
    compile ":swfobject:2.2.1"
    runtime ":resources:1.1.6"

        build(":tomcat:$grailsVersion",
              ":release:2.2.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }
    }
}

grails.plugin.location.omarOGC= "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-ogc"
grails.plugin.location.omarStager= "${System.getenv( 'OMAR_DEV_HOME' )}/plugins/omar-stager"

