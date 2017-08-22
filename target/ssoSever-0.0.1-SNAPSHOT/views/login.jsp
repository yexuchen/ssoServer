<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/8/18
  Time: 10:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="/ssoServer/sso/user/login">
        <input type="text" name="userName"><br>
        <input type="text" name="password"><br>
        <input type="text" name="returnUrl" value="<%=request.getParameter("returnUrl")%>"><br>
        <input type="submit">
    </form>
</body>
</html>
