package org.beetl.sql.mapper.springdata;


import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MapperExtBuilder;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.mapper.builder.ParameterParser;
import org.beetl.sql.mapper.builder.ReturnTypeParser;

import java.lang.reflect.Method;
import java.util.*;


/**
 * 模仿springdata，根据方法名得出sql语句
 * 参考 {@link "https://docs.spring.io/spring-data/jdbc/docs/2.0.1.RELEASE/reference/html/#jdbc.query-methods"}
 * 注意：并未完全支持Spring Data 所有查询关键字
 * @author xiandafu
 * @author 赵栩旸 zhaoxuyang19971015@foxmail.com
 */
public class SpringDataBuilder  implements MapperExtBuilder {

    static Map<String,Class>  conditionMap = new HashMap<>();
    static {
        conditionMap.put("After",After.class);
        conditionMap.put("GreaterThan",After.class);
        conditionMap.put("GreaterThanEqual",AfterEqual.class);
        conditionMap.put("Before",Before.class);
        conditionMap.put("LessThan",Before.class);
        conditionMap.put("LessThanEqual",BeforeEquals.class);
        conditionMap.put("Between",Between.class);
        conditionMap.put("NotBetween",NotBetween.class);
        conditionMap.put("In",In.class);
        conditionMap.put("NotIn",NotIn.class);
        conditionMap.put("IsNull",IsNull.class);
        conditionMap.put("Null",IsNull.class);
        conditionMap.put("IsNotNull",IsNotNull.class);
        conditionMap.put("NotNull",IsNotNull.class);
        conditionMap.put("Like",Like.class);
        conditionMap.put("StartingWith",Like.class);
        conditionMap.put("EndingWith",Like.class);
        conditionMap.put("NotLike",NotLike.class);
        conditionMap.put("IsNotLike",NotLike.class);
        conditionMap.put("Containing",Containing.class);
        conditionMap.put("NotContaining",NotContaining.class);
        conditionMap.put("Not",NotEquals.class);
        conditionMap.put("And",And.class);
        conditionMap.put("Or",Or.class);
        conditionMap.put("Equals",Equals.class);

    }
    static And and = new And();
    static Or or = new Or();

    static final List<String> reserved;
    //不要调整顺序，解析Not和NotIn跟顺序有关
    static {
        reserved = new ArrayList<>();

        reserved.add("queryBy");
        reserved.add("findBy");
        reserved.add("getBy");
        reserved.add("After");
        reserved.add("OrderBy");
        reserved.add("GreaterThan");
        reserved.add("GreaterThanEqual");
        reserved.add("Before");
        reserved.add("LessThan");
        reserved.add("LessThanEqual");
        reserved.add("Between");
        reserved.add("NotBetween");
        reserved.add("In");
        reserved.add("NotIn");
        reserved.add("IsNull");
        reserved.add("Null");
        reserved.add("IsNotNull");
        reserved.add("NotNull");
        reserved.add("Like");
        reserved.add("StartingWith");
        reserved.add("EndingWith");
        reserved.add("NotLike");
        reserved.add("IsNotLike");
        reserved.add("Containing");
        reserved.add("NotContaining");
        reserved.add("Not");
        reserved.add("And");
        reserved.add("Or");
        reserved.add("Asc");
        reserved.add("Desc");
    }

    public SpringDataBuilder(){

    }

    public Condition getConditionByWord(String word){
        if(word.equals("And")){
            return and;
        }else if(word.equals("Or")){
            return or;
        }
        try {
            return (Condition)conditionMap.get(word).newInstance();
        } catch (Exception e) {
            //不可能发生
           throw new IllegalStateException(e);
        }
    }

