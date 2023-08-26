package com.ibeetl.sql.pref;

import com.beetl.sql.pref.BeanPropertyAsm;
import com.beetl.sql.pref.BeanPropertyWriteFactory;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Data;
import lombok.SneakyThrows;
import org.beetl.sql.clazz.kit.BeanKit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Benchmark                     Mode  Cnt        Score         Error   Units
 * BeanASMPerfTest.direct       thrpt    3  3597282.120 ± 2031070.301  ops/ms
 * BeanASMPerfTest.myasm        thrpt    3  1889599.007 ± 1013919.850  ops/ms
 * BeanASMPerfTest.propertySet  thrpt    3     3462.095 ±    2748.584  ops/ms
 * BeanASMPerfTest.reflectAsm   thrpt    3    18580.151 ±   13286.590  ops/ms
 * </pre>
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(0)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class BeanASMPerfTest {
	PropertyDescriptor[] propertyDescriptors;
	private int[] reflectIndex = null;
	MethodAccess methodAccess = MethodAccess.get(MyTestBean.class);

	BeanPropertyAsm myBeanWrite = null;

	@Benchmark
	public MyTestBean direct() {
		MyTestBean testBean = new MyTestBean();
		testBean.setCol1(getAttr(1));
		testBean.setCol2(getAttr(2));
		testBean.setCol3(getAttr(3));
		testBean.setCol4(getAttr(4));
		testBean.setCol5(getAttr(5));
		testBean.setCol7(getAttr(6));
		testBean.setCol7(getAttr(7));
		testBean.setCol8(getAttr(8));
		return testBean;

	}

	@Benchmark
	public MyTestBean reflectAsm() {
		MyTestBean testBean = new MyTestBean();
		for(int i=1;i<=8;i++){
			methodAccess.invoke(testBean,reflectIndex[i],getAttr(i));
		}
		return testBean;
	}

	@Benchmark
	public MyTestBean myasm() {
		MyTestBean testBean = new MyTestBean();
		for(int i=1;i<=8;i++){
			myBeanWrite.setValue(i,testBean,getAttr(i));
		}
		return testBean;

	}

	@Benchmark
	public MyTestBean propertySet() throws InvocationTargetException, IllegalAccessException {
		MyTestBean testBean = new MyTestBean();
		for(int i=1;i<=8;i++){
			propertyDescriptors[i].getWriteMethod().invoke(testBean,getAttr(i));
		}
		return testBean;

	}


	@SneakyThrows
	@Setup
	public void init(){
		MethodAccess methodAccess = MethodAccess.get(MyTestBean.class);
		PropertyDescriptor[] propertyDescriptors = BeanKit.propertyDescriptors(MyTestBean.class);
		int[] index = new int[propertyDescriptors.length];
		int i=0;
		for(PropertyDescriptor ps:propertyDescriptors){
			Method method = ps.getWriteMethod();
			if(method==null){
				continue;
			}
			index[i++] = methodAccess.getIndex(method.getName());
		}

		reflectIndex = index;
		this.propertyDescriptors = propertyDescriptors;
		myBeanWrite = BeanPropertyWriteFactory.getBeanProperty(MyTestBean.class);

	}




	protected String getAttr(int index){
		return "v";
	}


	public static void main(String[] args) throws RunnerException {


		Options opt = new OptionsBuilder()
				.include(BeanASMPerfTest.class.getSimpleName())
				.build();
		new Runner(opt).run();
	}

	@Data
	public static class MyTestBean{
		protected String col1;
		protected String col2;
		protected String col3;
		protected String col4;
		protected String col5;
		protected String col6;
		protected String col7;
		protected String col8;
	}
}
