/*	NetFerret - A tool to make searching eaiser.
	(c) 1999 Dave Fletcher
	All Rights Reserved
   
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/


/*#include "ferret.h"*/


typedef struct {

	char*	text;
	char*	delim;
	int	delim_len;
	size_t	offset;

} Tokenizer, *TokenizerPtr;


TokenizerPtr new_tokenizer (char* text, char* delim);
int has_more_tokens(TokenizerPtr tok);
int count_tokens (TokenizerPtr tok);
char *next_token (TokenizerPtr tok);
char *remaining_text (TokenizerPtr tok);
void free_tokenizer(TokenizerPtr tok);
