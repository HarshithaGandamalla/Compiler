package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Expression extends ASTNode {
	
	private Type types;

	public Expression(Token firstToken) {
		super(firstToken);
	}
	
	
	 public Type get_type(){
		 return types;
	 }
	 
	 public void set_type(Type t){
		 types=t;
	 }


}
