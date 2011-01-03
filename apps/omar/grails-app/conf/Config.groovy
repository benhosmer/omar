import grails.util.Environment

//import org.ossim.postgis.Geometry
//import org.ossim.postgis.GeometryType
//import com.vividsolutions.jts.geom.Geometry

import org.joda.time.*
import org.joda.time.contrib.hibernate.*

grails.gorm.default.mapping = {
  cache true
  id generator: 'identity'
  "user-type" type: PersistentDateTime, class: DateTime
  "user-type" type: PersistentLocalDate, class: LocalDate
//  'user-type'(type: GeometryType, class: Geometry)
}
// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts
if ( System.env.OMAR_CONFIG )
{
  grails.config.locations = ["file:${System.env.OMAR_CONFIG}"]
}

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text-plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data',
        kml: 'application/vnd.google+earth.kml+xml'
]

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
grails.serverIP = InetAddress.localHost.hostAddress

// set per-environment serverURL stem for creating absolute links
environments {
  development {
    databaseName = "omardb-${appVersion}-dev"
    grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
  }
  test {
    databaseName = "omardb-${appVersion}-test"
    grails.serverURL = "http://${grails.serverIP}:${System.properties['server.port'] ?: '8080'}/${appName}"
  }
  production {
    databaseName = "omardb-${appVersion}-prod"
    grails.serverURL = "http://${grails.serverIP}/${appName}"
  }
}

