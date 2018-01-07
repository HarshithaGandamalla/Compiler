package cop5556fa17;

import java.net.URI;
import java.util.HashMap;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.AST.*;


import cop5556fa17.TypeUtils.Type;

public class TypeCheckVisitor implements ASTVisitor {
	

		@SuppressWarnings("serial")
		public static class SemanticException extends Exception {
			Token t;

			public SemanticException(Token t, String message) {
				super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
				this.t = t;
			}

		}		
		
      HashMap<String,Declaration> symbolTable = new HashMap<String,Declaration>();
	
	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		
		
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg); 

			
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		Expression e=declaration_Variable.e;

		if(declaration_Variable.e!=null)
		e=(Expression)declaration_Variable.e.visit(this, null);
		
		if(!symbolTable.containsKey(declaration_Variable.name)){
			
			symbolTable.put(declaration_Variable.name,declaration_Variable);
			
			
				declaration_Variable.set_type(TypeUtils.getType(declaration_Variable.type));
	
		
			
			if(declaration_Variable.e!=null){
               
				if(!e.get_type().equals(declaration_Variable.get_type()))
					throw new SemanticException(declaration_Variable.firstToken, "Type Exception Occured at visitDeclaration_Variable");

			
			}
			
			
			
		}
		else{
			String msg = "Symbol table should not contain key declaration_Variable."+declaration_Variable.name;
			throw new SemanticException(declaration_Variable.firstToken, msg );
		}
		return declaration_Variable;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub 
		//REQUIRE:  Expression0.Type == Expression1.Type  && Expression_Binary.Type ? ?
		
		Expression e0 = expression_Binary.e0;
		Expression e1 = expression_Binary.e1;
		Kind op=expression_Binary.op;
		
		if(e0!=null)
			e0=(Expression)e0.visit(this, null);
		//else 
		//	throw new SemanticException(expression_Binary.firstToken, "Type Exception Occured at visitExpression_Binary");
		  e1=expression_Binary.e1;
		if(e1!=null)
		{	
			e1=(Expression)e1.visit(this, null);
		
		
		
			
			if(op.equals(Kind.OP_EQ)||op.equals(Kind.OP_NEQ))
			{
					expression_Binary.set_type(Type.BOOLEAN);
			}
			else if((op.equals(Kind.OP_GE)||op.equals(Kind.OP_GT)||op.equals(Kind.OP_LT)||op.equals(Kind.OP_LE))&&(e0.get_type().equals(Type.INTEGER))){
				expression_Binary.set_type(Type.BOOLEAN);
			}else if((op.equals(Kind.OP_AND)||op.equals(Kind.OP_OR))&&(e0.get_type().equals(Type.INTEGER)||e0.get_type().equals(Type.BOOLEAN)))
				expression_Binary.set_type(e0.get_type());
			else if((op.equals(Kind.OP_DIV)||op.equals(Kind.OP_MINUS)||op.equals(Kind.OP_MOD)||op.equals(Kind.OP_PLUS)||op.equals(Kind.OP_POWER)||op.equals(Kind.OP_TIMES))&&(e0.get_type().equals(Type.INTEGER))){
				expression_Binary.set_type(Type.INTEGER);

			}else{
				expression_Binary.set_type(null);
			}

	
				
				if(!(e0.get_type().equals(e1.get_type()) && expression_Binary.get_type()!=null)){
					String msg = "expression_Binary type error";
					throw new SemanticException(expression_Binary.firstToken, msg );
				}
		}

		return expression_Binary;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		
		Expression e=expression_Unary.e;
		
		if(e!=null) 
		{	
			e=(Expression)e.visit(this, null);
		
		
		Type  t = expression_Unary.e.get_type();
	 	Kind op = expression_Unary.op;
		
	 	if(op.equals(Kind.OP_EXCL)&&(t.equals(Type.BOOLEAN)||t.equals(Type.INTEGER))){
	 		expression_Unary.set_type(t);
	 	}else if((op.equals(Kind.OP_PLUS)||op.equals(Kind.OP_MINUS))&&t.equals(Type.INTEGER)){
	 		expression_Unary.set_type(Type.INTEGER);
	 	}else{
	 		String msg = "Expression_Unary error of type";
			throw new SemanticException(expression_Unary.firstToken, msg );
	 	}
		
	 	if(expression_Unary.get_type().equals(null)){
	 		String msg = "Expression_Unary type should be set";
			throw new SemanticException(expression_Unary.firstToken, msg );
	 	}
	}
		return expression_Unary;

		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
	
		Expression e0=index.e0;
		Expression e1=index.e1;

		if(e0!=null) 
		{	e0=(Expression)e0.visit(this, null);
		
		 if(e1!=null) 
		 {	e1=(Expression)e1.visit(this, null);
		
		 if(index.e0.get_type().equals(Type.INTEGER)&&index.e1.get_type().equals(Type.INTEGER))
		 {
			Boolean b = index.e0.get_type().equals(Kind.KW_r) && index.e1.get_type().equals(Kind.KW_a); 
			index.setCartesian(!(e0.firstToken.kind==Kind.KW_r && e1.firstToken.kind==Kind.KW_a));
			
		 }
		 else{
			String msg = "index exp type should be INTEGER";
			throw new SemanticException(index.firstToken, msg );
		  }
	     }
	  }
		
		return index;
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		
		expression_PixelSelector.index.visit(this, arg);

		Declaration dec = symbolTable.get(expression_PixelSelector.name);
		
		if(dec!=null){
		if(dec.get_type().equals(Type.IMAGE))
		  expression_PixelSelector.set_type(Type.INTEGER);
		else if(expression_PixelSelector.index == null){
			expression_PixelSelector.set_type(dec.get_type());
		}
		else{
			String msg = "expression_PixelSelector name should be IMAGE or Index should be null";
			throw new SemanticException(expression_PixelSelector.firstToken, msg );
		}
		
		if(expression_PixelSelector.get_type()==null){
			String msg = "expression_PixelSelector type should bot be null";
			throw new SemanticException(expression_PixelSelector.firstToken, msg );
		}
		Index i=expression_PixelSelector.index;
		if(i!=null)
			i=(Index)i.visit(this, null);
		
		
		}
		else
		 throw new SemanticException(expression_PixelSelector.firstToken, "expression_PixelSelector.name dec should not be null");
  
		return expression_PixelSelector;
}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
	
		
		Expression conditionExpression=expression_Conditional.condition;
		Expression trueExpression=(Expression)expression_Conditional.trueExpression;
		Expression falseExpression=(Expression)expression_Conditional.falseExpression;
		
		
		if(conditionExpression!=null)
		{
			conditionExpression=(Expression)conditionExpression.visit(this, null);
		if(trueExpression!=null)
		{	
			trueExpression=(Expression)trueExpression.visit(this, null);

		if(falseExpression!=null)
		{	
			falseExpression=(Expression)falseExpression.visit(this, null);
			if(conditionExpression.get_type().equals(Type.BOOLEAN) && 
				expression_Conditional.trueExpression.get_type().equals(expression_Conditional.falseExpression.get_type())){
				expression_Conditional.set_type(expression_Conditional.trueExpression.get_type());
			}
			else
			{
				String msg = "Expression_Conditional should be boolean and trueExpression type should equal to  falseExpression";
				throw new SemanticException(expression_Conditional.firstToken, msg );
			}
	
		}
	}
	}
		
		return expression_Conditional;
}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		
		if(!symbolTable.containsKey(declaration_Image.name)) 
		{
			
			symbolTable.put(declaration_Image.name,declaration_Image);
			declaration_Image.set_type(Type.IMAGE);
		}
		else{
			String msg = "Symbol table should not contain key declaration_image.";
			throw new SemanticException(declaration_Image.firstToken, msg );
		}
		
		
       
        Source s=declaration_Image.source;
        
        if(declaration_Image.source!=null)
		 s=(Source) declaration_Image.source.visit(this, null);
        
        Expression xSize;
		Expression ySize;
    

	
		//throw new UnsupportedOperationException();
		
		 
