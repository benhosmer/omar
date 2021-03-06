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

        // Added to pick up joms jar from linux liboms rpm install location.
        flatDir name:"joms.jar", dirs:'/usr/share/java'

        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        def jomsVersion = System.getenv('OSSIM_VERSION')
        // runtime 'mysql:mysql-connector-java:5.1.21'
        compile "org.ossim:joms:${jomsVersion}"
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.2.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }
    }
}

grails.plugin.location.geoscript="${System.getenv('OMAR_DEV_HOME')}/plugins/geoscript"
