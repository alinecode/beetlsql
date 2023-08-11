package com.ibeetl.sql.pref;
import com.beetl.sql.pref.BeanPropertyWriteFactory;
import org.beetl.ow2.asm.ClassWriter;
import org.beetl.ow2.asm.FieldVisitor;
import org.beetl.ow2.asm.MethodVisitor;
import org.beetl.ow2.asm.Opcodes;


public class BeanASMProcessorDump implements Opcodes {

	public static void main(String[] args) throws  Exception {
		byte[] bs = dump();
		BeanPropertyWriteFactory.ByteClassLoader loader = new BeanPropertyWriteFactory.ByteClassLoader(BeanASMProcessorDump.class.getClassLoader());
		String name = "com.ibeetl.sql.pref.BeanASMProcessor2";
		loader.defineClass(name,bs);
	}

	public static byte[] dump() throws Exception {

		ClassWriter classWriter = new ClassWriter(0);


		MethodVisitor methodVisitor;


		classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/ibeetl/sql/pref/BeanASMProcessor2", null,
				"java/lang/Object", null);

		{
			methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(1, 1);
			methodVisitor.visitEnd();
		}

		classWriter.visitEnd();

		return classWriter.toByteArray();
	}
}
