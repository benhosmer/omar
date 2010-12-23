package org.ossim.omar

import org.springframework.beans.factory.InitializingBean
import org.codehaus.groovy.grails.plugins.springsecurity.RedirectUtils

import org.springframework.security.DisabledException
import org.springframework.security.ui.AbstractProcessingFilter
import org.springframework.security.ui.openid.OpenIDConsumerException
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter


import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserImpl
import org.springframework.security.context.SecurityContextHolder as SCH
import org.springframework.security.providers.UsernamePasswordAuthenticationToken
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.springframework.security.GrantedAuthorityImpl
import org.springframework.security.GrantedAuthority

/**
 * Login Controller (Example).
 */
class LoginController implements InitializingBean
{
  def registerFlag;
  /**
   * Dependency injection for the authentication service.
   */
  def authenticateService

  /**
   * Dependency injection for OpenIDConsumer.
   */
  def openIDConsumer

  /**
   * Dependency injection for OpenIDAuthenticationProcessingFilter.
   */
  def openIDAuthenticationProcessingFilter

  def index = {

    if ( isLoggedIn() )
    {
      redirect(uri: '/')
    }
    else
    {
      redirect(action: auth, params: params)
    }
  }

  def about = {
    render(view: 'about')
  }

  /**
   * Show the login page.
   */
  def auth = {
    def model = [:]
    model.registerFlag = registerFlag
    nocache(response)

    //println ApplicationHolder.application.mainContext.authenticationProcessingFilter.class.name

    if ( isLoggedIn() )
    {
      redirect(uri: '/')
    }

    if ( authenticateService.securityConfig.security.useOpenId )
    {
      render(view: 'openIdAuth')
    }
    else
    {
      render(view: 'auth', model: model)
    }
  }

  /**
   * Form submit action to start an OpenID authentication.
   */
  def openIdAuthenticate = {
    String openID = params['j_username']
    try
    {
      String returnToURL = RedirectUtils.buildRedirectUrl(
              request, response, openIDAuthenticationProcessingFilter.filterProcessesUrl)
      String redirectUrl = openIDConsumer.beginConsumption(request, openID, returnToURL)
      redirect(url: redirectUrl)
    }
    catch (OpenIDConsumerException e)
    {
      log.error "Consumer error: ${e.message}", e
      redirect(url: openIDAuthenticationProcessingFilter.authenticationFailureUrl)
    }
  }


  def autocreate = {

    //println session

    def ldapAuth = session[LdapAuthenticationProcessingFilter.LDAP_LAST_AUTH]
    def authorities = session[AutoCreateLdapUserDetailsMapper.LDAP_AUTOCREATE_CURRENT_AUTHORITIES]

    session.removeAttribute(LdapAuthenticationProcessingFilter.LDAP_LAST_AUTH)
    session.removeAttribute(AutoCreateLdapUserDetailsMapper.LDAP_AUTOCREATE_CURRENT_AUTHORITIES)

    //println "ldapAuth: ${ldapAuth}"
    //println "authorities: ${authorities}"

    // Still need default values,  can we get these from LDAP?!?!?
    def user = new AuthUser(
            username: ldapAuth?.name,
            userRealName: "user",
            passwd: 'notused',
            enabled: true,
            email: "user@user.com",
            emailShow: false,
            description: "Normal User"
    )

    // if no authorities specifed,  default to ROLE_USER
    if ( !authorities )
    {
      authorities = [new GrantedAuthorityImpl("ROLE_USER")] as GrantedAuthority[]
    }

    if ( !user.save(flush: true) )
    {
      // TODO handle error
    }

    // manually authenticate now since they're a valid LDAP user and now
    // have a corresponding database user too
    def userDetails = new GrailsUserImpl(user.username, user.passwd,
            true, true, true, true, authorities, user)

    SCH.context.authentication = new UsernamePasswordAuthenticationToken(
            userDetails, user.passwd, authorities)

    // redirect to originally-requested URL if there's a saved request
    def savedRequest = session[AbstractProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY]
    if ( savedRequest )
    {
      redirect url: savedRequest.fullRequestUrl
    }
    else
    {
      redirect uri: '/'
    }

  }

  // Login page (function|json) for Ajax access.
  def authAjax = {
    nocache(response)
    //this is example:
    render """
		<script type='text/javascript'>
		(function() {
			loginForm();
		})();
		</script>
		"""
  }

  /**
   * The Ajax success redirect url.
   */
  def ajaxSuccess = {
    nocache(response)
    render '{success: true}'
  }

  /**
   * Show denied page.
   */
  def denied = {
    redirect(uri: '/')
  }

  // Denial page (data|view|json) for Ajax access.
  def deniedAjax = {
    //this is example:
    render "{error: 'access denied'}"
  }

  /**
   * login failed
   */
  def authfail = {

    def username = session[AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY]
    def msg = ''
    def exception = session[AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY]
    if ( exception )
    {
      if ( exception instanceof DisabledException )
      {
        msg = "[$username] is disabled."
      }
      else
      {
        msg = "[$username] wrong username/password."
      }
    }

    if ( isAjax() )
    {
      render("{error: '${msg}'}")
    }
    else
    {
      flash.message = msg
      redirect(action: auth, params: params)
    }
  }

  /**
   * Check if logged in.
   */
  private boolean isLoggedIn()
  {
    def authPrincipal = authenticateService.principal()
    return authPrincipal != null && authPrincipal != 'anonymousUser'
  }

  private boolean isAjax()
  {
    return authenticateService.isAjax(request)
  }

  /** cache controls            */
  private void nocache(response)
  {
    response.setHeader('Cache-Control', 'no-cache') // HTTP 1.1
    response.addDateHeader('Expires', 0)
    response.setDateHeader('max-age', 0)
    response.setIntHeader('Expires', -1) //prevents caching at the proxy server
    response.addHeader('cache-Control', 'private') //IE5.x only
  }

  public void afterPropertiesSet()
  {
    registerFlag = grailsApplication.config.login?.registration?.enabled
  }
}
