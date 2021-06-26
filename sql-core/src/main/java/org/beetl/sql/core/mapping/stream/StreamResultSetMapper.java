package org.beetl.sql.core.mapping.stream;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.mapping.ResultSetMapper;

import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @param <T>
 */
public class  StreamResultSetMapper<T> implements ResultSetMapper<T> {
    public  static int  fetchSize = 100;
    @Override
    public List<T> mapping(ExecuteContext ctx, Class target, ResultSet resultSet, Annotation config) throws SQLException {
        resultSet.setFetchSize(fetchSize);
        return new SimpleList(ctx,resultSet,target);
    }

    static  class SimpleIterator<E> implements Iterator<E>{
        ResultSet rs = null;
        ExecuteContext ctx;
        Class<E> target;
        public SimpleIterator(ExecuteContext ctx,ResultSet rs,Class<E> target){
            this.ctx = ctx;
            this.rs = rs;

        }


        @Override
        public boolean hasNext() {
            try {
                return rs.next();
            } catch (SQLException exception) {
                throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,exception);
            }
        }

        @Override
        public E next() {
            try {
                E obj = ctx.customizedBeanProcessor.toBean(ctx,rs,target);
                return obj;
            } catch (SQLException exception) {
                throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,exception);
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
           throw new UnsupportedOperationException();
        }
    }

    static  class SimpleList<E> implements  List<E>{
        SimpleIterator simpleIterator = null;
        boolean used = false;
        public SimpleList(ExecuteContext ctx,ResultSet rs,Class<E> target){
            simpleIterator = new SimpleIterator(ctx,rs,target);
        }

        @Override
        public Iterator<E> iterator() {
            if(used){
                throw new BeetlSQLException(BeetlSQLException.ERROR,"无法再次遍历");
            }
            used = true;
            return simpleIterator;
        }

        @Override
        public  void forEach(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            for (E t : this) {
                action.accept(t);
            }
            try {
                simpleIterator.rs.close();
            } catch (SQLException exception) {
               //ignore
            }
        }

        @Override
        public int size() {
           return -1;
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException();
        }



        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {

        }

        @Override
        public E get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, E element) {

        }

        @Override
        public E remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<E> listIterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }
    }
}
