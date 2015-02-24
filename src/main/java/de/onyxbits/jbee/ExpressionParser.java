//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package de.onyxbits.jbee;



//#line 2 "expr_grammar.y"
import java.util.Vector;
import java.math.BigDecimal;
import java.text.ParseException;
//#line 21 "ExpressionParser.java"




public class ExpressionParser
             implements ExpressionParserTokens
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:TokenValue
String   yytext;//user variable to return contextual strings
TokenValue yyval; //used to return semantic vals from action routines
TokenValue yylval;//the 'lval' (result) I got from yylex()
TokenValue valstk[] = new TokenValue[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new TokenValue();
  yylval=new TokenValue();
  valptr=-1;
}
final void val_push(TokenValue val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    TokenValue[] newstack = new TokenValue[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final TokenValue val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final TokenValue val_peek(int relative)
{
  return valstk[valptr-relative];
}
final TokenValue dup_yyval(TokenValue val)
{
  return val;
}
//#### end semantic value section ####
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    2,    2,    2,
};
final static short yylen[] = {                            2,
    0,    1,    1,    2,    1,    4,    3,    3,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    3,    2,    3,
    2,    3,    3,    0,    3,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    4,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   23,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   22,    0,    6,    0,
};
final static short yydgoto[] = {                          6,
    7,   28,
};
final static short yysindex[] = {                       -22,
 -255,  -33,  -22,  -22,  -22,    0,  363,    0,  -22,  -53,
  -50,  211,  -22,  -22,  -22,  -22,  -22,  -22,  -22,  -22,
  -22,  -22,  -22,  -22,  -22,  -22,  363,  -41,    0,  394,
  394,  394,  401,  401,  398,  398,  -26,  -26,  -53,  -53,
  -53,  -53,    0,  -22,    0,  363,
};
final static short yyrindex[] = {                         9,
    1,   15,    0,    0,    0,    0,   10,    0,  -39,   40,
   29,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -37,    0,    0,   97,
  191,  192,  169,  173,  143,  155,  113,  127,   51,   65,
   79,   99,    0,    0,    0,  -35,
};
final static short yygindex[] = {                         0,
  493,    0,
};
final static int YYTABLESIZE=664;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         45,
    3,   24,    8,   26,   26,   25,    9,   26,    1,    2,
   24,    0,    0,    0,    5,   22,    0,    5,    0,    0,
   23,    0,    3,    0,    0,    0,    0,    0,   21,    0,
    0,   26,    0,    0,    0,    3,    0,    3,    3,   19,
   25,    3,    3,    3,    0,    3,    0,    3,    0,    5,
   17,    5,    5,    0,    0,    5,    5,    5,    3,    5,
    0,    5,    0,   21,   18,   21,   21,   25,    0,   21,
   21,   21,    5,   21,   19,   21,   19,   19,   14,    0,
   19,   19,   19,    0,   19,   17,   19,   17,   17,    0,
    0,   17,   17,   17,    3,   17,    9,   17,   20,   18,
    0,   18,   18,    4,    0,   18,   18,   18,    5,   18,
    0,   18,   15,   14,    0,   14,   14,    0,    0,   14,
   14,   14,   21,   14,    3,   14,   16,    0,    0,    0,
    0,    9,    0,   20,    9,   20,   20,    9,    5,   20,
   20,   20,   13,   20,    0,   20,    0,   15,    0,    0,
   15,    0,   21,   15,   12,   15,    0,   15,    0,    0,
    0,   16,    0,   19,   16,    0,    0,   16,    8,   16,
    0,   16,    7,    0,   17,    0,    0,   13,    0,    0,
   13,    0,    0,   13,    0,   13,    0,   13,   18,   12,
   10,   11,   12,    0,    0,   12,    0,   12,    0,   12,
    0,    0,   14,    8,    0,    0,    8,    7,    0,    8,
    7,    0,    0,    7,    0,    0,    0,   44,    0,   24,
    9,   26,   20,   25,    0,   10,   11,    0,   10,   11,
    0,   10,   11,    0,    1,    2,   15,    0,    0,    0,
    0,    0,    0,    0,    0,   15,    0,   24,   13,    0,
   16,   29,   22,   19,    0,   18,    0,   23,    0,    3,
    3,    3,    3,    3,    0,    0,   13,    0,   26,    0,
    0,    0,    0,    5,    5,    5,    5,    5,   12,    0,
    0,    0,    0,    0,    0,    0,    0,   21,   21,   21,
   21,   21,    8,    0,    0,    0,    7,    0,   19,   19,
   19,   19,   19,    0,   25,    0,    0,    0,    0,   17,
   17,   17,   17,   17,   10,   11,    0,    0,    0,    0,
    0,    0,    0,   18,   18,   18,   18,   18,    0,    0,
    0,    0,    0,    0,   14,    0,    0,   14,   14,   14,
   14,   14,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    9,    0,   20,   20,   20,
   20,   20,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   15,   15,   15,   15,   15,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   16,   16,   16,   16,   16,
    0,    0,    0,    0,    0,    0,    0,   15,    0,   24,
   13,   13,   13,   13,   22,   19,    0,   18,    0,   23,
    0,    0,    0,   12,   12,   12,    0,    0,    0,    0,
   26,    0,    0,    0,    0,    0,    0,    8,    8,    8,
   24,    7,    7,    7,   24,   22,   19,   24,   18,   22,
   23,    0,   22,   19,   23,   18,    0,   23,    0,   10,
   11,   26,    0,    0,    0,   26,   25,    0,   26,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   16,   17,   20,   21,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   14,   25,    0,    0,
    0,   25,    0,    0,   25,   10,   11,   12,    0,    0,
    0,   27,    0,    0,    0,   30,   31,   32,   33,   34,
   35,   36,   37,   38,   39,   40,   41,   42,   43,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   46,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   16,   17,   20,   21,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   16,   17,   20,   21,    0,    0,   20,
   21,    0,   20,   21,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
    0,   41,  258,   41,   58,   41,   40,   58,    0,    0,
   37,   -1,   -1,   -1,    0,   42,   -1,   40,   -1,   -1,
   47,   -1,   45,   -1,   -1,   -1,   -1,   -1,    0,   -1,
   -1,   58,   -1,   -1,   -1,   35,   -1,   37,   38,    0,
   94,   41,   42,   43,   -1,   45,   -1,   47,   -1,   35,
    0,   37,   38,   -1,   -1,   41,   42,   43,   58,   45,
   -1,   47,   -1,   35,    0,   37,   38,   94,   -1,   41,
   42,   43,   58,   45,   35,   47,   37,   38,    0,   -1,
   41,   42,   43,   -1,   45,   35,   47,   37,   38,   -1,
   -1,   41,   42,   43,   94,   45,    0,   47,    0,   35,
   -1,   37,   38,  126,   -1,   41,   42,   43,   94,   45,
   -1,   47,    0,   35,   -1,   37,   38,   -1,   -1,   41,
   42,   43,   94,   45,  124,   47,    0,   -1,   -1,   -1,
   -1,   35,   -1,   35,   38,   37,   38,   41,  124,   41,
   42,   43,    0,   45,   -1,   47,   -1,   35,   -1,   -1,
   38,   -1,  124,   41,    0,   43,   -1,   45,   -1,   -1,
   -1,   35,   -1,  124,   38,   -1,   -1,   41,    0,   43,
   -1,   45,    0,   -1,  124,   -1,   -1,   35,   -1,   -1,
   38,   -1,   -1,   41,   -1,   43,   -1,   45,  124,   35,
    0,    0,   38,   -1,   -1,   41,   -1,   43,   -1,   45,
   -1,   -1,  124,   35,   -1,   -1,   38,   35,   -1,   41,
   38,   -1,   -1,   41,   -1,   -1,   -1,  259,   -1,  259,
  124,  259,  124,  259,   -1,   35,   35,   -1,   38,   38,
   -1,   41,   41,   -1,  257,  258,  124,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   35,   -1,   37,   38,   -1,
  124,   41,   42,   43,   -1,   45,   -1,   47,   -1,  259,
  260,  261,  262,  263,   -1,   -1,  124,   -1,   58,   -1,
   -1,   -1,   -1,  259,  260,  261,  262,  263,  124,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  259,  260,  261,
  262,  263,  124,   -1,   -1,   -1,  124,   -1,  259,  260,
  261,  262,  263,   -1,   94,   -1,   -1,   -1,   -1,  259,
  260,  261,  262,  263,  124,  124,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  259,  260,  261,  262,  263,   -1,   -1,
   -1,   -1,   -1,   -1,  124,   -1,   -1,  259,  260,  261,
  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  259,   -1,  259,  260,  261,
  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  259,  260,  261,  262,  263,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  259,  260,  261,  262,  263,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   35,   -1,   37,
   38,  259,  260,  261,   42,   43,   -1,   45,   -1,   47,
   -1,   -1,   -1,  259,  260,  261,   -1,   -1,   -1,   -1,
   58,   -1,   -1,   -1,   -1,   -1,   -1,  259,  260,  261,
   37,  259,  260,  261,   37,   42,   43,   37,   45,   42,
   47,   -1,   42,   43,   47,   45,   -1,   47,   -1,  259,
  259,   58,   -1,   -1,   -1,   58,   94,   -1,   58,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  260,  261,  262,  263,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  124,   94,   -1,   -1,
   -1,   94,   -1,   -1,   94,    3,    4,    5,   -1,   -1,
   -1,    9,   -1,   -1,   -1,   13,   14,   15,   16,   17,
   18,   19,   20,   21,   22,   23,   24,   25,   26,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   44,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  260,  261,  262,  263,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  260,  261,  262,  263,   -1,   -1,  262,
  263,   -1,  262,  263,
};
}
final static short YYFINAL=6;
final static short YYMAXTOKEN=264;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,"'#'",null,"'%'","'&'",null,"'('","')'","'*'","'+'",
null,"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,"':'",
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'^'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'|'",null,"'~'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"NUM","IDENT","LSTSEP","BSHIFTL",
"BSHIFTR","PLUSPERCENT","MINUSPERCENT","NEG",
};
final static String yyrule[] = {
"$accept : input",
"input :",
"input : exp",
"exp : NUM",
"exp : NUM IDENT",
"exp : IDENT",
"exp : IDENT '(' explst ')'",
"exp : exp BSHIFTR exp",
"exp : exp BSHIFTL exp",
"exp : exp '&' exp",
"exp : exp '|' exp",
"exp : exp '#' exp",
"exp : exp '+' exp",
"exp : exp '-' exp",
"exp : exp '%' exp",
"exp : exp PLUSPERCENT exp",
"exp : exp MINUSPERCENT exp",
"exp : exp '*' exp",
"exp : exp '/' exp",
"exp : '-' exp",
"exp : exp '^' exp",
"exp : '~' exp",
"exp : exp ':' exp",
"exp : '(' exp ')'",
"explst :",
"explst : explst LSTSEP exp",
"explst : exp",
};

