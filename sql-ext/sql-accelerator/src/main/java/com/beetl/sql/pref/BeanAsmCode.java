package com.beetl.sql.pref;
import org.beetl.core.om.asm.Constants;
import org.beetl.ow2.asm.*;
import org.beetl.sql.clazz.kit.BeanKit;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.beetl.ow2.asm.Opcodes.*;

public class BeanAsmCode {
	static  final  String supperName  = BeanPropertyWrite.class.getName().replace('.','/');

	public static byte[] genCode(Class bean) throws Exception {
		String[] supperInterface = new String[]{supperName};
		ClassWriter classWriter = new ClassWriter(0);
		classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, getAsmClassName(getWriteClassName(bean)), null,
				"java/lang/Object", supperInterface);
		genConstruct(classWriter);
		genPropertyWrite(classWriter,bean);
		classWriter.visitEnd();
		return classWriter.toByteArray();
	}



	protected  static void genPropertyWrite(ClassWriter classWriter,Class bean) throws Exception{
		String beanAsmName = getAsmClassName(bean.getName());

		MethodVisitor  methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setValue", "(ILjava/lang/Object;Ljava/lang/Object;)V",
				null, null);
		methodVisitor.visitCode();
		methodVisitor.visitVarInsn(ALOAD, 2);
		methodVisitor.visitTypeInsn(CHECKCAST, beanAsmName);
		methodVisitor.visitVarInsn(ASTORE, 4);
		methodVisitor.visitVarInsn(ILOAD, 1);
		PropertyDescriptor[] ps = BeanKit.propertyDescriptors(bean);
		Map<Integer,Label> switchLabelMap = new HashMap<>();
		List<Label> switchLabelList = new ArrayList<>(ps.length);
		Map<Label,PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
		int i=0;
		for(PropertyDescriptor p:ps){
			if(p.getWriteMethod()==null){
				continue;
			}
			Label label = new Label();
			switchLabelMap.put(i++,label);
			switchLabelList.add(label);
			propertyDescriptorMap.put(label,p);
		}
		Label labelEnd = new Label();
		methodVisitor.visitTableSwitchInsn(1, switchLabelList.size(), labelEnd,
				switchLabelList.toArray(new Label[0]));
		i =0;
		for(Label label:switchLabelList){
			PropertyDescriptor propertyDescriptor = propertyDescriptorMap.get(label);
			methodVisitor.visitLabel(label);
			if (i==0) {
				methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{beanAsmName}, 0, null);
			} else {
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			}
			methodVisitor.visitVarInsn(ALOAD, 4);
			methodVisitor.visitVarInsn(ALOAD, 3);

			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanAsmName, propertyDescriptor.getWriteMethod().getName(),
					"(Ljava/lang/String;)V", false);
			if(i!=switchLabelList.size()-1){
				methodVisitor.visitJumpInsn(GOTO, labelEnd);
			}

			i++;

		}

		methodVisitor.visitLabel(labelEnd);
		methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(2, 5);
		methodVisitor.visitEnd();

	}

	protected  static  void genConstruct(ClassWriter classWriter){
		MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		methodVisitor.visitCode();
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(1, 1);
		methodVisitor.visitEnd();


	}

	public static String getWriteClassName(Class bean){
		return bean.getName()+"$"+"Setter";
	}
	public static String getAsmClassName(String name){
		return name.replace('.','/');
	}


}
