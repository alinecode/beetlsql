package com.beetl.sql.dynamic;

import lombok.Data;
import org.beetl.ow2.asm.*;
import org.beetl.sql.clazz.ColDesc;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLManager;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

import static org.beetl.ow2.asm.Opcodes.*;

/**
 * 根据表定义字节生成entity的字节码
 */
public class BeanTableAsmCode {

	public static byte[] genCode(SQLManager sqlManager,String name,String pkg,Class baseBeanClass) throws Exception {
		String supperName = baseBeanClass.getName().replace('.','/');
		TableDesc tableDesc = sqlManager.getTableDesc(name);
		if(tableDesc==null){
			throw new IllegalArgumentException("表不存在 "+name);
		}
		String clsName = sqlManager.getNc().getClassName(name);
		String  onwerClass = getAsmClassName(pkg+"."+clsName);
		ClassWriter classWriter = new ClassWriter(0);
		classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, onwerClass, null,
			supperName, null);

		AnnotationVisitor annotationVisitor0 = classWriter.visitAnnotation("Lorg/beetl/sql/annotation/entity/Table;", true);
		annotationVisitor0.visit("name", name);
		annotationVisitor0.visitEnd();

		genConstruct(classWriter,supperName);
		genPrivateFiled(classWriter, sqlManager,tableDesc,baseBeanClass);
		genProperty(classWriter,sqlManager,onwerClass,tableDesc, baseBeanClass);
		classWriter.visitEnd();
		return classWriter.toByteArray();
	}

	static void genProperty(ClassWriter classWriter,SQLManager sqlManager,String clsName,TableDesc tableDesc,Class baseClass) throws Exception {
		Set<String> cols = tableDesc.getCols();
		String owner = clsName;

		for(String col:cols){
			String attrName = sqlManager.getNc().getPropertyName(col);
			Field f = BeanKit.getField(baseClass,attrName);
			if(f!=null){
				//父类已经有了
				continue;
			}

			ColDesc colDesc  = tableDesc.getColDesc(col);
			String javaType =  getJavaType(colDesc.getSqlType());

			String setterMethod = StringKit.getMethodName(attrName,javaType.equals("java.lang.Boolean"),false);
			String getterMethod = StringKit.getMethodName(attrName,javaType.equals("java.lang.Boolean"),true);

			MethodVisitor  methodVisitor = null;

			methodVisitor = classWriter.visitMethod(ACC_PUBLIC, setterMethod, "(L"+getAsmClassName(javaType)+";)V",
				null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			//TODO 没有考虑byte[] 这种情况
			methodVisitor.visitFieldInsn(PUTFIELD, owner, attrName, "L"+getAsmClassName(javaType)+";");
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(2, 2);
			methodVisitor.visitEnd();


			methodVisitor = classWriter.visitMethod(ACC_PUBLIC, getterMethod, "()L"+getAsmClassName(javaType)+";", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitFieldInsn(GETFIELD, owner, attrName, "L"+getAsmClassName(javaType)+";");
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(1, 1);
			methodVisitor.visitEnd();

		}
	}

	static void genPrivateFiled(ClassWriter classWriter,SQLManager sqlManager,TableDesc tableDesc,Class baseClass) throws Exception {
		Set<String> cols = tableDesc.getCols();
		for(String col:cols){
			String attrName = sqlManager.getNc().getPropertyName(col);
			Field f = BeanKit.getField(baseClass,attrName);
			if(f!=null){
				//父类已经有了
				continue;
			}
			ColDesc colDesc  = tableDesc.getColDesc(col);
			String javaType =  getJavaType(colDesc.getSqlType());
			FieldVisitor fieldVisitor= classWriter.visitField(ACC_PRIVATE,attrName,"L"+getAsmClassName(javaType)+";",null,null);
			if(tableDesc.getIdNames().contains(col)){
				if(colDesc.isAuto()){
					AnnotationVisitor annotationVisitor0 = fieldVisitor.visitAnnotation("Lorg/beetl/sql/annotation/entity/AutoID;", true);
					annotationVisitor0.visitEnd();
				}else{
					AnnotationVisitor annotationVisitor0 = fieldVisitor.visitAnnotation("Lorg/beetl/sql/annotation/entity/AssignID;", true);
					annotationVisitor0.visitEnd();
				}
			}else if(colDesc.isAuto()){
				AnnotationVisitor annotationVisitor0 = fieldVisitor.visitAnnotation("Lorg/beetl/sql/annotation/entity/Auto;", true);
				annotationVisitor0.visitEnd();
			}
		}
	}

	public static String getJavaType(int colType){
		String javaType = JavaType.mapping.get(colType);
		if(javaType.equals("UNKNOW")||javaType.equals("OBJECT")){
			javaType = "java.lang.Object";
		}else if(javaType.equals("DATE")){
			javaType = "java.util.Date";
		}else if(javaType.equals("SPECIAL")){
			javaType = "java.math.BigDecimal";
		}else if(Character.isUpperCase(javaType.charAt(0))){
			javaType = "java.lang."+javaType;
		}
		return javaType;
	}


	protected  static  void genConstruct(ClassWriter classWriter,String supperName){

		MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		methodVisitor.visitCode();
		methodVisitor.visitVarInsn(ALOAD, 0);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, supperName, "<init>", "()V",
			false);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(1, 1);
		methodVisitor.visitEnd();

	}


	public static String getAsmClassName(String name){
		return name.replace('.','/');
	}






}
