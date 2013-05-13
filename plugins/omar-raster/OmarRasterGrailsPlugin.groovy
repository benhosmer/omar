import org.ossim.omar.raster.RasterEntrySearchService
import org.ossim.omar.raster.RasterEntryQuery
import org.ossim.omar.raster.RasterInfoParser
import java.awt.Color

class OmarRasterGrailsPlugin
{
  // the plugin version
  def version = "0.3"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.2.2 > *"
  // the other plugins this plugin depends on
  def dependsOn = [:]
  // resources that are excluded from plugin packaging
  def pluginExcludes = [
      "grails-app/views/error.gsp"
  ]

  def loadAfter = ['omarCore']

  // TODO Fill in these fields
  def author = "Scott Bortman"
  def authorEmail = "sbortman@radiantblue.com"
  def title = "OMAR Raster"
  def description = '''\\
OMAR Raster support
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/omar-raster"

  def doWithWebDescriptor = { xml ->
  }

  def doWithSpring = {
    // TODO Implement runtime spring config (optional)
    rasterInfoParser( RasterInfoParser )

//    imageDataQueryParam(org.ossim.omar.raster.RasterEntryQuery) { bean ->
    //      bean.singleton = false
    //    }


    imageryQueryParam( RasterEntryQuery ) { bean ->
      bean.singleton = false
    }

    imagerySearchService( RasterEntrySearchService ) {
      grailsApplication = ref( "grailsApplication" )
    }

    /*
    byFileType( org.ossim.omar.raster.PropertyNameStyle ) { bean ->
      propertyName = 'fileType'
      outlineLookupTable = [
          //aaigrid: 4,
          cadrg: new Color( 0, 255, 255, 255 ),
          cib: new Color(0,128,128,255),
          ccf: new Color( 128, 100, 255, 255 ),
          adrg: new Color( 50, 111, 111, 255 ),
          mrsid: new Color(0,188,0,255),
          //doqq: 2,
          dted: new Color( 0, 255, 0, 255 ), // green
          jpeg: new Color( 255, 255, 0, 255 ), // yellow
          jpeg2000: new Color( 255, 200, 0, 255 ), //
          landsat7: new Color( 255, 0, 255, 255 ), // purple
          nitf: new Color( 0, 0, 255, 255 ),  // blue
          tiff: new Color( 255, 0, 0, 255 ),  // red
          mpeg: new Color(164,254,255,255),
          unspecified: new Color(255,255,255,255) // white
      ]
    }

    bySensorId( org.ossim.omar.raster.PropertyNameStyle ) { bean ->
      propertyName = 'sensorId'
      outlineLookupTable = [
          'ACES_YOGI-HRI1': new Color( 255, 0, 0, 255 ),
          'ACES_YOGI-HRI2': new Color( 255, 0, 0, 255 ),
          'ACES_YOGI-HRI3': new Color( 255, 0, 0, 255 ),
          'ACES_YOGI-HSI': new Color( 255, 255, 0, 255 ),
          ALPHA: new Color( 255, 0, 255, 255 ),
          BRAVO: new Color( 0, 255, 0, 255 ),
          CHARLIE: new Color( 0, 255, 255, 255 ),
          DELTA: new Color( 0, 0, 255, 255 ),
          unspecified: new Color(255,255,255,255) // white
      ]
    }


    red( org.ossim.omar.raster.PropertyNameStyle ) { bean ->
      propertyName = 'id'
      defaultOutlineColor = new Color( 255, 0, 0, 255 )  // red
    }
    */
  }

  def doWithDynamicMethods = { ctx ->
    // TODO Implement registering dynamic methods to classes (optional)
  }

  def doWithApplicationContext = { applicationContext ->
    applicationContext.registerAlias( "imageryQueryParam", "imageDataQueryParam" )
    applicationContext.registerAlias( "imagerySearchService", "imageDataSearchService" )

    def params = [propertyName:"fileType",
            outlineLookupTable:[:]]
    def styles = application.config.rasterEntry?.styles
    def beanNames = []
    def beans = beans{
      styles?.each{style->
         def beanName = "by${style?.propertyName.capitalize()}"
         beanNames << beanName
         "${beanName}"(org.ossim.omar.raster.PropertyNameStyle){bean->
          propertyName = style.propertyName
          outlineLookupTable = style.outlineLookupTable
          fillLookupTable = style.fillLookupTable?:[:]
          defaultFillColor = style.defaultFillColor?:new Color( 0, 0, 0, 0 )
          defaultOutlineColor = style.defaultOutlineColor?:new Color( 255, 255, 255, 255 )
        }
      }
    }

    beanNames.each{beanName->
      applicationContext.registerBeanDefinition(beanName,
              beans.getBeanDefinition(beanName))
    }
      //applicationContext.registerBeanDefinition("byFileType",
      //        beans.getBeanDefinition("byFileType"))
    //}

    //println fileType?.propertyName
  }

  def onChange = { event ->
    // TODO Implement code that is executed when any artefact that this plugin is
    // watching is modified and reloaded. The event contains: event.source,
    // event.application, event.manager, event.ctx, and event.plugin.
  }

  def onConfigChange = { event ->
    // TODO Implement code that is executed when the project configuration changes.
    // The event is the same as for 'onChange'.
  }
}
