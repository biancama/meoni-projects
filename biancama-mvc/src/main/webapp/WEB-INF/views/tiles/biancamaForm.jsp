<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>

<div>
     
  <!--<start id="securityAuthorizeTag_access"/>--> 
	<sec:authorize access="hasRole('ROLE_ADMIN')">
	<!--<co id="co_renderForRoleBiancama"/>-->
    <s:url value="/biancama-mvc" var="biancama_url" />
	  <sf:form modelAttribute="biancama" 
	             method="POST" 
	             action="${spittle_url}">
	    <sf:label path="text"><s:message code="label.biancama" 
                         text="Enter Biancama:"/></sf:label>
	    <sf:textarea path="text" rows="2" cols="40" />  
	        <sf:errors path="text" />
	        
	    <br/>
	    <div class="biancamaSubmitIt">
	      <input type="submit" value="Do it!" 
	           class="status-btn round-btn disabled" />
	    </div>           
	  </sf:form>
	</sec:authorize>
  <!--<end id="securityAuthorizeTag_access"/>--> 
</div>
