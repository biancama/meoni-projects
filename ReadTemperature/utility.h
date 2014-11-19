#ifndef UTILITY_H_
#define UTILITY_H_ 1
/**
 * Utility function to replace in a string a part of antother string
 * search what find
 * replace what replace
 * where to find
 *
 */
#ifndef FILE_SUFFIX_TYPE
#define FILE_SUFFIX_TYPE ".csv"
#endif

#ifndef FILE_ERR_SUFFIX_TYPE
#define FILE_ERR_SUFFIX_TYPE ".err"
#endif

#ifndef FILE_HEADER
#define FILE_HEADER "DATA;ORA;T1;T2"
#endif

#ifndef LINE_MAX
#define LINE_MAX 1024
#endif

typedef struct {

	char*	fileName;
	char*	prefixName;
	char    overwrite;
	double maxTemp;
	double minTemp;

} Parameter, *ParameterPtr;

char *str_replace(const char *search, const char *replace, const char *subject);
char *replace(char *string, char *oldpiece, char *newpiece);
char* getSuffixDate(char* dest, char* date);
void getFolder(const char* _path, const char* _name);

FILE *getFile(const char* path, const char* name, const char overwrite);
ParameterPtr *getParameters(const char* fileName);
void freeParameters(ParameterPtr* pointer);
char* composePath(const char* firstPart,const char* secondPart );
void createErrorFile(const char* outpuDir, const char* fileNameOld);
int checkRange(double temp, double minTemp, double maxTemp);
#endif /*UTILITY_H_*/

