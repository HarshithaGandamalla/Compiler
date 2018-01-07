package cop5556fa17;

import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.ImageFrame;
import cop5556fa17.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */
	final int DEF_X=256;
    final int DEF_Y=256;
    final int Z = 16777215;


	HashMap<Scanner.Kind,Integer> hmopcode=new 	HashMap<Scanner.Kind,Integer>();
    
	FieldVisitor fv;

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	
	
	HashMap<TypeUtils.Type,String> hm;

	

	MethodVisitor mv; // visitor of method currently under construction
	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		
		hm=new HashMap<TypeUtils.Type,String>();
	    
	    

		hm.put(Type.BOOLEAN, "Z");
		hm.put(Type.INTEGER, "I");
		
		hmopcode.put(Kind.OP_PLUS, Opcodes.IADD);
		hmopcode.put(Kind.OP_MINUS, Opcodes.ISUB);
		hmopcode.put(Kind.OP_OR, Opcodes.IOR);
		hmopcode.put(Kind.OP_AND, Opcodes.IAND);
		hmopcode.put(Kind.OP_EQ, Opcodes.IF_ICMPEQ);
		hmopcode.put(Kind.OP_NEQ, Opcodes.IF_ICMPNE);
		hmopcode.put(Kind.OP_LT, Opcodes.IF_ICMPLT);
		hmopcode.put(Kind.OP_LE, Opcodes.IF_ICMPLE);
		hmopcode.put(Kind.OP_GT, Opcodes.IF_ICMPGT);
		hmopcode.put(Kind.OP_GE, Opcodes.IF_ICMPGE);
		hmopcode.put(Kind.OP_TIMES, Opcodes.IMUL);
		hmopcode.put(Kind.OP_DIV, Opcodes.IDIV);
		hmopcode.put(Kind.OP_MOD, Opcodes.IREM);


		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
	//	CodeGenUtils.genLog(GRADE, mv, "leaving main");
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at nd of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		
		mv.visitLocalVariable("x", "I", null, mainStart, mainEnd, 1);
		mv.visitLocalVariable("y", "I", null, mainStart, mainEnd, 2);
		mv.visitLocalVariable("X", "I", null, mainStart, mainEnd, 3);
		mv.visitLocalVariable("Y", "I", null, mainStart, mainEnd, 4);
		mv.visitLocalVariable("r", "I", null, mainStart, mainEnd, 5);
		mv.visitLocalVariable("R", "I", null, mainStart, mainEnd, 6);
		mv.visitLocalVariable("a", "I", null, mainStart, mainEnd, 7);
		mv.visitLocalVariable("A", "I", null, mainStart, mainEnd, 8);


		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.ful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//If you have trouble with failures in this routine, it may be use
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction lhs main method
		mv.visitEnd();
		
		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		// TODO 
		String fieldName = declaration_Variable.name;
		String fieldType = "";

		System.out.println("In dec variable");
		
		TypeUtils.Type type = declaration_Variable.get_type();	
		
		if(type.equals(Type.INTEGER)) {
			
			fieldType="I";
		fv = cw.visitField(ACC_STATIC, declaration_Variable.name, "I", null, null);
		}else if(type.equals(Type.BOOLEAN)){
			
			System.out.println("Decvariable bool");
			fieldType="Z";
		fv = cw.visitField(ACC_STATIC, declaration_Variable.name, "Z", null, null);
		
		}
		
		fv.visitEnd();
		
		if(declaration_Variable.e != null) {
			
		declaration_Variable.e.visit(this, arg);
		
		mv.visitFieldInsn(PUTSTATIC,className, declaration_Variable.name, fieldType);
		
		}
		return null;


		
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		// TODO 
		Label truelabel = new Label();
		Label falselabel = new Label();

		Expression e0=expression_Binary.e0;
		Expression e1=expression_Binary.e1;
		Type type0 = e0.get_type();
		Type type1 = e1.get_type();
		
		Kind op = expression_Binary.op;
		
		
		//Visiting E0 and E1
		if (e0 != null)
			e0.visit(this, arg);
		if (e1 != null)
			e1.visit(this, arg);
		
		if (op.equals(Kind.OP_PLUS)) {
			mv.visitInsn(IADD);
		} else if (op.equals(Kind.OP_MINUS)) {
			mv.visitInsn(ISUB);
		} else if (op.equals(Kind.OP_OR)) {
			mv.visitInsn(IOR);
		} else if (op.equals(Kind.OP_AND)) {
			mv.visitInsn(IAND);
		}else if (op.equals(Kind.OP_TIMES)) {
			mv.visitInsn(IMUL);
		} else if (op.equals(Kind.OP_DIV)) {
			mv.visitInsn(IDIV);
		} else if (op.equals(Kind.OP_MOD)) {
			mv.visitInsn(IREM);
		}else{
			
		
			mv.visitJumpInsn(hmopcode.get(op), truelabel);
			mv.visitLdcInsn(0);
			mv.visitJumpInsn(GOTO, falselabel);
			mv.visitLabel(truelabel);
			mv.visitLdcInsn(1);
			mv.visitLabel(falselabel);
			
		} 

	//	CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.get_type());
		return null;
		

	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		// TODO 
         
		System.out.println("IN exp unary");
		
		 Expression e = expression_Unary.e;
		 Kind op = expression_Unary.op;

         if (e != null)
			e.visit(this, arg);

		 
	    if (op.equals(Kind.OP_MINUS)) {
			
			mv.visitInsn(INEG);
		} 
		else if (op.equals(Kind.OP_EXCL)) {
			if (e.get_type().equals(TypeUtils.Type.INTEGER))
			{
				mv.visitLdcInsn(Integer.MAX_VALUE); //??
				mv.visitInsn(IXOR);
			} 
			if (e.get_type().equals(TypeUtils.Type.BOOLEAN)) {
				System.out.println("IN bool");

				Label truelabel = new Label();
				Label falselabel = new Label();

				mv.visitJumpInsn(IFEQ, falselabel);
				mv.visitLdcInsn(0);
				mv.visitJumpInsn(GOTO, truelabel);

				mv.visitLabel(falselabel);
				mv.visitLdcInsn(1);

				mv.visitLabel(truelabel);
			}
		}

		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.get_type());
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		
		System.out.println("Visitig index..");
		
		   if(index.e0 != null) {
			index.e0.visit(this, arg);
			}
			if(index.e1 != null) {
			index.e1.visit(this, arg);
			}
			
			if(!index.isCartesian()) {
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig,false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig,false);
			}
			
			return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		
		mv.visitFieldInsn(GETSTATIC, className,expression_PixelSelector.name , ImageSupport.ImageDesc);

		
		  if(expression_PixelSelector.index != null){
			  
			expression_PixelSelector.index.visit(this, arg);
			
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig,false);
			}
		return null;

	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO 
		System.out.println("In expression conditional..");
		Label truelabel = new Label();
		Label falselabel = new Label();
		
		Expression condition = expression_Conditional.condition;
		Expression trueExpression = expression_Conditional.trueExpression;
		Expression falseExpression = expression_Conditional.falseExpression;

		
		if(condition!=null)
		condition.visit(this, arg);
		
		
		mv.visitJumpInsn(IFEQ, falselabel);
		trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, truelabel);
		
		mv.visitLabel(falselabel);
		falseExpression.visit(this, arg);
		
		mv.visitLabel(truelabel);
		
		
		 return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		
		String fieldName = declaration_Image.name;
		
		
		
		
	    	fv = cw.visitField(ACC_STATIC, fieldName, ImageSupport.ImageDesc, null, null);
		    fv.visitEnd();
		
			
		if(declaration_Image.source!=null) {
				
				declaration_Image.source.visit(this, arg);
				
				
				if(declaration_Image.xSize==null && declaration_Image.ySize==null){
					
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
				
				}	
			  else{
				
				declaration_Image.xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
				declaration_Image.ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);
			   
				
				
			  }
				
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig,false);

				
		}
		else
		{
		
			System.out.println("Source in dec is null");
				
		 if(declaration_Image.xSize==null && declaration_Image.ySize==null){

			 mv.visitLdcInsn(DEF_X);
			 mv.visitLdcInsn(DEF_Y);
			 mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig,false);

		 //  mv.visitFieldInsn(GETSTATIC, className,"DEF_X","Ljava/lang/Integer");
		  // mv.visitFieldInsn(GETSTATIC, className,"DEF_Y","Ljava/lang/Integer");
         }else{
        	 
 		    	System.out.println("xsize ysize are available");

        		declaration_Image.xSize.visit(this, arg);
				declaration_Image.ySize.visit(this, arg);
			   
				 mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "makeImage", ImageSupport.makeImageSig,false);

				
         }
		 

	}
  
		mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name,ImageSupport.ImageDesc); 
		//mv.visitFieldInsn(PUTSTATIC, className, declaration_Image.name,ImageSupport.ImageDesc); 

		return null;
		
}
	
  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		// TODO HW6
		
		//LdcInsn??
		
