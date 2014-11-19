$(document).ready(function() {  
  $('a[href^="mailto:"]').addClass('mailto');
  $('a[href$=".pdf"]').addClass('pdflink');
  //$('a[href*="bianca"]').not('[href^="mailto:"]').addClass('biancalink');
  $('a[href*=bianca][href!="mailto:massimo.biancalani@gmail.com"]').addClass('biancalink');
});
