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
		ClassWriter classWriter = new ClassWriter(0);
		classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, getAsmClassName(getWriteClassName(bean)), null,
			supperName, null);
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
		List<Label> switchLabelList = new ArrayList<>(ps.length);
		Map<Label,PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
		List<Integer> labelIndex = new ArrayList<>();

		for(int i=0;i<ps.length;i++){
			PropertyDescriptor p = ps[i];
			if(p.getWriteMethod()==null){
				continue;
			}
			Label label = new Label();
			labelIndex.add(i);
			switchLabelList.add(label);
			propertyDescriptorMap.put(label,p);
		}

		Label labelEnd = new Label();
		Label labelDefault = new Label();

		methodVisitor.visitLookupSwitchInsn(labelDefault, labelIndex.stream().mapToInt(Integer::valueOf).toArray(),
			switchLabelList.toArray(new Label[0]));

		boolean isFirst = true;
		for(Label label:switchLabelList){
			PropertyDescriptor propertyDescriptor = propertyDescriptorMap.get(label);
			methodVisitor.visitLabel(label);
			if (isFirst) {
				methodVisitor.visitFrame(Opcodes.F_APPEND, 1,  new Object[]{beanAsmName}, 0, null);
				isFirst = false;
			} else {
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

			}
			methodVisitor.visitVarInsn(ALOAD, 4);
			methodVisitor.visitVarInsn(ALOAD, 3);
			String typeAsmName = getAsmClassName(propertyDescriptor.getPropertyType().getName());
			methodVisitor.visitTypeInsn(CHECKCAST, typeAsmName);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanAsmName, propertyDescriptor.getWriteMethod().getName(),
					"(L"+typeAsmName+";)V", false);
			methodVisitor.visitJumpInsn(GOTO, labelEnd);

		}
		//default:throwException(index,obj)
		methodVisitor.visitLabel(labelDefault);
		methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitVarInsn(ILOAD, 1);
		methodVisitor.visitVarInsn(ALOAD, 2);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, supperName, "throwException",
			"(ILjava/lang/Object;)V", false);


		methodVisitor.visitLabel(labelEnd);
		methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(3, 5);
		methodVisitor.visitEnd();

	}

	protected  static  void genConstruct(ClassWriter classWriter){

		MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		methodVisitor.visitCode();
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, supperName, "<init>", "()V",
			false);
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