//		  System.out.println(declaration_Image.ySize.get_type().equals(Type.INTEGER));

		if(declaration_Image.xSize!=null)
			{
			
        	xSize=(Expression) declaration_Image.xSize.visit(this, arg);

			  if(declaration_Image.ySize!=null)
			  {
				  
		        	ySize = (Expression) declaration_Image.ySize.visit(this, arg);

//				

				  if(!(declaration_Image.xSize.get_type().equals(Type.INTEGER) && declaration_Image.ySize.get_type().equals(Type.INTEGER))){
					    String msg = "xSize and ySize should be INTEGERS";
						throw new SemanticException(declaration_Image.firstToken, msg );
				  }
				 
			  }else{
				    String msg = "ySize of declaration_Image is empty but xSize id not";
					throw new SemanticException(declaration_Image.firstToken, msg );
			  }
			}
		
		return declaration_Image;
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		//Source_StringLIteral.Type <= if isValidURL(fileOrURL) then URL else FILE
		
		
		if(isValidURL(source_StringLiteral.fileOrUrl)){
			
		
		
			source_StringLiteral.set_type(Type.URL);	
		
		
		
		}else{
		
			
	    	source_StringLiteral.set_type(Type.FILE);	

	    }

	     return source_StringLiteral;
	}

	public boolean isValidURL(String urlStr) {
	    try {
	      URI uri = new URI(urlStr);
	      return uri.getScheme().equals("http") || uri.getScheme().equals("https");
	    }
	    catch (Exception e) {
	        return false;
	    }
	}
	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		
		Expression e=(Expression)source_CommandLineParam.paramNum;

		if(e!=null){
			e=(Expression)e.visit(this, null);
		
		
		//source_CommandLineParam.set_type(source_CommandLineParam.paramNum.get_type());
		
		source_CommandLineParam.set_type(null);
		
			
		if(!e.get_type().equals(Type.INTEGER))
		{
			String msg = "Expression.paramnum in source_CommandLineParam type should be INTEGER";
			throw new SemanticException(source_CommandLineParam.firstToken, msg );
		}
		
//		if(!source_CommandLineParam.get_type().equals(Type.INTEGER))
//		{
//			String msg = "source_CommandLineParam type should be INTEGER";
//			throw new SemanticException(source_CommandLineParam.firstToken, msg );
//		}
		
		
	}

         return source_CommandLineParam;
}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		
		if(symbolTable.get(source_Ident.name)!=null)
		{
			source_Ident.set_type(symbolTable.get(source_Ident.name).get_type());

			if(source_Ident.get_type().equals(Type.FILE)|| source_Ident.get_type().equals(Type.URL))
				return source_Ident;
			else
				throw new SemanticException(source_Ident.firstToken,"Not a file or url");
		}
		else
		{
			throw new SemanticException(source_Ident.firstToken,"source_Ident.name not found");
		}
          
         // return source_Ident;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		  Source s=declaration_SourceSink.source;
		  
		  if(s!=null){
				s=(Source)declaration_SourceSink.source.visit(this, null);

		if(!symbolTable.containsKey(declaration_SourceSink.name)){
			symbolTable.put(declaration_SourceSink.name,declaration_SourceSink);
			if(declaration_SourceSink.type.equals(Kind.KW_url))
			{
				declaration_SourceSink.set_type(Type.URL);
	
			}
		   if(declaration_SourceSink.type.equals(Kind.KW_file))
			{
				declaration_SourceSink.set_type(Type.FILE);
	
			}
		   
		 

		  System.out.println("declaration_SourceSink.source "+s);
		   //System.out.println("declaration_SourceSink.get_type(): "+declaration_SourceSink.get_type());

			if((s.get_type()==null)||(s.get_type().equals(declaration_SourceSink.get_type())))
			{
				
				
				//do nothing
			}
			else {
				String msg = "declaration_SourceSink and Source type should be equal or source should be null";
				throw new SemanticException(declaration_SourceSink.firstToken, msg );
			}
			
		   }
		else{
			String msg = "Symbol table should not contain key declaration_SourceSink.";
			throw new SemanticException(declaration_SourceSink.firstToken, msg );
		}
		//throw new UnsupportedOperationException();
	
		
		
		//throw new UnsupportedOperationException();
	}
		return declaration_SourceSink;
		
}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
//		Expression_IntLit ::=  value
//				Expression_IntLIt.Type <= INTEGER
		
		expression_IntLit.set_type(Type.INTEGER);
		return expression_IntLit;

	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		
		Expression e=(Expression)expression_FunctionAppWithExprArg.arg;

		if(e!=null){
		e=(Expression)expression_FunctionAppWithExprArg.arg.visit(this, null);
		
		if(expression_FunctionAppWithExprArg.arg.get_type().equals(Type.INTEGER)){
			expression_FunctionAppWithExprArg.set_type(Type.INTEGER);
		}
		else{
			String msg = "expression_FunctionAppWithExprArg.Expression type should be INTEGER";
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, msg );
		}
	}
		
		return expression_FunctionAppWithExprArg;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		
		Index i=(Index)expression_FunctionAppWithIndexArg.arg;

		if(i!=null)
		i=(Index)i.visit(this, null);
		
		expression_FunctionAppWithIndexArg.set_type(Type.INTEGER);
		
		
		return expression_FunctionAppWithIndexArg;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		 //Expression_PredefinedName.TYPE <= INTEGER
				expression_PredefinedName.set_type(Type.INTEGER);
				
				return expression_PredefinedName;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
