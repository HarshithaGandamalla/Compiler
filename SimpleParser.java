package cop5556fa17;



import java.util.Arrays;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.SimpleParser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}


	Scanner scanner;
	Token t;

	SimpleParser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public void parse() throws SyntaxException {
		program();
		matchEOF();
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	void program() throws SyntaxException {
		//TODO  implement this
		
		if(t.isKind(IDENTIFIER)){
			consume();
			
			while(t.isKind(KW_int)||t.isKind(KW_boolean)||t.isKind(KW_image)||t.isKind(KW_url)||t.isKind(KW_file)||t.isKind(IDENTIFIER)){
				
			if(t.isKind(KW_int)||t.isKind(KW_boolean)||t.isKind(KW_image)||t.isKind(KW_url)||t.isKind(KW_file)){
				declaration();
				match(SEMI);
			}
			else{
				
			  if(t.isKind(IDENTIFIER)){//statement code followed by semi
				statement();
				match(SEMI);
			  } 
			}
		 }	
			
		}
		else{
		throw new SyntaxException(t,"Expected IDENTIFIER");
		}
		
	}

	

	 void declaration()  throws SyntaxException {
		// TODO Auto-generated method stub
	   if(t.isKind(KW_int)||t.isKind(KW_boolean)){ //variable_declaration
		   consume();
		   if(t.isKind(IDENTIFIER)){
			   consume();
			   if(t.isKind(OP_ASSIGN)){
				   consume();
				   expression();
			   }
		   }
		   else{
				throw new SyntaxException(t,"Expected IDENTIFIER");
		   }
	   }
	   if(t.isKind(KW_image)){ //image_declaration
          
             imageDeclaration();
	   }
	   if(t.isKind(KW_url)||t.isKind(KW_file)){ //source_sink_declaratopn
		   sourceSinkDeclaration();
	   }
	}
	 
	 void statement()  throws SyntaxException {
			// TODO Auto-generated method stub
		   if(t.isKind(IDENTIFIER)){
			   consume();
			  
			   if(t.isKind(OP_RARROW)){
				   consume();
				   if(t.isKind(IDENTIFIER)||t.isKind(KW_SCREEN)){
					   consume();
				   }else{
					   
					   throw new SyntaxException(t,"Expected IDENTIFIER or KW_SCREEN ");

				   }
			   }
			   else if(t.isKind(OP_LARROW)){
				   consume();
				   source(); 
				  
			   }
			   else if(t.isKind(LSQUARE)){//Assignment_statement
				   consume();
				   lhsSelector();
				    match(RSQUARE);
				    match(OP_ASSIGN);
				    expression();

				   
			   }
			   else
			   {// if epsilon
			    match(OP_ASSIGN);
			    expression();
			     
			   }
			  
		   }
		   else{
				
			   throw new SyntaxException(t,"Expected  IDENTIFIER at statement");

		   }
			
		}
	  

	void lhsSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.isKind(LSQUARE)){
			consume();
			if(t.isKind(KW_x)){
				consume();
				match(COMMA);
				match(KW_y);
			
			}
			if(t.isKind(KW_r)){
				consume();
				match(COMMA);
				match(KW_A);
				}
			if(t.isKind(RSQUARE)){
				consume();
			}
			else{
				  throw new SyntaxException(t,"Expected RSQUARE");

			}
			
		}
		else
		{
		  throw new SyntaxException(t,"Expected LSQUARE");

		}
		
	}

	void imageDeclaration()  throws SyntaxException {
			// TODO Auto-generated method stub
		   match(KW_image);
		   if(t.isKind(LSQUARE)){
			   consume();
			   expression();
			   match(COMMA);
			   expression();
			   match(RSQUARE);
		   }
		   match(IDENTIFIER);
		   if(t.isKind(OP_LARROW)){
			   consume();
			   source();
		   }
			
		}
	 void sourceSinkDeclaration()  throws SyntaxException {
			// TODO Auto-generated method stub
		    if(t.isKind(KW_url)||t.isKind(KW_file)){
		    	consume();
		    	match(IDENTIFIER);
		    	match(OP_ASSIGN);
		    	source();
		    }
		    else{
				  
		    	throw new SyntaxException(t,"Expected KW_url or KW_file");

		    }
			
		}

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * 
	 * @throws SyntaxException
	 */
	void expression() throws SyntaxException {
		//TODO implement this.
		orExpression();
		if(t.isKind(OP_Q))
		{
			consume();
			expression();
			match(OP_COLON);
			expression();
			
		}
		//throw new UnsupportedOperationException();
	}

	
	void orExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		andExpression();
		
		while(t.isKind(OP_OR)){
			consume();
			andExpression();
		}
		
	}
	
	void andExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		eqExpression();
		
		while(t.isKind(OP_AND)){
			consume();
			eqExpression();
		}
		
	}
	
	void eqExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		relExpression();
		
		while(t.isKind(OP_EQ)||t.isKind(OP_NEQ)){
			consume();
			relExpression();
		}
		
	}
	
	void relExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		addExpression();
		
		while(t.isKind(OP_LT)||t.isKind(OP_GT)||t.isKind(OP_LE)||t.isKind(OP_GE)){
			consume();
			addExpression();
		}
		
	}
	
	void addExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		multExpression();
		
		while(t.isKind(OP_PLUS)||t.isKind(OP_MINUS)){
			consume();
			multExpression();
		}
		
	}
	
	void multExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		unaryExpression();
		
		while(t.isKind(OP_TIMES)||t.isKind(OP_DIV)||t.isKind(OP_MOD)){
			consume();
			unaryExpression();
		}
		
	}
	
	void unaryExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		if(t.isKind(OP_PLUS)){
			consume();
			unaryExpression();
		}
		else if(t.isKind(OP_MINUS)){
			consume();
			unaryExpression();

		}
		else{
			unaryExpressionNotPlusOrMinus();
		}
		
	}
	void unaryExpressionNotPlusOrMinus() throws SyntaxException {
		// TODO Auto-generated method stub
		
		if(t.isKind(OP_EXCL)){
			consume();
			unaryExpression();
		}
		else if(t.isKind(KW_x)||t.isKind(KW_y)||t.isKind(KW_r)||t.isKind(KW_a)||t.isKind(KW_X)||t.isKind(KW_Y)||t.isKind(KW_Z)||t.isKind(KW_A)||t.isKind(KW_R)||t.isKind(KW_DEF_X)||t.isKind(KW_DEF_Y)){
		   consume();	
		}
		else if(t.isKind(BOOLEAN_LITERAL)||t.isKind(INTEGER_LITERAL)||t.isKind(LPAREN)||t.isKind(KW_sin)||t.isKind(KW_cos)||t.isKind(KW_atan)||t.isKind(KW_abs)||t.isKind(KW_cart_x)||t.isKind(KW_cart_y)||t.isKind(KW_polar_a)||t.isKind(KW_polar_r)){
			primary();
		}
		else if(t.isKind(IDENTIFIER)||t.isKind(BOOLEAN_LITERAL)){
			identOrPixelSelectorExpression();
		}
		else{
	    	throw new SyntaxException(t,"Expected unaryExpressionNotPlusOrMinus"+t.kind);
		}
		
	}
	
	void primary() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.isKind(INTEGER_LITERAL)||t.isKind(BOOLEAN_LITERAL)){
			consume();
		}
		else if(t.isKind(LPAREN)){
			consume();
			expression();
			match(RPAREN);
		}
		else{
			functionApplication();
		}
		
	}
	void functionApplication() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.isKind(KW_sin)||t.isKind(KW_cos)||t.isKind(KW_atan)||t.isKind(KW_abs)||t.isKind(KW_cart_x)||t.isKind(KW_cart_y)||t.isKind(KW_polar_a)||t.isKind(KW_polar_r))
		{
			consume();
			if(t.isKind(LPAREN)){
				consume();
				expression();
				match(RPAREN);
				
			}
			else if(t.isKind(LSQUARE)){
				consume();
				selector();
				match(RSQUARE);
			}else{
		    	throw new SyntaxException(t,"Expected LPAREN or LSQUARE ");

			}
		}
		else{
	    	throw new SyntaxException(t,"Expected functionName");
	
		}
		
	}
  
	void Functionname() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.isKind(KW_sin)||t.isKind(KW_cos)||t.isKind(KW_atan)||t.isKind(KW_abs)||t.isKind(KW_cart_x)||t.isKind(KW_cart_y)||t.isKind(KW_polar_a)||t.isKind(KW_polar_r))
		{
			consume();
		}
		
	}
	void selector() throws SyntaxException {
		// TODO Auto-generated method stub
		expression();
		match(COMMA);
		expression();
		
	}
	void identOrPixelSelectorExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		match(IDENTIFIER);
		if(t.isKind(LSQUARE)){
			consume();
			selector();
			match(RSQUARE);
		}
		
	}
	
	
	public void RaSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.isKind(KW_r)){
			consume();
			match(COMMA);
			match(KW_A);
			}
		else
		{
			throw new SyntaxException(t,"Expected KW_r");
		}
		
	}
	void source()  throws SyntaxException {
			// TODO Auto-generated method stub
		 if(t.isKind(STRING_LITERAL)||t.isKind(IDENTIFIER)){
			   consume();
		   }
		   else if(t.isKind(OP_AT)){
			   consume();
			   expression();
		   }
		   else{
			  
			   throw new SyntaxException(t,"Expected STRING_LITERAL or IDENTIFIER or OP_AT expression ");
		   }
		}


	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message =  "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
	
	private Token match(Kind kind) throws SyntaxException{
		
		if (t.isKind(kind)) {
			return consume();
		}
		String message =  "Expected kind at " + t.line + ":" + t.kind;
		throw new SyntaxException(t,message);
	}
	
	private Token consume() throws SyntaxException{
		Token temp = t;
		t = scanner.nextToken();
		return temp;
	}

	

	
	
	
}