//		    if(source_StringLiteral.get_type().equals()) {
//			 mv.visitFieldInsn(GETSTATIC, className, source_StringLiteral.fileOrUrl, ImageSupport.StringDesc);
//			}
//			else if(source_StringLiteral.get_type().equals(Type.URL)){
//			 mv.visitFieldInsn(GETSTATIC, className, source_StringLiteral.fileOrUrl, "Ljava/net/URL;");
//			}
		
        mv.visitLdcInsn(source_StringLiteral.fileOrUrl);
        
		return null;
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		mv.visitVarInsn(ALOAD, 0);
		source_CommandLineParam.paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		// TODO HW6
		
		
//		       if( source_Ident.get_type().equals(Type.FILE)) {
//				 mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, "Ljava/io/File;");
//				}
//				else if(source_Ident.get_type().equals(Type.URL)){
//				 mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, "Ljava/net/URL;");
//				}

		 mv.visitFieldInsn(GETSTATIC, className, source_Ident.name, ImageSupport.StringDesc);
		       
		       return null;

	//	throw new UnsupportedOperationException();
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		//throw new UnsupportedOperationException();
		
		Type t=declaration_SourceSink.get_type();
		
//		if(t.equals(Type.URL))
//		 {
//			fv = cw.visitField(ACC_STATIC, declaration_SourceSink.name, "Ljava/net/URL", null, null);
//		 }
//		else {
//			if(t.equals(Type.FILE)) 
//			fv = cw.visitField(ACC_STATIC, declaration_SourceSink.name, "Ljava/io/File", null, null);	
//		}
		
		fv = cw.visitField(ACC_STATIC, declaration_SourceSink.name, ImageSupport.StringDesc, null, null);
		
		fv.visitEnd();

		
		if(declaration_SourceSink.source!=null) {
		
			declaration_SourceSink.source.visit(this, arg);
			
//			if(t.equals(TypeUtils.Type.URL))
//			mv.visitFieldInsn(PUTSTATIC, className,declaration_SourceSink.name,"Ljava/net/URL");
//
//			if(t.equals(TypeUtils.Type.FILE))
//			mv.visitFieldInsn(PUTSTATIC, className,declaration_SourceSink.name,"Ljava/io/File");
			
			mv.visitFieldInsn(PUTSTATIC, className,declaration_SourceSink.name, ImageSupport.StringDesc);
		}	
		return null;
	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		
		System.out.println("Loading int expression on stack");
		mv.visitLdcInsn(expression_IntLit.value);
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6
		if(expression_FunctionAppWithExprArg.arg != null) {
			expression_FunctionAppWithExprArg.arg.visit(this, arg);
			}
			if( expression_FunctionAppWithExprArg.function == Kind.KW_abs) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"abs",RuntimeFunctions.absSig,false);

			}else if(expression_FunctionAppWithExprArg.function == Kind.KW_log) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"log",RuntimeFunctions.logSig, false);
			}
			return null;
	//	throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {

		//throw new UnsupportedOperationException();
		if(expression_FunctionAppWithIndexArg.arg.e0 != null) {
			expression_FunctionAppWithIndexArg.arg.e0.visit(this, arg);
			}
			if(expression_FunctionAppWithIndexArg.arg.e1 != null) {
			expression_FunctionAppWithIndexArg.arg.e1.visit(this, arg);
			}
			
			if( expression_FunctionAppWithIndexArg.function == Kind.KW_cart_x) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"cart_x",RuntimeFunctions.cart_xSig,false);

			}else if(expression_FunctionAppWithIndexArg.function == Kind.KW_cart_y) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"cart_y",RuntimeFunctions.cart_ySig, false);
			}else if(expression_FunctionAppWithIndexArg.function == Kind.KW_polar_a) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"polar_a",RuntimeFunctions.polar_aSig, false);
			}else if(expression_FunctionAppWithIndexArg.function == Kind.KW_polar_r) {
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"polar_r",RuntimeFunctions.polar_rSig, false);
			}
			return null;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6
		
		//throw new UnsupportedOperationException();
		Kind kind = expression_PredefinedName.kind;
		
		System.out.println("In predefined name");
	
		switch(kind)
		{
		case KW_x:
		mv.visitVarInsn(ILOAD, 1);
		break;
		case KW_y:
		mv.visitVarInsn(ILOAD, 2);
		break; 
		case KW_X:
		mv.visitVarInsn(ILOAD, 3);
		break;
		case KW_Y:
		mv.visitVarInsn(ILOAD, 4);
		break; 
		case KW_r:
		mv.visitVarInsn(ILOAD,1 );
		mv.visitVarInsn(ILOAD,2);
		mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r",RuntimeFunctions.polar_rSig, false);

		mv.visitVarInsn(ISTORE,5);
		mv.visitVarInsn(ILOAD,5);



		break;
		case KW_a:
			
			mv.visitVarInsn(ILOAD,1 );
			mv.visitVarInsn(ILOAD,2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a",RuntimeFunctions.polar_aSig, false);

			mv.visitVarInsn(ISTORE,7);
			mv.visitVarInsn(ILOAD,7);
			
		break;
		case KW_R:
		mv.visitVarInsn(ILOAD, 6);
		break;
		case KW_A:
		mv.visitVarInsn(ILOAD, 8);
		break;
		case KW_Z:
		mv.visitLdcInsn(Z);
		break;
		case KW_DEF_X:
		mv.visitLdcInsn(DEF_X);
		break;
		case KW_DEF_Y:
		mv.visitLdcInsn(DEF_Y);
		break;
		default:
		break;
		}
		return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		// TODO in HW5:  only INTEGER and BOOLEAN
		// TODO HW6 remaining cases
		
				
		if (statement_Out.getDec().get_type().equals(TypeUtils.Type.INTEGER)) {
		
			System.out.println("Int type");

			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, statement_Out.name, "I");
			CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
		
		} else if(statement_Out.getDec().get_type().equals(TypeUtils.Type.BOOLEAN)) {
			
			System.out.println("Boolean type");
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className,statement_Out.name, "Z");
			
			CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);

			
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);

			
		}else {
			if(statement_Out.getDec().get_type().equals(TypeUtils.Type.IMAGE)) {
				
			mv.visitFieldInsn(GETSTATIC, className,statement_Out.name,ImageSupport.ImageDesc);
			
			CodeGenUtils.genLogTOS(GRADE, mv, Type.IMAGE);
			
			if(statement_Out.sink!=null)
			{
				System.out.println("Visiting sink now. ");
				statement_Out.sink.visit(this, arg);
			}
			
           }
		}
		
		
		
	
		
		return null;
		
		//Correction:   You will need a call to CodeGenUtils.genLogTOS() in Statement_Out
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		
		
		if(statement_In.source!=null)
		{
			System.out.println("Visiting source");
			statement_In.source.visit(this, arg);
			
		}

		
		if(statement_In.getDec().get_type().equals(TypeUtils.Type.IMAGE)) {
		
			Declaration_Image DI=(Declaration_Image) statement_In.getDec();
			
			if(statement_In.source!=null)
			{
				System.out.println("Affriming source");
               if(DI.xSize==null && DI.ySize==null)
				{
					System.out.println("null sizes..");

            	   mv.visitInsn(ACONST_NULL);	
  			       mv.visitInsn(ACONST_NULL);

  			       mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig,false);

					
				  mv.visitFieldInsn(PUTSTATIC, className, statement_In.name,ImageSupport.ImageDesc);

				}else {
					
					System.out.println("Should set the xy sizes..");
					//use getx and gety
					
					DI.xSize.visit(this, arg);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);

					DI.ySize.visit(this, arg);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",false);



					mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "readImage", ImageSupport.readImageSig,false);

					mv.visitFieldInsn(PUTSTATIC, className, statement_In.name,ImageSupport.ImageDesc);

				}
				
				

			}
			

			
			
		}
		else if (statement_In.getDec().get_type().equals(TypeUtils.Type.INTEGER)) {
			
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "I");


		}

		else if (statement_In.getDec().get_type().equals(TypeUtils.Type.BOOLEAN)) {
			
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTSTATIC, className, statement_In.name, "Z");


		}
		else {
			
			
			System.out.println("ERROR should be either image or int or bool");
									
//				mv.visitFieldInsn(PUTSTATIC, className, statement_In.name,ImageSupport.ImageDesc);
			
			
		}

		return null;
	}

	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	
	
	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		//TODO  (see comment)
		