    @Override
    public MapperInvoke parse(Class entity, Method m) {

        String method = m.getName();
        List<String> list = splitStr(method);
        String query =list.get(0);
        if(!(query.equals("queryBy")||query.equals("getBy")||query.equals("findBy"))){
            throw new BeetlSQLException(BeetlSQLException.UNKNOW_MAPPER_SQL_TYPE,"方法名不符合 "+m.getName());
        }
        List<Condition> conditions = new ArrayList<>();
        String lastAttr = null;
        Condition lastCondition = null;
        for(int i=1;i<list.size();i++){
            String word = list.get(i);
            if(reserved.contains(word)){
                if(word.equals("OrderBy")){
                    OrderBy orderBy = new OrderBy();

                    String attr = list.get(i+1);
                    orderBy.setAttr(attr);
                    if(list.size()>i+2){
                        String sortStr = list.get(i+2);
                        if(sortStr.equals("Asc")){
                            orderBy.asc = true;
                        }else{
                            orderBy.asc = false;
                        }
                    }
                    addDefaultEquals(conditions,lastAttr,lastCondition);
                    lastCondition=null;
                    lastAttr = null;
                    conditions.add(orderBy);

                    break;
                }else if(word.equals("And")||word.equals("Or")){
                    addDefaultEquals(conditions,lastAttr,lastCondition);
                    lastCondition=null;
                    lastAttr = null;
                    conditions.add(getConditionByWord(word));
                    continue;
                }
                lastCondition = getConditionByWord(word);
                if(lastAttr==null){
                    throw new BeetlSQLException(BeetlSQLException.UNKNOW_MAPPER_SQL_TYPE,"方法名不符合 "+m.getName());
                }
                lastCondition.setAttr(lastAttr);
                conditions.add(lastCondition);
                lastCondition=null;
                lastAttr = null;

            }else{
                lastAttr = word;
            }
        }

        addDefaultEquals(conditions,lastAttr,lastCondition);
        ParameterParser parameterParser = new ParameterParser(m);
        MethodParamsHolder paramsHolder = parameterParser.getHolder();

        ReturnTypeParser returnTypeParser = new ReturnTypeParser(m,entity);
        boolean isSingle = true;
        if(returnTypeParser.isCollection()){
            isSingle = false;
        }
        SpringDataSelectMI springData = new SpringDataSelectMI(conditions,paramsHolder,isSingle);
        return springData;

    }

    protected  void addDefaultEquals(List<Condition> conditions,String  lastAttr,Condition lastCondition){
        if(lastAttr!=null&&lastCondition==null){
            //默认为equals
            Condition equals = getConditionByWord("Equals");
            equals.setAttr(lastAttr);
            conditions.add(equals);
        }
    }

