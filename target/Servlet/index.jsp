<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ge.itestep.Lesson_Four.Task" %>
<jsp:useBean id="task" class="ge.itestep.Lesson_Four.Task" scope="request" />
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>

<ul>
    <c:forEach var="task" items="${tasks}">
        <li><c:out value="${task.task}" /></li>
    </c:forEach>
</ul>
<form action="/Servlet/welcome" method="post">

    <input type="text" name="task" placeholder="task">
    <textarea name="note"></textarea>
    <input type="checkbox" value="1" name="iscompleted">
    <input type="date" name="duedate">
    <button type="submit">submit</button>

</form>
</body>
</html>