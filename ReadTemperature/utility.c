#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <dirent.h>
#include <errno.h>
#include "utility.h"
/* Given a string, replaces all instances of "oldpiece" with "newpiece".
 *
 * Modified this routine to eliminate recursion and to avoid infinite
 * expansion of string when newpiece contains oldpiece.  --Byron

*/

char *replace(char *string, char *oldpiece, char *newpiece) {

   int str_index, newstr_index, oldpiece_index, end, new_len, old_len, cpy_len;
   char *c;
   static char newstring[1024];

   if ((c = (char *) strstr(string, oldpiece)) == NULL)

      return string;

   new_len        = strlen(newpiece);
   old_len        = strlen(oldpiece);
   end            = strlen(string)   - old_len;
   oldpiece_index = c - string;


   newstr_index = 0;
   str_index = 0;
   while(str_index <= end && c != NULL)
   {

      /* Copy characters from the left of matched pattern occurence */
      cpy_len = oldpiece_index-str_index;
      strncpy(newstring+newstr_index, string+str_index, cpy_len);
      newstr_index += cpy_len;
      str_index    += cpy_len;

      /* Copy replacement characters instead of matched pattern */
      strcpy(newstring+newstr_index, newpiece);
      newstr_index += new_len;
      str_index    += old_len;

      /* Check for another pattern match */
      if((c = (char *) strstr(string+str_index, oldpiece)) != NULL)
         oldpiece_index = c - string;


   }
   /* Copy remaining characters from the right of last matched pattern */    strcpy(newstring+newstr_index, string+str_index);

   return newstring;
}

/**
 * Utility function to replace in a string a part of another string
 *
 */

char *str_replace(const char *search, const char *replace, const char *subject)
{

  if (search == NULL || replace == NULL || subject == NULL) return NULL;
  if (strlen(search) == 0 || strlen(replace) == 0 || strlen(subject) == 0) return NULL;

  //char *replaced = (char*)calloc(1, 1), *temp = NULL;
   char *replaced = NULL, *temp = NULL;
  const char *p = subject, *p3 = subject, *p2 = NULL;
  int  found = 0;

  while ( (p = strstr(p, search)) != NULL) {
    found = 1;
    temp = realloc(replaced, strlen(replaced) + (p - p3) + strlen(replace));
    if (temp == NULL) {
      free(replaced);
      return NULL;
    }
    replaced = temp;
    strncat(replaced, p - (p - p3), p - p3);
    strcat(replaced, replace);
    p3 = p + strlen(search);
    p += strlen(search);
    p2 = p;
  }

  if (found == 1) {
    if (strlen(p2) > 0) {
      temp = realloc(replaced, strlen(replaced) + strlen(p2) + 1);
      if (temp == NULL) {
        free(replaced);
        return NULL;
      }
      replaced = temp;
      strcat(replaced, p2);
    }
  } else {
    temp = realloc(replaced, strlen(subject) + 1);
    if (temp != NULL) {
      replaced = temp;
      strcpy(replaced, subject);
    }
  }
  return replaced;
}

/**
 * Given a date in format DD/MM/YYYY return YYYYMMDD
 *
 */
 char* getSuffixDate(char* dest, char* date){
 	char* year, *days, *month, *tokendsaved;
 	days = strtok_r(date, "/", &tokendsaved); //
 	month = strtok_r(NULL, "/", &tokendsaved);
 	year = strtok_r(NULL, "/", &tokendsaved);
 	int lenght = strlen(year);
 	lenght += strlen(month);
 	lenght += strlen(days);

 	strcpy(dest, year);
 	strcat(dest, month);
 	strcat(dest, days);
 	return dest;
 }

 /**
 * Given a path and a folder Name
 * return if folder already exist a pointer to the folder, otherwise
 * it creates th new dir and return pointer
 *
 */