//#line 54 "expr_grammar.y"

private Lexer lexer;
private MathLib mathLib;

protected ExpressionParser(MathLib mathLib, Lexer lexer) {
  this.lexer = lexer;
  this.mathLib = mathLib;
  if (lexer == null || mathLib == null) {
    throw new NullPointerException();
  }
}

private TokenValue actCollect(TokenValue lst, BigDecimal val) {
  TokenValue ret = lst;
  if (ret == null) {
    ret = new TokenValue(new Vector<BigDecimal>());
  }
  if (val != null) {
    ret.lstval.add(val);
  }
  return ret;
}

private TokenValue actMemory(TokenValue x) {
  BigDecimal res = mathLib.onLookup(x.sval);
  if (res == null) {
    // Don't rely on a properly implemented mathlib
    throw new NotDefinedException(x.sval);
  }
  return new TokenValue(res);
}

private TokenValue actEmpty() {
  return new TokenValue(mathLib.onEmptyExpression());
}

private TokenValue actCall(TokenValue name, TokenValue param) {
  return new TokenValue(mathLib.onCall(name.sval, param.lstval));
}

private TokenValue actPower(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onExponentiation(x.nval, y.nval));
}

private TokenValue actNot(TokenValue num) {
  return new TokenValue(mathLib.onBitwiseNot(num.nval));
}

