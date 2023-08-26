package com.beetl.sql.pref;
import lombok.Data;
import org.beetl.ow2.asm.*;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;
import org.beetl.sql.clazz.kit.StringKit;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
			PropertyDescriptorWrap propertyDescriptorWrap =new PropertyDescriptorWrap(bean,ps[i],i);
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
		//TODO，修改成tableswitch
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
			String paramAsmDesc = null;
			Class paramType =propertyDescriptor.getProp().getPropertyType();
			if(paramType.isPrimitive()){
				BoxClass boxType = getPrimitiveBoxType(paramType);
				String typeAsmName = getAsmClassName(boxType.getType().getName());
				methodVisitor.visitTypeInsn(CHECKCAST, typeAsmName);
				//unbox
				methodVisitor.visitMethodInsn(INVOKEVIRTUAL, typeAsmName, boxType.getValueMethod(), "()"+boxType.valueMethodRetType, false);
				paramAsmDesc = "("+boxType.valueMethodRetType+")";
			}else{
				String typeAsmName = getAsmClassName(paramType.getName());
				methodVisitor.visitTypeInsn(CHECKCAST, typeAsmName);
				paramAsmDesc = "(L"+typeAsmName+";)";
			}

			Class clss = propertyDescriptor.getSetMethod().getReturnType();
			String retTypeDesc = "V";
			if(clss!=void.class){
				//链式调用
				retTypeDesc = "L"+getAsmClassName(clss.getName())+";";
			}

			//调用方法名+参数+返回值
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanAsmName, propertyDescriptor.getSetMethod().getName(),
				paramAsmDesc+retTypeDesc, false);
			if(clss!=void.class){
				methodVisitor.visitInsn(POP);
			}

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
			PropertyDescriptorWrap propertyDescriptorWrap = new PropertyDescriptorWrap(bean,ps[i],i);

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
			Class paramType =propertyDescriptor.getProp().getPropertyType();

			if(paramType.isPrimitive()){
				BoxClass boxType = getPrimitiveBoxType(paramType);
				methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanAsmName, propertyDescriptor.getProp().getReadMethod().getName(),
					"()"+boxType.valueMethodRetType, false);
				String paramAsmDesc = "("+boxType.valueMethodRetType+")L"+getAsmClassName(boxType.getType().getName())+";";
				//box
				methodVisitor.visitMethodInsn(INVOKESTATIC, getAsmClassName(boxType.getType().getName()), "valueOf", paramAsmDesc,
					false);

			}else{
				String typeAsmName = getAsmClassName(propertyDescriptor.getProp().getPropertyType().getName());
				methodVisitor.visitMethodInsn(INVOKEVIRTUAL, beanAsmName, propertyDescriptor.getProp().getReadMethod().getName(),
					"()L"+typeAsmName+";", false);
			}

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
	@Data
	static class BoxClass{
		Class type;
		String valueMethod;
		String valueMethodRetType;
		public BoxClass(Class boxType,String boxValueMethod,String boxValueMethodRetType){
			this.type = boxType;
			this.valueMethod = boxValueMethod;
			this.valueMethodRetType = boxValueMethodRetType;
		}
	}

	static Map<Class,BoxClass> boxMap = new HashMap();
	static {
		boxMap.put(int.class,new BoxClass(Integer.class,"intValue","I"));
		boxMap.put(double.class,new BoxClass(Double.class,"doubleValue","D"));
		boxMap.put(long.class,new BoxClass(Long.class,"longValue","J"));
		boxMap.put(short.class,new BoxClass(Short.class,"shortValue","S"));
		boxMap.put(byte.class,new BoxClass(Byte.class,"byteValue","B"));
	}

	protected static BoxClass getPrimitiveBoxType(Class type){
		BoxClass boxClass = boxMap.get(type);
		if(boxClass==null){
			//不可能到达这里
			throw new UnsupportedOperationException("不支持的原始类型 "+type.getName()+" 需要改成封装类型");
		}
		return boxClass;
	}

	public static String getWriteClassName(Class bean){
		return bean.getName()+"$"+"ASM";
	}
	public static String getAsmClassName(String name){
		return name.replace('.','/');
	}



}