//		Statement_Out ::= name Sink
//				Statement_Out.Declaration <= name.Declaration 
//			               REQUIRE:  (name.Declaration != null)
//			              REQUIRE:   ((name.Type == INTEGER || name.Type == BOOLEAN) && Sink.Type == SCREEN)
//				                  ||  (name.Type == IMAGE && (Sink.Type ==FILE || Sink.Type == SCREEN))

		// TODO Auto-generated method stub
				
		
		Sink s=(Sink)statement_Out.sink;

		if(s!=null)
		{	
			s=(Sink)s.visit(this, null);
		    Declaration dec = symbolTable.get(statement_Out.name);
		    
		    if(dec!=null){
			
			Type t=dec.get_type();
			statement_Out.setDec(dec);

			Type sinkType=statement_Out.sink.get_type();

			if(((t.equals(Type.INTEGER)||t.equals(Type.BOOLEAN))&& sinkType.equals(Type.SCREEN))
			    ||(t.equals(Type.IMAGE)&&(sinkType.equals(Type.FILE) || sinkType.equals(Type.SCREEN)))){
				
				
			}else{
				String msg = "ERROR:  REQUIRE: ((name.Type == INTEGER || name.Type == BOOLEAN) && Sink.Type == SCREEN   ||  (name.Type == IMAGE && (Sink.Type ==FILE || Sink.Type == SCREEN))";
				throw new SemanticException(statement_Out.firstToken, msg );
			}
			
		}
		   else
		   {
			    String msg = "ERROR: (name.Declaration == null)";
				throw new SemanticException(statement_Out.firstToken, msg );
	    	}
		
		}
		
		return statement_Out;

		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		
