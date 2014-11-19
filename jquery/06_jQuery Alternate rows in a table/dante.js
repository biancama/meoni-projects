$(document).ready(function() {  
	//$('tr:eq(0)').addClass('title');
	//$('tr:nth-child(1)').addClass('title');
	
	$('tr:first-child').addClass('title');
	
	// Alternate
	//$('tr:odd').addClass('alt');
	$('tr:nth-child(even)').addClass('alt');
	$('td:contains(Commedia)').addClass('highlight');
});