    /**
     * 按照关键字分割字符串
     * @param str 待分割的字符串
     * @return 分割完成的字符串列表
     */
    private static List<String> splitStr(String str) {
        List<String> result = new ArrayList<>();
        Map<Integer, String> indexContentMap = new TreeMap<>();

        // 提取关键词，提取完后将字符所在位置置空
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < reserved.size(); i++) {
            String key = reserved.get(i);
            for (; ; ) {
                int index = sb.indexOf(key);
                // 源字符串中不存在该关键词
                if (index < 0) {
                    break;
                }
                // 提取
//                indexContentMap.put(str.indexOf(key), key);
                indexContentMap.put(index, key);
                // 置空
                for (int j = 0; j < key.length(); j++) {
                    sb.setCharAt(index + j, '\0');
                }
            }
        }
        // 提取非关键词，顺序提取
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) != '\0') {
                int startIndex = i;
                while (i < sb.length() && sb.charAt(i) != '\0') {
                    i++;
                }
                indexContentMap.put(startIndex, sb.substring(startIndex, i));
            }
        }
        // 将提取的内容(TreeMap保证了有序性)存入列表
        for (Map.Entry<Integer, String> entry : indexContentMap.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }





    static class Context{
        int begin;
        Query query;
        Class target;
        Object[] args;
        boolean isAnd = true;
        MethodParamsHolder holder;
        public void movePara(){
            begin =begin+1;
        }
        public void movePara(int i){
            begin =begin+i;
        }

    }

     interface KeyWord{
        public void run(Context ctx);
    }


    static abstract  class Condition implements KeyWord{
        String  attr;
        public void setAttr(String attr){
            this.attr = attr;
        }

        public String getCol(Class cls,Query query){
            return query.sqlManager.getNc().getColName(cls,attr);
        }


    }

    static class And extends   Condition{

        @Override
        public void run(Context ctx) {
           ctx.isAnd = true;
        }
    }

    static class Or extends   Condition{

        @Override
        public void run(Context ctx) {
            ctx.isAnd = false;
        }
    }

    static class After extends   Condition{

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andGreat(col,ctx.args[ctx.begin]);
            }else{
                ctx.query.orGreat(col,ctx.args[ctx.begin]);
            }

            ctx.movePara();
        }
    }

    static class AfterEqual extends   Condition{

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andGreatEq(col,ctx.args[ctx.begin]);
            }else{
                ctx.query.orGreatEq(col,ctx.args[ctx.begin]);
            }
            ctx.movePara();
        }
    }


    static class Before extends   Condition{

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andLess(col,ctx.args[ctx.begin]);
            }else{
                ctx.query.orLess(col,ctx.args[ctx.begin]);
            }

            ctx.movePara();
        }
    }


    static class BeforeEquals extends   Condition{

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andLessEq(col,ctx.args[ctx.begin]);
            }else{
                ctx.query.orLessEq(col,ctx.args[ctx.begin]);
            }

            ctx.movePara();
        }
    }

    static class Equals extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andEq(col,ctx.args[ctx.begin]);
            }else{
                ctx.query.orEq(col,ctx.args[ctx.begin]);
            }

            ctx.movePara();
        }
    }

    static class NotEquals extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andNotEq(col,ctx.args[ctx.begin]);
            }else{
                ctx.query.orNotEq(col,ctx.args[ctx.begin]);
            }
            ctx.movePara();
        }
    }

    static class Between extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andBetween(col,ctx.args[ctx.begin],ctx.args[ctx.begin+1]);
            }else{
                ctx.query.orBetween(col,ctx.args[ctx.begin],ctx.args[ctx.begin+1]);
            }

            ctx.movePara(2);
        }
    }

    static class In extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andIn(col,(Collection)ctx.args[ctx.begin]);
            }else{
                ctx.query.orIn(col,(Collection)ctx.args[ctx.begin]);
            }

            ctx.movePara();
        }
    }

    static class NotIn extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andNotIn(col,(Collection)ctx.args[ctx.begin]);
            }else{
                ctx.query.orNotIn(col,(Collection)ctx.args[ctx.begin]);
            }

            ctx.movePara();
        }
    }

    static class NotBetween extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andNotBetween(col,ctx.args[ctx.begin],ctx.args[ctx.begin+1]);
            }else{
                ctx.query.orNotBetween(col,ctx.args[ctx.begin],ctx.args[ctx.begin+1]);
            }

            ctx.movePara(2);
        }
    }

    static class IsNull extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andIsNull(col);
            }else{
                ctx.query.orIsNull(col);
            }

        }
    }

    static class IsNotNull extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andIsNotNull(col);
            }else{
                ctx.query.orIsNotNull(col);
            }

        }
    }

    static class Like extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andLike(col,ctx.args[ctx.begin]);
            }else{
                ctx.query.orLike(col,ctx.args[ctx.begin]);
            }
            ctx.movePara();
        }
    }

    static class NotLike extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andNotLike(col,ctx.args[ctx.begin]);
            }else{
                ctx.query.orNotLike(col,ctx.args[ctx.begin]);
            }

            ctx.movePara();
        }
    }

    static class Containing extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andLike(col,"%"+ctx.args[ctx.begin]+"%");;
            }else{
                ctx.query.orLike(col,"%"+ctx.args[ctx.begin]+"%");;
            }

            ctx.movePara();
        }
    }

    static class NotContaining extends   Condition {

        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(ctx.isAnd){
                ctx.query.andNotLike(col,"%"+ctx.args[ctx.begin]+"%");;
            }else{
                ctx.query.orNotLike(col,"%"+ctx.args[ctx.begin]+"%");;
            }

            ctx.movePara();
        }
    }


    static class OrderBy extends   Condition{
        boolean asc;
        public OrderBy(){
        }
        @Override
        public void run(Context ctx) {
            String col = getCol(ctx.target,ctx.query);
            if(asc){
                ctx.query.asc(col);
            }else{
                ctx.query.desc(col);
            }
            ctx.movePara();
        }

    }



    public static void main(String[] args){
//        String exp ="(queryBy|findBy)((.+?)(Equals|After|Before))";

        String method = "queryByNameAndDepartmentIdOrderByIdAsc";
        List<String> list  =  splitStr(method);
        System.out.println(list);
    }

}