//		    Statement_In ::= name Source
//			Statement_In.Declaration <= name.Declaration
//			REQUIRE:  (name.Declaration != null) & (name.type == Source.type) --??
  
	    	Source s=statement_In.source;

	    	if(s!=null){
		     s=(Source)s.visit(this, null);
	    	
		   
//		   
//		  if(symbolTable.get(statement_In.name)!=null)
//		  {  
			  Type t = symbolTable.get(statement_In.name).get_type();
		  
			  System.out.println("symbolTable.get(statement_In.name): type"+symbolTable.get(statement_In.name));
			  statement_In.setDec(symbolTable.get(statement_In.name));
//
//			if(t.equals(statement_In.source.get_type()))
//			{ //add name type check too
//			  
//			  
//		    }
//		    else
//		    {
//		 	    String msg = "ERROR: (name.type != Source.type)"+t+" "+s.get_type();
//				throw new SemanticException(statement_In.firstToken, msg ); 
//		    }
	    //   }
//		  else
//		  {
//		   String msg = "ERROR: (name.Declaration == null) ";
//		   throw new SemanticException(statement_In.firstToken, msg ); 
//	     }
	  }
		
		 return statement_In;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
//		   Statement_Assign ::=  LHS  Expression
//				REQUIRE:  LHS.Type == Expression.Type
//				StatementAssign.isCartesian <= LHS.isCartesian
       
		Expression exp = statement_Assign.e;
		LHS lhs=statement_Assign.getLHS();
		
		
		if(exp!=null){
		  exp=(Expression)exp.visit(this, null);
		   if(lhs!=null)
		     lhs=(LHS)lhs.visit(this, null);
			
		   statement_Assign.setCartesian(lhs.isCartesian());

		   if(lhs.get_type().equals(exp.get_type()) || (lhs.get_type().equals(Type.IMAGE) && exp.get_type().equals(Type.INTEGER))){
		   }
		  else{
			String msg = "ERROR: LHS.Type != Expression.Type";
			throw new SemanticException(statement_Assign.firstToken, msg );
		  }
	}
		
		return statement_Assign;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
