package com.chic.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import static com.chic.lexer.ChicTokenTypes.*;

%%

%class _ChicLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType


// ── Class members ──────────────────────────────────────────────────────────
%{
    // Nesting depth for nested block comments (Chic supports nesting).
    private int commentDepth = 0;
%}

// ── States ─────────────────────────────────────────────────────────────────
%state IN_STRING
%state IN_CSTRING
%state IN_CHAR
%state IN_BLOCK_COMMENT

// ── Macro definitions ──────────────────────────────────────────────────────
DIGIT      = [0-9]
HEX_DIGIT  = [0-9a-fA-F]
ID_CHAR    = [a-zA-Z0-9_]
WHITE      = [ \t\r\n]+

%%

// ==========================================================================
// INITIAL STATE
// ==========================================================================

<YYINITIAL> {

    // ── Whitespace ─────────────────────────────────────────────────────────
    {WHITE}                         { return WHITE_SPACE; }

    // ── Line comment ───────────────────────────────────────────────────────
    "//" [^\r\n]*                   { return LINE_COMMENT; }

    // ── Block comment start (nested comments supported) ────────────────────
    "/*"                            { commentDepth = 1; yybegin(IN_BLOCK_COMMENT); return BLOCK_COMMENT; }

    // ── C-string literal prefix: c"..." ────────────────────────────────────
    // Must come before the identifier rule because 'c' alone is an identifier.
    // JFlex longest-match picks the two-char prefix 'c"' over the single 'c'.
    "c\""                           { yybegin(IN_CSTRING); return CSTRING_LITERAL; }

    // ── String and char literals ───────────────────────────────────────────
    "\""                            { yybegin(IN_STRING); return STRING_LITERAL; }
    "'"                             { yybegin(IN_CHAR);   return CHAR_LITERAL; }

    // ── Number literals (longer prefixes before plain decimal) ─────────────
    "0x" {HEX_DIGIT}+               { return HEX_LITERAL; }
    "0b" [01]+                      { return BIN_LITERAL; }
    "0o" [0-7]+                     { return OCT_LITERAL; }
    {DIGIT}+ "." {DIGIT}+           { return FLOAT_LITERAL; }
    {DIGIT}+                        { return INT_LITERAL; }

    // ── Compile-time directives (#if, #else, #elif, #end) ──────────────────
    // Must precede the identifier rule; JFlex longest-match handles 'elif' vs 'else'.
    "#if"                           { return KW_CT_IF; }
    "#elif"                         { return KW_CT_ELIF; }
    "#else"                         { return KW_CT_ELSE; }
    "#end"                          { return KW_CT_END; }

    // ── Multi-word keywords (contain underscores) ──────────────────────────
    "or_else"                       { return KW_OR_ELSE; }
    "or_error"                      { return KW_OR_ERROR; }
    "raw_union"                     { return KW_RAW_UNION; }

    // ── Single-word keywords ───────────────────────────────────────────────
    "import"                        { return KW_IMPORT; }
    "namespace"                     { return KW_NAMESPACE; }
    "let"                           { return KW_LET; }
    "var"                           { return KW_VAR; }
    "func"                          { return KW_FUNC; }
    "for"                           { return KW_FOR; }
    "in"                            { return KW_IN; }
    "step"                          { return KW_STEP; }
    "continue"                      { return KW_CONTINUE; }
    "break"                         { return KW_BREAK; }
    "if"                            { return KW_IF; }
    "else"                          { return KW_ELSE; }
    "return"                        { return KW_RETURN; }
    "true"                          { return KW_TRUE; }
    "false"                         { return KW_FALSE; }
    "null"                          { return KW_NULL; }
    "new"                           { return KW_NEW; }
    "release"                       { return KW_RELEASE; }
    "sizeof"                        { return KW_SIZEOF; }
    "enum"                          { return KW_ENUM; }
    "struct"                        { return KW_STRUCT; }
    "union"                         { return KW_UNION; }
    "alias"                         { return KW_ALIAS; }
    "extension"                     { return KW_EXTENSION; }
    "match"                         { return KW_MATCH; }
    "cast"                          { return KW_CAST; }
    "defer"                         { return KW_DEFER; }
    "inline"                        { return KW_INLINE; }
    "try"                           { return KW_TRY; }

    // ── Built-in primitive types ───────────────────────────────────────────
    // JFlex longest-match: 'i32foo' hits identifier rule (longer); 'i32' alone
    // hits builtin_type (tie → first rule wins since it appears first).
    "i8" | "i16" | "i32" | "i64"   { return BUILTIN_TYPE; }
    "u8" | "u16" | "u32" | "u64"   { return BUILTIN_TYPE; }
    "f32" | "f64"                   { return BUILTIN_TYPE; }
    "bool" | "void" | "char" | "string"  { return BUILTIN_TYPE; }

    // ── @ as standalone token vs. @-prefixed decorator identifier ──────────
    // Must appear before the general identifier rule.
    // '@' alone → AT;  '@foo' (one or more ID chars) → IDENTIFIER.
    "@" {ID_CHAR}+                  { return IDENTIFIER; }
    "@"                             { return AT; }

    // ── General identifier (letters, _, #, @  anywhere inside) ────────────
    [a-zA-Z_#] [a-zA-Z0-9_@#]*     { return IDENTIFIER; }

    // ── Three-character operators ──────────────────────────────────────────
    "..."                           { return ELLIPSIS; }
    "<<="                           { return LSHIFT_EQ; }
    ">>="                           { return RSHIFT_EQ; }

    // ── Two-character operators ────────────────────────────────────────────
    ".."                            { return DOUBLE_DOT; }
    ":="                            { return ASSIGN; }
    "::"                            { return DOUBLE_COLON; }
    "=>"                            { return FAT_ARROW; }
    "=="                            { return DOUBLE_EQ; }
    "!="                            { return NOT_EQ; }
    "->"                            { return ARROW; }
    "--"                            { return MINUS_MINUS; }
    "-="                            { return MINUS_EQ; }
    "++"                            { return PLUS_PLUS; }
    "+="                            { return PLUS_EQ; }
    "*="                            { return STAR_EQ; }
    "/="                            { return SLASH_EQ; }
    "%="                            { return PERCENT_EQ; }
    "<<"                            { return LSHIFT; }
    "<="                            { return LT_EQ; }
    ">>"                            { return RSHIFT; }
    ">="                            { return GT_EQ; }
    "&&"                            { return AMP_AMP; }
    "||"                            { return PIPE_PIPE; }

    // ── Single-character operators and punctuation ─────────────────────────
    "."                             { return DOT; }
    ":"                             { return COLON; }
    ";"                             { return SEMICOLON; }
    ","                             { return COMMA; }
    "="                             { return EQ; }
    "<"                             { return LT; }
    ">"                             { return GT; }
    "+"                             { return PLUS; }
    "-"                             { return MINUS; }
    "*"                             { return STAR; }
    "/"                             { return SLASH; }
    "%"                             { return PERCENT; }
    "&"                             { return AMP; }
    "|"                             { return PIPE; }
    "^"                             { return CARET; }
    "~"                             { return TILDE; }
    "!"                             { return BANG; }
    "{"                             { return LBRACE; }
    "}"                             { return RBRACE; }
    "("                             { return LPAREN; }
    ")"                             { return RPAREN; }
    "["                             { return LBRACKET; }
    "]"                             { return RBRACKET; }

    // ── Anything unrecognised ──────────────────────────────────────────────
    .                               { return BAD_CHARACTER; }
}

// ==========================================================================
// BLOCK COMMENT STATE  (supports nested /* ... */ )
// ==========================================================================

<IN_BLOCK_COMMENT> {
    "/*"            { commentDepth++; return BLOCK_COMMENT; }
    "*/"            { if (--commentDepth == 0) yybegin(YYINITIAL); return BLOCK_COMMENT; }
    [^*/]+          { return BLOCK_COMMENT; }
    "*"             { return BLOCK_COMMENT; }
    "/"             { return BLOCK_COMMENT; }
    <<EOF>>         { yybegin(YYINITIAL); return BLOCK_COMMENT; }
}

// ==========================================================================
// STRING LITERAL STATE  "..."
// ==========================================================================

<IN_STRING> {
    "\""            { yybegin(YYINITIAL); return STRING_LITERAL; }
    "\\\\" | \\[ntr\"0]     { return STRING_LITERAL; }
    [^\"\\\r\n]+    { return STRING_LITERAL; }
    [\r\n]          { yybegin(YYINITIAL); return BAD_CHARACTER; }
    <<EOF>>         { yybegin(YYINITIAL); return STRING_LITERAL; }
}

// ==========================================================================
// C-STRING LITERAL STATE  c"..."
// ==========================================================================

<IN_CSTRING> {
    "\""            { yybegin(YYINITIAL); return CSTRING_LITERAL; }
    "\\\\" | \\[ntr\"0]     { return CSTRING_LITERAL; }
    [^\"\\\r\n]+    { return CSTRING_LITERAL; }
    [\r\n]          { yybegin(YYINITIAL); return BAD_CHARACTER; }
    <<EOF>>         { yybegin(YYINITIAL); return CSTRING_LITERAL; }
}

// ==========================================================================
// CHAR LITERAL STATE  '.'
// ==========================================================================

<IN_CHAR> {
    "'"             { yybegin(YYINITIAL); return CHAR_LITERAL; }
    "\\\\" | \\[ntr\\'0]     { return CHAR_LITERAL; }
    [^'\\\r\n]      { return CHAR_LITERAL; }
    [\r\n]          { yybegin(YYINITIAL); return BAD_CHARACTER; }
    <<EOF>>         { yybegin(YYINITIAL); return CHAR_LITERAL; }
}
