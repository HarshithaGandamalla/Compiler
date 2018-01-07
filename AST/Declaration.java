package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Declaration extends ASTNode {
	
	private Type types;


	public Declaration(Token firstToken) {
		super(firstToken);
	}

	 public Type get_type(){
		 return types;
	 }
	 
	 public void set_type(Type t){
		 types=t;
	 }


}