void getFolder(const char* path, const char* name){
 	DIR  *newDir = NULL;
 	char *absoluteFileName;
 	int status;

 	absoluteFileName = malloc(strlen(path) + strlen(name) +2);

 	strcpy(absoluteFileName, path);
 	strcat(absoluteFileName, "/");
 	strcat(absoluteFileName, name);
 	newDir = opendir(absoluteFileName);
 	if (errno == ENOENT){
 		status = mkdir(absoluteFileName, S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
 		newDir = opendir(absoluteFileName);
 	}
 	free(absoluteFileName);
 	closedir(newDir);
 }

 /**
 * Given a path and a folder Name
 * return file to write
 *
 */
 FILE *getFile(const char* path, const char* name, const char overwrite){
 	char year[5];
 	char month[3];
 	const char *pointer = name;

 	FILE *fp;
 	char *absoluteFileName, *newPath;

 	strncpy(year, pointer, 4); year[4] = '\0';
 	pointer = pointer + 4;
 	strncpy(month, pointer, 2); month[2] = '\0';

 	getFolder(path, year);
	newPath = malloc(strlen(path) + strlen(year) + 2);
	strcpy(newPath, path);
 	strcat(newPath, "/");
 	strcat(newPath, year);

 	getFolder(newPath, month);
	free(newPath);

 	absoluteFileName = malloc(strlen(path) + strlen(year) + strlen(month) + strlen(name) + strlen(FILE_SUFFIX_TYPE) + 4);
 	strcpy(absoluteFileName, path);
 	strcat(absoluteFileName, "/");
 	strcat(absoluteFileName, year);
 	strcat(absoluteFileName, "/");
 	strcat(absoluteFileName, month);
 	strcat(absoluteFileName, "/");
 	strcat(absoluteFileName, name);
 	strcat(absoluteFileName, FILE_SUFFIX_TYPE);

 	if (overwrite == 'R'){// it's not  overwrite

 		fp=fopen(absoluteFileName,"r");
		if (fp != NULL)
		{
			if (fclose(fp) < 0){
				fprintf(stderr, "%s: Couldn’t close file %s; %s\n", "getFile", absoluteFileName, strerror (errno));
				exit (EXIT_FAILURE);
			}
			return NULL; // it's not overwrite
		}
 	}
 	fp=fopen(absoluteFileName,"w");
		if (fp == NULL)
		{
			fprintf(stderr, "%s: Couldn’t open file %s; %s\n", "getFile", absoluteFileName, strerror (errno));
			exit (EXIT_FAILURE);
		}
		// write the header
		fputs(FILE_HEADER, fp);
		fputs("\n", fp);


 	free(absoluteFileName);
 	return fp;
 }

 /**
 * Given a fileName parameter
 * return an array of parameters
 * Each line of parameters file must be:
 * dataFileName;roomName;R (No overwrite) W (overwrite);minTemp;maxTemp;
 *
 */
 ParameterPtr *getParameters(const char* fileName){
	//ParameterPtr *returnResult = NULL;
	FILE *fp;
	char line[LINE_MAX];
	ParameterPtr* result = NULL;
	ParameterPtr pStruct = NULL;
 	int counter = 0;
 	// Open Parameters file
 	fp=fopen(fileName,"r");
	if (fp == NULL)
	{
		fprintf(stderr, "%s: Couldn’t open file %s; %s\n", "getParameters", fileName, strerror (errno));
		exit (EXIT_FAILURE);
	}
 	// gets all the lines

 	while (fgets(line, LINE_MAX, fp) != NULL) {
 		if (strncmp(line, "#", 1) == 0){
 			continue;
 		}
 		result = realloc(result, (counter + 1) * sizeof(ParameterPtr));

 		pStruct = malloc(sizeof(Parameter));
 		// fill parameter
 		char* fileName, *prefix, *overWrite, *minTemp, *maxTemp, *tokendsaved;
 		fileName = strtok_r(line, ";", &tokendsaved); //
 		prefix= strtok_r(NULL, ";", &tokendsaved);
 		overWrite= strtok_r(NULL, ";", &tokendsaved);
 		minTemp = strtok_r(NULL, ";", &tokendsaved);
 		maxTemp = strtok_r(NULL, ";", &tokendsaved);

 		pStruct->fileName = malloc((strlen(fileName) + 1)* sizeof(char) );
 		pStruct->prefixName = malloc((strlen(prefix) + 1)* sizeof(char) );
 		strcpy(pStruct->fileName, fileName);
 		strcpy(pStruct->prefixName, prefix);
 		pStruct->overwrite = *overWrite;
 		pStruct->minTemp = atof(minTemp);
 		pStruct->maxTemp = atof(maxTemp);
 		*(result + counter) = pStruct;
 		counter++;

 	} // end while
	// close parameters file
	int err = fclose(fp);
	if (err < 0){
		fprintf(stderr, "%s: Couldn’t close file %s; %s\n", "getParameters", fileName, strerror (errno));
		exit (EXIT_FAILURE);
	}
	result = realloc(result, (counter + 1) * sizeof(ParameterPtr));
	*(result + counter) = NULL;
	return result;
 }

 /**
 * Given a pointer to Parameter
 * free memory allocated
 *
 */
 void freeParameters(ParameterPtr* pointer){
	 int counter = 0;
	 ParameterPtr* apPointer = pointer;
	 while ( *(apPointer + counter) != NULL)
	 {
		 free( *(apPointer + counter));
		 counter++;
	 }
	 free( *(apPointer + counter));
	 free(apPointer);
 }


 /**
  * Function to concatenate  two part of a URL
  */
 char* composePath(const char* firstPart,const char* secondPart ){
	 const char* slash ="/";

	 char* result = calloc (strlen(firstPart) + strlen(secondPart) + 1, sizeof(char) );
	 result = strcat(result, firstPart);
	 result = strcat(result, slash);
	 result = strcat(result, secondPart);
	 return result;

 }
 /**
  * Create a anomalia file
  */
 void createErrorFile(const char* path, const char* name){
	 	char year[5];
	 	char month[3];
	 	const char *pointer = name;

	 	FILE *fp;
	 	char *absoluteFileName, *newPath;

	 	strncpy(year, pointer, 4); year[4] = '\0';
	 	pointer = pointer + 4;
	 	strncpy(month, pointer, 2); month[2] = '\0';

	 	getFolder(path, year);
		newPath = malloc(strlen(path) + strlen(year) + 2);
		strcpy(newPath, path);
	 	strcat(newPath, "/");
	 	strcat(newPath, year);

	 	getFolder(newPath, month);
		free(newPath);

	 	absoluteFileName = malloc(strlen(path) + strlen(year) + strlen(month) + strlen(name) + strlen(FILE_SUFFIX_TYPE) + 4);
	 	strcpy(absoluteFileName, path);
	 	strcat(absoluteFileName, "/");
	 	strcat(absoluteFileName, year);
	 	strcat(absoluteFileName, "/");
	 	strcat(absoluteFileName, month);
	 	strcat(absoluteFileName, "/");
	 	strcat(absoluteFileName, name);
	 	strcat(absoluteFileName, FILE_ERR_SUFFIX_TYPE);

	 	fp=fopen(absoluteFileName,"w");
			if (fp == NULL)
			{
				fprintf(stderr, "%s: Couldn’t open file %s; %s\n", "getFile", absoluteFileName, strerror (errno));
				exit (EXIT_FAILURE);
			}
			fputs("Error", fp);
			fputs("\n", fp);


	 	free(absoluteFileName);
	 	int err = fclose(fp);
	 	if (err < 0){
	 		fprintf(stderr, "%s: Couldn’t close file %s; %s\n", "getFile", absoluteFileName, strerror (errno));
	 		exit (EXIT_FAILURE);
	 	}
 }



 int checkRange(double temp, double minTemp, double maxTemp){
	 if (temp >= minTemp && temp <= maxTemp){
		 return 0;
	 }else{
		 return 1;
	 }
 }

