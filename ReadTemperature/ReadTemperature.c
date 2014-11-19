#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <dirent.h>
#include "utility.h"



#ifndef LINE_MAX
#define LINE_MAX 1024
#endif



char line[LINE_MAX];

int main(int argc, char *argv[])
{

	FILE* fp;
	int count=1;
	char *str, *strsaved;
	char *token;
	line[0] = '\0';
	ParameterPtr *arrayParameters;
	int outsideRange = 0;

	if (argc != 3) {
        fprintf(stderr, "Usage: %s parameterFile.txt outputDir\n",
                argv[0]);
        exit(EXIT_FAILURE);
    }
    // Check if output dir is a correct directory
	DIR *dirID = opendir(argv[2]);
	if (dirID == NULL) {
		fprintf(stderr, "%s: Couldn’t open output dir %s; %s\n", argv[0], argv[2], strerror (errno));
		exit (EXIT_FAILURE);
	} else {
		closedir(dirID);
	}
	arrayParameters = getParameters(argv[1]);

	int i = 0;
	while (*(arrayParameters + i) != NULL) {
		const char* fileName = (*(arrayParameters + i))->fileName;
		const char* outputDir = composePath(argv[2],
				(*(arrayParameters + i))->prefixName);
		printf("save files for file %s in %s\n", fileName, outputDir);
		fp = fopen(fileName, "r");
		if (fp == NULL) {
			fprintf(stderr, "%s: Couldn’t open file %s; %s\n", argv[0],
					fileName, strerror(errno));
			freeParameters(arrayParameters);
			exit(EXIT_FAILURE);
		}
		char fileNameOld[LINE_MAX];
		strcpy(fileNameOld, "");
		FILE *fileToWrite;
		fileToWrite = NULL;

		while (fgets(line, LINE_MAX, fp) != NULL) { // read a line of input file
			//printf("Line %d: %s",count, line);
			str = line;
			token = strtok_r(str, " \t", &strsaved); // the input file is of type
			// 	    DATE    	TIME  	T1	T2	T3	T4	DI1	DI2	DI3	DI4	DO1	DO2	POn	T.c
			// 1 	14/04/2005	23:00	 1,5 	 2 		  -  	  -  	 Off	 Off	  - 	  - 	 Off	  -

			if (atoi(token) > 0) {
				// It's number get data
				char strConcatenated[LINE_MAX];
				strcpy(strConcatenated, "");
				char fileName[LINE_MAX];

				token = strtok_r(NULL, " \t", &strsaved);
				strcat(strConcatenated, token);
				strcat(strConcatenated, ";"); // date in the format DD/MM/YYYY
				getSuffixDate(fileName, token);
				if (strcmp(fileName, fileNameOld) != 0) {
					if (fileToWrite != NULL) {
						int closeID = fclose(fileToWrite);
						if (closeID < 0) {
							fprintf(stderr, "%s: Couldn’t close file %s; %s\n",
									argv[0], fileName, strerror(errno));
							exit(EXIT_FAILURE);
						}
						if (outsideRange == 1){
							createErrorFile(outputDir, fileNameOld);
						}
						outsideRange = 0;
					}
					fileToWrite = getFile(outputDir, fileName,
							(*(arrayParameters + i))->overwrite);
					strcpy(fileNameOld, fileName);
				}
				if (fileToWrite == NULL) {
					continue;
				}
				token = strtok_r(NULL, " \t", &strsaved);
				strcat(strConcatenated, token);
				strcat(strConcatenated, ";"); // hour in the format HH:MM

				token = strtok_r(NULL, " \t", &strsaved);
				strcat(strConcatenated, replace(token, ",", "."));
				strcat(strConcatenated, ";"); // First Paramenter
				if (outsideRange == 0){
					outsideRange = checkRange(atof(token), (*(arrayParameters + i))->minTemp, (*(arrayParameters + i))->maxTemp);
				}


				token = strtok_r(NULL, " \t", &strsaved);
				strcat(strConcatenated, replace(token, ",", ".")); // Second Paramenter
				strcat(strConcatenated, "\n");

				//printf("token %d: %s",count, strConcatenated);

				fputs(strConcatenated, fileToWrite);
			}
			strsaved = NULL;

			//		printf("Linea %d: %s",count, token);

			count++;

		} // end while (fgets(line, LINE_MAX, fp) != NULL) {
		if (fileToWrite != NULL) {
			fclose(fileToWrite);
			if (outsideRange == 1){
				createErrorFile(outputDir, fileNameOld);
			}
			outsideRange = 0;
		}
		fclose(fp);
		printf("Done files for file %s in %s\n",fileName, outputDir);
		i++;
	} // for (int i = 0; i < numberOfFiles ...
	freeParameters(arrayParameters);
	exit(EXIT_SUCCESS);
}
