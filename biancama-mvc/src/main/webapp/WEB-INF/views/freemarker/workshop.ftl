<html>
<head><title>Biancama FreeMarker Spring MVC</title>
<body>
<div id="header">
<H2>
    Workshops of today
</H2>
</div>
 
<div id="content">
<table style="border: 1px solid; width: 500px; text-align:center">
 <thead style="background:#fcf">
  <tr>
   <th>Title</th>
   <th>Teacher</th>
  </tr>
 </thead>
 <tbody>
 <#list model["workshops"] as workshop>
  <tr>
   <td>${workshop.title}</td>
   <td>${workshop.teacher.username}</td>
  </tr>
  </#list>
 </tbody>
</table>

<#if ! model["workshops"]?has_content >
 There are currently no workshops today for that user .
</#if>

</div> 
</body>
