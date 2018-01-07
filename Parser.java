package cop5556fa17;

import cop5556fa17.AST.*;


import java.util.ArrayList;
import java.util.Arrays;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class Parser{

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

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input.
	 * Checks for EOF
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program node = program();
		matchEOF();
		return node;
	}
	

	/**
	 * Program ::=  IDENTIFIER   ( Declaration SEMI | Statement SEMI )*   
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {

		Token first = t;
		Token name;
		
		//todo add to arraylist 
		ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		
		if(t.isKind(IDENTIFIER)){
			
			name=t; //check if name = identifier
			consume();
			
			while(t.isKind(KW_int)||t.isKind(KW_boolean)||t.isKind(KW_image)||t.isKind(KW_url)||t.isKind(KW_file)||t.isKind(IDENTIFIER)){
				
			if(t.isKind(KW_int)||t.isKind(KW_boolean)||t.isKind(KW_image)||t.isKind(KW_url)||t.isKind(KW_file)){
//				declaration();

				decsAndStatements.add(declaration());
				match(SEMI);
			}
			else{
				
			  if(t.isKind(IDENTIFIER)){
				//statement();
				decsAndStatements.add(statement());

				match(SEMI);
			  } 
			}
		 }	
			
		}
		else{
		throw new SyntaxException(t,"Expected IDENTIFIER at program"+t.kind);
		}
		
		return new Program(first,name,decsAndStatements);
		
	}

	

	 Declaration declaration()  throws SyntaxException {

		 Token first=t;
		 Declaration dec;
		 
		if(t.isKind(KW_int)||t.isKind(KW_boolean)){ //variable_declaration
			 
		   dec = variableDeclaration();
	   }
		else if(t.isKind(KW_image)){ //image_declaration
          
             dec = imageDeclaration();
	   }
		else if(t.isKind(KW_url)||t.isKind(KW_file)){ //source_sink_declaratopn
		   dec = sourceSinkDeclaration();
	   }
		else{
			   throw new SyntaxException(t,"Expected declaration");

		}
	   
	   return dec;
	}
	 
	 Statement statement()  throws SyntaxException {
			// TODO Auto-generated method stub
		 
		 Token first=t;
		 Statement stat;
		 
		   if(t.isKind(IDENTIFIER)){
			   Token name = t;
			   consume();
			  
			   if(t.isKind(OP_RARROW)){ //Imageoutstatement
				   
				   consume();
				   if(t.isKind(IDENTIFIER)){ //store This ident name ?
					   //sink
					   Token name1=t;
					   Sink sink= new Sink_Ident(first,name1);
					   consume();
					   stat = new Statement_Out(first,name,sink); //which name? name or name1
				   }
				   else if(t.isKind(KW_SCREEN))
				   {
					  
					      Sink sink= new Sink_SCREEN(first);
					      consume();
						  stat = new Statement_Out(first,name,sink);
				   }
				   else{
					   
					   throw new SyntaxException(t,"Expected IDENTIFIER or KW_SCREEN ");

				   }
			   }
			   else if(t.isKind(OP_LARROW)){ //ImageInstatement
				  
				   consume();
				   Source source =  source(); 
				   
				   stat = new Statement_In(first,name,source);
				  
			   }
			   else if(t.isKind(LSQUARE)){//Assignment_statement
				   //todo lhs return
				  
				   consume();
				   Index index = lhsSelector();
				 
				    match(RSQUARE);
				    match(OP_ASSIGN);
				  
				    Expression e= expression();
				   
				    LHS lhs=new LHS(first,name,index);
				   
                    stat = new Statement_Assign(first, lhs, e);
				   
			   }
			   else
			   {// if epsilon
			    
				match(OP_ASSIGN);
			    Expression e = expression();
			    
			    LHS lhs=new LHS(first,name,null);

			    
			    stat = new Statement_Assign(first,lhs,e); //check if null?
			     
			   }
			  
		   }
		   else{
				
			   throw new SyntaxException(t,"Expected  IDENTIFIER at statement");

		   }
		   
		   return stat;
			
		}
	  

	Index lhsSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		Expression e0 = null;
		Expression e1 = null;
		Index index;
		Token first=t;
		
		if(t.isKind(LSQUARE)){
			
			consume();
			
			if(t.isKind(KW_x)){ //XySelector
				
				e0 = new Expression_PredefinedName(t,t.kind);
				consume();
				match(COMMA);
				//match(KW_y);
				e1 = new Expression_PredefinedName(t,t.kind);
				consume();
			
			}
			if(t.isKind(KW_r)){ // RaSelector 
				
				e0 = new Expression_PredefinedName(t,t.kind);

				consume();
				match(COMMA);
				//match(KW_A);
				e1 = new Expression_PredefinedName(t,t.kind);
				consume();

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
		
		index=new Index(first,e0,e1); //check this okay if e0 and e1 are null?
		
		return index;
	}
	
	Declaration_Variable variableDeclaration()  throws SyntaxException {
		// TODO Auto-generated method stub
		
		Token first = t;
     	Token type = null;
		Token name = null;
		Expression expr = null;
		Declaration_Variable dec;
		if(t.isKind(KW_int)||t.isKind(KW_boolean)){ //variable_declaration
			  type = t;
			  consume();
			  if(t.isKind(IDENTIFIER)){
				   name = t;
				   consume();
				   if(t.isKind(OP_ASSIGN)){
					  
					   consume();
					   expr = expression();
					   
				   }
			   }
			   else{
					throw new SyntaxException(t,"Expected IDENTIFIER at declaration"+t.kind);
			   }
		   }
		  
		     dec = new  Declaration_Variable(first,type,name, expr);
				
		     return dec;
		  // Declaration_Variable(Token firstToken,  Token type, Token name, Expression e)  
		
	}

	Declaration_Image imageDeclaration()  throws SyntaxException {
			// TODO Auto-generated method stub
		
		Token first = t;
		Expression xSize = null;
		Expression ySize = null;
		Token name;
		Source src = null;

		   match(KW_image);
		   if(t.isKind(LSQUARE)){
			   consume();
			 xSize = expression();
			 System.out.println("xSize:"+xSize);;
			   match(COMMA);
			 ySize = expression();
			 System.out.println("ySize:"+ySize);;

			   match(RSQUARE);
		   }
		   
		   name=t;
		   
		   match(IDENTIFIER);
		   if(t.isKind(OP_LARROW)){
			   consume();
			  src = source();
		   }
		   
		   Declaration_Image dec = new Declaration_Image(first,  xSize,  ySize, name , src);
		   
		   return dec;

		   //Declaration_Image(Token firstToken, Expression xSize, Expression ySize, Token name,Source source)
			
		}
	
	
	
	 Declaration_SourceSink sourceSinkDeclaration()  throws SyntaxException {
			// TODO Auto-generated method stub
		  
		    Token first = t;
			Token name;
			Token type;
			Source src = null;
			
		 //Declaration_SourceSink(Token firstToken, Token type, Token name, Source source)
		    if(t.isKind(KW_url)||t.isKind(KW_file)){
		    	type = t;
		    	consume();
		    	name=t;
		    	match(IDENTIFIER);
		    	
		    	match(OP_ASSIGN);
		    	src = source();
		    }
		    else{
				  
		    	throw new SyntaxException(t,"Expected KW_url or KW_file");

		    }
		    
		    Declaration_SourceSink dec = new Declaration_SourceSink(first,type,name,src);
		    
		    return dec;
		    
			
		}

	/**
	 * Expression ::=  OrExpression  OP_Q  Expression OP_COLON Expression    | OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental development.
	 * @return 
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		//TODO implement this.
		
		Token first = t;

		Expression expr;
		Expression condition;
		Expression trueExpression = null;
		Expression falseExpression = null;
		
		condition = orExpression();
		
		if(t.isKind(OP_Q))
		{
			consume();
			trueExpression = expression();
			match(OP_COLON);
			falseExpression = expression();
		    expr = new Expression_Conditional(first,condition,trueExpression,falseExpression);
		    return expr;

			
		}
		
		//Expression_Conditional(Token firstToken, Expression condition, Expression trueExpression,Expression falseExpression)
		
       return condition;
	}

	
	Expression orExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		

		Token first = t;
		Expression expr;
		Expression e0;
		Expression e1 = null;
		Token op = null;
		
		expr = andExpression();
		
		while(t.isKind(OP_OR)){
			op=t;
			consume();
			e1 = andExpression();
			expr = new Expression_Binary(first,expr,op,e1);
		}
		
		//Expression_Binary
		return expr;
	}
	
	Expression andExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		

		Token first = t;
		Expression expr;
		Expression e0;
		Expression e1 = null;
		Token op = null;
		
		expr = eqExpression();
		
		while(t.isKind(OP_AND)){
			op=t;
			consume();
			e1 = eqExpression();
			expr = new Expression_Binary(first,expr,op,e1);

		}
		
		//Expression_Binary
		return expr;

		
	}
	
	Expression eqExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		Token first = t;
		Expression expr;
		Expression e0;
		Expression e1 = null;
		Token op = null;
		
		expr = relExpression();
		
		while(t.isKind(OP_EQ)||t.isKind(OP_NEQ)){
			op=t;
			consume();
			e1 = relExpression();
			expr = new Expression_Binary(first,expr,op,e1);
		}
		
		//Expression_Binary
		return expr;


	}
	
	Expression relExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		Token first = t;
		Expression expr;
		Expression e0;
		Expression e1 = null;
		Token op = null;
		
		expr = addExpression();
		
		while(t.isKind(OP_LT)||t.isKind(OP_GT)||t.isKind(OP_LE)||t.isKind(OP_GE)){
			op=t;
			consume();
			e1 = addExpression();
			expr = new Expression_Binary(first,expr,op,e1);
		}
		
		//Expression_Binary
		return expr;

	}
	
	Expression addExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		Token first = t;
		Expression expr;
		Expression e0;
		Expression e1 = null;
		Token op = null;
		

		expr = multExpression();
		
		while(t.isKind(OP_PLUS)||t.isKind(OP_MINUS)){
			op=t;
			consume();
			e1= multExpression();
			expr = new Expression_Binary(first,expr,op,e1);
		}
		//Expression_Binary
		return expr;

	}
	
	Expression multExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		Token first = t;
		Expression expr;
		Expression e0;
		Expression e1 = null;
		Token op = null;

		expr = unaryExpression();
		while(t.isKind(OP_TIMES)||t.isKind(OP_DIV)||t.isKind(OP_MOD)){
			op=t;
			consume();
			e1 = unaryExpression();
			expr = new Expression_Binary(first,expr,op,e1);

		}
		

		return expr;
		//Expression_Binary(Token firstToken, Expression e0, Token op, Expression e1) 
         
		
	}
	
	Expression unaryExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		
		Token first = t;
		Token op;
		Expression e0;
		Expression expr;

		
		if(t.isKind(OP_PLUS)){
			op=t;
			consume();
			e0 = unaryExpression();
			expr=new Expression_Unary(first,op,e0);
		}
		else if(t.isKind(OP_MINUS)){
			op=t;
			consume();
			e0 = unaryExpression();
			expr=new Expression_Unary(first,op,e0);

		}
		else{
			expr = unaryExpressionNotPlusOrMinus();
		}
		
		return expr;
		
		//Expression_Unary(Token firstToken, Token op, Expression e)
	}
	
	Expression unaryExpressionNotPlusOrMinus() throws SyntaxException {
		// TODO Auto-generated method stub
		
		Expression expr;
		Expression e0;
		Token first = t;
		
		if(t.isKind(OP_EXCL)){
			Token op=t;
			consume();
			e0 = unaryExpression();
			expr = new Expression_Unary(first,op,e0);
			//Expression_Unary
		}
		else if(t.isKind(KW_x)||t.isKind(KW_y)||t.isKind(KW_r)||t.isKind(KW_a)||t.isKind(KW_X)||t.isKind(KW_Y)||t.isKind(KW_Z)||t.isKind(KW_A)||t.isKind(KW_R)||t.isKind(KW_DEF_X)||t.isKind(KW_DEF_Y)){
			   
			Kind name=t.kind;
			consume();	
		    expr = new Expression_PredefinedName(first,name);
		   //Expression_PredefinedName
		}
		else if(t.isKind(BOOLEAN_LITERAL)||t.isKind(INTEGER_LITERAL)||t.isKind(LPAREN)||t.isKind(KW_sin)||t.isKind(KW_cos)||t.isKind(KW_atan)||t.isKind(KW_abs)||t.isKind(KW_cart_x)||t.isKind(KW_cart_y)||t.isKind(KW_polar_a)||t.isKind(KW_polar_r)){
			
		  expr = primary(); //returns expression
			
		}
		else if(t.isKind(IDENTIFIER)){
		 expr = identOrPixelSelectorExpression();
		}
		else{
	    	throw new SyntaxException(t,"Expected unaryExpressionNotPlusOrMinus"+t.kind);
		}

		return expr;
		
	}
	
	Expression primary() throws SyntaxException {
		// TODO Auto-generated method stub
		
		Token first=t;
		Expression expr = null;
		
		if(t.isKind(INTEGER_LITERAL)){
			//Expression_IntLit(Token firstToken, int value)
			expr = new Expression_IntLit(first,t.intVal());
			consume();
			
		}else if(t.isKind(BOOLEAN_LITERAL)){
			// Expression_BooleanLit(Token firstToken, boolean value) 
			
			boolean value;

			if(t.getText().equals("true"))
				value=true;
			else
				value=false;
			
			expr=new  Expression_BooleanLit(first,value) ; //?????????????
			consume();
	
		}
		else if(t.isKind(LPAREN)){
			consume();
			expr = expression();
			match(RPAREN);
		}
		else{
			expr = functionApplication(); //return expr?
		}
		
		return expr;
		
	}
	Expression functionApplication() throws SyntaxException {
		// TODO Auto-generated method stub
		
		Token first = t;
		Expression expr;
		
		if(t.isKind(KW_sin)||t.isKind(KW_cos)||t.isKind(KW_atan)||t.isKind(KW_abs)||t.isKind(KW_cart_x)||t.isKind(KW_cart_y)||t.isKind(KW_polar_a)||t.isKind(KW_polar_r))
		{
			Expression e0;
			Kind kind = t.kind;
			consume();
			if(t.isKind(LPAREN)){
				consume();
				e0 = expression();
				match(RPAREN);
				expr = new Expression_FunctionAppWithExprArg(first,kind,e0);
				//Expression_FunctionAppWithExprArg(Token firstToken, Kind function, Expression arg)
			}
			else if(t.isKind(LSQUARE)){
				consume();
				Index index = selector();
				match(RSQUARE);
				expr = new Expression_FunctionAppWithIndexArg(first, kind , index);
				
			}else{
		    	throw new SyntaxException(t,"Expected LPAREN or LSQUARE ");

			}
		}
		else{
	    	throw new SyntaxException(t,"Expected functionName");
	
		}
		
		return expr;
	}
  
	void Functionname() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.isKind(KW_sin)||t.isKind(KW_cos)||t.isKind(KW_atan)||t.isKind(KW_abs)||t.isKind(KW_cart_x)||t.isKind(KW_cart_y)||t.isKind(KW_polar_a)||t.isKind(KW_polar_r))
		{
			consume();
		}
		
	}
	
	Index selector() throws SyntaxException {
		// TODO Auto-generated method stub
		Index index;
		Token first =t;
	   
		Expression e0 = expression();
		match(COMMA);
	    Expression e1 =	expression();
	
	    index = new Index(first,e0,e1);
	    
	    return index;
		
	}
	Expression identOrPixelSelectorExpression() throws SyntaxException {
		// TODO Auto-generated method stub
		Token first = t;
		Token name = t;
		Index index;
		Expression expr;
//		Expression_Ident(Token firstToken, Token ident)
       
		expr = new Expression_Ident(first, name);

		match(IDENTIFIER);
		
		if(t.isKind(LSQUARE)){
			
			consume();
		    index =	selector();
			match(RSQUARE);
			
		    expr =new Expression_PixelSelector(first,name,index);
		  
		}
		
		return expr;
	}
	
	
	public void RaSelector() throws SyntaxException {
		// TODO Auto-generated method stub
		if(t.isKind(KW_r)){
			consume();
			match(COMMA);
			match(KW_a);
			}
		else
		{
			throw new SyntaxException(t,"Expected KW_r");
		}
		
	}
	Source source()  throws SyntaxException {
		
		Token first=t;
		Source source = null;
		
		 if(t.isKind(STRING_LITERAL)){
			 
			  source = new Source_StringLiteral(first,t.getText());// ??????
			   consume();
		  }
		 else if(t.isKind(IDENTIFIER))
		 {
			 Token name=t;
			 source = new Source_Ident(first,name);
			 consume();
		 }
		 else if(t.isKind(OP_AT)){
			 
			 consume();
			 Expression expr = expression();
			 source = new Source_CommandLineParam(first,expr);

		  }
		 else
		   {
			  
			   throw new SyntaxException(t,"Expected STRING_LITERAL or IDENTIFIER or OP_AT expression ");
		   }
		 
		 return source;
		
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
