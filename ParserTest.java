package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.AST.*;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Simple test case with an empty program. This test expects an exception
	 * because all legal programs must have at least an identifier
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */


	
	@Test
	public void testExp14() throws LexicalException, SyntaxException {
		String input = "+x*y/t%a+g-u>2<8>=9<=12==0!=9 & x+y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression expAst = parser.expression();
		show(expAst);
		assertEquals(expAst.toString(),
				"Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_DIV, e1=Expression_Ident [name=t]], op=OP_MOD, e1=Expression_PredefinedName [name=KW_a]], op=OP_PLUS, e1=Expression_Ident [name=g]], op=OP_MINUS, e1=Expression_Ident [name=u]], op=OP_GT, e1=Expression_IntLit [value=2]], op=OP_LT, e1=Expression_IntLit [value=8]], op=OP_GE, e1=Expression_IntLit [value=9]], op=OP_LE, e1=Expression_IntLit [value=12]], op=OP_EQ, e1=Expression_IntLit [value=0]], op=OP_NEQ, e1=Expression_IntLit [value=9]], op=OP_AND, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_PLUS, e1=Expression_PredefinedName [name=KW_y]]]");
	}
	
	@Test
	public void testExp15() throws LexicalException, SyntaxException {
		String input = "+x*y/t%a+g-u";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression expAst = parser.expression();
		show(expAst);
		assertEquals(expAst.toString(),
				"Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_DIV, e1=Expression_Ident [name=t]], op=OP_MOD, e1=Expression_PredefinedName [name=KW_a]], op=OP_PLUS, e1=Expression_Ident [name=g]], op=OP_MINUS, e1=Expression_Ident [name=u]]");
	}
	
	@Test
	public void testExpRanAbc() throws LexicalException, SyntaxException {
		String input = "x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y & x / y - x * y >= x / y - x * y != x / y - x * y >= x / y - x * y";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression expAst = parser.expression();
		show(expAst);
		assertEquals(expAst.toString(),
				"Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]], op=OP_NEQ, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]], op=OP_GE, e1=Expression_Binary [e0=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_DIV, e1=Expression_PredefinedName [name=KW_y]], op=OP_MINUS, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]]]]]]");
	}
	
	@Test
	public void testExpRan() throws LexicalException, SyntaxException {
		String input = "+x*y/t%a+g-u>2<8>=9<=12==0!=9 & x+y & t+s | f+g ? +x*y/t%a+g-u>2<8>=9<=12==0!=9 & x+y & t+s | f+g:+x*y/t%a+g-u>2<8>=9<=12==0!=9 & x+y & t+s | f+g";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		Expression expAst = parser.expression();
		show(expAst);
		assertEquals(expAst.toString(),
				"Expression_Conditional [condition=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_DIV, e1=Expression_Ident [name=t]], op=OP_MOD, e1=Expression_PredefinedName [name=KW_a]], op=OP_PLUS, e1=Expression_Ident [name=g]], op=OP_MINUS, e1=Expression_Ident [name=u]], op=OP_GT, e1=Expression_IntLit [value=2]], op=OP_LT, e1=Expression_IntLit [value=8]], op=OP_GE, e1=Expression_IntLit [value=9]], op=OP_LE, e1=Expression_IntLit [value=12]], op=OP_EQ, e1=Expression_IntLit [value=0]], op=OP_NEQ, e1=Expression_IntLit [value=9]], op=OP_AND, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_PLUS, e1=Expression_PredefinedName [name=KW_y]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Ident [name=t], op=OP_PLUS, e1=Expression_Ident [name=s]]], op=OP_OR, e1=Expression_Binary [e0=Expression_Ident [name=f], op=OP_PLUS, e1=Expression_Ident [name=g]]], trueExpression=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_DIV, e1=Expression_Ident [name=t]], op=OP_MOD, e1=Expression_PredefinedName [name=KW_a]], op=OP_PLUS, e1=Expression_Ident [name=g]], op=OP_MINUS, e1=Expression_Ident [name=u]], op=OP_GT, e1=Expression_IntLit [value=2]], op=OP_LT, e1=Expression_IntLit [value=8]], op=OP_GE, e1=Expression_IntLit [value=9]], op=OP_LE, e1=Expression_IntLit [value=12]], op=OP_EQ, e1=Expression_IntLit [value=0]], op=OP_NEQ, e1=Expression_IntLit [value=9]], op=OP_AND, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_PLUS, e1=Expression_PredefinedName [name=KW_y]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Ident [name=t], op=OP_PLUS, e1=Expression_Ident [name=s]]], op=OP_OR, e1=Expression_Binary [e0=Expression_Ident [name=f], op=OP_PLUS, e1=Expression_Ident [name=g]]], falseExpression=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Binary [e0=Expression_Unary [op=OP_PLUS, e=Expression_PredefinedName [name=KW_x]], op=OP_TIMES, e1=Expression_PredefinedName [name=KW_y]], op=OP_DIV, e1=Expression_Ident [name=t]], op=OP_MOD, e1=Expression_PredefinedName [name=KW_a]], op=OP_PLUS, e1=Expression_Ident [name=g]], op=OP_MINUS, e1=Expression_Ident [name=u]], op=OP_GT, e1=Expression_IntLit [value=2]], op=OP_LT, e1=Expression_IntLit [value=8]], op=OP_GE, e1=Expression_IntLit [value=9]], op=OP_LE, e1=Expression_IntLit [value=12]], op=OP_EQ, e1=Expression_IntLit [value=0]], op=OP_NEQ, e1=Expression_IntLit [value=9]], op=OP_AND, e1=Expression_Binary [e0=Expression_PredefinedName [name=KW_x], op=OP_PLUS, e1=Expression_PredefinedName [name=KW_y]]], op=OP_AND, e1=Expression_Binary [e0=Expression_Ident [name=t], op=OP_PLUS, e1=Expression_Ident [name=s]]], op=OP_OR, e1=Expression_Binary [e0=Expression_Ident [name=f], op=OP_PLUS, e1=Expression_Ident [name=g]]]]");
	}
	
//	@Test
//	public void testNameOnlyExp() throws LexicalException, SyntaxException {
//		String input = "prog k [[x,y]]";  //Legal program with only a name
//		show(input);            //display input
//		Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
//		show(scanner);    //display the tokens
//		Parser parser = new Parser(scanner);   //create parser
//		Program ast = parser.parse();          //parse program and get AST
//		show(ast);                             //Display the AST
////		assertEquals(ast.name, "prog");        //Check the name field in the Program object
////		assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
//	}

	

}
