package com.beetl.sql.pref;
import org.beetl.ow2.asm.*;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.beetl.ow2.asm.Opcodes.*;

public class BeanAsmCode {
	static  final  String supperName  = BeanPropertyAsm.class.getName().replace('.','/');

	public static byte[] genCode(Class bean) throws Exception {
		ClassWriter classWriter = new ClassWriter(0);
		classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, getAsmClassName(getWriteClassName(bean)), null,
			supperName, null);
		genConstruct(classWriter);
		genPropertyWrite(classWriter,bean);
		genPropertyRead(classWriter,bean);
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
		Map<Label,PropertyDescriptorWrap> propertyDescriptorMap = new HashMap<>();
		List<Integer> labelIndex = new ArrayList<>();

		for(int i=0;i<ps.length;i++){
			PropertyDescriptorWrap propertyDescriptorWrap = BeanKit.getClassProperty(bean,i);
			if(propertyDescriptorWrap.getSetMethod()==null){
				continue;
			}
			Label label = new Label();
			labelIndex.add(i);
			switchLabelList.add(label);
			propertyDescriptorMap.put(label,propertyDescriptorWrap);
		}

		Label labelEnd = new Label();
		Label labelDefault = new Label();

		methodVisitor.visitLookupSwitchInsn(labelDefault, labelIndex.stream().mapToInt(Integer::valueOf).toArray(),
			switchLabelList.toArray(new Label[0]));

		boolean isFirst = true;
		for(Label label:switchLabelList){
			PropertyDescriptorWrap propertyDescriptor = propertyDescriptorMap.get(label);
			methodVisitor.visitLabel(label);
			if (isFirst) {
				methodVisitor.visitFrame(Opcodes.F_APPEND, 1,  new Object[]{beanAsmName}, 0, null);
				isFirst = false;
			} else {
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

			}
			methodVisitor.visitVarInsn(ALOAD, 4);
			methodVisitor.visitVarInsn(ALOAD, 3);
			String typeAsmName = getAsmClassName(propertyDescriptor.getProp().getName());
			methodVisitor.visitTypeInsn(CHECKCAST, typeAsmName);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanAsmName, propertyDescriptor.getSetMethod().getName(),
					"(L"+typeAsmName+";)V", false);
			methodVisitor.visitJumpInsn(GOTO, labelEnd);

		}
		//default:throwException(index,obj)
		methodVisitor.visitLabel(labelDefault);
		methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitVarInsn(ILOAD, 1);
		methodVisitor.visitVarInsn(ALOAD, 2);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, getAsmClassName(BeanPropertyAsm.class.getName()), "throwException",
				"(ILjava/lang/Object;)Ljava/lang/RuntimeException;", false);
		methodVisitor.visitInsn(ATHROW);


		methodVisitor.visitLabel(labelEnd);
		methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(3, 5);
		methodVisitor.visitEnd();

	}

	protected  static void genPropertyRead(ClassWriter classWriter,Class bean) throws Exception{
		String beanAsmName = getAsmClassName(bean.getName());

		MethodVisitor   methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "(ILjava/lang/Object;)Ljava/lang/Object;",
				null, null);
		methodVisitor.visitCode();
		methodVisitor.visitVarInsn(ALOAD, 2);
		methodVisitor.visitTypeInsn(CHECKCAST, beanAsmName);
		methodVisitor.visitVarInsn(ASTORE, 3);
		methodVisitor.visitVarInsn(ILOAD, 1);
		PropertyDescriptor[] ps = BeanKit.propertyDescriptors(bean);
		List<Label> switchLabelList = new ArrayList<>(ps.length);
		Map<Label,PropertyDescriptorWrap> propertyDescriptorMap = new HashMap<>();
		List<Integer> labelIndex = new ArrayList<>();

		for(int i=0;i<ps.length;i++){
			PropertyDescriptorWrap propertyDescriptorWrap = BeanKit.getClassProperty(bean,i);

			Label label = new Label();
			labelIndex.add(i);
			switchLabelList.add(label);
			propertyDescriptorMap.put(label,propertyDescriptorWrap);
		}

		Label labelDefault = new Label();

		methodVisitor.visitLookupSwitchInsn(labelDefault, labelIndex.stream().mapToInt(Integer::valueOf).toArray(),
				switchLabelList.toArray(new Label[0]));

		boolean isFirst = true;
		for(Label label:switchLabelList){
			PropertyDescriptorWrap propertyDescriptor = propertyDescriptorMap.get(label);
			methodVisitor.visitLabel(label);
			if (isFirst) {
				methodVisitor.visitFrame(Opcodes.F_APPEND, 1,  new Object[]{beanAsmName}, 0, null);
				isFirst = false;
			} else {
				methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

			}
			methodVisitor.visitVarInsn(ALOAD, 3);
			String typeAsmName = getAsmClassName(propertyDescriptor.getProp().getName());

			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanAsmName, propertyDescriptor.getProp().getReadMethod().getName(),
					"()L"+typeAsmName+";", false);
			methodVisitor.visitInsn(ARETURN);

		}
		//default:throwException(index,obj)
		methodVisitor.visitLabel(labelDefault);
		methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitVarInsn(ILOAD, 1);
		methodVisitor.visitVarInsn(ALOAD, 2);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, getAsmClassName(BeanPropertyAsm.class.getName()), "throwException",
				"(ILjava/lang/Object;)Ljava/lang/RuntimeException;", false);
		methodVisitor.visitInsn(ATHROW);
		methodVisitor.visitMaxs(3, 4);
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