//		LHS.Declaration <= symbolTable.lookupDec(name)
//	              LHS.Type <= LHS.Declaration.Type
//	              LHS.isCarteisan <= Index.isCartesian
		
		Index i=(Index)lhs.index;
		if(i!=null)
			i=(Index)i.visit(this, null);
		
		if(symbolTable.containsKey(lhs.name)){
			
			lhs.setDec(symbolTable.get(lhs.name));

			lhs.set_type(lhs.getDec().get_type());
			if(i!=null)
			lhs.setCartesian(lhs.index.isCartesian());
			else
				lhs.setCartesian(false);

				
			
			
		}
		else{
			String msg = "Symbol table should  contain key lhs.name";
            throw new SemanticException(lhs.firstToken,msg);
         }


		 return lhs;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
//		Sink_SCREEN.Type <= SCREEN
		
		sink_SCREEN.set_type(Type.SCREEN);
		return sink_SCREEN;
		
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {

		
		if(symbolTable.get(sink_Ident.name)!=null)
		{
			
		
			
		 sink_Ident.set_type(symbolTable.get(sink_Ident.name).get_type());
       
		 if(!sink_Ident.get_type().equals(Type.FILE))
		 {
			String msg = "sink_Ident type should be FILE";
            throw new SemanticException(sink_Ident.firstToken,msg);
		 }
		}
		else
			throw new SemanticException(sink_Ident.firstToken, "sink_Ident.name not found");
		
		return sink_Ident;

	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
			throws Exception {
	
		expression_BooleanLit.set_type(Type.BOOLEAN);

		
		return expression_BooleanLit;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
//		Expression_Ident  ::=   name
//				Expression_Ident.Type <= symbolTable.lookupType(name)
		
				
		if(symbolTable.get(expression_Ident.name)!=null)	
    		expression_Ident.set_type(symbolTable.get(expression_Ident.name).get_type());
		else
			throw new SemanticException(expression_Ident.firstToken,"expression_Ident.name not found");
		
		return expression_Ident;
	}

}
