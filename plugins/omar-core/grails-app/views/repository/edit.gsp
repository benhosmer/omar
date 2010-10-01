<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="generatedViews"/>
  <title>OMAR: Edit Repository ${fieldValue(bean: repository, field: 'id')}</title>
</head>
<body>
<content tag="content">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <span class="menuButton"><g:link class="list" action="list">Repository List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">Create Repository</g:link></span>
  </div>
  <div class="body">
    <h1>OMAR: Edit Repository ${fieldValue(bean: repository, field: 'id')}</h1>
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${repository}">
      <div class="errors">
        <g:renderErrors bean="${repository}" as="list"/>
      </div>
    </g:hasErrors>
    <g:form method="post">
      <input type="hidden" name="id" value="${repository?.id}"/>
      <div class="dialog">
        <table>
          <tbody>
          <tr class="prop">
            <td valign="top" class="name">
              <label for="baseDir">Base Dir:</label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: repository, field: 'baseDir', 'errors')}">
              <input type="text" id="baseDir" name="baseDir" value="${fieldValue(bean: repository, field: 'baseDir')}" size="128"/>
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