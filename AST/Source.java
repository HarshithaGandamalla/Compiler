package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Source extends ASTNode{


	public Source(Token firstToken) {
		super(firstToken);
	}
	
	private Type types;

	 public Type get_type(){
		 return types;
	 }
	 
	 public void set_type(Type t){
		 types=t;
	 }
}