// log4j configuration
log4j = {
  // Example of changing the log pattern for the default console
  // appender:
  //
  appenders {
    appender new org.ossim.omar.DbAppender(name: "wmsLoggingAppender",
            threshold: org.apache.log4j.Level.INFO,
            tableMapping: [width: ":width", height: ":height", layers: ":layers", styles: ":styles",
                    format: ":format", request: ":request", bbox: ":bbox", internal_time: ":internalTime",
                    render_time: ":renderTime", total_time: ":totalTime", start_date: ":startDate",
                    end_date: ":endDate", user_name: ":userName", ip: ":ip", url: ":url", mean_gsd: ":meanGsd",
                    geometry: "ST_GeomFromText(:geometry, 4326)"],
            tableName: "wms_log"
    )
    appender new org.apache.log4j.DailyRollingFileAppender(name: "omarDataManagerAppender",
            datePattern: "'.'yyyy-MM-dd",
            file: "/tmp/logs/omarDataManagerAppender.log",
            layout: pattern(conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5} %m%n'))
    appender new org.apache.log4j.DailyRollingFileAppender(name: "omarAppender",
            datePattern: "'.'yyyy-MM-dd",
            file: "/tmp/logs/omar.log",
            layout: pattern(conversionPattern: '[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5} %m%n'))
  }

  info wmsLoggingAppender: 'grails.app.service.org.ossim.omar.WmsLogService', additivity: true
  info 'omarDataManagerAppender': '*DataManagerService', additivity: false
  info omarAppender: 'grails.app', additivity: false

  error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
          'org.codehaus.groovy.grails.web.pages', //  GSP
          'org.codehaus.groovy.grails.web.sitemesh', //  layouts
          'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
          'org.codehaus.groovy.grails.web.mapping', // URL mapping
          'org.codehaus.groovy.grails.commons', // core / classloading
          'org.codehaus.groovy.grails.plugins', // plugins
          'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'

  warn 'org.mortbay.log'
}
//log4j.logger.org.springframework.security='off,stdout'

/** *********************************************************************************************************/
wms {
  referenceDataDirectory = referenceDataDirectory ?: "/data/omar"
  mapServExt = (System.properties["os.name"].startsWith("Windows")) ? ".exe" : ""
  serverAddress = grails.serverIP
  useTileCache = true
  mapFile = "${referenceDataDirectory}/bmng.map"

  base {
    defaultOptions = [isBaseLayer: true, buffer: 0, transitionEffect: "resize"]
    layers = [
            [
                    url: (useTileCache) ? "http://${serverAddress}/tilecache/tilecache.py" : "http://${serverAddress}/cgi-bin/mapserv${mapServExt}?map=${mapFile}",
                    params: [layers: (useTileCache) ? "omar" : "Reference", format: "image/jpeg"],
                    name: "Reference Data",
                    options: defaultOptions
            ]
    ]
  }

  supportIE6 = true

  data {
    mapFile = null

    switch ( Environment.current.name.toUpperCase() )
    {
    case "DEVELOPMENT":
      mapFile = "${referenceDataDirectory}/omar-2.0-dev.map"
      break
    case "PRODUCTION":
      mapFile = "${referenceDataDirectory}/omar-2.0-prod.map"
      break
    case "TEST":
      mapFile = "${referenceDataDirectory}/omar-2.0-test.map"
      break
    }


    raster = [
            url: "${grails.serverURL}/ogc/footprints",
            params: [layers: (supportIE6) ? "Imagery" : "ImageData", format: (supportIE6) ? "image/gif" : "image/png"],
            name: "OMAR Imagery Coverage",
            options: [styles: "green", footprintLayers: "Imagery"]
    ]

    video = [
            url: "${grails.serverURL}/ogc/footprints",
            params: [layers: (supportIE6) ? "Videos" : "VideoData", format: (supportIE6) ? "image/gif" : "image/png"],
            name: "OMAR Video Coverage",
            options: [styles: "red", footprintLayers: "Videos"]

    ]
  }

  // Note the colors are normalized floats
  styles = [
          default: [
                  outlinecolor: [r: 0.0, g: 1.0, b: 0, a: 1.0],
                  width: 1
          ],
          red: [
                  outlinecolor: [r: 1.0, g: 0.0, b: 0.0, a: 1.0],
                  width: 1
          ],
          green: [
                  outlinecolor: [r: 0.0, g: 1.0, b: 0.0, a: 1.0],
                  width: 1
          ],
          blue: [
                  outlinecolor: [r: 0.0, g: 0.0, b: 1.0, a: 1.0],
                  width: 1
          ]
  ]

  vector {
    maxcount = 10000
  }
}


thumbnail {
  cacheDir = (System.properties["os.name"] == "Windows XP") ? "c:/temp" : "${wms.referenceDataDirectory}/omar-cache"
  defaultSize = 512
}

security {
  level = 'UNCLASS'
//level = 'SECRET'
//level = 'TOPSECRET'
  sessionTimeout = 60
}

image.download.prefix = "http://${grails.serverIP}"

/** ********************************* CONDITIONALS FOR VIEWS                   ***********************************************/
// flags for different views
//
views {
  home {
    // we can conditionally turn off browsing on the home page
    browseEnabled = true
  }
  mapView {
    defaultOverlayVisiblity = false
  }
}
/** *********************************************************************************************************/

videoStreaming {
  flashDirRoot = "/Library/WebServer/Documents/videos"
  //flashDirRoot = "/var/www/html/videos"
  flashUrlRoot = "http://${grails.serverIP}/videos"
}

rasterEntry {
  tagHeaderList = [
          "File Type",
          "Class Name",
          "Mission",
          "Country",
          "Target Id",
          "BE",
          "Sensor",
          "Image Id"
  ]


  tagNameList = [
          "fileType",
          "className",
          "missionId",
          "countryCode",
          "targetId",
          "beNumber",
          "sensorId",
          "title"
  ]

  searchTagData = [
          [name: "fileType", description: "File Type"],
          [name: "className", description: "Class Name"],
          [name: "missionId", description: "Mission"],
          [name: "countryCode", description: "Country"],
          [name: "targetId", description: "Target Id"],
          [name: "beNumber", description: "BE Number"],
          [name: "sensorId", description: "Sensor"],
          [name: "title", description: "Image Id"],
          [name: "niirs", description: "niirs"]
  ]
}

videoDataSet {
  searchTagData = [
          [name: "otherTagsXml.filename=", description: "Feed"]
  ]
}

login {
  registration {
    /**
     * registration has the following values:
     *  true: Allows users to register a new account by following the register link on the OMAR login page.
     * false: Prevents user registration and removes the register link from the OMAR login page. We recommend
     *        setting enabled to false if you are using LDAP for user authentication.
     */
    enabled = true

    /**
     * userVerification has the following values:
     *   none: Enables a new user account upon registration.
     * manual: Requires an administrator to enable new user accounts.
     *  email: Requires email verification before enabling the account, but also requires the modification
     *         of SecurityConfig.groovy in the omar-security plugin to specify your mail host settings.
     */
    userVerification = "none"

    if ( userVerification == "email" )
    {
      useMail = "true"
    }
  }
}

kml {
  maxImages = 100
  maxVideos = 100
  defaultImages = 10
  defaultVideos = 10
  daysCoverage = 30
  viewRefreshTime = 2
}


grails.doc.authors = "Garrett Potts"
grails.doc.license = "LGPL"
grails.doc.copyright = "RadiantBlue Technologies"
grails.doc.footer = ""
grails.doc.title = "OMAR"
grails.doc.subtitle = ""
grails.doc.logo = """<a href="http://www.ossim.org" ><img src="../img/OMAR.png" border="0"/></a>"""
grails.doc.sponsorLogo = """<a href="http://www.radiantblue.com" ><img src="../img/RBT.png" border="0"/></a>"""
grails.doc.images = new File("web-app/images")

ogcFilterQueryFields {
  raster {
    include = null
    exclude = null
    override = null
//    include=["niirs", "width", "height", "title"]
//    exclude=null
//    override=[title:[description:"ID of the image",
//                     label:"Image ID"
//                    ]
//             ]

  }
  video {
    include = null
    exclude = null
    override = null
  }
}

tomcat {
  servers = [
          localhost: [url: "http://localhost:8080/manager", username: "tomcat", password: "s3cret"]
  ]
}
