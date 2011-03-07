<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Edit Permission ${requestmap.id}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
	<span class="menuButton"><g:link class="list" action="list">Permission List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">Create Permission</g:link></span>
  </div>
  <div class="body">
    <h1>OMAR: Edit Permission ${requestmap.id}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
	</g:if>
    <g:hasErrors bean="${requestmap}">
      <div class="errors">
	    <g:renderErrors bean="${requestmap}" as="list"/>
	  </div>
    </g:hasErrors>
    <g:form method="post">
      <input type="hidden" name="id" value="${requestmap?.id}"/>
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="url">URL:</label>
			</td>
            <td valign="top" class="value ${hasErrors(bean:requestmap,field:'url','errors')}">
		      <input type="text" id="url" name="url" value="${requestmap?.url?.encodeAsHTML()}"/>
			</td>
          </tr>
          <tr class="prop">
            <td valign="top" class="name">
			  <label for="configAttribute">Roles (comma-delimited):</label>
			</td>
<%
def names = []
org.springframework.util.StringUtils.commaDelimitedListToStringArray(requestmap.configAttribute).each { role ->
	if (role.startsWith('ROLE_')) {
		names << role.substring(5).toLowerCase().encodeAsHTML()
	}
	else {
		names << role.encodeAsHTML()
	}
}
%>
            <td valign="top" class="value ${hasErrors(bean:requestmap,field:'configAttribute','errors')}">
		      <input type="text" name='configAttribute'  value="${names.join(',')}"/>
			</td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="buttons">
        <span class="button"><g:actionSubmit class="save" value="Update"/></span>
		<span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
	</div>
    </g:form>
  </div>
</content>
</body>
</html>