//		if (lhs.index != null)
//			lhs.index.visit(this, arg);

		
		
		if(lhs.get_type().equals(TypeUtils.Type.INTEGER))
	 	mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "I");
		else if(lhs.get_type().equals(TypeUtils.Type.BOOLEAN))
		 	mv.visitFieldInsn(PUTSTATIC, className, lhs.name, "Z");
		else {
			
			if(lhs.get_type().equals(TypeUtils.Type.IMAGE)) {
				
				System.out.println("In lhs Image");
				
				mv.visitFieldInsn(GETSTATIC, className, lhs.name, ImageSupport.ImageDesc);
			    mv.visitVarInsn(ILOAD, 1);
			    mv.visitVarInsn(ILOAD, 2);
			    mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className,"setPixel", ImageSupport.setPixelSig, false);
			    
			}
		}

		
		
		

		return null;
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		//TODO HW6
	//	throw new UnsupportedOperationException();
		
		System.out.println("ouputting to screen");
		
		
		
		mv.visitMethodInsn(INVOKESTATIC,ImageFrame.className, "makeFrame",ImageSupport.makeFrameSig, false);

		mv.visitInsn(POP);
		
		return null;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//TODO HW6
		//throw new UnsupportedOperationException();
		
		

		
		mv.visitFieldInsn(GETSTATIC, className, sink_Ident.name, ImageSupport.StringDesc);

		mv.visitMethodInsn(INVOKESTATIC,ImageSupport.className, "write",ImageSupport.writeSig, false);


		return null;
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		//TODO

		System.out.println("In exp bool_lit");

		mv.visitLdcInsn(expression_BooleanLit.value);
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
		
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		//TODO
		
		String fieldtype = hm.get(expression_Ident.get_type());
	
		mv.visitFieldInsn(GETSTATIC, className, expression_Ident.name, fieldtype);

		return null;

	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		if(statement_Assign.lhs.get_type().equals(Type.INTEGER) || statement_Assign.lhs.get_type().equals(Type.BOOLEAN)) {
			System.out.println("In statement assign bool");
			statement_Assign.e.visit(this, arg);
			statement_Assign.lhs.visit(this, arg);
			}
		else {
			System.out.println("In image under assign ");

				boolean bool=statement_Assign.lhs.isCartesian();
			
			
				mv.visitFieldInsn(GETSTATIC,className, statement_Assign.lhs.name,ImageSupport.ImageDesc);
                mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
				mv.visitVarInsn(ISTORE, 3);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
				mv.visitVarInsn(ISTORE, 4);
				
			
			    Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 1);
				Label l1 = new Label();
				mv.visitLabel(l1);
				Label l2 = new Label();
				mv.visitJumpInsn(GOTO, l2);
				Label l3 = new Label();
				mv.visitLabel(l3);
				mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 2);
				Label l4 = new Label();
				mv.visitLabel(l4);
				Label l5 = new Label();
				mv.visitJumpInsn(GOTO, l5);
				Label l6 = new Label();
				mv.visitLabel(l6);
				mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {Opcodes.INTEGER}, 0, null);
				statement_Assign.e.visit(this, arg);
				statement_Assign.lhs.visit(this, arg);
				Label l7 = new Label();
				mv.visitLabel(l7);
				mv.visitIincInsn(2, 1);
				mv.visitLabel(l5);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ILOAD, 2);
				mv.visitVarInsn(ILOAD, 4);
				mv.visitJumpInsn(IF_ICMPLT, l6);
				Label l8 = new Label();
				mv.visitLabel(l8);
				mv.visitIincInsn(1, 1);
				mv.visitLabel(l2);
				mv.visitFrame(Opcodes.F_CHOP,1, null, 0, null);
				mv.visitVarInsn(ILOAD, 1);
				mv.visitVarInsn(ILOAD, 3);
				mv.visitJumpInsn(IF_ICMPLT, l3);		
				
				

			
		}
		
		return null;
	}

}
