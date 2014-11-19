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

#include <glib.h>
#include <memory.h>

#include "stringtokenizer.h"



TokenizerPtr new_tokenizer (char* text, char* delim)
{
	TokenizerPtr tok;

#ifdef HDEBUG
	tok = h_alloc (sizeof (Tokenizer), "tok", "new_tokenizer", 9);
#else
	tok = g_malloc (sizeof (Tokenizer));
#endif

	/*printf ("new_tokenizer: \"%s\", \"%s\"\n", text, delim);*/
	tok->text = text;
	tok->delim = delim;
	tok->delim_len = strlen (delim);
	tok->offset = 0;
	return tok;
}


int has_more_tokens(TokenizerPtr tok)
{
	/*printf ("has_more_tokens testing: \"%s\"\n", (tok->text)+tok->offset);*/
	if (tok->offset >= strlen(tok->text)) return 0;
	return (strcspn ((tok->text)+tok->offset, tok->delim) != 0);
}


int count_tokens (TokenizerPtr tok)
{
	int i = 0;
	size_t this_offs = strcspn (tok->text+tok->offset, tok->delim);
	size_t offs = 0;
	int len = strlen (tok->text);

	if (this_offs != 0)
	{
		offs+=this_offs;
		i++;
	}

	/*printf ("count_tokens: %d, %d\n", this_offs, i);*/

	while ((this_offs != 0) && (offs < len))
	{
		this_offs = strcspn (tok->text+offs, tok->delim);
		i++;

	/*	printf ("count_tokens in \"%s\" | offset: %d, tokens: %d\n",
				tok->text+offs, this_offs, i);
	*/
		offs+=this_offs;
	}
	return i;
}


int is_delim (TokenizerPtr tok, size_t pos)
{
	int i;
	for (i=0; i<tok->delim_len; i++)
	{
		if (tok->delim[i] == tok->text[pos])
			return 1;
	}
	return 0;
}


void remove_leading_delimiters (TokenizerPtr tok) {
	while (is_delim(tok, tok->offset) &&
		(tok->offset < strlen(tok->text)))
			tok->offset++;
}


/*	Ownership of return value is
	transferred to calling function.
*/
char *next_token (TokenizerPtr tok)
{
	char *ret;
	char *ptroffset = (tok->text)+tok->offset;
	size_t len = strcspn (ptroffset, tok->delim);

#ifdef HDEBUG
	ret = h_alloc (len+1, "ret", "next_token", 83);
#else
	ret = g_malloc (len+1);
#endif
	memcpy (ret, ptroffset, len);
	ret[len] = 0;
	tok->offset += len;
	remove_leading_delimiters(tok);
	/*printf ("next_token returning: \"%s\"\n", ret);*/
	return ret;
}


/*	Ownership of return value is
	transferred to calling function.
*/
char *remaining_text (TokenizerPtr tok)
{
	char *ret;
	char *ptroffset = (tok->text)+tok->offset;
	size_t len = strlen (ptroffset)+1;
#ifdef HDEBUG
	ret = h_alloc (len, "ret", "remaining_text", 101);
#else
	ret = g_malloc (len);
#endif
	memcpy (ret, ptroffset, len);
	/*printf ("remaining_text returning: \"%s\"\n", ret);*/
	return ret;
}


void free_tokenizer(TokenizerPtr tok)
{
#ifdef HDEBUG
	h_free (tok, "tok", "free_tokenizer", 110);
#else
	g_free (tok);
#endif
}