private TokenValue actAnd(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onBitwiseAnd(x.nval, y.nval));
}

private TokenValue actOr(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onBitwiseOr(x.nval, y.nval));
}

private TokenValue actXor(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onBitwiseXor(x.nval, y.nval));
}

private TokenValue actShiftLeft(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onBitshiftLeft(x.nval, y.nval));
}

private TokenValue actShiftRight(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onBitshiftRight(x.nval, y.nval));
}

private TokenValue actAdd(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onAddition(x.nval, y.nval));
}

private TokenValue actSubtract(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onSubtraction(x.nval, y.nval));
}

private TokenValue actRemainder(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onModulation(x.nval, y.nval));
}

private TokenValue actAddPercent(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onPercentAddition(x.nval, y.nval));
}

private TokenValue actSubtractPercent(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onPercentSubtraction(x.nval, y.nval));
}

private TokenValue actMultiply(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onMultiplication(x.nval, y.nval));
}

private TokenValue actDivide(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onDivision(x.nval, y.nval));
}

private TokenValue actMove(TokenValue x, TokenValue y) {
  return new TokenValue(mathLib.onMovePoint(x.nval, y.nval));
}

private TokenValue actNegate(TokenValue x) {
  return new TokenValue(mathLib.onNegation(x.nval));
}

