
<%@ page import="org.ossim.omar.Report" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'report.label', default: 'Report')}" />
        <title>OMAR: List Reports</title>
    </head>
    <body>
    <content tag="content">
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">OMAR Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">Create Report</g:link></span>
        </div>
        <div class="body">
            <h1>List Reports</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'report.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'report.name.label', default: 'Name')}" />
                        
                            <g:sortableColumn property="email" title="${message(code: 'report.email.label', default: 'Email')}" />
                        
                            <g:sortableColumn property="createdDate" title="${message(code: 'report.createdDate.label', default: 'Created Date')}" />
                        
                            <g:sortableColumn property="report" title="${message(code: 'report.report.label', default: 'Report')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${reportInstanceList}" status="i" var="reportInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${reportInstance.id}">${fieldValue(bean: reportInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: reportInstance, field: "name")}</td>
                        
                            <td>${fieldValue(bean: reportInstance, field: "email")}</td>
                        
                            <td><g:formatDate date="${reportInstance.createdDate}" /></td>
                        
                            <td>${fieldValue(bean: reportInstance, field: "report")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${reportInstanceTotal}" />
            </div>
        </div>
      </content>
    </body>
</html>
