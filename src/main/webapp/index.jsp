<!DOCTYPE html>
<%
   String contextPath = getServletContext().getContextPath();
%>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Errai - Tutorial</title>
<meta name="description" content="">
<meta name="author" content="">

<link href="<%=contextPath%>/bootstrap/css/bootstrap.css" rel="stylesheet">
<link href="<%=contextPath%>/bootstrap/css/bootstrap-responsive.css" rel="stylesheet">
<link href="<%=contextPath%>/css/application.css" rel="stylesheet">

<link href='http://fonts.googleapis.com/css?family=Gudea:400,700' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Inconsolata' rel='stylesheet' type='text/css'>

<link rel="shortcut icon" href="<%=contextPath%>/favicon.ico">
<link rel="apple-touch-icon" href="<%=contextPath%>/favicon.ico">

<script type="text/javascript" src="<%=contextPath%>/app/app.nocache.js"></script>
</head>

<body>
  <div id="rootPanel"></div>

  <iframe src="javascript:''" id="__gwt_historyFrame" style="width: 0; height: 0; border: 0"></iframe>

  <script src="<%=contextPath%>/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>