void yyerror(String s) {
  String token = lexer.lastMatch();
  mathLib.onSyntaxError(lexer.getPosition()-token.length()+1, token.trim());
  // Don't rely on a properly implemented mathlib
  throw new ArithmeticException("syntax error - also your mathlib is buggy");
}

int yylex() {
  try {
    int ret = lexer.nextExpressionToken();
    if (ret==NUM || ret==IDENT) {
      yylval = lexer.value;
    }
    return ret;
  }
  catch (ParseException e) {
    mathLib.onTokenizeError(lexer.inp, e.getErrorOffset());
  }
  // Don't rely on a properly implemented mathlib
  throw new IllegalArgumentException("Won't tokenize - also your mathlib is buggy");
}

//#line 462 "ExpressionParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    //if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      //if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        //if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          //if (yydebug)
          //  yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        //if (yydebug)
          //debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      //if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            //if (yydebug)
              //debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            //if (yydebug)
              //debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        //if (yydebug)
          //{
          //yys = null;
          //if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          //if (yys == null) yys = "illegal-symbol";
          //debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          //}
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    //if (yydebug)
      //debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 21 "expr_grammar.y"
{ yyval = actEmpty(); }
break;
case 3:
//#line 25 "expr_grammar.y"
{ yyval = val_peek(0); }
break;
case 4:
//#line 26 "expr_grammar.y"
{ yyval = actMultiply(val_peek(1), actMemory(val_peek(0))); }
break;
case 5:
//#line 27 "expr_grammar.y"
{ yyval = actMemory(val_peek(0)); }
break;
case 6:
//#line 28 "expr_grammar.y"
{ yyval = actCall(val_peek(3), val_peek(1)); }
break;
case 7:
//#line 29 "expr_grammar.y"
{ yyval = actShiftRight(val_peek(2), val_peek(0)); }
break;
case 8:
//#line 30 "expr_grammar.y"
{ yyval = actShiftLeft(val_peek(2), val_peek(0)); }
break;
case 9:
//#line 31 "expr_grammar.y"
{ yyval = actAnd(val_peek(2), val_peek(0)); }
break;
case 10:
//#line 32 "expr_grammar.y"
{ yyval = actOr(val_peek(2), val_peek(0)); }
break;
case 11:
//#line 33 "expr_grammar.y"
{ yyval = actXor(val_peek(2), val_peek(0)); }
break;
case 12:
//#line 34 "expr_grammar.y"
{ yyval = actAdd(val_peek(2), val_peek(0)); }
break;
case 13:
//#line 35 "expr_grammar.y"
{ yyval = actSubtract(val_peek(2), val_peek(0)); }
break;
case 14:
//#line 36 "expr_grammar.y"
{ yyval = actRemainder(val_peek(2), val_peek(0)); }
break;
case 15:
//#line 37 "expr_grammar.y"
{ yyval = actAddPercent(val_peek(2), val_peek(0)); }
break;
case 16:
//#line 38 "expr_grammar.y"
{ yyval = actSubtractPercent(val_peek(2), val_peek(0)); }
break;
case 17:
//#line 39 "expr_grammar.y"
{ yyval = actMultiply(val_peek(2), val_peek(0)); }
break;
case 18:
//#line 40 "expr_grammar.y"
{ yyval = actDivide(val_peek(2), val_peek(0)); }
break;
case 19:
//#line 41 "expr_grammar.y"
{ yyval = actNegate(val_peek(0)); }
break;
case 20:
//#line 42 "expr_grammar.y"
{ yyval = actPower(val_peek(2), val_peek(0)); }
break;
case 21:
//#line 43 "expr_grammar.y"
{ yyval = actNot(val_peek(0)); }
break;
case 22:
//#line 44 "expr_grammar.y"
{ yyval = actMove(val_peek(2), val_peek(0)); }
break;
case 23:
//#line 45 "expr_grammar.y"
{ yyval = val_peek(1); }
break;
case 24:
//#line 48 "expr_grammar.y"
{ yyval = actCollect(null, null); }
break;
case 25:
//#line 49 "expr_grammar.y"
{ yyval = actCollect(val_peek(2), val_peek(0).nval); }
break;
case 26:
//#line 50 "expr_grammar.y"
{ yyval = actCollect(null, val_peek(0).nval); }
break;
//#line 711 "ExpressionParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    //if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      //if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        //if (yydebug)
          //yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      //if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
